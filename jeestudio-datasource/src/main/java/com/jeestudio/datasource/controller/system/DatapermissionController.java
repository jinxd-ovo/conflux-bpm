package com.jeestudio.datasource.controller.system;

import com.jeestudio.datasource.service.system.DatapermissionService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Datapermission Controller
 * @author: gaoqk
 * @Date: 2020-08-27
 */
@Api(value = "DatapermissionController ",tags = "Datapermission Controller")
@RestController
@RequestMapping("${datasourcePath}/datapermission")
public class DatapermissionController {

    @Autowired
    private DatapermissionService datapermissionService;

    /**
     * Get dataPermission tree
     */
    @ApiOperation(value = "/datapermissionTree",tags = "Get datapermission tree")
    @GetMapping("/datapermissionTree")
    public ResultJson getDatapermissionTree(){
        return datapermissionService.getDatapermissionTree();
    }

    /**
     * Get permissions
     */
    @GetMapping("/getPermission")
    public ResultJson getPermission(@RequestParam("id") String id) {
        return datapermissionService.getPermission(id);
    }

    /**
     * Save permissions
     */
    @PostMapping("/savePermission")
    public ResultJson savePermission(@RequestParam("id") String id, @RequestParam("ids") String ids) {
        return datapermissionService.savePermission(id, ids);
    }
}
