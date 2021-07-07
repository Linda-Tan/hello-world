package com.junliang.boot;

import java.io.InputStream;
import java.security.KeyStore;

import com.junliang.boot.utils.PdfBoxSignature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }


  @Bean
  public PdfBoxSignature pdfBoxSignature() throws Exception {
    //证书
    String pkcs12 = "/key/pkcs12.p12";
    String pws = "hly123";
    KeyStore keystore = KeyStore.getInstance("PKCS12");
    //密码
    char[] pin = pws.toCharArray();
		try (InputStream input = getClass().getResourceAsStream(pkcs12)) {
			keystore.load(input, pin);
		}
    	return new PdfBoxSignature(keystore,pin);
	}
}
