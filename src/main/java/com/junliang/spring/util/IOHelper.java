package com.junliang.spring.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class IOHelper {

    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为5秒
        conn.setConnectTimeout(5000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = input2byte(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()&& !saveDir .isDirectory()) {
            saveDir.mkdir();
        }

        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        log.info("info:" + url + " download success");

    }

    public static String greaterNewFileName(String originalName) {
        int pos = originalName.lastIndexOf(".");
        // 获得上传的文件后缀
        String suffix = originalName.substring(pos);
        String name = originalName.substring(0, pos);
        // 生成新的文件名
        String newFileName = name + "~" + UUID.randomUUID().toString() + suffix;
        return newFileName;
    }

    public static String readFile(String filepath) throws IOException {
        File file =new File(filepath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        StringBuilder content = new StringBuilder();
        try {
            int len;
            while ((len = isr.read()) != -1) {
                content.append((char) len);
            }
        }finally{
            isr.close();
        }
        return content.toString();
    }
    //从指定目录下查找指定文件
    public static void findFile(File root,String fileName,ArrayList<File> rs){
        if(!root.isDirectory()){
            if(fileName.equals(root.getName())){
                rs.add(root);
            }
        }else{
            for (File f : root.listFiles()) {
                findFile(f, fileName,rs);
            }
        }
    }

    public static String getClassPath(String file){
        URL classPath = Thread.currentThread().getContextClassLoader().getResource("");
        String proFilePath = classPath.toString();
        proFilePath = proFilePath.substring(6);
        proFilePath= proFilePath+file;
        proFilePath = proFilePath.replace("/", File.separator);
        return proFilePath;
    }
}
