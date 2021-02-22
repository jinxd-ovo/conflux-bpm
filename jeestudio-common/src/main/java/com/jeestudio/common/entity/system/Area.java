package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.TreeEntity;

/**
 * @Description: Area
 * @author: whl
 * @Date: 2019-11-29
 */
public class Area extends TreeEntity<Area> {

    private static final long serialVersionUID = 1L;

    private String code;
    private String types;

    public Area() {
        super();
    }

    public Area(String id){
        super(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public  Area getParent() {
        return parent;
    }

    @Override
    public void setParent(Area parent) {
        this.parent = parent;
    }

    public String getParentId() {
        return parent != null && parent.getId() != null ? parent.getId() : "0";
    }
}
