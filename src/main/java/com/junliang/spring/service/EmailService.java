package com.junliang.spring.service;

import com.junliang.spring.pojo.bean.Email;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.File;

/**
 *
 * 支持自定义发件人昵称
 * 支持扩展邮件Message
 * 支持抄送／HTML／附件
 * 支持异步发送
 * 支持邮件模板
 *
 */
@Slf4j
@Service
public class EmailService {



    @Resource
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;//thymeleaf

   // private MimeMessage message = mailSender.createMimeMessage();

    @Value("${spring.mail.username}")
    public String fromUser;//发送者


    /**
     * 发送邮件
     * @param email
     */
    public void send(@Valid Email email) {
        log.info("发送邮件：{}", email.getContent());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromUser);
        message.setTo(email.getEmail());
        message.setSubject(email.getSubject());
        message.setText(email.getContent());
        mailSender.send(message);
    }

    /**
     * 发送邮件
     * @param simpleMailMessage
     */
    public void send(SimpleMailMessage simpleMailMessage) {
        Assert.notNull(simpleMailMessage, "simpleMailMessage must not be null");
        log.info("发送邮件：{}", simpleMailMessage.getText());
        simpleMailMessage.setFrom(fromUser);
        mailSender.send(simpleMailMessage);
    }

    /**
     * 发送邮件
     * @param mail
     * @throws Exception
     */
    public void sendBytemplate(Email mail) throws Exception {
        Assert.notNull(mail, "email must not be null");
        log.info("发送邮件：{}", mail.getContent());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromUser);
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        if (StringUtils.isNotBlank(mail.getContent()))
        helper.setText(mail.getContent());

        //如果有抄送、密送
        if (null != mail.getBcc() && mail.getBcc().length > 0)
            helper.setBcc(mail.getBcc());
        if (null != mail.getCc() && mail.getCc().length > 0)
            helper.setCc(mail.getCc());

        //如果有指定模板
        if (StringUtils.isNotBlank(mail.getTemplate())) {
            Context context = new Context();
            context.setVariable("email", mail.getKvMap());
            String text = templateEngine.process(mail.getTemplate(), context);
            helper.setText(text, true);
        }
        //如果有附件
        if (null != mail.getAddAttachments() && mail.getAddAttachments().length > 0) {
            for (File file : mail.getAddAttachments()) {
                helper.addAttachment(file.getName(), file);
            }
        }
        mailSender.send(message);
    }


}
