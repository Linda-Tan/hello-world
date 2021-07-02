package com.junliang.boot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public abstract class CreateSignatureBase implements SignatureInterface {
	private PrivateKey privateKey;

	private Certificate[] certificateChain;

	private boolean externalSigning;

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
	public CreateSignatureBase(KeyStore keystore, char[] pin)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
		// grabs the first alias from the keystore and get the private key. An
		// alternative method or constructor could be used for setting a specific
		// alias that should be used.
		Enumeration<String> aliases = keystore.aliases();
		String alias;
		Certificate cert = null;
		while (aliases.hasMoreElements()) {
			alias = aliases.nextElement();
			setPrivateKey((PrivateKey) keystore.getKey(alias, pin));
			Certificate[] certChain = keystore.getCertificateChain(alias);
			if (certChain == null) {
				continue;
			}
			setCertificateChain(certChain);
			cert = certChain[0];
			if (cert instanceof X509Certificate) {
				// avoid expired certificate
				((X509Certificate) cert).checkValidity();
			}
			break;
		}

		if (cert == null) {
			throw new IOException("Could not find certificate");
		}
	}

	public final void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public final void setCertificateChain(final Certificate[] certificateChain) {
		this.certificateChain = certificateChain;
	}

	/**
	 * SignatureInterface implementation.
	 *
	 * This method will be called from inside of the pdfbox and create the PKCS #7 signature.
	 * The given InputStream contains the bytes that are given by the byte range.
	 *
	 * This method is for internal use only.
	 *
	 * Use your favorite cryptographic library to implement PKCS #7 signature creation.
	 *
	 * @throws IOException
	 */
	@Override
	public byte[] sign(InputStream content) throws IOException {
		try {
			List<Certificate> certList = new ArrayList<>(Arrays.asList(certificateChain));
			JcaCertStore certs = new JcaCertStore(certList);
			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
			org.bouncycastle.asn1.x509.Certificate cert = org.bouncycastle.asn1.x509.Certificate.getInstance(certificateChain[0].getEncoded());
			ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
			gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, new X509CertificateHolder(cert)));
			gen.addCertificates(certs);
			CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
			CMSSignedData signedData = gen.generate(msg, false);

			return signedData.getEncoded();
		}
		catch (GeneralSecurityException | CMSException | OperatorCreationException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Set if external signing scenario should be used.
	 * If {@code false}, SignatureInterface would be used for signing.
	 * <p>
	 *     Default: {@code false}
	 * </p>
	 * @param externalSigning {@code true} if external signing should be performed
	 */
	public void setExternalSigning(boolean externalSigning) {
		this.externalSigning = externalSigning;
	}

	public boolean isExternalSigning() {
		return externalSigning;
	}

	/**
	 * Get the access permissions granted for this document in the DocMDP transform parameters
	 * dictionary. Details are described in the table "Entries in the DocMDP transform parameters
	 * dictionary" in the PDF specification.
	 *
	 * @param doc document.
	 * @return the permission value. 0 means no DocMDP transform parameters dictionary exists. Other
	 * return values are 1, 2 or 3. 2 is also returned if the DocMDP transform parameters dictionary
	 * is found but did not contain a /P entry, or if the value is outside the valid range.
	 */
	public int getMDPPermission(PDDocument doc) {
		COSBase base = doc.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.PERMS);
		if (base instanceof COSDictionary) {
			COSDictionary permsDict = (COSDictionary) base;
			base = permsDict.getDictionaryObject(COSName.DOCMDP);
			if (base instanceof COSDictionary) {
				COSDictionary signatureDict = (COSDictionary) base;
				base = signatureDict.getDictionaryObject("Reference");
				if (base instanceof COSArray) {
					COSArray refArray = (COSArray) base;
					for (int i = 0; i < refArray.size(); ++i) {
						base = refArray.getObject(i);
						if (base instanceof COSDictionary) {
							COSDictionary sigRefDict = (COSDictionary) base;
							if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject("TransformMethod"))) {
								base = sigRefDict.getDictionaryObject("TransformParams");
								if (base instanceof COSDictionary) {
									COSDictionary transformDict = (COSDictionary) base;
									int accessPermissions = transformDict.getInt(COSName.P, 2);
									if (accessPermissions < 1 || accessPermissions > 3) {
										accessPermissions = 2;
									}
									return accessPermissions;
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}

	public void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions) {
		COSDictionary sigDict = signature.getCOSObject();

		// DocMDP specific stuff
		COSDictionary transformParameters = new COSDictionary();
		transformParameters.setItem(COSName.TYPE, COSName.getPDFName("TransformParams"));
		transformParameters.setInt(COSName.P, accessPermissions);
		transformParameters.setName(COSName.V, "1.2");
		transformParameters.setNeedToBeUpdated(true);

		COSDictionary referenceDict = new COSDictionary();
		referenceDict.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));
		referenceDict.setItem("TransformMethod", COSName.getPDFName("DocMDP"));
		referenceDict.setItem("DigestMethod", COSName.getPDFName("SHA1"));
		referenceDict.setItem("TransformParams", transformParameters);
		referenceDict.setNeedToBeUpdated(true);

		COSArray referenceArray = new COSArray();
		referenceArray.add(referenceDict);
		sigDict.setItem("Reference", referenceArray);
		referenceArray.setNeedToBeUpdated(true);

		// Catalog
		COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
		COSDictionary permsDict = new COSDictionary();
		catalogDict.setItem(COSName.PERMS, permsDict);
		permsDict.setItem(COSName.DOCMDP, signature);
		catalogDict.setNeedToBeUpdated(true);
		permsDict.setNeedToBeUpdated(true);
	}

	static class CMSProcessableInputStream implements CMSTypedData {
		private InputStream in;

		private final ASN1ObjectIdentifier contentType;

		CMSProcessableInputStream(InputStream is) {
			this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), is);
		}

		CMSProcessableInputStream(ASN1ObjectIdentifier type, InputStream is) {
			contentType = type;
			in = is;
		}

		@Override
		public Object getContent() {
			return in;
		}

		@Override
		public void write(OutputStream out) throws IOException {
			// read the content only one time
			IOUtils.copy(in, out);
			in.close();
		}

		@Override
		public ASN1ObjectIdentifier getContentType() {
			return contentType;
		}
	}
}
