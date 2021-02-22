package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.service.system.SecLogService;
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
 * @Description: SecLog Controller
 * @author: David
 * @Date: 2020-06-18
 */
@Api(value = "SecLogController ",tags = "SecLog Controller")
@RestController
@RequestMapping("${adminPath}/system/secLog")
public class SecLogController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(SecLogController.class);

    @Autowired
    private SecLogService secLogService;

    /**
     * Get secLog list
     */
    @ApiOperation(value = "data", tags = "Get secLog list")
    @RequiresPermissions("user")
    @PostMapping("/data")
    public ResultJson data(@RequestBody Zform zform, @RequestParam("path") String path, @RequestParam("formNo") String formNo, @RequestParam("traceFlag") String traceFlag) {
        try {
            ResultJson resultJson = secLogService.data(zform, path, formNo, traceFlag, currentUserName, ip);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get secLog data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get secSwitch
     */
    @ApiOperation(value = "secSwitch", tags = "Get secSwitch")
    @RequiresPermissions("user")
    @GetMapping("/secSwitch")
    public ResultJson secSwitch() {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.put("secSwitch", secLogService.getSecSwitch());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get secSwitch: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get passwordExpired
     */
    @ApiOperation(value = "passwordExpired", tags = "Get passwordExpired")
    @RequiresPermissions("user")
    @GetMapping("/passwordExpired")
    public ResultJson passwordExpired() {
        ResultJson resultJson = secLogService.passwordExpired(currentUserName);
        resultJson.setToken(token);
        return resultJson;
    }

    /**
     * Get SecLogSpace
     */
    @ApiOperation(value = "getSecLogSpace", tags = "Get SecLogSpace")
    @RequiresPermissions("user")
    @GetMapping("/getSecLogSpace")
    public ResultJson getSecLogSpace() {
        ResultJson resultJson = secLogService.getSecLogSpace();
        resultJson.setToken(token);
        return resultJson;
    }
}
