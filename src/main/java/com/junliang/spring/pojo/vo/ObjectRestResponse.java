package com.junliang.spring.pojo.vo;

import com.junliang.spring.constant.StatusCodeEnum;
import lombok.Data;

import java.util.List;

@Data
public class ObjectRestResponse<T> extends BaseResponse {
         T result;



    public ObjectRestResponse() {

    }
    public ObjectRestResponse(T result) {
       this.result= result;
    }

    public ObjectRestResponse(StatusCodeEnum exPramError, T result) {
        this.status=exPramError.getCode();
        this.message=exPramError.getMessage();
        this.result=result;

    }
}
