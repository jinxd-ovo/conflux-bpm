package com.jeestudio.common.entity.act;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Task setting version
 * @author: houxl
 * @Date: 2020-01-11
 */
public class TaskSettingVersion extends DataEntity<TaskSettingVersion> {

    private static final long serialVersionUID = 1L;

    private String userTaskId;
    private String userTaskName;
    private String procDefId;
    private String settingValue;
    private String permissionType;
    private String permission;
    private String ruleArgs;

    public TaskSettingVersion() {
        super();
    }

    public TaskSettingVersion(String id){
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

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRuleArgs() {
        return ruleArgs;
    }

    public void setRuleArgs(String ruleArgs) {
        this.ruleArgs = ruleArgs;
    }
}
