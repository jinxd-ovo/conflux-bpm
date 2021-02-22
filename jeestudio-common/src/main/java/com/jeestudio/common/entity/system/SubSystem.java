package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Sub system
 * @author: whl
 * @Date: 2019-11-29
 */
public class SubSystem extends DataEntity<SubSystem> {

    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private String baseurl;
    private int sort;
    private String startup;
    private String cache;
    private String caurl;
    private String enable;
    private String syncable;
    private String internal;
    private String getTokenService;
    private String modifyUserService;
    private String serviceDesc;
    private String tokenUserNamePassword;

    public SubSystem() {
        super();
    }

    public SubSystem(String id){
        super(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getCaurl() {
        return caurl;
    }

    public void setCaurl(String caurl) {
        this.caurl = caurl;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getSyncable() {
        return syncable;
    }

    public void setSyncable(String syncable) {
        this.syncable = syncable;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public String getGetTokenService() {
        return getTokenService;
    }

    public void setGetTokenService(String getTokenService) {
        this.getTokenService = getTokenService;
    }

    public String getModifyUserService() {
        return modifyUserService;
    }

    public void setModifyUserService(String modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getTokenUserNamePassword() {
        return tokenUserNamePassword;
    }

    public void setTokenUserNamePassword(String tokenUserNamePassword) {
        this.tokenUserNamePassword = tokenUserNamePassword;
    }
}
