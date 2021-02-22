package com.jeestudio.common.entity.cms;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Prt Site
 * @author: houxl
 * @Date: 2020-07-02
 */
public class PrtSite extends DataEntity<PrtSite> {

    private static final long serialVersionUID = 1L;
    private String ownerCode;
    private String code;
    private String name;
    private String keyword;
    private String httpPath;
    private String resource;
    private Integer sort;
    private String status;

    public PrtSite() {
        super();
    }

    public PrtSite(String id){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
