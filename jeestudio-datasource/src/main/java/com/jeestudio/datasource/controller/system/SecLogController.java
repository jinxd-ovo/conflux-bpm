package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.datasource.service.common.ZformService;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.service.system.SecLogService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: SecLog Controller
 * @author: David
 * @Date: 2020-06-18
 */
@Api(value = "SecLogController ",tags = "SecLog Controller")
@RestController
@RequestMapping("${datasourcePath}/secLog")
public class SecLogController {

    @Autowired
    private SecLogService secLogService;

    @Autowired
    protected GenTableService genTableService;

    @Autowired
    protected ZformService zformService;

    /**
     * Save secLog
     */
    @PostMapping("/saveSecLog")
    public void saveSecLog(@RequestParam("account") String account, @RequestParam("ip") String ip, @RequestParam("content") String content, @RequestParam("type") String type, @RequestParam("result") String result) {
        secLogService.saveSecLog(account, ip, content, type, result);
    }

    /**
     * Save secLog
     */
    @PostMapping("/saveSecLogZform")
    public void saveSecLogZform(@RequestParam("account") String account, @RequestParam("ip") String ip, @RequestParam("result") String result, @RequestParam("formNo") String formNo, @RequestParam("action") String action) {
        secLogService.saveSecLogZform(account, ip, result, formNo, action);
    }

    /**
     * Get secLog list
     */
    @ApiOperation(value = "/data", tags = "Get secLog list")
    @ApiImplicitParams({@ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "traceFlag", value = "traceFlag", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String"),
            @ApiImplicitParam(name = "secSwitch", value = "secSwitch", required = false, dataType = "Boolean")})
    @PostMapping("/data")
    public Page<Zform> data(@RequestBody Zform zform,
                            @RequestParam("path") String path,
                            @RequestParam("loginName") String loginName,
                            @RequestParam("traceFlag") String traceFlag,
                            @RequestParam("parentId") String parentId,
                            @RequestParam("secSwitch") Boolean secSwitch) throws Exception {
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        if (zform.getPageParam() == null) {
            zform.setPageParam(new PageParam());
        }
        if (secSwitch) {
            String dsf = null;
            if (loginName.startsWith("secadmin")) {
                dsf = " AND (a.account_ LIKE 'auditadmin%' OR (a.account_ NOT LIKE 'auditadmin%' AND a.account_ NOT LIKE 'secadmin%' AND a.account_ NOT LIKE 'sysadmin%' AND a.account_ != 'admin')) ";
            } else if (loginName.startsWith("auditadmin")) {
                dsf = " AND ((a.account_ LIKE 'sysadmin%' OR a.account_ LIKE 'secadmin%') AND a.account_ != 'admin') ";
            } else if ("admin".equals(loginName)) {
                dsf = " AND 1 = 1 ";
            } else {
                dsf = " AND 1 != 1 ";
            }
            zform.getSqlMap().put("dsf", dsf);
        }
        Page<Zform> page = zformService.findPage(new Page<Zform>(zform.getPageParam().getPageNo(), zform.getPageParam().getPageSize(), zform.getPageParam().getOrderBy()),
                zform,
                path,
                loginName,
                genTable,
                traceFlag,
                parentId);
        return page;
    }

    @GetMapping("/getSecLogSpace")
    public ResultJson getSecLogSpace() {
        return secLogService.getSecLogSpace();
    }
}
