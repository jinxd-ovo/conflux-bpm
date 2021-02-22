package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.param.AssignParam;
import com.jeestudio.datasource.service.system.RoleService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Role Controller
 * @author: whl
 * @Date: 2020-03-11
 */
@Api(value = "RoleController ",tags = "Role Controller")
@RestController
@RequestMapping("${datasourcePath}/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Get permissions
     */
    @GetMapping("/getAuth")
    public ResultJson getAuth(@RequestParam("id") String id) {
        return roleService.getAuth(id);
    }

    /**
     * Save permissions
     */
    @PostMapping("/saveAuth")
    public ResultJson saveAuth(@RequestParam("id") String id, @RequestParam("ids") String ids) {
        return roleService.saveAuth(id, ids);
    }

    /**
     * Get assigned roles
     */
    @GetMapping("/getAssign")
    public ResultJson getAssign(@RequestParam("id") String id) {
        return roleService.getAssign(id);
    }

    /**
     * Get assigned data roles
     */
    @GetMapping("/getDataAssign")
    public ResultJson getDataAssign(@RequestParam("id") String id) {
        return roleService.getDataAssign(id);
    }

    /**
     * Save assign roles
     */
    @PostMapping("/saveAssign")
    public ResultJson saveAssign(@RequestParam("id") String id, @RequestParam("ids") String ids) {
        return roleService.saveAssign(id, ids);
    }

    /**
     * Save assign roles by param
     */
    @PostMapping("/saveAssignByParam")
    public ResultJson saveAssignByParam(@RequestBody AssignParam assignParam) {
        return roleService.saveAssign(assignParam.getId(), assignParam.getIds());
    }

    /**
     * Save assign data roles by param
     */
    @PostMapping("/saveDataAssignByParam")
    public ResultJson saveDataAssignByParam(@RequestBody AssignParam assignParam) {
        return roleService.saveDataAssign(assignParam.getId(), assignParam.getIds());
    }
}
