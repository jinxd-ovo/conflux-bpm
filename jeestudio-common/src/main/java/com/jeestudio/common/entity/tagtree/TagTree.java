package com.jeestudio.common.entity.tagtree;

import java.io.Serializable;

/**
 * @Description: Tag tree
 * @author: whl
 * @Date: 2020-01-19
 */
public class TagTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String parenId;
    private String name;
    private String loginName;
    private Boolean hasChildren;
    private Boolean disabled = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parenId;
    }

    public void setParentId(String parenId) {
        this.parenId = parenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
