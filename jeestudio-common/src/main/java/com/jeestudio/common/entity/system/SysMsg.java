package com.jeestudio.common.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.common.DataEntity;

import java.util.Date;

/**
 * @Description: Sys msg
 * @author: houxl
 * @Date: 2020-01-09
 */
public class SysMsg extends DataEntity<SysMsg> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String types;
    private String title;
    private String content;
    private String record;
    private String menuName;
    private String menuName_EN;
    private String menuHref;
    private User sender;
    private Date sendTime;
    private User recipient;
    private Date readTime;
    private String status;
    private Date beginSendTime;
    private Date endSendTime;
    private Date beginReadTime;
    private Date endReadTime;

    public SysMsg() {
        super();
    }

    public SysMsg(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName_EN() {
        return menuName_EN;
    }

    public void setMenuName_EN(String menuName_EN) {
        this.menuName_EN = menuName_EN;
    }

    public String getMenuHref() {
        return menuHref;
    }

    public void setMenuHref(String menuHref) {
        this.menuHref = menuHref;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getBeginSendTime() {
        return beginSendTime;
    }

    public void setBeginSendTime(Date beginSendTime) {
        this.beginSendTime = beginSendTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndSendTime() {
        return endSendTime;
    }

    public void setEndSendTime(Date endSendTime) {
        this.endSendTime = endSendTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getBeginReadTime() {
        return beginReadTime;
    }

    public void setBeginReadTime(Date beginReadTime) {
        this.beginReadTime = beginReadTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndReadTime() {
        return endReadTime;
    }

    public void setEndReadTime(Date endReadTime) {
        this.endReadTime = endReadTime;
    }
}
