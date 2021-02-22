package com.jeestudio.services.admin.controller.cms;

import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.controller.user.UserController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Cms Controller
 * @author: David
 * @Date: 2020-07-02
 */
@Api(value = "CmsController ",tags = "Cms Controller")
@RestController
@RequestMapping("${adminPath}/app/cms")
public class CmsController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get news index
     */
    @ApiOperation(value = "getIndex",tags = "Get news index")
    @RequiresPermissions("user")
    @GetMapping("/getIndex")
    public ResultJson getIndex(@RequestParam("activeChannel") String activeChannel) {
        try {
            ResultJson resultJson = datasourceFeign.getIndex(activeChannel, currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get news index: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get news list
     */
    @ApiOperation(value = "getInfoList",tags = "Get news list")
    @RequiresPermissions("user")
    @GetMapping("/getInfoList")
    public ResultJson getInfoList(@RequestParam("activeChannel") String activeChannel, @RequestParam("length") String length) {
        try {
            ResultJson resultJson = datasourceFeign.getInfoList(activeChannel, length);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get info list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get news
     */
    @ApiOperation(value = "getInfo",tags = "Get news")
    @RequiresPermissions("user")
    @GetMapping("/getInfo")
    public ResultJson getInfo(@RequestParam("infoId") String infoId) {
        try {
            ResultJson resultJson = datasourceFeign.getInfo(infoId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get info: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
