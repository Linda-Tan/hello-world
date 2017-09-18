package com.junliang.helloworld.pojo.vo;

import lombok.Data;

@Data
public class BaseResponse {
    private Integer status;
    private String message;


    public BaseResponse() {
        this.status=200;
        this.message="success";

    }

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
