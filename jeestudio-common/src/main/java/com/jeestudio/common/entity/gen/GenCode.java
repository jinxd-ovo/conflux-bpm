package com.jeestudio.common.entity.gen;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.common.DataEntity;

import java.util.Date;

/**
 * @Description: Gen code
 * @author: whl
 * @Date: 2020-02-12
 */
public class GenCode extends DataEntity<GenCode> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String username;
    private String groupId;
    private String moduleName;
    private String fileId;
    private String fileName;
    private Date createTime;
    private String path;

    public GenCode() {
        super();
    }

    public GenCode(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
