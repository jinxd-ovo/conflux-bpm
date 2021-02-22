package com.jeestudio.common.entity.cms;

import com.jeestudio.common.entity.common.TreeEntity;
import com.jeestudio.common.view.system.UserView;

/**
 * @Description: Prt Channel
 * @author: houxl
 * @Date: 2020-07-02
 */
public class PrtChannel extends TreeEntity<PrtChannel> {

    private static final long serialVersionUID = 1L;
    private String ownerCode;
    private PrtModel channelMod;
    private PrtModel infoMod;
    private String useable;
    private String infoType;
    private UserView chargeUser;
    private String parentUser;
    private String ifComment;
    private PrtSite site;
    private String chargeUserName;

    public PrtChannel() {
        super();
    }

    public PrtChannel(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public PrtModel getChannelMod() {
        return channelMod;
    }

    public void setChannelMod(PrtModel channelMod) {
        this.channelMod = channelMod;
    }

    public PrtModel getInfoMod() {
        return infoMod;
    }

    public void setInfoMod(PrtModel infoMod) {
        this.infoMod = infoMod;
    }

    public String getUseable() {
        return useable;
    }

    public void setUseable(String useable) {
        this.useable = useable;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public UserView getChargeUser() {
        return chargeUser;
    }

    public void setChargeUser(UserView chargeUser) {
        this.chargeUser = chargeUser;
    }

    public String getParentUser() {
        return parentUser;
    }

    public void setParentUser(String parentUser) {
        this.parentUser = parentUser;
    }

    public String getIfComment() {
        return ifComment;
    }

    public void setIfComment(String ifComment) {
        this.ifComment = ifComment;
    }

    public PrtSite getSite() {
        return site;
    }

    public void setSite(PrtSite site) {
        this.site = site;
    }

    public String getChargeUserName() {
        return chargeUserName;
    }

    public void setChargeUserName(String chargeUserName) {
        this.chargeUserName = chargeUserName;
    }

    @Override
    public PrtChannel getParent() {
        return this.parent;
    }

    @Override
    public void setParent(PrtChannel parent) {
        this.parent = parent;
    }

    public String getParentId() {
        return parent != null && parent.getId() != null ? parent.getId() : "0";
    }
}
