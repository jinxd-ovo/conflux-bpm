package com.jeestudio.common.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jeestudio.common.entity.act.Act;
import com.jeestudio.common.entity.act.TaskPermission;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: Entity of activiti
 * @author: houxl
 * @Date: 2020-01-09
 */
public abstract class ActEntity<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String procInsId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String procTaskName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TaskPermission procTaskPermission;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String procDefKey;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String currentAssignee;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String currentStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String currentAssigneeQuery;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customSort;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> procInsIdList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] tempLoginName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tempNodeKey;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customNodeName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tempRuleArgsClass;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Map<String, String>> ruleArgs = Maps.newHashMap();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date createTimeBegin;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date createTimeEnd;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Act act;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String edocType;

    public ActEntity() {
        super();
    }

    public ActEntity(String id) {
        super(id);
    }

    public Act getAct() {
        if (act == null){
            act = new Act();
        }
        return act;
    }

    public void setAct(Act act) {
        this.act = act;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getProcTaskName() {
        return procTaskName;
    }

    public void setProcTaskName(String procTaskName) {
        this.procTaskName = procTaskName;
    }

    public TaskPermission getProcTaskPermission() {
        return procTaskPermission;
    }

    public void setProcTaskPermission(TaskPermission procTaskPermission) {
        this.procTaskPermission = procTaskPermission;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getCurrentAssignee() {
        return currentAssignee;
    }

    public void setCurrentAssignee(String currentAssignee) {
        this.currentAssignee = currentAssignee;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentAssigneeQuery() {
        return currentAssigneeQuery;
    }

    public void setCurrentAssigneeQuery(String currentAssigneeQuery) {
        this.currentAssigneeQuery = currentAssigneeQuery;
    }

    public String getCustomSort() {
        return customSort;
    }

    public void setCustomSort(String customSort) {
        this.customSort = customSort;
    }

    public String[] getTempLoginName() {
        return tempLoginName;
    }

    public void setTempLoginName(String[] tempLoginName) {
        this.tempLoginName = tempLoginName;
    }

    public Map<String, Map<String, String>> getRuleArgs() {
        return ruleArgs;
    }

    public void setRuleArgs(Map<String, Map<String, String>> ruleArgs) {
        this.ruleArgs = ruleArgs;
    }

    public String getRuleArgsJson() {
        if (ruleArgs == null) {
            return new Gson().toJson(Maps.newHashMap());
        } else {
            return new Gson().toJson(ruleArgs);
        }
    }

    public String getTempNodeKey() {
        return tempNodeKey;
    }

    public void setTempNodeKey(String tempNodeKey) {
        this.tempNodeKey = tempNodeKey;
    }

    public String getEdocType() {
        return edocType;
    }

    public void setEdocType(String edocType) {
        this.edocType = edocType;
    }

    public List<String> getProcInsIdList() {
        return procInsIdList;
    }

    public void setProcInsIdList(List<String> procInsIdList) {
        this.procInsIdList = procInsIdList;
    }

    public String getCustomNodeName() {
        return customNodeName;
    }

    public void setCustomNodeName(String customNodeName) {
        this.customNodeName = customNodeName;
    }

    public String getTempRuleArgsClass() {
        return tempRuleArgsClass;
    }

    public void setTempRuleArgsClass(String tempRuleArgsClass) {
        this.tempRuleArgsClass = tempRuleArgsClass;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}
