package com.junliang.spring.restcontrol;

import com.jcraft.jsch.Logger;
import com.junliang.spring.pojo.bean.Email;
import com.junliang.spring.pojo.vo.BaseResponse;
import com.junliang.spring.pojo.vo.ObjectRestResponse;
import com.junliang.spring.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequestMapping("hello")
@RestController
public class TestController {


    @GetMapping("/world")
    public ObjectRestResponse index() {

        return new ObjectRestResponse("hellowzho直接后果orld");
    }

    @Autowired
    JavaMailSender mailSender;

    @RequestMapping("sendemail")
    public BaseResponse sendEmail() {
        try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("li926893@gmail.com");
            message.setTo("tyf201314@qq.com");
            message.setSubject("测试邮件主题");
            message.setText("测试邮件内容");
            this.mailSender.send(mimeMessage);

            return new ObjectRestResponse("hellowzho直接后果orld");
        } catch (Exception ex) {

            log.error("发送失败",ex);
            return new ObjectRestResponse("发送失败");
        }
    }

    @Resource
    private EmailService emailService;

    @GetMapping("sendEmailTemplate")
    public BaseResponse sendEmailTemplate() {
        try {
            Email email =new Email();
            Map<String,String> map=new HashMap<>(2);
            map.put("text","welcome");
            email.setKvMap(map);
            email.setTemplate("welcome");
            email.setEmail(new String[]{"tyf201314@qq.com"});
            email.setSubject("默认主题");
            email.setCc(new String[]{"li926893@gmail.com"});
            emailService.sendBytemplate(email);
            return new ObjectRestResponse("hellowzho直接后果orld");
        } catch (Exception ex) {
            log.error("发送失败",ex);
            return new BaseResponse(4000,"发送失败");
        }
    }

}
