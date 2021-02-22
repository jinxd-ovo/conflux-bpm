package com.jeestudio.common.entity.act;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.common.entity.system.User;

import java.util.Date;

/**
 * @Description: Assignee setting
 * @author: houxl
 * @Date: 2020-01-09
 */
public class AssigneeSetting extends DataEntity<AssigneeSetting> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private Date beginTime;
    private Date endTime;
    private User assignee;
    private String processScope;
    private Date beginEndTime;
    private Date endEndTime;

    public AssigneeSetting() {
        super();
    }

    public AssigneeSetting(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public String getProcessScope() {
        return processScope;
    }

    public void setProcessScope(String processScope) {
        this.processScope = processScope;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getBeginEndTime() {
        return beginEndTime;
    }

    public void setBeginEndTime(Date beginEndTime) {
        this.beginEndTime = beginEndTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndEndTime() {
        return endEndTime;
    }

    public void setEndEndTime(Date endEndTime) {
        this.endEndTime = endEndTime;
    }
}
