package com.jeestudio.services.admin.controller.user;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.cacheFeign.CacheUserFeign;
import com.jeestudio.services.admin.service.system.SecLogService;
import com.jeestudio.services.admin.service.system.UserService;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: User Controller
 * @author: whl
 * @Date: 2020-01-19
 */
@Api(value = "UserController ",tags = "User Controller")
@RestController
@RequestMapping("${adminPath}/sys/user")
public class UserController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    private CacheUserFeign cacheUserFeign;

    @Autowired
    private SecLogService secLogService;

    /**
     * Get current user view
     */
    @ApiOperation(value = "getCurrentUserView", tags = "Get current user view")
    @RequiresPermissions("user")
    @GetMapping("/getCurrentUserView")
    public ResultJson getCurrentUserView() {
        try {
            ResultJson resultJson = userService.getCurrentUserView(currentUserId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get current user view: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save user
     */
    @ApiOperation(value = "save", tags = "Save user")
    @RequiresPermissions("user")
    @PostMapping("/save")
    public ResultJson save(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = userService.save(zform, currentUserName);
            resultJson.setToken(token);
            if (resultJson.getCode() == null || resultJson.getCode() != ResultJson.CODE_FAILED) {
                resultJson.setCode(ResultJson.CODE_SUCCESS);
                resultJson.setMsg("保存用户成功");
                resultJson.setMsg_en("User data was saved successfully.");
            }
            if (resultJson.getCode() != null && resultJson.getCode() == ResultJson.CODE_SUCCESS) {
                cacheUserFeign.deleteHash(Global.USER_CACHE, "_" + zform.getS01());
            }
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save user data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get login exception count
     */
    @ApiOperation(value = "getLoginExceptionCount", tags = "Get login exception count")
    @RequiresPermissions("user")
    @GetMapping("/getLoginExceptionCount")
    public ResultJson getLoginExceptionCount(@RequestParam("loginName") String loginName) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setToken(token);
            Integer count = userService.getLoginExceptionCount(loginName);
            resultJson.put("count", count);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get login exception count: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Clear login exception count
     */
    @ApiOperation(value = "clearLoginExceptionCount", tags = "Clear login exception count")
    @RequiresPermissions("user")
    @PostMapping("/clearLoginExceptionCount")
    public ResultJson clearLoginExceptionCount(@RequestParam("loginName") String loginName) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setToken(token);
            userService.clearLoginExceptionCount(loginName);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to clear login exception count: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Is password expired
     */
    @ApiOperation(value = "isPasswordExpired", tags = "Is password expired")
    @RequiresPermissions("user")
    @GetMapping("/isPasswordExpired")
    public ResultJson isPasswordExpired(@RequestParam("loginName") String loginName) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setToken(token);
            Boolean expired = userService.isPasswordExpired(loginName);
            resultJson.put("expired", expired);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to check user password expiration: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Unlock user
     */
    @ApiOperation(value = "unlockUser", tags = "Unlock user")
    @RequiresPermissions("user")
    @PostMapping("/unlockUser")
    public ResultJson unlockUser(@RequestParam("loginName") String loginName) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setToken(token);
            userService.unlockUser(loginName);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            secLogService.saveSecLog(currentUserName, ip, "解锁用户成功", "解锁用户", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to unlock user: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "解锁用户失败", "解锁用户", Global.NO);
            return failed();
        }
    }

    /**
     * Lock user
     */
    @ApiOperation(value = "lockUser", tags = "Lock user")
    @RequiresPermissions("user")
    @PostMapping("/lockUser")
    public ResultJson lockUser(@RequestParam("loginName") String loginName) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setToken(token);
            userService.lockUser(loginName);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            secLogService.saveSecLog(currentUserName, ip, "锁定用户成功", "锁定用户", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to lock user: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "锁定用户失败", "锁定用户", Global.NO);
            return failed();
        }
    }

    /**
     * Add login exception count
     */
    @ApiOperation(value = "addLoginExceptionCount", tags = "Add login exception count")
    @RequiresPermissions("user")
    @PostMapping("/addLoginExceptionCount")
    public ResultJson addLoginExceptionCount(@RequestParam("loginName") String loginName) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setToken(token);
            userService.addLoginExceptionCount(loginName);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to add login exception count: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Change password
     */
    @ApiOperation(value = "changePassword", tags = "Change password")
    @RequiresPermissions("user")
    @PostMapping("/changePassword")
    public ResultJson changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        try {
            ResultJson resultJson = userService.changePassword(oldPassword, newPassword, currentUserName);
            if (StringUtil.isNotBlank(currentUserName) && resultJson.getCode() != null && resultJson.getCode() == ResultJson.CODE_SUCCESS) {
                cacheUserFeign.deleteHash(Global.USER_CACHE, "_" + currentUserName);
            }
            resultJson.setToken(token);
            secLogService.saveSecLog(currentUserName, ip, "修改密码成功", "修改密码", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to update password: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "修改密码失败", "修改密码", Global.YES);
            return failed();
        }
    }
}
