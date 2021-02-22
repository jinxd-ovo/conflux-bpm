package com.jeestudio.common.entity.act;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Task setting
 * @author: houxl
 * @Date: 2020-01-11
 */
public class TaskSetting extends DataEntity<TaskSetting> {

    private static final long serialVersionUID = 1L;

    private String userTaskId;
    private String userTaskName;
    private String procDefKey;
    private String permissionType;
    private String settingValue;
    private String ruleArgs;

    private TaskPermission taskPermission;

    public TaskSetting() {
        super();
    }

    public TaskSetting(String id){
        super(id);
    }

    public String getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(String userTaskId) {
        this.userTaskId = userTaskId;
    }

    public String getUserTaskName() {
        return userTaskName;
    }

    public void setUserTaskName(String userTaskName) {
        this.userTaskName = userTaskName;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getRuleArgs() {
        return ruleArgs;
    }

    public void setRuleArgs(String ruleArgs) {
        this.ruleArgs = ruleArgs;
    }

    public TaskPermission getTaskPermission() {
        return taskPermission;
    }

    public void setTaskPermission(TaskPermission taskPermission) {
        this.taskPermission = taskPermission;
    }
}
