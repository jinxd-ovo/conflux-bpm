package com.jeestudio.services.admin.controller.system;

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
 * @Description: Organization Controller
 * @author: houxl
 * @Date: 2020-12-09
 */
@Api(value = "OrganizationController ",tags = "Organization Controller")
@RestController
@RequestMapping("${adminPath}/system/org")
public class OrganizationController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    @Autowired
    private SecLogService secLogService;

    /**
     * Get assigned users of organization
     */
    @ApiOperation(value = "getAssign", tags = "Get assigned users of organization")
    @RequiresPermissions("user")
    @GetMapping("/getAssign")
    public ResultJson getAssign(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getOrgAssign(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get assigned users of organization: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save assign users of organization
     */
    @ApiOperation(value = "saveAssign", tags = "Save assign users of organization")
    @RequiresPermissions("user")
    @PostMapping("/saveAssign")
    public ResultJson saveAssign(@RequestBody AssignParam assignParam) {
        try {
            ResultJson resultJson = datasourceFeign.saveOrgAssignByParam(assignParam);
            resultJson.setToken(token);
            secLogService.saveSecLog(currentUserName, ip, "分配用户成功", "分配用户", Global.YES);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save users of organization: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLog(currentUserName, ip, "分配用户失败", "分配用户", Global.NO);
            return failed();
        }
    }
}
