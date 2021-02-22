package com.jeestudio.common.entity.act;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.common.ActEntity;

import java.util.Date;

/**
 * @Description: Urge process
 * @author: houxl
 * @Date: 2020-01-11
 */
public class UrgeProcess extends ActEntity<UrgeProcess> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String procCategory;
    private String procCreateUser;
    private String procCreateTime;
    private String duringTime;
    private Date startTime;
    private String cnode;
    private String cuser;
    private String cuserId;
    private String taskId;
    private String lastUrgeUser;
    private String lastUrgeUserId;
    private Date lastUrgeDate;
    private Integer urgeCount;
    private String requestMapping;
    private String entityId;

    public UrgeProcess() {
        super();
    }

    public UrgeProcess(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getProcCategory() {
        return procCategory;
    }

    public void setProcCategory(String procCategory) {
        this.procCategory = procCategory;
    }

    public String getProcCreateUser() {
        return procCreateUser;
    }

    public void setProcCreateUser(String procCreateUser) {
        this.procCreateUser = procCreateUser;
    }

    public String getProcCreateTime() {
        return procCreateTime;
    }

    public void setProcCreateTime(String procCreateTime) {
        this.procCreateTime = procCreateTime;
    }

    public String getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(String duringTime) {
        this.duringTime = duringTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getCnode() {
        return cnode;
    }

    public void setCnode(String cnode) {
        this.cnode = cnode;
    }

    public String getCuser() {
        return cuser;
    }

    public void setCuser(String cuser) {
        this.cuser = cuser;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getLastUrgeUser() {
        return lastUrgeUser;
    }

    public void setLastUrgeUser(String lastUrgeUser) {
        this.lastUrgeUser = lastUrgeUser;
    }

    public String getLastUrgeUserId() {
        return lastUrgeUserId;
    }

    public void setLastUrgeUserId(String lastUrgeUserId) {
        this.lastUrgeUserId = lastUrgeUserId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getLastUrgeDate() {
        return lastUrgeDate;
    }

    public void setLastUrgeDate(Date lastUrgeDate) {
        this.lastUrgeDate = lastUrgeDate;
    }

    public Integer getUrgeCount() {
        return urgeCount;
    }

    public void setUrgeCount(Integer urgeCount) {
        this.urgeCount = urgeCount;
    }

    public String getRequestMapping() {
        return requestMapping;
    }

    public void setRequestMapping(String requestMapping) {
        this.requestMapping = requestMapping;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
