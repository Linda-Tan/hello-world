package com.junliang.boot.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.junliang.boot.utils.PdfBoxSignature;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
public class HelloWorldController {

	@RequestMapping("/")
	String home() {
		log.trace("qweqw");
		log.info("qweqw");
		log.debug("qweqw");
		log.warn("qweqw");
		log.error("qweqw");

		return "Hello World!";
	}

	@Resource
	private PdfBoxSignature pdfBoxSignature;

	@PostMapping("/test")
	public String upload(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "visible", required = false) MultipartFile visible,
			HttpServletResponse response) throws IOException {
		String fileName = file.getOriginalFilename();
		if (null != fileName && !file.isEmpty() && fileName.endsWith(".pdf")) {
			OutputStream os = response.getOutputStream();
			try (PDDocument doc = PDDocument.load(file.getInputStream())) {
				if (null != visible && !visible.isEmpty()) {
					pdfBoxSignature.signPDF(doc, os, visible.getInputStream());
				}
				else {
					pdfBoxSignature.signPDF(doc, os);
				}
			}
		}
		return fileName;
	}

}
