package com.jeestudio.common.entity.act;

import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.common.entity.system.User;

/**
 * @Description: Urge setting
 * @author: houxl
 * @Date: 2020-01-11
 */
public class UrgeSetting extends DataEntity<UrgeSetting> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private User urgeUser;
    private String urgeLimit;
    private String urgeContent;
    private String urgeProcess;

    public UrgeSetting() {
        super();
    }

    public UrgeSetting(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public User getUrgeUser() {
        return urgeUser;
    }

    public void setUrgeUser(User urgeUser) {
        this.urgeUser = urgeUser;
    }

    public String getUrgeLimit() {
        return urgeLimit;
    }

    public void setUrgeLimit(String urgeLimit) {
        this.urgeLimit = urgeLimit;
    }

    public String getUrgeContent() {
        return urgeContent;
    }

    public void setUrgeContent(String urgeContent) {
        this.urgeContent = urgeContent;
    }

    public String getUrgeProcess() {
        return urgeProcess;
    }

    public void setUrgeProcess(String urgeProcess) {
        this.urgeProcess = urgeProcess;
    }
}
