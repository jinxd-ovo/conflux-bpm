package com.jeestudio.common.entity.gen;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: Gen table children
 * @author: whl
 * @Date: 2020-02-07
 */
public class GenTableChildren implements Serializable {

    private String id;
    private String key;
    private List<GenTableColumnView> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public List<GenTableColumnView> getData() {
        return data;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setData(List<GenTableColumnView> data) {
        this.data = data;
    }
}
