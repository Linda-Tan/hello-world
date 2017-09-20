package com.junliang.helloworld.restcontrol;

import com.junliang.helloworld.pojo.vo.BaseResponse;
import com.junliang.helloworld.pojo.vo.ObjectRestResponse;
import com.junliang.helloworld.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Toni_ on 2017/9/20.
 */
@RequestMapping("/auth")
@RestController
public class AuthController {


    @Resource
    private UserService userService;

    @PostMapping("/token")
    public BaseResponse authorzie(String username, String password) {
        //校验用户合法信息
        String token = userService.login(username, password);

        return new ObjectRestResponse<>(token);
    }


}
