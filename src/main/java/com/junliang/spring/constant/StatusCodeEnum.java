package com.junliang.spring.constant;


import lombok.Getter;
import lombok.Setter;

public enum StatusCodeEnum {

    EX_OTHER_CODE(5000,""),
    EX_PRAM_ERROR(1002,"请求参数不正确");

    @Getter
    private int code;
    @Getter
    private String message;

    StatusCodeEnum(int c, String s) {
        this.code=c;
        this.message=s;
    }
}
