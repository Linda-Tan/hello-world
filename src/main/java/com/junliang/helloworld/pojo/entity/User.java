package com.junliang.helloworld.pojo.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="user")
public class User {

    @Id
    @GenericGenerator(name="uuid", strategy="uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator="uuid")
    private String id;

    private String name;

    private String password;

}
