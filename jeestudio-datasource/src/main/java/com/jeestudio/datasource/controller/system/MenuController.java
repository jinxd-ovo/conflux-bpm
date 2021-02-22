package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.entity.system.MenuResult;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.datasource.service.system.MenuDataService;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: Menu Controller
 * @author: whl
 * @Date: 2020-01-14
 */
@Api(value = "MenuController ",tags = "Menu Controller")
@RestController
@RequestMapping("${datasourcePath}/menu")
public class MenuController {

    @Autowired
    private MenuDataService menuService;
    /**
     * Get menu list by user id
     */
    @ApiOperation(value = "/getMenuListByUser",tags = "Get menu list by user id")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId",value = "userId",required = true,dataType = "String")})
    @GetMapping("/getMenuListByUser")
    public List<MenuResult> getMenuListByUser(@RequestParam("userId") String userId){
        User user = UserUtil.get(userId);
        List<MenuResult> map = UserUtil.getMenuList(user);
        return map;
    }

    /**
     * Check permission by user id and permission key
     */
    @ApiOperation(value = "/hasPermission",tags = "Check permission by user id and permission key")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId",value = "userId",required = true,dataType = "String"),
            @ApiImplicitParam(name = "permission",value = "permission",required = true,dataType = "String")})
    @PostMapping("/hasPermission")
    public Boolean hasPermission(@RequestParam("userId") String userId, @RequestParam("permission") String permission){
        Boolean hasPermission = false;
        if (Global.YES.equals(userId)) {
            hasPermission = true;
        } else if (StringUtil.isEmpty(permission)) {

        } else {
            hasPermission = menuService.hasPermission(userId, permission);
        }
        return hasPermission;
    }

    /**
     * Get menu tree
     */
    @ApiOperation(value = "/menuTree",tags = "Get menu tree")
    @GetMapping("/menuTree")
    public ResultJson getMenuTagTree(){
        return menuService.getMenuTagTree();
    }

    /**
     * Refresh menu cache
     */
    @ApiOperation(value = "/refreshMenuCache",tags = "Refresh menu cache")
    @GetMapping("/refreshMenuCache")
    public ResultJson refreshDictCache(){
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        menuService.refreshMenuCache();
        resultJson.setMsg("刷新菜单缓存成功");
        resultJson.setMsg_en("Refresh menu cache success");
        return resultJson;
    }

    /**
     * Create menu group
     */
    @ApiOperation(value = "/createMenuGroup",tags = "Create menu group")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo",value = "formNo",required = true,dataType = "String"),
            @ApiImplicitParam(name = "parentId",value = "parentId",required = true,dataType = "String"),
            @ApiImplicitParam(name = "icon",value = "icon",required = true,dataType = "String")})
    @PostMapping("/createMenuGroup")
    public ResultJson createMenuGroup(String formNo, String parentId, String icon) {
        return menuService.createMenuGroup(formNo, parentId, icon);
    }
}
