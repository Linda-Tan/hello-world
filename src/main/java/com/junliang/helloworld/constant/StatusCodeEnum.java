package com.junliang.helloworld.constant;


import lombok.Getter;
import lombok.Setter;

public enum StatusCodeEnum {

    @Getter@Setter
    EX_OTHER_CODE(5000,"");

    @Getter@Setter
    private int code;
    @Getter@Setter
    private String message;

    StatusCodeEnum(int c, String s) {
        this.code=c;
        this.message=s;
    }
}
