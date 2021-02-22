package com.jeestudio.services.admin.controller.app;

import com.google.common.collect.Maps;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: App Controller
 * @author: David
 * @Date: 2020-07-03
 */
@Api(value = "AppZformController ",tags = "AppZform Controller")
@RestController
@RequestMapping("${adminPath}/app/zform")
public class AppZformController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(AppZformController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get data by id
     */
    @ApiOperation(value = "get",tags = "Get data by id")
    @RequiresPermissions("user")
    @PostMapping("/get")
    public ResultJson get(@RequestParam("id") String id, @RequestParam("formNo") String formNo, @RequestParam("procDefKey") String procDefKey) {
        try {
            ResultJson resultJson = new ResultJson();
            Zform zform = null;
            if (StringUtil.isEmpty(procDefKey)) {
                zform = datasourceFeign.getZform(formNo, id, currentUserName);
            } else {
                zform = datasourceFeign.getZformWithAct(formNo, id, procDefKey, currentUserName);
            }
            resultJson.put("data", zform);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get data by id: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * List data
     */
    @ApiOperation(value = "data",tags = "List data")
    @RequiresPermissions("user")
    @PostMapping("/data/{formNo}")
    public ResultJson list(@PathVariable("formNo") String formNo, @RequestParam("path") String path, @RequestBody Zform zform, @RequestParam("length") String length) {
        try {
            ResultJson resultJson = new ResultJson();
            zform.setFormNo(formNo);
            int len = StringUtil.isBlank(length) ? 0 : Integer.parseInt(length);
            Page<Zform> page = datasourceFeign.findZformData(zform, path, currentUserName, "", "");
            List<Zform> rows = page.getList();
            LinkedHashMap<String, Object> datas = Maps.newLinkedHashMap();
            if ((len + 10) < rows.size()) {
                datas.put("rows", rows.subList(len, len + 10));
                datas.put("nomMore", false);
            } else if ((len + 10) == rows.size()) {
                datas.put("rows", rows.subList(len, len + 10));
                datas.put("nomMore", true);
            } else{
                datas.put("rows", rows.subList(len, rows.size()));
                datas.put("nomMore", true);
            }
            resultJson.setData(datas);
            resultJson.put("formNo", formNo);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to list data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save data
     */
    @ApiOperation(value = "save",tags = "Save data")
    @RequiresPermissions("user")
    @PostMapping("/save")
    public ResultJson save(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = datasourceFeign.saveZform(zform, currentUserName, "/dynamic/zform");
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Delete data
     */
    @ApiOperation(value = "delete",tags = "Delete data")
    @RequiresPermissions("user")
    @PostMapping("/delete")
    public ResultJson delete(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = datasourceFeign.deleteAllZform(zform.getFormNo(), zform.getId());
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete all Zform: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
