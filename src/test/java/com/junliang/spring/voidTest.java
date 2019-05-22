package com.junliang.spring;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.junliang.spring.util.Base64;
import com.junliang.spring.util.IOHelper;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

@Log4j2
public class voidTest {

    //@Autowired
    //StringEncryptor stringEncryptor;
    //
    //@Test
    //public void encryptPwd() {
    //    String result = stringEncryptor.encrypt("root");
    //    System.out.println(result);
    //}



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
        restTemplate.getMessageConverters().add(new FastJsonHttpMessageConverter());
        JSONObject jsonObject = restTemplate.getForObject("https://cn.bing.com/HPImageArchive.aspx?format=js&n=1", JSONObject.class);
        String sourcePath =jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        String url = "https://cn.bing.com" + sourcePath;
        //String usrHome = System.getProperty("user.home");
        String savePath = System.getProperty("user.home") + File.separator+ "Pictures" +File.separator + "bing";
        String filename = LocalDate.now() +sourcePath.substring(sourcePath.lastIndexOf("."),sourcePath.lastIndexOf("&"));

        log.info(savePath);

        //IOHelper.downloadFile(url,savePath);
        IOHelper.downLoadFromUrl(url,filename,savePath.replace("C:","D:"));
    }


    @Test
    public void test() {

        String str = "/th?id=OHR.xiaoicepainting_ZH-CN8581660984_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp";

        System.out.println(str.substring(str.lastIndexOf("."),str.lastIndexOf("&")));

    }


    @Test
    public void testLog() {

        log.debug("Debugging log");
        log.info("Info log");
        log.warn("Hey, This is a warning!");
        log.error("Oops! We have an Error. OK");
        log.fatal("Damn! Fatal error. Please fix me.");

    }

}
