package com.jeestudio.common.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.utils.IdGen;
import com.jeestudio.utils.StringUtil;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Data entity
 * @author: whl
 * @Date: 2019-11-29
 */
public abstract class DataEntity<T> extends BaseEntity<T> {

    private static final long serialVersionUID = 1L;

    protected String remarks = "";
    protected User createBy;
    protected Date createDate;
    protected User updateBy;
    protected Date updateDate;
    protected String delFlag;
    protected Map<String,String> rules = null;
    protected String viewFlag;
    protected PageParam pageParam;

    public DataEntity() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public DataEntity(String id) {
        super(id);
    }

    /**
     * Execute method before inserting, need to call manually
     */
    @Override
    public void preInsert(){
        // Unlimited ID is UUID, call setisnewrecord() to use custom ID
        if (this.isNewRecord){
            if(StringUtil.isEmpty(getPreId())) {
                setId(IdGen.uuid());
            } else {
                setId(getPreId());
            }
        }
        this.updateDate = new Date();
        this.createDate = this.updateDate;
    }

    /**
     * Execute method before update, need to call manually
     */
    @Override
    public void preUpdate(){
        this.updateDate = new Date();
    }

    @Length(min=0, max=255)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonIgnore
    public User getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @JsonIgnore
    @Length(min=1, max=1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Map<String,String> getRules() {
        if (rules == null) {
            rules = new HashMap<String,String>();
            if (this.getCreateBy() != null) rules.put("createBy", this.getCreateBy().getId());
        }
        return rules;
    }

    public void setRules(Map rules) {
        this.rules = rules;
    }

    public String getViewFlag() {
        return viewFlag;
    }

    public void setViewFlag(String viewFlag) {
        this.viewFlag = viewFlag;
    }

    public PageParam getPageParam() {
        return pageParam;
    }

    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }
}
