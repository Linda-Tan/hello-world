package com.junliang.spring.pojo.vo;

import lombok.Data;

@Data
public class ObjectRestResponse<T> extends BaseResponse {
         T result;



    public ObjectRestResponse() {

    }
    public ObjectRestResponse(T result) {
       this.result= result;
    }

}
