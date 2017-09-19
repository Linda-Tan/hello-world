package com.junliang.helloworld.pojo.domain;

import java.io.Serializable;
import javax.persistence.*;

public class User implements Serializable {
    @Id
    @GeneratedValue(generator="UUID")
    private String id;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
}