package com.junliang.spring.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class TableResultResponse<T> extends BaseResponse {

    int total;
    int page;
    int size;
    List<T> rows;

    public TableResultResponse(int total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public TableResultResponse(Long total, List<T> rows) {
        this.total = total.intValue();
        this.rows = rows;
    }

}
