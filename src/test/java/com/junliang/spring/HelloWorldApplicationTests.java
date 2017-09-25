package com.junliang.spring;

import com.junliang.spring.dao.mapper.UserMapper;
import com.junliang.spring.dao.repository.UserRepository;
import com.junliang.spring.pojo.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class HelloWorldApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserMapper userMapper;
	@Test
	public void contextLoads() {
	}

	@Test
	public void test() throws Exception {
		 User user= new User();
		 user.setName("admin");
		 user.setPassword("password");
		userRepository.save(user);
		//userRepository.save(new User());
		//userRepository.save(new User());
		//userMapper.insert(new com.junliang.spring.pojo.domain.User());
		//userMapper.insert(new com.junliang.spring.pojo.domain.User());

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
