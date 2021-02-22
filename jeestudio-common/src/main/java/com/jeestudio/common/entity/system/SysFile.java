package com.jeestudio.common.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.common.DataEntity;

import java.util.Date;

/**
 * @Description: Sys file
 * @author: houxl
 * @Date: 2019-01-11
 */
public class SysFile extends DataEntity<SysFile> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String groupId;
    private String name;
    private String ext;
    private String type;
    private String size;
    private String path;
    private String pdfPath;
    private String thumbPath;
    private Date uploadTime;
    private User uploadUser;
    private Integer sort;
    private String desc;
    private Integer duration;
    private String secFlag;
    private Integer visitCount;
    private String content;
    private String toPdf;
    private Date beginUploadTime;
    private Date endUploadTime;
    private String failType;
    private String secretLevel;

    public SysFile() {
        super();
    }

    public SysFile(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public User getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(User uploadUser) {
        this.uploadUser = uploadUser;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSecFlag() {
        return secFlag;
    }

    public void setSecFlag(String secFlag) {
        this.secFlag = secFlag;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToPdf() {
        return toPdf;
    }

    public void setToPdf(String toPdf) {
        this.toPdf = toPdf;
    }

    public Date getBeginUploadTime() {
        return beginUploadTime;
    }

    public void setBeginUploadTime(Date beginUploadTime) {
        this.beginUploadTime = beginUploadTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndUploadTime() {
        return endUploadTime;
    }

    public void setEndUploadTime(Date endUploadTime) {
        this.endUploadTime = endUploadTime;
    }

    public String getFailType() {
        return failType;
    }

    public void setFailType(String failType) {
        this.failType = failType;
    }

    public String getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
    }
}
