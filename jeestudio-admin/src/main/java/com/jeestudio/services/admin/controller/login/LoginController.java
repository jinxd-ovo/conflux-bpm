package com.jeestudio.services.admin.controller.login;


import com.jeestudio.common.entity.system.User;
import com.jeestudio.services.admin.config.jwt.SignCache;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.cacheFeign.CacheUserFeign;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.services.admin.service.system.SecLogService;
import com.jeestudio.services.admin.service.system.UserService;
import com.jeestudio.utils.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.Random;

/**
 * @Description: Login Controller
 * @author: whl
 * @Date: 2019-12-03
 */
@Api(value = "GtoaLoginController ",tags = "Login Controller")
@RestController
@RequestMapping("${adminPath}")
public class LoginController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SignCache signCache;
    @Autowired
    private DatasourceFeign datasourceFeign;
    @Autowired
    private SecLogService secLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private CacheUserFeign cacheUserFeign;

    @Value(value = "${sec.showValidateCode}")
    private boolean showValidateCode;

    private static final String VALIDATE_CODE = "validateCode";

    @Value(value = "${sso.switch}")
    private boolean ssoSwitch;

    @Value(value = "${sso.server-url}")
    private String ssoServerUrl;

    @Value(value = "${ldap.switch}")
    private String ldapSwitch;

    @Value(value = "${ldap.url}")
    private String ldapUrl;

    @Value(value = "${ldap.searchBase}")
    private String ldapSearchBase;

    @Value(value = "${ldap.defaultBase}")
    private String ldapDefaultBase;

    /**
     * Login
     */
    @ApiOperation(value = "login")
    @PostMapping("/login")
    @HystrixCommand(fallbackMethod = "loginOutage")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        if(this.ssoSwitch) {
            ResultJson resultJson = new ResultJson();
            String username = request.getParameter("username");
            username = Aes.aesDecrypt(username);
            User user = datasourceFeign.getUserByLoginName(username);
            // Token
            String token = signCache.getTokenSetCache(username, user.getId());
            userService.clearLoginExceptionCount(username);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("登录成功");
            resultJson.setMsg_en("Login successfully");
            resultJson.setToken(token);
            resultJson.put("loginName", user.getLoginName());
            resultJson.put("name", user.getName());
            resultJson.put("secLevel", user.getSecLevel());
            resultJson.put("officeCode", user.getOffice().getCode());
            resultJson.put("companyCode", user.getCompany().getCode());
            secLogService.saveSecLog(username, request.getRemoteAddr(), "登录成功", "登录", Global.YES);
            return JsonConvertUtil.objectToJson(resultJson);
        } else {
            ResultJson resultJson = new ResultJson();
            String ldapusername = request.getParameter("ldapusername");
            String ldappassword = request.getParameter("ldappassword");
            if (StringUtil.isNotEmpty(ldapusername) && StringUtil.isNotEmpty(ldappassword)) {
                String account = this.loginLdap(ldapusername, ldappassword);
                if (StringUtil.isNotEmpty(account)) {
                    User user = datasourceFeign.getUserByLoginName(account);
                    // Token
                    String token = signCache.getTokenSetCache(account, user.getId());
                    userService.clearLoginExceptionCount(account);
                    resultJson.setCode(ResultJson.CODE_SUCCESS);
                    resultJson.setMsg("登录成功");
                    resultJson.setMsg_en("Login successfully");
                    resultJson.setToken(token);
                    resultJson.put("loginName", user.getLoginName());
                    resultJson.put("name", user.getName());
                    resultJson.put("secLevel", user.getSecLevel());
                    resultJson.put("officeCode", user.getOffice().getCode());
                    resultJson.put("companyCode", user.getCompany().getCode());
                    secLogService.saveSecLog(account, request.getRemoteAddr(), "域用户登录成功", "登录", Global.YES);
                } else {
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("登录失败");
                    resultJson.setMsg_en("LDAP login failed");
                    secLogService.saveSecLog(account, request.getRemoteAddr(), "域用户登录失败", "登录", Global.NO);
                }
                return JsonConvertUtil.objectToJson(resultJson);
            } else {
                String username = request.getParameter("username");
                if (showValidateCode) {
                    String validateCode = request.getParameter(VALIDATE_CODE);
                    String code = cacheUserFeign.getObject(VALIDATE_CODE);
                    if (StringUtil.isNotEmpty(validateCode) && StringUtil.isNotEmpty(code) && validateCode.equalsIgnoreCase(code)) {

                    } else {
                        resultJson.setCode(ResultJson.CODE_FAILED);
                        resultJson.setMsg("验证码错误，请重试");
                        resultJson.setMsg_en("You have provided a wrong validation code, please try again");
                        secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                        return JsonConvertUtil.objectToJson(resultJson);
                    }
                }
                //Judge User
                String password = request.getParameter("password");
                User user = datasourceFeign.getUserByLoginName(username);
                if (user != null && Global.NO.equals(user.getLoginFlag())) {
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("该帐号已禁止登录");
                    resultJson.setMsg_en("This account is no longer allowed to log in");
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                    return JsonConvertUtil.objectToJson(resultJson);
                }
                if(user != null && Global.YES.equals(user.getSsoLoginFlag())) {
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("该帐号只允许域控登录");
                    resultJson.setMsg_en("Only domain login is allowed for this account");
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                    return JsonConvertUtil.objectToJson(resultJson);
                }
                if (user == null || false == ValidatePassword.validateUserPassword(password, user.getPassword())) {
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("用户或密码错误，请重试");
                    resultJson.setMsg_en("Username and password do not match or you do not have an account yet.");
                    userService.addLoginExceptionCount(username);
                    if (secLogService.getSecSwitch() && userService.getLoginExceptionCount(user.getLoginName()) >= 5) {
                        userService.lockUser(username);
                    }
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                    return JsonConvertUtil.objectToJson(resultJson);
                }
                if (false == Global.YES.equals(user.getUseable())) {//Account discontinued
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("该账号已停用，无法登录");
                    resultJson.setMsg_en("The account has been disabled and cannot log in.");
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                    return JsonConvertUtil.objectToJson(resultJson);
                }
                // Token
                String token = signCache.getTokenSetCache(username, user.getId());
                userService.clearLoginExceptionCount(username);
                resultJson.setCode(ResultJson.CODE_SUCCESS);
                resultJson.setMsg("登录成功");
                resultJson.setMsg_en("Login Successful");
                resultJson.setToken(token);
                resultJson.put("loginName", user.getLoginName());
                resultJson.put("name", user.getName());
                resultJson.put("secLevel", user.getSecLevel());
                resultJson.put("officeCode", user.getOffice().getCode());
                resultJson.put("companyCode", user.getCompany().getCode());
                if (secLogService.getSecSwitch() && userService.isPasswordExpired(user.getLoginName())) {
                    resultJson.put("passwordExpired", "true");
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("该帐号密码已过期");
                    resultJson.setMsg_en("The password has expired");
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                } else if (secLogService.getSecSwitch() && userService.getLoginExceptionCount(user.getLoginName()) >= 5) {
                    userService.lockUser(user.getLoginName());
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("该帐号已禁止登录");
                    resultJson.setMsg_en("This account is no longer allowed to log in");
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录失败", "登录", Global.NO);
                } else {
                    secLogService.saveSecLog(username, request.getRemoteAddr(), "登录成功", "登录", Global.YES);
                }
                return JsonConvertUtil.objectToJson(resultJson);
            }
        }
    }

    /**
     * LDAP login
     */
    private String loginLdap(String username, String password) {
        String account = username;
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, ldapDefaultBase + "\\" + username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, ldapUrl);
        try {
            LdapContext ctx = new InitialLdapContext(env, null);
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "(&(objectClass=user)(sAMAccountName=" + account + "))";
            String returnedAtts[] = {"sAMAccountName","cn"};
            searchCtls.setReturningAttributes(returnedAtts);
            NamingEnumeration<javax.naming.directory.SearchResult> answer = ctx.search(ldapSearchBase, searchFilter,searchCtls);
            if(answer.hasMoreElements()) {
                javax.naming.directory.SearchResult sr = answer.next();
                Attributes atts = sr.getAttributes();
                String srName = sr.getName();
	        	account = (String)atts.get("sAMAccountName").get();
            }
            ctx.close();
        }catch (NamingException e) {
            logger.error("Error occurred while trying to login by LDAP: " + ExceptionUtils.getStackTrace(e));
            account = "";
        }
        return account;
    }

    /**
     * Login outage
     */
    public String loginOutage(HttpServletRequest request, HttpServletResponse response) {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_FAILED);
        resultJson.setMsg("登录异常，请稍后再试");
        resultJson.setMsg_en("Login Error. Please try again later.");
        return JsonConvertUtil.objectToJson(resultJson);
    }

    /**
     * Logout
     */
    @ApiOperation(value = "logout")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityUtils.getSubject().logout();
        signCache.deleteTokenCache(token, currentUserName, currentUserId);
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("退出成功");
        resultJson.setMsg_en("Logout successful");
        secLogService.saveSecLog(currentUserName, request.getRemoteAddr(), "退出成功", "退出", Global.YES);
        return JsonConvertUtil.objectToJson(resultJson);
    }

    /**
     * Create validate code
     */
    @ApiOperation(value = "createCharacter")
    @GetMapping("/createCharacter")
    public String createCharacter() {
        char[] codeSeq = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
                'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String str = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);
            s.append(str);
        }
        cacheUserFeign.setObjectExpireMinute(VALIDATE_CODE, s.toString(), 1L);
        return s.toString();
    }

    /**
     * Get showValidateCode flag
     */
    @ApiOperation(value = "showValidateCode", tags = "Get showValidateCode flag")
    @GetMapping("/showValidateCode")
    public ResultJson showValidateCode() {
        ResultJson resultJson = new ResultJson();
        resultJson.put("showValidateCode", this.showValidateCode);
        resultJson.setToken(token);
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        return resultJson;
    }

    /**
     * Get SSO Server Url
     */
    @ApiOperation(value = "getSsoServerUrl", tags = "Get SsoServerUrl")
    @GetMapping("/getSsoServerUrl")
    public String getSsoServerUrl() {
        return this.ssoServerUrl;
    }

    /**
     * Get SSO Switch
     */
    @ApiOperation(value = "getSsoSwitch", tags = "Get SsoSwitch")
    @GetMapping("/getSsoSwitch")
    public String getSsoSwitch() {
        return String.valueOf(this.ssoSwitch);
    }

    /**
     * Get LDAP Switch
     */
    @ApiOperation(value = "getLdapSwitch", tags = "Get LDAP Switch")
    @GetMapping("/getLdapSwitch")
    public String getLdapSwitch() {
        return String.valueOf(this.ldapSwitch);
    }
}
