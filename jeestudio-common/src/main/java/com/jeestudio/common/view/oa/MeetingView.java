package com.jeestudio.common.view.oa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.system.User;

import java.util.Date;

/**
 * @Description: Meeting View
 * @author: houxl
 * @Date: 2020-08-18
 */
public class MeetingView {

    private static final long serialVersionUID = 1L;

    private String id;
    private String ownerCode;
    private String typeCode;
	private String name;
	private User convener;
	private String convener_phone;
    private String place;
    private Date meetingStart;
    private Date meetingStop;
    private User participant;
    private String reminderCode;
    private String content;

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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getConvener() {
        return convener;
    }

    public void setConvener(User convener) {
        this.convener = convener;
    }

    public String getConvener_phone() {
        return convener_phone;
    }

    public void setConvener_phone(String convener_phone) {
        this.convener_phone = convener_phone;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    public Date getMeetingStart() {
        return meetingStart;
    }

    public void setMeetingStart(Date meetingStart) {
        this.meetingStart = meetingStart;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    public Date getMeetingStop() {
        return meetingStop;
    }

    public void setMeetingStop(Date meetingStop) {
        this.meetingStop = meetingStop;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public String getReminderCode() {
        return reminderCode;
    }

    public void setReminderCode(String reminderCode) {
        this.reminderCode = reminderCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
