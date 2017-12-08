package com.junliang.spring.restcontrol;

import com.junliang.spring.pojo.vo.BaseResponse;
import com.junliang.spring.pojo.vo.ObjectRestResponse;
import com.junliang.spring.service.UserService;
import com.junliang.spring.util.IOHelper;
import com.junliang.spring.util.RSAHelper;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Toni_ on 2017/9/20.
 */
@RequestMapping("/auth")
@RestController
public class AuthController {


    @Value("${myProps.jwt.rsa-public-key-file-path}")
    private String pubKeyPath;

    @Resource
    private UserService userService;

    @PostMapping("/token")
    public BaseResponse authorzie(String username,String password) {
        userService.test1();
        userService.test();
        //校验用户合法信息
        String token = userService.login(username, password);

        return new ObjectRestResponse<>(token);
    }

}
