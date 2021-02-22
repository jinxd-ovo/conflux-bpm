package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.entity.system.Auth;
import com.jeestudio.common.param.AssignParam;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.services.admin.service.system.SecLogService;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Role Controller
 * @author: whl
 * @Date: 2020-03-11
 */
@Api(value = "RoleController ",tags = "Role Controller")
@RestController
@RequestMapping("${adminPath}/system/role")
public class RoleController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    @Autowired
    private SecLogService secLogService;

    /**
     * Get permissions
     */
    @ApiOperation(value = "getAuth", tags = "Get permissions")
    @RequiresPermissions("user")
    @GetMapping("/getAuth")
    public ResultJson getAuth(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getAuth(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to achieve authorization: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save permissions
     */
    @ApiOperation(value = "saveAuth", tags = "Save permissions")
    @RequiresPermissions("user")
    @PostMapping("/saveAuth")
    public ResultJson saveAuth(@RequestBody Auth auth) {
        try {
            ResultJson resultJson = datasourceFeign.saveAuth(auth.getId(), auth.getIds());
            resultJson.setToken(token);
            secLogService.saveSecLog(currentUserName, ip, "权限设置成功", "权限设置", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save authorization: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "权限设置失败", "权限设置", Global.NO);
            return failed();
        }
    }

    /**
     * Get assigned roles
     */
    @ApiOperation(value = "getAssign", tags = "Get assigned roles")
    @RequiresPermissions("user")
    @GetMapping("/getAssign")
    public ResultJson getAssign(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getAssign(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get assigned roles: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get assigned data roles
     */
    @ApiOperation(value = "getDataAssign", tags = "Get assigned data roles")
    @RequiresPermissions("user")
    @GetMapping("/getDataAssign")
    public ResultJson getDataAssign(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getDataAssign(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get assigned data roles: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save assign roles
     */
    @ApiOperation(value = "saveAssign", tags = "Save assign roles")
    @RequiresPermissions("user")
    @PostMapping("/saveAssign")
    public ResultJson saveAssign(@RequestBody AssignParam assignParam) {
        try {
            ResultJson resultJson = datasourceFeign.saveAssignByParam(assignParam);
            resultJson.setToken(token);
            secLogService.saveSecLog(currentUserName, ip, "分配用户成功", "分配用户", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save roles assignment: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "分配用户失败", "分配用户", Global.NO);
            return failed();
        }
    }

    /**
     * Save assign data roles
     */
    @ApiOperation(value = "saveDataAssign", tags = "Save assign data roles")
    @RequiresPermissions("user")
    @PostMapping("/saveDataAssign")
    public ResultJson saveDataAssign(@RequestBody AssignParam assignParam) {
        try {
            ResultJson resultJson = datasourceFeign.saveDataAssignByParam(assignParam);
            resultJson.setToken(token);
            secLogService.saveSecLog(currentUserName, ip, "分配用户成功", "分配用户", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save data roles assignment: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "分配用户失败", "分配用户", Global.NO);
            return failed();
        }
    }
}
