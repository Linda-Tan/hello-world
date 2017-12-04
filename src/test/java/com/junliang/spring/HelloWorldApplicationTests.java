package com.junliang.spring;

import com.junliang.spring.dao.mapper.UserMapper;
import com.junliang.spring.dao.repository.UserRepository;
import com.junliang.spring.pojo.entity.User;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloWorldApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
    }

    @Autowired(required=true)
     private StringEncryptor stringEncryptor;//密码解码器自动注入

    @Test
    public void stringEncryptortest() {
        System.out.println(stringEncryptor.encrypt("root"));
    }
    @Test
    public void test() throws Exception {
        User user = new User();
        user.setName("admin");
        user.setPassword("password");
        userRepository.save(user);
        //userRepository.save(new User());
        //userRepository.save(new User());
        //userMapper.insert(new com.junliang.spring.pojo.domain.User());
        //userMapper.insert(new com.junliang.spring.pojo.domain.User());

    }


    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void sendSimpleEmail() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("li926893@163.com");//发送者.
        message.setTo("318615621@qq.com");//接收者.
        message.setSubject("测试邮件（邮件主题）");//邮件主题.
        message.setText("这是邮件内容");//邮件内容.

        mailSender.send(message);//发送邮件
    }

    public static void main(String[] args) {
        Integer a=127,b=127;
        System.out.println(a==b);
        Integer c=124,d=124;
        System.out.println(c==d);

        int s=128,f=128;
        System.out.println(s==f);


	}
	@Test
	public void simpleClientHttpRequestFactory() {
		//SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		//requestFactory.setConnectTimeout(10000);
		//requestFactory.setReadTimeout(10000);
		RestTemplate restTemplate = new RestTemplate();
		Map map=restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=ACCESS_TOKEN",Map.class);
		System.out.println(map);
		System.out.println(map.get("errcode"));
		System.out.println(map.get("errmsg"));
	}


}
