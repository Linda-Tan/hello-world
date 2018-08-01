package com.junliang.spring.exception;

import com.junliang.spring.constant.ResponseCode;
import com.junliang.spring.constant.StatusCodeEnum;
import com.junliang.spring.pojo.vo.BaseResponse;
import com.junliang.spring.pojo.vo.ObjectRestResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.executable.ValidateOnExecution;
import java.util.*;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public BaseResponse baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        log.error(ex.getMessage(), ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse otherExceptionHandler(HttpServletResponse response, Exception ex) {
        log.error(ex.getMessage(), ex);
        return new BaseResponse(ResponseCode.EX_OTHER_CODE, ex.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResponse MethodArgumentNotValidHandler(MethodArgumentNotValidException exception){
        List invalidArguments = new ArrayList<>();
        //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            Set invalidArgument =new HashSet(3);
            invalidArgument.add(error.getDefaultMessage());
            invalidArgument.add(error.getField());
            invalidArgument.add(error.getRejectedValue());
            invalidArguments.add(invalidArgument);
        }
        return new ObjectRestResponse(StatusCodeEnum.EX_PRAM_ERROR, invalidArguments);
    }
}
