package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: User sso subsystem
 * @author: whl
 * @Date: 2019-11-29
 */
public class UserSsoSubsystem extends DataEntity<UserSsoSubsystem> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String userId;
    private SubSystem ssoSubsystem;
    private String loginName;
    private String password;
    private String isAllow;

    public UserSsoSubsystem() {
        super();
    }

    public UserSsoSubsystem(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SubSystem getSsoSubsystem() {
        return ssoSubsystem;
    }

    public void setSsoSubsystem(SubSystem ssoSubsystem) {
        this.ssoSubsystem = ssoSubsystem;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsAllow() {
        return isAllow;
    }

    public void setIsAllow(String isAllow) {
        this.isAllow = isAllow;
    }
}
