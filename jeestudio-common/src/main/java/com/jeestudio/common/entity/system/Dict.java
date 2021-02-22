package com.jeestudio.common.entity.system;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jeestudio.common.entity.common.TreeEntity;

/**
 * @Description: Dict
 * @author: whl
 * @Date: 2019-12-04
 */
public class Dict extends TreeEntity<Dict> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String code;
    private String parentCode;
    private String editFlag;
    private String code1;
    private String code2;

    public Dict() {
        super();
    }

    public Dict(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    @JsonBackReference
    public Dict getParent() {
        return parent;
    }

    @Override
    public void setParent(Dict parent) {
        this.parent = parent;
    }

    public String getParentId() {
        return parent != null && parent.getId() != null && false == "".equals(parent.getId()) ? parent.getId() : "0";
    }
}