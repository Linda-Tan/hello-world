package com.junliang.spring.controller;


import com.junliang.spring.util.IOHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("game")
public class GameController {

    @GetMapping("login")
    public String longin() {

        return "login";
    }

    public String fileUpload(MultipartFile[] files) {

        if (files == null && files.length == 0) {
            return "";
        }
        // 循环获取file数组中得文件
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            // 保存文件
            if (null != file) {
                // 取得当前上传文件的文件名称
                String myFileName = file.getOriginalFilename();
                String contentType = file.getContentType();
                try {
                    InputStream in = file.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int buf_size = 1024;
                    byte[] buffer = new byte[buf_size];
                    int len = 0;
                    while (-1 != (len = in.read(buffer, 0, buf_size))) {
                        bos.write(buffer, 0, len);
                    }
                    String fileName = IOHelper.greaterNewFileName(myFileName);

                } catch (IOException e) {
                    log.error("Could not upload file " + file.getOriginalFilename(), e);
                    // return new MessageBean(false, "上传失败");
                }

            }
        }


        return "";
    }


}
