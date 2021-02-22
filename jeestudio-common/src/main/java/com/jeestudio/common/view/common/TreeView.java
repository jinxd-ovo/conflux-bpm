package com.jeestudio.common.view.common;

/**
 * @Description: Tree view
 * @author: houxl
 * @Date: 2020-02-20
 */
public class TreeView {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String parentId;
    private int sort;
    private boolean hasChildren;
    private String name_EN;

    public String getName_EN() {
        return name_EN;
    }

    public void setName_EN(String name_EN) {
        this.name_EN = name_EN;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
