package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.param.AssignParam;
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
 * @Description: Datapermission Controller
 * @author: gaoqk
 * @Date: 2020-08-27
 */
@Api(value = "DatapermissionController ",tags = "Datapermission Controller")
@RestController
@RequestMapping("${adminPath}/system/datapermission")
public class DatapermissionController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(DatapermissionController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get permissions
     */
    @ApiOperation(value = "getPermission", tags = "Get permissions")
    @RequiresPermissions("user")
    @GetMapping("/getPermission")
    public ResultJson getPermission(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getDataPermission(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get data permissions: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save permissions
     */
    @ApiOperation(value = "savePermission", tags = "save permissions")
    @RequiresPermissions("user")
    @PostMapping("/savePermission")
    public ResultJson savePermission(@RequestBody AssignParam assignParam) {
        try {
            ResultJson resultJson = datasourceFeign.saveDataPermission(assignParam.getId(), assignParam.getIds());
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save permissions: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
