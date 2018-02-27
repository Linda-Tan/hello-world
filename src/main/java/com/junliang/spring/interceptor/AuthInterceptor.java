package com.junliang.spring.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class AuthInterceptor extends HandlerInterceptorAdapter {

    //TODO 网关鉴定token是否有效。拦截器检验用户或服务是否可以调用当前服务。这里需要搭建鉴权中心；

    @Value("myProps.auth.token-header")
    private String tokenHeader;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return super.preHandle(request, response, handler);
    }
}
