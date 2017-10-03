package com.junliang.spring.pojo.bean;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Map;

@Data
public class Email {

    //必填参数
    @NotNull
    private String[] email;//接收方邮件
    @NotNull
    private String subject;//主题
    @NotNull
    private String content;//邮件内容
    //选填
    private String[] cc;//抄送
    private String[] bcc;//密送

    private String template;//模板
    private Map<String, String> kvMap;// 自定义参数

    private File[] addAttachments; //附件

    public Email(){
        super();
    }

    public Email(String[] email, String subject, String content, String template,
                 Map<String, String> kvMap) {
        super();
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.template = template;
        this.kvMap = kvMap;
    }
}
