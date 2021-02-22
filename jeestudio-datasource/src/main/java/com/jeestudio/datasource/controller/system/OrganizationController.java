package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.param.AssignParam;
import com.jeestudio.datasource.service.system.OrganizationService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Organization Controller
 * @author: houxl
 * @Date: 2020-12-09
 */
@Api(value = "OrganizationController ",tags = "Organization Controller")
@RestController
@RequestMapping("${datasourcePath}/org")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * Get assigned users
     */
    @GetMapping("/getOrgAssign")
    public ResultJson getOrgAssign(@RequestParam("id") String id) {
        return organizationService.getAssign(id);
    }

    /**
     * Save assign users
     */
    @PostMapping("/saveOrgAssignByParam")
    public ResultJson saveOrgAssign(@RequestBody AssignParam assignParam) {
        return organizationService.saveAssign(assignParam.getId(), assignParam.getIds());
    }
}
