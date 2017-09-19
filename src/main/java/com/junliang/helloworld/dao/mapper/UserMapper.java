package com.junliang.helloworld.dao.mapper;

import com.junliang.helloworld.pojo.domain.User;
import org.mybatis.spring.annotation.MapperScan;
import tk.mybatis.mapper.common.Mapper;


//@Mapper
public interface UserMapper extends Mapper<User> {
}