package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Post
 * @author: whl
 * @Date: 2019-11-29
 */
public class Post extends DataEntity<Post> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private String typeCode;
    private Integer sort;
    private String useable;

    private String officeId;

    public Post() {
        super();
    }

    public Post(String id){
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getUseable() {
        return useable;
    }

    public void setUseable(String useable) {
        this.useable = useable;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
