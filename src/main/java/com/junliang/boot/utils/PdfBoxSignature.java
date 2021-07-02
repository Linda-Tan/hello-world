package com.junliang.boot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/**
 * Pdf 签名
 * @author junlinag.li
 * @date 2021/6/23
 */
@Slf4j
public class PdfBoxSignature extends CreateSignatureBase {

	/**
	 * Initialize the signature creator with a keystore (pkcs12) and pin that should be used for the
	 * signature.
	 *
	 * @param keystore is a pkcs12 keystore.
	 * @param pin is the pin for the keystore / private key
	 * @throws KeyStoreException if the keystore has not been initialized (loaded)
	 * @throws NoSuchAlgorithmException if the algorithm for recovering the key cannot be found
	 * @throws UnrecoverableKeyException if the given password is wrong
	 * @throws CertificateException if the certificate is not valid as signing time
	 * @throws IOException if no certificate could be found
	 */
	public PdfBoxSignature(KeyStore keystore, char[] pin) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
		super(keystore, pin);
	}

	/**
	 *  对pdf进行数字签名
	 * @param document 待签名 PDF
	 * @param output 接收签名返回数据
	 * @throws IOException
	 */
	public void signPDF(PDDocument document, OutputStream output) throws IOException {
		signPDF(document, output, null);
	}
	/**
	 * 对pdf进行可见的数字签名
	 * @param document  pdf 对象
	 * @param output 输出流
	 * @param signImage 签名图片
	 * @throws IOException
	 */
	public void signPDF(PDDocument document, OutputStream output, InputStream signImage)
			throws IOException {
		int accessPermissions = getMDPPermission(document);
		if (accessPermissions == 1) {
			throw new IllegalStateException("PDF没有修改权限");
		}
		// create signature dictionary
		PDSignature signature = new PDSignature();

		if (document.getVersion() >= 1.5f && accessPermissions == 0) {
			setMDPPermission(document, signature, 2);
		}

		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		if (acroForm != null && acroForm.getNeedAppearances()) {
			if (acroForm.getFields().isEmpty()) {
				acroForm.getCOSObject().removeItem(COSName.NEED_APPEARANCES);
			}
			else {
				log.warn("/NeedAppearances is set, signature may be ignored by Adobe Reader");
			}
		}

		signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
		signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
		signature.setName("e-archives");
		signature.setLocation("上海");
		signature.setReason("签名");
		signature.setSignDate(Calendar.getInstance());
		// Optional: certify
		if (accessPermissions == 0) {
			setMDPPermission(document, signature, 2);
		}
		if (signImage != null) {
			PDVisibleSignDesigner visibleSignDesigner = new PDVisibleSignDesigner(document, signImage, 1);
			visibleSignDesigner.xAxis(50).yAxis(100).zoom(-50).adjustForRotation();
			PDVisibleSigProperties visibleSignatureProperties = new PDVisibleSigProperties();
			visibleSignatureProperties.signerName(signature.getName()).signerLocation(signature.getLocation())
					.signatureReason(signature.getReason()).
					preferredSize(0).page(1).visualSignEnabled(true).
					setPdVisibleSignature(visibleSignDesigner);
			visibleSignatureProperties.buildSignature();
			try (SignatureOptions signatureOptions = new SignatureOptions()) {
				signatureOptions.setVisualSignature(visibleSignatureProperties.getVisibleSignature());
				signatureOptions.setPage(visibleSignatureProperties.getPage() - 1);
				document.addSignature(signature, this, signatureOptions);
			}
		}
		else {
			document.addSignature(signature, this);
		}
		// write incremental (only for signing purpose)
		document.saveIncremental(output);
		document.close();
	}
}
