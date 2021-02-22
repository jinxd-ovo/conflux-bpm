package com.jeestudio.common.entity.act;

import com.jeestudio.common.entity.common.ActEntity;

import java.util.Map;

/**
 * @Description: Process tree
 * @author: houxl
 * @Date: 2020-01-11
 */
public class ProcessTree extends ActEntity<ProcessTree> {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> map;

    public ProcessTree() {}
    public ProcessTree(Map<String, Object> map) {
        super();
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
