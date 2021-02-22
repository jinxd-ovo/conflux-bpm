package com.jeestudio.common.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.utils.Collections3;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.StringUtil;
import java.util.Date;
import java.util.List;

/**
 * @Description: User
 * @author: whl
 * @Date: 2019-11-28
 */
public class User extends DataEntity<User> {

    private static final long serialVersionUID = 1L;

    private Office company;
    private Office office;
    private String loginName;
    private String password;
    private String no;
    private String name;
    private String email;
    private String phone;
    private String mobile;
    private String userType;
    private String loginIp;
    private Date loginDate;
    private String loginFlag;
    private String photo;
    private String qrCode;
    private String oldLoginName;
    private String newPassword;
    private String sign;
    private String oldLoginIp;
    private Date oldLoginDate;
    private Role role;
    private List<Role> roleList = Lists.newArrayList();
    private String desPassword;
    private String ssoLoginFlag;
    private Post post;
    private Post partPost;
    private Level level;
    private String useable;
    private Integer sortIndex;
    private String ssoDesPassword;
    private String newSsoDesPassword;
    private List<UserSsoSubsystem> userSsoSubsystemList = Lists.newArrayList();
    private String isSys = Global.NO;
    private String secLevel;
    private Integer loginExceptionCount;
    private Date passwordExpiredDate;
    private String oldPassword;
    private String confirmNewPassword;
    private String imageBaseCode;
    private String status;
    private String title;
    private List<Datapermission> datapermissionList = Lists.newArrayList();

    public User() {
        super();
        this.loginFlag = Global.YES;
    }

    public User(String id){
        super(id);
    }

    public User(String id, String loginName){
        super(id);
        this.loginName = loginName;
    }

    public User(Role role){
        super();
        this.role = role;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getRoleNames() {
        return Collections3.extractToString(roleList, "name", ",");
    }

    public String getRoleEnNames() {
        return Collections3.extractToString(roleList, "enname", ",");
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

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public String getDesPassword() {
        return desPassword;
    }

    public void setDesPassword(String desPassword) {
        this.desPassword = desPassword;
    }

    public String getSsoLoginFlag() {
        return ssoLoginFlag;
    }

    public void setSsoLoginFlag(String ssoLoginFlag) {
        this.ssoLoginFlag = ssoLoginFlag;
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

    public String getSsoDesPassword() {
        return ssoDesPassword;
    }

    public void setSsoDesPassword(String ssoDesPassword) {
        this.ssoDesPassword = ssoDesPassword;
    }

    public String getNewSsoDesPassword() {
        return newSsoDesPassword;
    }

    public void setNewSsoDesPassword(String newSsoDesPassword) {
        this.newSsoDesPassword = newSsoDesPassword;
    }

    public List<UserSsoSubsystem> getUserSsoSubsystemList() {
        return userSsoSubsystemList;
    }

    public void setUserSsoSubsystemList(List<UserSsoSubsystem> userSsoSubsystemList) {
        this.userSsoSubsystemList = userSsoSubsystemList;
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getImageBaseCode() {
        return imageBaseCode;
    }

    public void setImageBaseCode(String imageBaseCode) {
        this.imageBaseCode = imageBaseCode;
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

    public List<Datapermission> getDatapermissionList() {
        return datapermissionList;
    }

    public void setDatapermissionList(List<Datapermission> datapermissionList) {
        this.datapermissionList = datapermissionList;
    }
}
