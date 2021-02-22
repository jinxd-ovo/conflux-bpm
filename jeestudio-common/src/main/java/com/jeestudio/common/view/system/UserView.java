package com.jeestudio.common.view.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.common.entity.system.*;
import com.jeestudio.utils.Collections3;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: User view
 * @author: whl
 * @Date: 2019-11-28
 */
public class UserView extends DataEntity<UserView> {

    private static final long serialVersionUID = 1L;

    private Office company;
    private Office office;
    private String loginName;
    private String no;
    private String name;
    private String email;
    private String phone;
    private String mobile;
    private String userType;
    private String loginIp;
    private Date loginDate;
    private String loginFlag;
    private String oldLoginName;
    private String oldLoginIp;
    private Date oldLoginDate;
    private Post post;
    private Post partPost;
    private Level level;
    private String useable;
    private Integer sortIndex;
    private String isSys = Global.NO;
    private String secLevel;
    private Integer loginExceptionCount;
    private Date passwordExpiredDate;
    private String status;
    private String title;

    public UserView() {
        super();
        this.loginFlag = Global.YES;
    }

    public UserView(String id){
        super(id);
    }

    public UserView(String id, String loginName){
        super(id);
        this.loginName = loginName;
    }

    public UserView(Role role){
        super();
    }

    public String getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    public String getId() {
        return id;
    }

    public Office getCompany() {
        return company;
    }

    public void setCompany(Office company) {
        this.company = company;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getOldLoginName() {
        return oldLoginName;
    }

    public void setOldLoginName(String oldLoginName) {
        this.oldLoginName = oldLoginName;
    }

    public String getOldLoginIp() {
        if (oldLoginIp == null){
            return loginIp;
        }
        return oldLoginIp;
    }

    public void setOldLoginIp(String oldLoginIp) {
        this.oldLoginIp = oldLoginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getOldLoginDate() {
        if (oldLoginDate == null){
            return loginDate;
        }
        return oldLoginDate;
    }

    public void setOldLoginDate(Date oldLoginDate) {
        this.oldLoginDate = oldLoginDate;
    }

    public boolean isAdmin(){
        return isAdmin(this.id);
    }

    public static boolean isAdmin(String id){
        return id != null && "1".equals(id);
    }

    @Override
    public String toString() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPartPost() {
        return partPost;
    }

    public void setPartPost(Post partPost) {
        this.partPost = partPost;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getUseable() {
        return useable;
    }

    public void setUseable(String useable) {
        this.useable = useable;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public boolean isSystem(){
        return isSystem(this.loginName);
    }

    public static boolean isSystem(String loginName){
        return loginName != null && (loginName.startsWith("sysadmin") || loginName.startsWith("secadmin") || loginName.startsWith("auditadmin"));
    }

    public String getIsSys() {
        return isSys;
    }

    public void setIsSys(String isSys) {
        if (StringUtil.isEmpty(isSys)) isSys = "0";
        this.isSys = isSys;
    }

    public String getSecLevel() {
        return secLevel;
    }

    public void setSecLevel(String secLevel) {
        this.secLevel = secLevel;
    }

    public Integer getLoginExceptionCount() {
        return loginExceptionCount;
    }

    public void setLoginExceptionCount(Integer loginExceptionCount) {
        this.loginExceptionCount = loginExceptionCount;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getPasswordExpiredDate() {
        return passwordExpiredDate;
    }

    public void setPasswordExpiredDate(Date passwordExpiredDate) {
        this.passwordExpiredDate = passwordExpiredDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
