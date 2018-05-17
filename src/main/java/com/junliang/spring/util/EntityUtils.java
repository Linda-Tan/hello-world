package com.junliang.spring.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * * 实体类相关工具类
 * 解决问题： 1、快速对实体的常驻字段，如：createBy、updateBy等值快速注入
 * @author Tony
 * @date 2018/04/10
 */
public class EntityUtils {

    // 默认属性
    public final static String[] cfields = {"createBy","createDate"};

    // 默认属性
    public final static String[] ufields = {"updateBy","updateDate"};


    public static <T> void setEntityInfo(T entity,String[] fields){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String name = "";
        if(request!=null) {
            name = String.valueOf(request.getHeader("userName"));
            //name = URLDecoder.decode(name);
        }

        Field field = ReflectionUtils.getAccessibleField(entity, "id");
        // 默认值
        Object [] value = null;
        if(field!=null&&field.getType().equals(Long.class)){
            value = new Object []{name,new Date()};
        }
        // 填充默认属性值
        setDefaultValues(entity, fields, value);
    }



    private static <T> void setDefaultValues(T entity, String[] fields, Object[] value) {
        for(int i=0;i<fields.length;i++){
            String field = fields[i];
            if(ReflectionUtils.hasField(entity, field)){
                ReflectionUtils.invokeSetter(entity, field, value[i]);
            }
        }
    }

    public static <T> boolean isPKNotNull(T entity,String field){
        if(!ReflectionUtils.hasField(entity, field)) {
            return false;
        }
        Object value = ReflectionUtils.getFieldValue(entity, field);
        return value!=null&&!"".equals(value);
    }
}
