package com.jeestudio.common.entity.gen;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Gen develop user
 * @author: houxl
 * @Date: 2020-01-18
 */
public class GenDevelopUser extends DataEntity<GenDevelopUser> {

	private static final long serialVersionUID = 1L;

	private String ownerCode;
	private String email;
	private String mac1;
	private String mac2;
	private String mac3;
	private String mac4;
	private String mac5;
	private Integer okNum;
	private Date okDate;
	private String visitLog;
	
	public GenDevelopUser() {
		super();
	}

	public GenDevelopUser(String id){
		super(id);
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getMac1() {
		return mac1;
	}

	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}
	
	public String getMac2() {
		return mac2;
	}

	public void setMac2(String mac2) {
		this.mac2 = mac2;
	}
	
	public String getMac3() {
		return mac3;
	}

	public void setMac3(String mac3) {
		this.mac3 = mac3;
	}
	
	public String getMac4() {
		return mac4;
	}

	public void setMac4(String mac4) {
		this.mac4 = mac4;
	}
	
	public String getMac5() {
		return mac5;
	}

	public void setMac5(String mac5) {
		this.mac5 = mac5;
	}
	
	public Integer getOkNum() {
		return okNum;
	}

	public void setOkNum(Integer okNum) {
		this.okNum = okNum;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getOkDate() {
		return okDate;
	}

	public void setOkDate(Date okDate) {
		this.okDate = okDate;
	}
	
	public String getVisitLog() {
		return visitLog;
	}

	public void setVisitLog(String visitLog) {
		this.visitLog = visitLog;
	}

	@Override
	public String toString() {
		return "mac1=" + mac1 + ", mac2=" + mac2 + ", mac3="
				+ mac3 + ", mac4=" + mac4 + ", mac5=" + mac5;
	}
}