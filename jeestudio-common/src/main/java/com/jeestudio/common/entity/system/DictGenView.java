package com.jeestudio.common.entity.system;

import java.io.Serializable;

/**
 * @Description: Dict gen view
 * @author: whl
 * @Date: 2020-02-10
 */
public class DictGenView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
