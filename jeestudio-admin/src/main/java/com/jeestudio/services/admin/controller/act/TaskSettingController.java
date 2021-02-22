package com.jeestudio.services.admin.controller.act;

import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.act.TaskSetting;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: TaskSetting Controller
 * @author: David
 * @Date: 2020-01-14
 */
@Api(value = "GtoaTaskSettingController ",tags = "TaskSetting Controller")
@RestController
@RequestMapping("${adminPath}/act/taskSetting")
public class TaskSettingController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get task setting
     */
    @ApiOperation(value = "getTaskSetting",tags = "Get task setting")
    @RequiresPermissions("user")
    @GetMapping("/getTaskSetting")
    public ResultJson getTaskSetting(String procDefKey, String userTaskId, String userTaskName) {
        ResultJson resultJson = new ResultJson();
        LinkedHashMap<String, Object> taskSetting = datasourceFeign.getTaskSetting(procDefKey, userTaskId, userTaskName);
        resultJson.setCode(0);
        resultJson.setMsg("获取节点权限设置成功");
        resultJson.setMsg_en("Got node permission settings successfully");
        resultJson.setToken(token);
        resultJson.put("taskSetting", taskSetting);
        return resultJson;
    }

    /**
     * Save task setting
     */
    @ApiOperation(value = "saveTaskSetting",tags = "Save task setting")
    @RequiresPermissions("user")
    @PostMapping("/saveTaskSetting")
    public ResultJson saveTaskSetting(TaskSetting taskSetting) {
        try {
            ResultJson resultJson = datasourceFeign.saveTaskSetting(taskSetting);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save task settings: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get users by id index
     */
    @ApiOperation(value = "getUsersByIdIndex",tags = "Get users by id index")
    @RequiresPermissions("user")
    @GetMapping("/getUsersByIdIndex")
    public ResultJson getUsersByIdIndex(String objId) {
        try {
            ResultJson resultJson = new ResultJson();
            List<User> list = datasourceFeign.getUsersByIdIndexForTaskSetting(objId);
            resultJson.put("list", list);
            resultJson.setCode(0);
            resultJson.setMsg("获取用户成功");
            resultJson.setMsg_en("Get users successfully");
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get users by id index: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Update user task id
     */
    @ApiOperation(value = "updateUserTaskId",tags = "Update user task id")
    @RequiresPermissions("user")
    @GetMapping("/updateUserTaskId")
    public ResultJson updateUserTaskId(String oldId, String newId) {
        try {
            ResultJson resultJson = datasourceFeign.updateUserTaskId(oldId, newId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to update user task id: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get permission list
     */
    @ApiOperation(value = "getPermissionList",tags = "Get permission list")
    @RequiresPermissions("user")
    @PostMapping("/getPermissionList")
    public ResultJson getPermissionList(@RequestBody TaskPermission taskPermission) {
        try {
            ResultJson resultJson = new ResultJson();
            Page<TaskPermission> page = taskPermission.getPage();
            LinkedHashMap<String, Object> map = datasourceFeign.permissionData(taskPermission, page.getPageNo(), page.getPageSize());
            resultJson.setData(map);
            resultJson.setCode(0);
            resultJson.setMsg("获取节点权限模板成功");
            resultJson.setMsg_en("Get permission list successfully");
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get permission list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get gentable list
     */
    @ApiOperation(value = "genTableList",tags = "Get gentable list")
    @RequiresPermissions("user")
    @PostMapping("/genTableList")
    public ResultJson genTableList() {
        try {
            ResultJson resultJson = datasourceFeign.genTableList();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get genTable list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get gentable column list
     */
    @ApiOperation(value = "genTableColumnList",tags = "Get gentable column list")
    @RequiresPermissions("user")
    @GetMapping("/genTableColumnList")
    public ResultJson genTableColumnList(@RequestParam("name") String name) {
        try {
            ResultJson resultJson = datasourceFeign.genTableColumnList(name);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get genTable column list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
