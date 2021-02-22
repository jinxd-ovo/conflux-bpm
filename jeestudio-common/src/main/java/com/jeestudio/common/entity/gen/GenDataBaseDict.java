package com.jeestudio.common.entity.gen;

import java.io.Serializable;

/**
 * @Description: Gen database dict
 * @author: whl
 * @Date: 2020-02-12
 */
public class GenDataBaseDict implements Serializable {

    private String name;
    private String comments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
