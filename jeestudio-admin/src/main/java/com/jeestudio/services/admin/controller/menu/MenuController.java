package com.jeestudio.services.admin.controller.menu;

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
 * @Description: Menu Controller
 * @author: whl
 * @Date: 2019-12-25
 */
@Api(value = "GtoaMenuController ",tags = "Menu Controller")
@RestController
@RequestMapping("${adminPath}/sys/menu")
public class MenuController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private DatasourceFeign datasourceFeign;

    /**
     * Get menu list
     */
    @ApiOperation(value = "getMenuList", tags = "Get menu list")
    @RequiresPermissions("user")
    @GetMapping("/getMenuList")
    public ResultJson getMenuList() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取菜单成功");
        resultJson.setMsg_en("Menu was loaded successfully.");
        resultJson.setToken(token);
        resultJson.put("menu", datasourceFeign.getMenuListByUser(currentUserId));
        return resultJson;
    }

    /**
     * Check permission by user id and permission key
     */
    @ApiOperation(value = "hasPermission", tags = "Check permission by user id and permission key")
    @RequiresPermissions("user")
    @PostMapping("/hasPermission")
    public ResultJson hasPermission(String permission) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            resultJson.put("hasPermission", datasourceFeign.hasPermission(currentUserId, permission));
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while checking permission: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Refresh menu cache
     */
    @ApiOperation(value = "/refreshMenuCache", tags = "Refresh menu cache")
    @RequiresPermissions("user")
    @GetMapping("/refreshMenuCache")
    public ResultJson refreshMenuCache() {
        try {
            ResultJson resultJson = datasourceFeign.refreshMenuCache();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to refresh menu cache: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Create menu group
     */
    @ApiOperation(value = "/createMenuGroup", tags = "Create menu group")
    @RequiresPermissions("user")
    @PostMapping("/createMenuGroup")
    public ResultJson createMenuGroup(String formNo, String parentId, String icon) {
        try {
            ResultJson resultJson = datasourceFeign.createMenuGroup(formNo, parentId, icon);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to create menu group: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
