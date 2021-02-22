package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

import javax.validation.constraints.NotNull;

/**
 * @Description: Level
 * @author: whl
 * @Date: 2019-11-29
 */
public class Level extends DataEntity<Level> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private Integer sort;
    private String useable;

    public Level() {
        super();
    }

    public Level(String id){
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

    @NotNull(message="Sort can not be blank")
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
}
