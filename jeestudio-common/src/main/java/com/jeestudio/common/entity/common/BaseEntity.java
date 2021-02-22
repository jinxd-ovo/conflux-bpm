package com.jeestudio.common.entity.common;

import com.google.common.collect.Maps;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.utils.StringUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description: Base entity
 * @author: whl
 * @Date: 2019-11-28
 */
public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Delete tag (0: normal; 1: delete; 2: audit;)
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String DEL_FLAG_AUDIT = "2";

    protected String preId;
    protected String id;
    protected User currentUser;
    protected Page<T> page;

    /**
     * Custom SQL
     */
    protected Map<String, String> sqlMap;

    /**
     * Is it a new record
     */
    protected boolean isNewRecord = true;

    public BaseEntity() {

    }

    public BaseEntity(String id) {
        this();
        this.id = id;
    }

    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCurrentUser() {
        if(currentUser == null){
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Page<T> getPage() {
        if (page == null){
            page = new Page<T>();
        }
        return page;
    }

    public Page<T> setPage(Page<T> page) {
        this.page = page;
        return page;
    }

    public Map<String, String> getSqlMap() {
        if (sqlMap == null){
            sqlMap = Maps.newHashMap();
        }
        return sqlMap;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    /**
     * Execute method before inserting, subclass implementation.
     */
    public abstract void preInsert();

    /**
     * Execute method before update, subclass implementation.
     */
    public abstract void preUpdate();

    /**
     * Is it a new record
     */
    public boolean getIsNewRecord() {
        return this.isNewRecord ? StringUtil.isBlank(getId()) : false;
    }

    /**
     * Is it a new record
     */
    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity<?> that = (BaseEntity<?>) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
