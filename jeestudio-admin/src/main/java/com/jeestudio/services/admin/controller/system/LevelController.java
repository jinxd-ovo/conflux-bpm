package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.Level;
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
 * @Description: Level Controller
 * @author: David
 * @Date: 2020-02-01
 */
@Api(value = "LevelController ",tags = "Level Controller")
@RestController
@RequestMapping("${adminPath}/system/level")
public class LevelController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(ZformController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get level list
     */
    @ApiOperation(value = "data",tags = "Get level list")
    @RequiresPermissions("user")
    @PostMapping("/data")
    public ResultJson data(@RequestBody Level level) {
        try {
            ResultJson resultJson = new ResultJson();
            Page<Level> page = datasourceFeign.findLevelData(level);
            resultJson.put("data", page);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get level list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
