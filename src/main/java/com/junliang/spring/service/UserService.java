package com.junliang.spring.service;

import com.junliang.spring.aop.DataSource;
import com.junliang.spring.dao.mapper.UserMapper;
import com.junliang.spring.dao.repository.UserRepository;
import com.junliang.spring.exception.BaseException;
import com.junliang.spring.pojo.bean.UserInfo;
import com.junliang.spring.pojo.entity.User;
import com.junliang.spring.util.BeanCopierUtils;
import com.junliang.spring.util.RSAHelper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Toni_ on 2017/9/20.
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.rsa-private-key-file-path}")
    private String privateKeyFilePath;

    @DataSource(name = "readDataSource")
    public String login(String username, String password) {

        User user = userRepository.findByName(username);
        if (user == null)
            throw new BaseException(4001, "用户名不存在");

        if (!user.getPassword().equals(password))
            throw new BaseException(4002, "用户密码错误");

        UserInfo userInfo = new UserInfo();
        BeanCopierUtils.copyProperties(user, userInfo);


        //PropertyUtils
        //BeanCopier.create() 难用

        return generateToken(userInfo);
    }

    public void test(){

        com.junliang.spring.pojo.domain.User user =new com.junliang.spring.pojo.domain.User();
        user.setName("admin1");
        user.setPassword("password2");
        userMapper.insert(user);
    }

    @DataSource(name = "readDataSource")
    public void test1(){

        User user = new User();
        user.setName("admin1");
        user.setPassword("password2");
        userRepository.save(user);
    }

    private String generateToken(UserInfo userInfo) {

        String compactJws = null;
        try {
            compactJws = Jwts.builder()
                    .setSubject(userInfo.getName())
                    .setExpiration(Date.from(LocalDateTime.now().plusWeeks(1).atZone(ZoneId.of(ZoneId.SHORT_IDS.get("CTT"))).toInstant()))
                    .claim(userInfo.getId(), userInfo.getName())
                    .signWith(SignatureAlgorithm.RS256, RSAHelper.getPrivateKey(privateKeyFilePath))
                    .compact();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(5001, "生成Token失败。");
        }
        return compactJws;
    }


}
