package com.junliang.spring;

import com.alibaba.fastjson.JSONObject;
import com.junliang.spring.util.Base64;
import com.junliang.spring.util.IOHelper;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
public class voidTest {

    @Autowired
    StringEncryptor stringEncryptor;

    @Test
    public void encryptPwd() {
        String result = stringEncryptor.encrypt("root");
        System.out.println(result);
    }



    @Test
    public void jsonCan() throws UnsupportedEncodingException {

        byte[] aaas = Base64.encode("aaa");

        System.out.println(new String(aaas));
        byte[] decode = java.util.Base64.getDecoder().decode(new String(aaas));

        System.out.println(new String(decode));

    }


    @Test
    public void getBingPicture() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = restTemplate.getForObject("https://cn.bing.com/HPImageArchive.aspx?format=js&n=1", JSONObject.class);
        String sourcePath =jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        String url = "https://cn.bing.com" + sourcePath;
        //String usrHome = System.getProperty("user.home");
        String savePath = System.getProperty("user.home") + File.separator+ "Pictures" +File.separator + "bing";
        String filename =sourcePath.substring(sourcePath.lastIndexOf("/"));

        System.out.println(savePath);

        //IOHelper.downLoadFromUrl(url, filename, savePath);
        IOHelper.downLoadFromUrl(url,filename,savePath.replace("C:","D:"));
    }


}
