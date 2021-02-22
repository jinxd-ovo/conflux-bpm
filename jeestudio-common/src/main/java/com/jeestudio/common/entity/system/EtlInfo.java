package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Etl Info
 * @author: houxl
 * @Date: 2020-11-17
 */
public class EtlInfo extends DataEntity<EtlInfo> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String internal;
    private String status;
    private Long sort;
    private String scriptfile;
    private String host;
    private String database;
    private String pattern;
    private String port;
    private String userName;
    private String password;
    private String logChannelId;
    private String ownerCode;

    public EtlInfo() {
        super();
    }

    public EtlInfo(String id){
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public String getScriptfile() {
        return scriptfile;
    }

    public void setScriptfile(String scriptfile) {
        this.scriptfile = scriptfile;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogChannelId() {
        return logChannelId;
    }

    public void setLogChannelId(String logChannelId) {
        this.logChannelId = logChannelId;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }
}
