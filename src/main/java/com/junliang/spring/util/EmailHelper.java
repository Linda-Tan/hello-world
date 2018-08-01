package com.junliang.spring.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步发送邮件
 */

@Log4j2
public class EmailHelper {

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(6);

    private static final AtomicInteger count = new AtomicInteger(1);

    public static void start(final JavaMailSender mailSender,final SimpleMailMessage message) {
        service.execute(() -> {
            try {
                if (count.get() == 2) {
                    service.shutdown();
                    log.info("the task is down");
                }
                log.info("start send email and the index is " + count);
                mailSender.send(message);
                log.info("send email success");
            }catch (Exception e){
                log.error("send email fail" , e);
            }

        });
    }
    public static void startHtml(final JavaMailSender mailSender, final MimeMessage message) {
        service.execute(() -> {
            try {
                if (count.get() == 2) {
                    service.shutdown();
                    log.info("the task is down");
                }
                log.info("start send email and the index is " + count);
                mailSender.send(message);
                log.info("send email success");
            }catch (Exception e){
                log.error("send email fail" , e);
            }

        });
    }



}
