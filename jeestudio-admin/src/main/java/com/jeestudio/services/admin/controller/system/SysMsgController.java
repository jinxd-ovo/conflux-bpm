package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.entity.system.SysMsg;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
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
 * @Description: SysMsg Controller
 * @author: gaoqk
 * @Date: 2020-08-20
 */
@Api(value = "SysMsgController", tags = "SysMsg Controller")
@RestController
@RequestMapping("${adminPath}/system/sysMsg")
public class SysMsgController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(SysMsgController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * get data list
     */
    @ApiOperation(value = "data",tags = "get data list")
    @RequiresPermissions("user")
    @PostMapping("/data")
    public ResultJson data(@RequestBody SysMsg sysMsg) {
        try {
            ResultJson resultJson = datasourceFeign.getSysMsgData(sysMsg, currentUserName);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get system message data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Set Read
     */
    @ApiOperation(value = "setRead",tags = "set read")
    @RequiresPermissions("user")
    @PostMapping("/setRead")
    public ResultJson setRead(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = new ResultJson();
            String message = datasourceFeign.setRead(id);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg(message);
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to set message to read: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Set Read All
     */
    @ApiOperation(value = "setReadAll",tags = "set read all")
    @RequiresPermissions("user")
    @PostMapping("/setReadAll")
    public ResultJson setReadAll() {
        try {
            ResultJson resultJson = new ResultJson();
            String message = datasourceFeign.setReadAll(currentUserName);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg(message);
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to set all message to read: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get Unread Count
     */
    @ApiOperation(value = "getUnreadCount",tags = "get unread count")
    @RequiresPermissions("user")
    @GetMapping("/getUnreadCount")
    public ResultJson getUnreadCount() {
        try {
            ResultJson resultJson = new ResultJson();
            Integer count = datasourceFeign.getUnreadCount(currentUserId);
            resultJson.setTotal(count);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get unread message number: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
