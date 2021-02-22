package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.system.Role;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.view.system.UserView;
import com.jeestudio.datasource.controller.dynamic.ZformController;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.BeanUtil;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description: User Controller
 * @author: whl
 * @Date: 2019-11-30
 */
@Api(value = "UserController ",tags = "User Controller")
@RestController
@RequestMapping("${datasourcePath}/user")
public class UserController extends ZformController {

    /**
     * Get user by login name
     */
    @ApiOperation(value = "/getUserByLoginName", tags = "Get user by login name")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getUserByLoginName")
    public User getUserByLoginName(@RequestParam("loginName") String loginName) {
        return UserUtil.getByLoginName(loginName);
    }

    /**
     * Get user name by login name
     *
     * @param loginName
     * @return user name
     */
    @ApiOperation(value = "/getUserNameByLoginName", tags = "Get user name by login name")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getUserNameByLoginName")
    public String getUserNameByLoginName(@RequestParam("loginName") String loginName) {
        User user = UserUtil.getByLoginName(loginName);
        if (user != null) {
            return user.getName();
        } else {
            return null;
        }
    }

    /**
     * Get user permission by login name
     */
    @ApiOperation(value = "/getUserPermissionByLoginName", tags = "Get user permission by login name")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getUserPermissionByLoginName")
    public LinkedHashMap<String, List<String>> getUserPermissionByLoginName(@RequestParam("loginName") String loginName) {
        User user = UserUtil.getByLoginName(loginName);
        LinkedHashMap<String, List<String>> permissionMap = new LinkedHashMap<String, List<String>>();
        if (user != null) {
            List<String> roleList = new ArrayList<>();
            for (Role role : user.getRoleList()) {
                roleList.add(role.getEnname());
            }
            permissionMap.put("role", roleList);
            List<String> list = UserUtil.getMenuPermissionList(user);
            List<String> menuList = new ArrayList<>();
            for (String menuPermission : list) {
                if (StringUtil.isNotBlank(menuPermission)) {
                    for (String permission : StringUtil.split(menuPermission, ",")) {
                        menuList.add(permission);
                    }
                }
            }
            permissionMap.put("menu", menuList);
        }
        return permissionMap;
    }

    /**
     * Get user by login name
     */
    @ApiOperation(value = "/getUserMsgByLoginName", tags = "Get user by login name")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getUserMsgByLoginName")
    public User getUserMsgByLoginName(@RequestParam("loginName") String loginName) {
        User user = UserUtil.getByLoginName(loginName);
        return user;
    }

    /**
     * Get user by user id
     */
    @ApiOperation(value = "/getUserById", tags = "Get user by user id")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "userId", required = true, dataType = "String")})
    @GetMapping("/getUserById")
    public User getUserById(@RequestParam("userId") String userId) {
        User user = UserUtil.get(userId);
        return user;
    }

    /**
     * Get current user view
     */
    @ApiOperation(value = "/getCurrentUserView", tags = "Get current user view")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "userId", required = true, dataType = "String")})
    @GetMapping("/getCurrentUserView")
    public ResultJson getCurrentUserView(@RequestParam("userId") String userId) {
        ResultJson resultJson = new ResultJson();
        try {
            UserView userView = userService.getUserView(userId);
            resultJson.put("userView", userView);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("获取用户成功");
            resultJson.setMsg_en("Get current user view success");
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("获取用户失败");
            resultJson.setMsg_en("Get current user view fail");
        }
        return resultJson;
    }

    /**
     * Save user
     */
    @ApiOperation(value = "/saveUser", tags = "Save user")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/saveUser")
    public ResultJson save(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
            User currentUser = UserUtil.getByLoginName(loginName);
            zform.setTempRuleArgsClass(TaskPermission.class.getSimpleName());
            if (false == zform.getIsNewRecord()) {
                //Update
                Zform t = zformService.get(zform.getId(), genTable);
                BeanUtil.copyBeanNotNull2Bean(zform, t);
                //s03 is password
                if (StringUtil.isNotEmpty(zform.getS03())) {
                    t.setS03(UserUtil.encryptPassword(zform.getS03()));
                }
                t.setUpdateBy(currentUser);
                zformService.saveAct(this.getClass().getName(), t, loginName, genTable);
            } else {
                //Insert
                zform.setCreateBy(currentUser);
                zform.setUpdateBy(currentUser);
                zform.setOwnerCode(currentUser.getCompany().getCode());
                //s03 is password
                if (StringUtil.isNotEmpty(zform.getS03())) {
                    zform.setS03(UserUtil.encryptPassword(zform.getS03()));
                }
                zformService.saveAct(this.getClass().getName(), zform, loginName, genTable);
            }
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("保存用户成功");
            resultJson.setMsg_en("Save user success");
        } catch (Exception e) {
            logger.error("Save user error:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("保存用户失败");
            resultJson.setMsg_en("Save user failed");
        }
        return resultJson;
    }

    /**
     * Get login exception count
     */
    @GetMapping("/getLoginExceptionCount")
    public Integer getLoginExceptionCount(@RequestParam("loginName") String loginName) {
        Integer count = userService.getLoginExceptionCount(loginName);
        return count;
    }

    /**
     * Clear login exception count
     */
    @PostMapping("/clearLoginExceptionCount")
    public void clearLoginExceptionCount(@RequestParam("loginName") String loginName) {
        userService.clearLoginExceptionCount(loginName);
    }

    /**
     * Is password expired
     */
    @GetMapping("/isPasswordExpired")
    public Boolean isPasswordExpired(@RequestParam("loginName") String loginName) {
        Date expiredDate = userService.isPasswordExpired(loginName);
        Date nowDate = new Date();
        Boolean expired = false;
        if (expiredDate == null) {
            expired = false;
        } else {
            int compareTo = expiredDate.compareTo(nowDate);
            if (compareTo > 0) {
                expired = false;
            } else {
                expired = true;
            }
        }
        return expired;
    }

    /**
     * Unlock user
     */
    @PostMapping("/unlockUser")
    public void unlockUser(@RequestParam("loginName") String loginName) {
        userService.unlockUser(loginName);
    }

    /**
     * Lock user
     */
    @PostMapping("/lockUser")
    public void lockUser(@RequestParam("loginName") String loginName) {
        userService.lockUser(loginName);
    }

    /**
     * Add login exception count
     */
    @PostMapping("/addLoginExceptionCount")
    public void addLoginExceptionCount(@RequestParam("loginName") String loginName) {
        userService.addLoginExceptionCount(loginName);
    }

    /**
     * Change password
     */
    @PostMapping("/changePassword")
    public void changePassword(@RequestParam("password") String password, @RequestParam("loginName") String loginName) {
        userService.changePassword(password, loginName);
    }
}
