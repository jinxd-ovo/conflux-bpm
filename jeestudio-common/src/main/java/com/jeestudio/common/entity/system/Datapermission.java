package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Datapermission
 * @author: David
 * @Date: 2020-08-27
 */
public class Datapermission extends DataEntity<Datapermission> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String mainTable;
    private String expression;
    private Integer sort;

    public Datapermission() {
        super();
    }

    public Datapermission(String id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
