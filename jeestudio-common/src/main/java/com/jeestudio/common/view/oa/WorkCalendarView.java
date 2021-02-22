package com.jeestudio.common.view.oa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.view.system.OfficeView;

import java.util.Date;
import java.util.List;

/**
 * @Description: Work Calendar View
 * @author: houxl
 * @Date: 2020-08-18
 */
public class WorkCalendarView {

    private static final long serialVersionUID = 1L;

    private String id;
    private String ownerCode;
    private String calendarTypeCode;
    private String title;
    private String content;
    private User recieveUserIds;
    private User createUserId;
    private String emergencyLevelCode;
    private Date startTime;
    private Date endTime;
    private String calendarReminderCode;
    private String status;
    private String recieveUsers;
    private String createUserName;
    private String typeCode;
    private String levelCode;
    private String reminderCode;
    private String recieveUsernames ;
    private String createUserOfficeId;
    private OfficeView office;
    private String calendarOpen;
    private List<String> calendarReminderCodeList;
    private List<String> emergencyLevelCodeList;
    private String createString;
    private String del;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getCalendarTypeCode() {
        return calendarTypeCode;
    }

    public void setCalendarTypeCode(String calendarTypeCode) {
        this.calendarTypeCode = calendarTypeCode;
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

    public User getRecieveUserIds() {
        return recieveUserIds;
    }

    public void setRecieveUserIds(User recieveUserIds) {
        this.recieveUserIds = recieveUserIds;
    }

    public User getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(User createUserId) {
        this.createUserId = createUserId;
    }

    public String getEmergencyLevelCode() {
        return emergencyLevelCode;
    }

    public void setEmergencyLevelCode(String emergencyLevelCode) {
        this.emergencyLevelCode = emergencyLevelCode;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCalendarReminderCode() {
        return calendarReminderCode;
    }

    public void setCalendarReminderCode(String calendarReminderCode) {
        this.calendarReminderCode = calendarReminderCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecieveUsers() {
        return recieveUsers;
    }

    public void setRecieveUsers(String recieveUsers) {
        this.recieveUsers = recieveUsers;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getReminderCode() {
        return reminderCode;
    }

    public void setReminderCode(String reminderCode) {
        this.reminderCode = reminderCode;
    }

    public String getRecieveUsernames() {
        return recieveUsernames;
    }

    public void setRecieveUsernames(String recieveUsernames) {
        this.recieveUsernames = recieveUsernames;
    }

    public String getCreateUserOfficeId() {
        return createUserOfficeId;
    }

    public void setCreateUserOfficeId(String createUserOfficeId) {
        this.createUserOfficeId = createUserOfficeId;
    }

    public OfficeView getOffice() {
        return office;
    }

    public void setOffice(OfficeView office) {
        this.office = office;
    }

    public String getCalendarOpen() {
        return calendarOpen;
    }

    public void setCalendarOpen(String calendarOpen) {
        this.calendarOpen = calendarOpen;
    }

    public List<String> getCalendarReminderCodeList() {
        return calendarReminderCodeList;
    }

    public void setCalendarReminderCodeList(List<String> calendarReminderCodeList) {
        this.calendarReminderCodeList = calendarReminderCodeList;
    }

    public List<String> getEmergencyLevelCodeList() {
        return emergencyLevelCodeList;
    }

    public void setEmergencyLevelCodeList(List<String> emergencyLevelCodeList) {
        this.emergencyLevelCodeList = emergencyLevelCodeList;
    }

    public String getCreateString() {
        return createString;
    }

    public void setCreateString(String createString) {
        this.createString = createString;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }
}
