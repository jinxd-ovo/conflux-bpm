package com.jeestudio.services.admin.controller.act;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.view.act.ProcessView;
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

/**
 * @Description: Process Controller
 * @author: David
 * @Date: 2020-08-13
 */
@Api(value = "ProcessController", tags = "Process Controller")
@RestController
@RequestMapping("${adminPath}/act/process")
public class ProcessController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get process list
     */
    @ApiOperation(value = "getProcessList",tags = "Get process list")
    @RequiresPermissions("user")
    @RequestMapping(value="/list", method = RequestMethod.POST, produces = "application/json")
    public ResultJson getProcessList(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = datasourceFeign.getProcessList(zform.getS01(), zform.getPageParam().getPageNo(), zform.getPageParam().getPageSize());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get process list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Update State
     */
    @ApiOperation(value = "updateState",tags = "update State")
    @RequiresPermissions("user")
    @RequestMapping(value="/update/{state}")
    public ResultJson updateState(@PathVariable("state") String state, @RequestParam("procDefId") String procDefId) {
        try {
            ResultJson resultJson = new ResultJson();
            String message = datasourceFeign.updateState(state, procDefId);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg(message);
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to update state: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Delete
     */
    @ApiOperation(value = "delete",tags = "delete")
    @RequiresPermissions("user")
    @RequestMapping(value="/delete")
    public ResultJson delete(@RequestParam("deploymentId") String deploymentId) {
        try {
            ResultJson resultJson = new ResultJson();
            datasourceFeign.deleteProcess(deploymentId);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete deployment: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * toModel
     */
    @ApiOperation(value = "toModel",tags = "toModel")
    @RequiresPermissions("user")
    @RequestMapping(value="/toModel")
    public ResultJson toModel(@RequestParam("procDefId") String procDefId) {
        try {
            ResultJson resultJson = new ResultJson();
            String message = datasourceFeign.convertToModel(procDefId);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg(message);
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to convert to model: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * deploy
     */
    @ApiOperation(value = "deploy",tags = "deploy")
    @RequiresPermissions("user")
    @RequestMapping(value="/deploy")
    public ResultJson deploy(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            String message = datasourceFeign.processDeploy(zform.getS01(), zform.getS02());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg(message);
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to deploy process: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * running list
     */
    @ApiOperation(value = "runningList",tags = "running list")
    @RequiresPermissions("user")
    @RequestMapping(value="/runningList", method = RequestMethod.POST, produces = "application/json")
    public ResultJson runningList(@RequestBody ProcessView processView) {
        try {
            ResultJson resultJson = datasourceFeign.processRunningList(processView);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get running list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * history list
     */
    @ApiOperation(value = "historyList",tags = "history list")
    @RequiresPermissions("user")
    @RequestMapping(value="/historyList", method = RequestMethod.POST, produces = "application/json")
    public ResultJson historyList(@RequestBody ProcessView processView) {
        try {
            ResultJson resultJson = datasourceFeign.processHistoryList(processView);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get history list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * history photo
     */
    @ApiOperation(value = "historyPhoto",tags = "history photo")
    @RequiresPermissions("user")
    @RequestMapping(value="/trace/photo")
    public ResultJson historyPhoto(@RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("executionId") String executionId) {
        try {
            ResultJson resultJson = new ResultJson();
            datasourceFeign.processHistoryPhoto(processDefinitionId, executionId);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to create trace photo: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * delete ProcIns
     */
    @ApiOperation(value = "deleteProcIns",tags = "delete ProcIns")
    @RequiresPermissions("user")
    @RequestMapping(value="/deleteProcIns")
    public ResultJson deleteProcIns(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("reason") String reason) {
        try {
            ResultJson resultJson = new ResultJson();
            String message = datasourceFeign.deleteProcIns(processInstanceId, reason);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg(message);
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete process: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
