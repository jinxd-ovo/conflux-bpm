package com.jeestudio.common.entity.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeestudio.utils.Reflections;
import com.jeestudio.utils.StringUtil;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Description: Tree entity
 * @author: whl
 * @Date: 2019-11-29
 */
public abstract class TreeEntity<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;

    protected T parent;
    protected String parentIds;
    protected String name;
    @JsonProperty(value = "name_EN")
    protected String nameEn;
    protected String fullPathName;
    protected String fullPathName_EN;
    protected Integer sort;
    private boolean hasChildren;

    public TreeEntity() {
        super();
        this.sort = 30;
    }

    public TreeEntity(String id) {
        super(id);
    }

    /**
     * The parent object can only be implemented by a subclass.
     * The parent class implementation of mybatis cannot be read.
     */
    @JsonBackReference
    @NotNull
    public abstract T getParent();

    /**
     * The parent object can only be implemented by a subclass.
     * The parent class implementation of mybatis cannot be read.
     */
    public abstract void setParent(T parent);

    @Length(min=1, max=2000)
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getFullPathName() {
        if (false == StringUtil.isEmpty(fullPathName) && fullPathName.indexOf(",") == 0) {
            return fullPathName.substring(1);
        } else {
            return fullPathName;
        }
    }

    public void setFullPathName(String fullPathName) {
        this.fullPathName = fullPathName;
    }

    public String getFullPathName_EN() {
        if (false == StringUtil.isEmpty(fullPathName_EN) && fullPathName_EN.indexOf(",") == 0) {
            return fullPathName_EN.substring(1);
        } else {
            return fullPathName_EN;
        }
    }

    public void setFullPathName_EN(String fullPathName_EN) {
        this.fullPathName_EN = fullPathName_EN;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getParentId() {
        String id = null;
        if (parent != null){
            id = (String) Reflections.getFieldValue(parent, "id");
        }
        return StringUtil.isNotBlank(id) ? id : "0";
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
