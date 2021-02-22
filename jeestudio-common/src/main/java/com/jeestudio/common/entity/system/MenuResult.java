package com.jeestudio.common.entity.system;

import com.jeestudio.utils.Global;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: Menu result
 * @author: whl
 * @Date: 2019-12-25
 */
public class MenuResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private MenuResult parent;
    private String pageID;
    private String parentID;
    private String pageName;
    private String pageName_EN;
    private String pageIcon;
    private String pageUrl;
    private String sign;
    private String actionUrl;
    private String type;
    private Integer orderNo;
    private String remarks;
    private String permission;
    private String isShow;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;


    public MenuResult() {
        this.orderNo = 30;
        this.isShow = Global.YES;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName_EN() {
        return pageName_EN;
    }

    public void setPageName_EN(String pageName_EN) {
        this.pageName_EN = pageName_EN;
    }

    public String getPageIcon() {
        return pageIcon;
    }

    public void setPageIcon(String pageIcon) {
        this.pageIcon = pageIcon;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public MenuResult getParent() {
        return parent;
    }

    public void setParent(MenuResult parent) {
        this.parent = parent;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
