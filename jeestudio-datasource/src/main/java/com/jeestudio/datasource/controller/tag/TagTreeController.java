package com.jeestudio.datasource.controller.tag;

import com.jeestudio.datasource.service.system.AreaService;
import com.jeestudio.datasource.service.system.OfficeService;
import com.jeestudio.datasource.service.system.UserService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TagTree Controller
 * @author: whl
 * @Date: 2020-01-19
 */
@Api(value = "TagTreeController ",tags = "TagTree Controller")
@RestController
@RequestMapping("${datasourcePath}/tag")
public class TagTreeController {

    @Autowired
    private UserService userService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private OfficeService officeService;

    /**
     * Get user tree async
     */
    @ApiOperation(value = "/userTreeAsync", tags = "Get user tree async")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String")})
    @GetMapping("/userTreeAsync")
    public ResultJson getUserTagTreeAsync(@RequestParam("id") String id) {
        return userService.getUserTagTreeAsync(id);
    }

    /**
     * Get user tree
     */
    @ApiOperation(value = "/userTree", tags = "Get User Tag Tree")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = false, defaultValue = "", dataType = "String")})
    @GetMapping("/userTree")
    public ResultJson getUserTagTree(@RequestParam("loginName") String loginName) {
        return userService.getUserTagTree(loginName);
    }

    /**
     * Get office tree
     */
    @ApiOperation(value = "/officeTagTree", tags = "Get Office Tag Tree")
    @GetMapping("/officeTagTree")
    public ResultJson getOfficeTagTree() {
        return officeService.getOfficeTagTree();
    }

    /**
     * Get office tree async
     */
    @GetMapping("/officeTagTreeAsync")
    public ResultJson getOfficeTagTreeAsync(@RequestParam("id") String id) {
        return officeService.getOfficeTagTreeAsync(id);
    }

    /**
     * Get area tree async
     */
    @GetMapping("/areaTagTreeAsync")
    public ResultJson getAreaTagTreeAsync(@RequestParam("id") String id) {
        return areaService.getAreaTagTreeAsync(id);
    }

    /**
     * Get area tree
     */
    @GetMapping("/areaTagTree")
    public ResultJson getAreaTagTree() {
        return areaService.getAreaTagTree();
    }
}
