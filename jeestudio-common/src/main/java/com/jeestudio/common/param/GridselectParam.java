package com.jeestudio.common.param;

/**
 * @Description: Gridselect param
 * @author: houxl
 * @Date: 2020-01-21
 */
public class GridselectParam {

    private static final long serialVersionUID = 1L;

    private String tableName;
    private String searchKey;
    private String searchValue;
    private PageParam pageParam;
    private String dsfPlus;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public PageParam getPageParam() {
        return pageParam;
    }

    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }

    public String getDsfPlus() {
        return dsfPlus;
    }

    public void setDsfPlus(String dsfPlus) {
        this.dsfPlus = dsfPlus;
    }
}
