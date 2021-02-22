package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.view.system.OfficeView;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.controller.dynamic.ZformController;
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
 * @Description: Office Controller
 * @author: David
 * @Date: 2020-02-06
 */
@Api(value = "OfficeController ",tags = "Office Controller")
@RestController
@RequestMapping("${adminPath}/system/office")
public class OfficeController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(ZformController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get office view list
     */
    @ApiOperation(value = "data",tags = "Get office view list")
    @RequiresPermissions("user")
    @GetMapping("/viewData")
    public ResultJson viewData() {
        try {
            ResultJson resultJson = datasourceFeign.findOfficeViewData(new OfficeView());
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get office view data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
