package com.jeestudio.services.admin.controller.tag;

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
 * @Description: TagTree Controller
 * @author: whl
 * @Date: 2020-01-19
 */
@Api(value = "TagTreeController ",tags = "TagTree Controller")
@RestController
@RequestMapping("${adminPath}/sys/tagTree")
public class TagTreeController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(TagTreeController.class);

    @Autowired
    private DatasourceFeign datasourceFeign;

    /**
     * Get user tree async
     */
    @ApiOperation(value = "userTreeAsync", tags = "Get user tree async")
    @RequiresPermissions("user")
    @PostMapping("/userTreeAsync")
    public ResultJson userTreeAsync(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getUserTagTreeAsync(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get user tree async: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get user tree
     */
    @ApiOperation(value = "userTree", tags = "Get user tree")
    @RequiresPermissions("user")
    @PostMapping("/userTree")
    public ResultJson userTree() {
        try {
            ResultJson resultJson = datasourceFeign.getUserTagTree(currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get user tree: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get office tree
     */
    @ApiOperation(value = "officeTree", tags = "Get office tree")
    @RequiresPermissions("user")
    @PostMapping("/officeTree")
    public ResultJson officeTree() {
        try {
            ResultJson resultJson = datasourceFeign.getOfficeTagTree();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get office tree: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get office tree async
     */
    @ApiOperation(value = "officeTreeAsync", tags = "Get office tree async")
    @RequiresPermissions("user")
    @PostMapping("/officeTreeAsync")
    public ResultJson officeTreeAsync(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getOfficeTagTreeAsync(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get office tree async: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get area tree async
     */
    @ApiOperation(value = "areaTreeAsync", tags = "Get area tree async")
    @RequiresPermissions("user")
    @PostMapping("/areaTreeAsync")
    public ResultJson areaTreeAsync(@RequestParam("id") String id) {
        try {
            ResultJson resultJson = datasourceFeign.getAreaTagTreeAsync(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get area tree async: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get area tree
     */
    @ApiOperation(value = "areaTree", tags = "Get area tree")
    @RequiresPermissions("user")
    @PostMapping("/areaTree")
    public ResultJson areaTree() {
        try {
            ResultJson resultJson = datasourceFeign.getAreaTagTree();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get area tree: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get menu tree
     */
    @ApiOperation(value = "menuTree", tags = "Get menu tree")
    @RequiresPermissions("user")
    @GetMapping("/menuTree")
    public ResultJson menuTree() {
        try {
            ResultJson resultJson = datasourceFeign.getMenuTagTree();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get menu tree: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get dataPermission tree
     */
    @ApiOperation(value = "dataPermissionTree", tags = "Get dataPermission tree")
    @RequiresPermissions("user")
    @GetMapping("/dataPermissionTree")
    public ResultJson dataPermissionTree() {
        try {
            ResultJson resultJson = datasourceFeign.getDataPermissionTree();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get dataPermission tree: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
