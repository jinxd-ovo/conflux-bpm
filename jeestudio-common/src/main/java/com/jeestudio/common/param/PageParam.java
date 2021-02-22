package com.jeestudio.common.param;

import java.util.Map;

/**
 * @Description: Page param entity
 * @author: houxl
 * @Date: 2020-01-21
 */
public class PageParam {

    private static final long serialVersionUID = 1L;

    private int pageNo = 1;
    private int pageSize = 10;
    private String orderBy = "";
    private Map<String, String> paramMap;

    public PageParam() {
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
}
