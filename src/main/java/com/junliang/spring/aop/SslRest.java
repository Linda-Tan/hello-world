package com.junliang.spring.aop;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SslRest {

    //TODO 2017/9/25

}
