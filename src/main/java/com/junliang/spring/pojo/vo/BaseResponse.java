package com.junliang.spring.pojo.vo;


import lombok.Data;

@Data
public class BaseResponse {

    Integer status;
    String message;



    public BaseResponse() {
        this.status=200;
        this.message="success";

    }

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
