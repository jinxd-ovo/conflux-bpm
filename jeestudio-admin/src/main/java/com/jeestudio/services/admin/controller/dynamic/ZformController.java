package com.jeestudio.services.admin.controller.dynamic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumn;
import com.jeestudio.common.entity.system.DictResult;
import com.jeestudio.common.param.GridselectParam;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.common.view.common.TreeView;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.services.admin.service.system.SecLogService;
import com.jeestudio.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description: Zform Controller
 * @author: David
 * @Date: 2020-01-23
 */
@Api(value = "ZformController ",tags = "Zform Controller")
@RestController
@RequestMapping("${adminPath}/dynamic/zform")
public class ZformController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(ZformController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    @Autowired
    private SecLogService secLogService;

    @Value("${fileRoot}")
    private String fileRoot;

    @RequiresPermissions("user")
    @PostMapping("/deleteProc")
    public ResultJson deleteProcess(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = datasourceFeign.deleteProc(zform);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete process: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get zform by id and formNo
     */
    @ApiOperation(value = "getZform",tags = "Get zform by id and formNo")
    @RequiresPermissions("user")
    @GetMapping("/getZform")
    public ResultJson getZform(@RequestParam("formNo") String formNo, @RequestParam("id") String id) {
        try {
            ResultJson resultJson = new ResultJson();
            Zform zform = datasourceFeign.getZform(formNo, id, currentUserName);
            resultJson.put("data", zform);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform by id and formNo: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get zform map by id and formNo
     */
    @ApiOperation(value = "getZformMap",tags = "Get zform map by id and formNo")
    @RequiresPermissions("user")
    @GetMapping("/getZformMap")
    public ResultJson getZformMap(@RequestParam("formNo") String formNo, @RequestParam("id") String id) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap zform = datasourceFeign.getZformMap(formNo, id, currentUserName);
            resultJson.put("data", zform);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform map by id and formNo: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get zform with act
     */
    @ApiOperation(value = "getZformWithAct",tags = "Get zform with act")
    @RequiresPermissions("user")
    @GetMapping("/getZformWithAct")
    public ResultJson getZformWithAct(@RequestParam("formNo") String formNo, @RequestParam("id") String id, @RequestParam("procDefKey") String procDefKey) {
        try {
            ResultJson resultJson = new ResultJson();
            Zform zform = datasourceFeign.getZformWithAct(formNo, id, procDefKey, currentUserName);
            resultJson.put("data", zform);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform with act: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get zform list
     * @param path
     * @param zform
     * @return zform list in Json format
     */
    @ApiOperation(value = "data",tags = "Get zform list")
    @RequiresPermissions("user")
    @PostMapping("/data")
    public ResultJson data(@RequestBody Zform zform, @RequestParam("path") String path, @RequestParam("formNo")  String formNo, @RequestParam("traceFlag")  String traceFlag) {
        try {
            ResultJson resultJson = new ResultJson();
            zform.setFormNo(formNo);
            Page<Zform> page = datasourceFeign.findZformData(zform, path, currentUserName, traceFlag, "");
            resultJson.setRows(page.getList());
            resultJson.setTotal(page.getCount());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            secLogService.saveSecLogZform(currentUserName, ip, Global.YES, formNo, secLogService.ACTION_QUERY);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform data: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLogZform(currentUserName, ip, Global.NO, formNo, secLogService.ACTION_QUERY);
            return failed();
        }
    }

    /**
     * Get zform map
     * @param path
     * @param zform
     * @return zform list in Json format
     */
    @ApiOperation(value = "datamap",tags = "Get zform map")
    @RequiresPermissions("user")
    @PostMapping("/datamap")
    public ResultJson datamap(@RequestBody Zform zform, @RequestParam("path") String path, @RequestParam("formNo")  String formNo, @RequestParam("traceFlag")  String traceFlag) {
        try {
            ResultJson resultJson = new ResultJson();
            zform.setFormNo(formNo);
            Page<Zform> page = datasourceFeign.findZformDataMap(zform, path, currentUserName, traceFlag, "");
            resultJson.setRows(page.getMap());
            resultJson.setTotal(page.getCount());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            secLogService.saveSecLogZform(currentUserName, ip, Global.YES, formNo, secLogService.ACTION_QUERY);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform map data: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLogZform(currentUserName, ip, Global.NO, formNo, secLogService.ACTION_QUERY);
            return failed();
        }
    }

    /**
     * Get zform list data children
     */
    @ApiOperation(value = "dataChildren",tags = "Get zform list data children")
    @RequiresPermissions("user")
    @PostMapping("/dataChildren")
    public ResultJson dataChildren(@RequestBody Zform zform, @RequestParam("path") String path, @RequestParam("formNo")  String formNo, @RequestParam("traceFlag")  String traceFlag, @RequestParam("parentId")  String parentId) {
        try {
            ResultJson resultJson = new ResultJson();
            zform.setFormNo(formNo);
            Page<Zform> page = new Page<Zform>();
            if (StringUtil.isNotEmpty(parentId)) {
                page = datasourceFeign.findZformData(zform, path, currentUserName, traceFlag, parentId);
            }
            resultJson.setRows(page.getList());
            resultJson.setTotal(page.getCount());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform children data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get gridselect data
     */
    @ApiOperation(value = "gridselectData",tags = "Get gridselect data")
    @RequiresPermissions("user")
    @PostMapping("/gridselectData")
    public ResultJson gridselectData(@RequestBody GridselectParam gridselectParam) {
        try {
            ResultJson resultJson = datasourceFeign.findGridselectData(gridselectParam);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while try to get gridselect data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get zform tree data
     */
    @ApiOperation(value = "treeData",tags = "Get zform tree data")
    @RequiresPermissions("user")
    @PostMapping("/treeData")
    public ResultJson treeData(@RequestParam("parentId") String parentId, @RequestParam("formNo")  String formNo, @RequestParam("traceFlag")  String traceFlag) {
        try {
            ResultJson resultJson = new ResultJson();
            Zform zform = new Zform();
            zform.setParent(new Zform(parentId, formNo));
            zform.setFormNo(formNo);
            Page<Zform> page = datasourceFeign.findZformData(zform, "", currentUserName, traceFlag, "");
            List<TreeView> treeViewList = Lists.newArrayList();
            for(Zform theZform : page.getList()) {
                TreeView treeView = new TreeView();
                treeView.setId(theZform.getId());
                treeView.setName(theZform.getS01());
                treeView.setName_EN(theZform.getS50());
                treeView.setParentId(theZform.getParent() != null ? theZform.getParent().getId() : "0");
                treeView.setSort(theZform.getSort() != null ? theZform.getSort() : 0);
                treeView.setHasChildren(theZform.isHasChildren());
                treeViewList.add(treeView);
            }
            resultJson.put("data", treeViewList);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform tree data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get zform list data count
     */
    @ApiOperation(value = "dataCount",tags = "Get zform list data count")
    @RequiresPermissions("user")
    @PostMapping("/dataCount")
    public ResultJson dataCount(@RequestBody Zform zform, @RequestParam("path") String path, @RequestParam("formNo")  String formNo) {
        try {
            ResultJson resultJson = new ResultJson();
            zform.setFormNo(formNo);
            resultJson.put("data", datasourceFeign.findZformDataCount(zform, path, currentUserName));
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform list data count: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save zform
     */
    @ApiOperation(value = "save",tags = "Save zform")
    @RequiresPermissions("user")
    @PostMapping("/save")
    public ResultJson save(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = datasourceFeign.saveZform(zform, currentUserName, "/dynamic/zform");
            resultJson.setToken(token);
            secLogService.saveSecLogZform(currentUserName, ip, Global.YES, zform.getFormNo(), secLogService.ACTION_SAVE);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save zform: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLogZform(currentUserName, ip, Global.NO, zform.getFormNo(), secLogService.ACTION_SAVE);
            return failed();
        }
    }

    /**
     * Save zform map
     */
    @ApiOperation(value = "savemap",tags = "Save zform map")
    @RequiresPermissions("user")
    @PostMapping("/savemap")
    public ResultJson savemap(@RequestBody JSONObject zformMap) {
        Zform zform = null;
        String formNo = "";
        try {
            zform = this.getZformFromMap(zformMap);
            formNo = zform.getFormNo();
            if (StringUtil.isNotEmpty(formNo)) {
                ResultJson resultJson = datasourceFeign.saveZform(zform, currentUserName, "/dynamic/zform");
                resultJson.setToken(token);
                secLogService.saveSecLogZform(currentUserName, ip, Global.YES, zform.getFormNo(), secLogService.ACTION_SAVE);
                return resultJson;
            } else {
                return failed();
            }
        } catch (Exception e) {
            logger.error("Error occurred while trying to save zform: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLogZform(currentUserName, ip, Global.NO, zform.getFormNo(), secLogService.ACTION_SAVE);
            return failed();
        }
    }

    private Zform getZformFromMap(JSONObject zformMap) throws IllegalAccessException {
        Zform zform = new Zform();
        String formNo = null;
        Set<String> it = zformMap.keySet();
        if (it.contains("formNo")) {
            formNo = zformMap.getString("formNo");
            zform.setFormNo(formNo);
            GenTable genTable = datasourceFeign.getGenTableByFormNo(formNo);
            String value = null;
            for(GenTableColumn column : genTable.getColumnList()) {
                if (it.contains(column.getName())) {
                    value = zformMap.getString(column.getName());
                    if (value != null) {
                        String javaField = column.getJavaField();
                        if (StringUtil.contains(javaField, "|")){
                            javaField = javaField.substring(0, javaField.indexOf("."));
                        }
                        Field field = Reflections.getThisField(zform.getClass(), javaField);
                        if (field != null) {
                            field.setAccessible(true);
                            field.set(zform, value);
                        }
                    }
                }
            }
        }
        return zform;
    }

    /**
     * Save zform before saving children
     */
    @ApiOperation(value = "beforeSave",tags = "Save zform before saving children")
    @RequiresPermissions("user")
    @PostMapping("/beforeSave")
    public ResultJson beforeSave(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = datasourceFeign.beforeSaveZform(zform, currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save zform before saving children: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Delete zform
     */
    @ApiOperation(value = "delete",tags = "Delete zform")
    @RequiresPermissions("user")
    @PostMapping("/delete")
    public ResultJson delete(@RequestParam("formNo")  String formNo, @RequestParam("ids")  String ids) {
        try {
            ResultJson resultJson = datasourceFeign.deleteAllZform(formNo, ids);
            resultJson.setToken(token);
            secLogService.saveSecLogZform(currentUserName, ip, Global.YES, formNo, secLogService.ACTION_DELETE);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete zform: " + ExceptionUtils.getStackTrace(e));
            secLogService.saveSecLogZform(currentUserName, ip, Global.NO, formNo, secLogService.ACTION_DELETE);
            return failed();
        }
    }

    /**
     * Get user list
     */
    @ApiOperation(value = "getUserList",tags = "Get user list")
    @RequiresPermissions("user")
    @PostMapping("/getUserList")
    public ResultJson getUserList(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> targetUserInfo = datasourceFeign.getUserListZform(zform, currentUserName);
            resultJson.setData(targetUserInfo);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get user list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get node list
     */
    @ApiOperation(value = "getNodeList",tags = "Get node list")
    @RequiresPermissions("user")
    @PostMapping("/getNodeList")
    public ResultJson getNodeList(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> targetNodeInfo = datasourceFeign.getNodeListZform(zform, currentUserName);
            resultJson.setData(targetNodeInfo);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get node list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get current user
     * @return
     */
    @ApiOperation(value = "getCurrentUser",tags = "Get current user")
    @RequiresPermissions("user")
    @GetMapping("/getCurrentUser")
    public ResultJson getCurrentUser() {
        try {
            ResultJson resultJson = new ResultJson();
            String userName = datasourceFeign.getUserNameByLoginName(currentUserName);
            resultJson.put("data", userName);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get current user: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get rule variables with zform
     * @param zform
     * @param procDefKey
     * @return
     */
    @ApiOperation(value = "getRuleArgs",tags = "Get rule variables with zform")
    @RequiresPermissions("user")
    @PostMapping("/getRuleArgs")
    public ResultJson getRuleArgs(@RequestBody Zform zform, @RequestParam("procDefKey")  String procDefKey) {
        try {
            logger.info("formNo:" + zform.getFormNo() + ", procDefKey:" + procDefKey);
            ResultJson resultJson = new ResultJson();
            Zform t = datasourceFeign.getRuleArgsZform(zform, procDefKey, currentUserName);
            resultJson.put("data", t);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get rule variables by zform: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Create a node
     */
    @ApiOperation(value = "createNode",tags = "Create a node")
    @RequiresPermissions("user")
    @PostMapping("/createNode")
    public ResultJson createNode(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.createNodeZform(zform, currentUserName);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to create a node: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Delete a node
     */
    @ApiOperation(value = "deleteNode",tags = "Delete a node")
    @RequiresPermissions("user")
    @PostMapping("/deleteNode")
    public ResultJson deleteNode(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.deleteNodeZform(zform, currentUserName);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete a node: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get back tasks
     */
    @ApiOperation(value = "backward",tags = "Get back tasks")
    @RequiresPermissions("user")
    @PostMapping("/backward")
    public ResultJson backward(@RequestParam("formNo")  String formNo, @RequestParam("ids")  String ids) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.backwardZform(formNo, ids, currentUserName);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get backward tasks: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Revoke tasks
     */
    @ApiOperation(value = "revoke",tags = "Revoke tasks")
    @RequiresPermissions("user")
    @PostMapping("/revoke")
    public ResultJson revoke(@RequestParam("formNo")  String formNo, @RequestParam("ids")  String ids) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.revokeZform(formNo, ids);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to revoke tasks: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Create a notify node
     */
    @ApiOperation(value = "notifyNode",tags = "Create a notify node")
    @RequiresPermissions("user")
    @PostMapping("/notifyNode")
    public ResultJson notifyNode(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.notifyNodeZform(zform, currentUserName);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to create a notify node: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Create a distribute node
     */
    @ApiOperation(value = "distributeNode",tags = "Create a distribute node")
    @RequiresPermissions("user")
    @PostMapping("/distributeNode")
    public ResultJson distributeNode(@RequestBody Zform zform) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.distributeNodeZform(zform, currentUserName);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to create a distribute node: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Check whether a node could be fallback or not
     */
    @ApiOperation(value = "rollBackCheck",tags = "Check whether a node could be fallback or not")
    @RequiresPermissions("user")
    @GetMapping("/rollBackCheck")
    public ResultJson rollBackCheck(@RequestParam("procInsId")  String procInsId) {
        try {
            ResultJson resultJson = new ResultJson();
            LinkedHashMap<String, Object> map = datasourceFeign.rollBackCheckZform(procInsId, currentUserName);
            resultJson.setData(map);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to check whether a node could rollback: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get process definition list
     */
    @ApiOperation(value = "getProcDefList",tags = "Get process definition list")
    @RequiresPermissions("user")
    @GetMapping("/getProcDefList")
    public ResultJson getProcDefList(@RequestParam(value = "formNo") String formNo) {
        try {
            ResultJson resultJson  = datasourceFeign.getProcDefList(formNo, currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get process definition list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Is cache view
     */
    @ApiOperation(value = "isCacheView",tags = "Is cache view")
    @RequiresPermissions("user")
    @GetMapping("/isCacheView")
    public ResultJson isCacheView(@RequestParam(value = "processInstanceId") String processInstanceId) {
        try {
            ResultJson resultJson  = datasourceFeign.isCacheView(processInstanceId, currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get cache view: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Is cache data
     */
    @ApiOperation(value = "isCacheData",tags = "Is cache data")
    @RequiresPermissions("user")
    @GetMapping("/isCacheData")
    public ResultJson isCacheData(@RequestParam(value = "processInstanceId") String processInstanceId) {
        try {
            ResultJson resultJson  = datasourceFeign.isCacheData(processInstanceId, currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get cache data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get trace photo
     */
    @ApiOperation(value = "tracePhoto",tags = "Get trace photo")
    @RequiresPermissions("user")
    @GetMapping("/tracePhoto")
    public ResultJson tracePhoto(@RequestParam(value = "procDefId") String procDefId, @RequestParam(value = "procInsId") String procInsId) {
        try {
            ResultJson resultJson  = datasourceFeign.tracePhoto(procDefId, procInsId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get trace photo: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get history flow
     */
    @ApiOperation(value = "histoicFlow",tags = "Get history flow")
    @RequiresPermissions("user")
    @GetMapping("/histoicFlow")
    public ResultJson histoicFlow(@RequestParam(value = "procInsId") String procInsId) {
        try {
            ResultJson resultJson  = datasourceFeign.histoicFlow(procInsId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get history flow: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get task list
     */
    @ApiOperation(value = "getTaskList",tags = "Get task list")
    @RequiresPermissions("user")
    @PostMapping("/getTaskList")
    public ResultJson getTaskList(@RequestBody Zform zform,
                                           @RequestParam("path") String path) {
        try {
            String loginName = currentUserName;
            ResultJson resultJson = new ResultJson();
            Map<String, Object> taskMap = datasourceFeign.getTaskList(zform, path, loginName);
            if(taskMap != null){
                resultJson.setRows(taskMap.get("rows"));
                resultJson.setTotal(taskMap.get("total"));
            }else{
                resultJson.setRows(Lists.newArrayList());
                resultJson.setTotal("0");
            }
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get task list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get work data
     */
    @ApiOperation(value = "getWorkData",tags = "Get work data")
    @RequiresPermissions("user")
    @PostMapping("/getWorkData")
    public ResultJson getWorkData() {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = new GenTable();
            genTable.setIsMobile(Global.YES);
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(1);
            pageParam.setPageSize(Integer.MAX_VALUE);
            genTable.setPageParam(pageParam);
            Page<GenTable> page = datasourceFeign.findPage(genTable);
            List<GenTable> list = page.getList();
            JSONArray array = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                GenTable gen = list.get(i);
                JSONObject obj = new JSONObject();
                obj.put("id", gen.getName());
                obj.put("name", gen.getComments());
                obj.put("func", gen.getName() + "/list.html");
                obj.put("icon", gen.getMobileIcon());
                obj.put("choose", false);
                obj.put("index", 0);
                obj.put("url", "");
                array.add(obj);
            }
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.put("data", array);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get work data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Export genTable to Excel
     */
    @ApiOperation(value = "expdata",tags = "Export genTable to Excel")
    //@RequiresPermissions("user")
    @ApiImplicitParams({@ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
    @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
    @ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
    @ApiImplicitParam(name = "traceFlag", value = "traceFlag", required = false, dataType = "String"),
    @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String")})
    @GetMapping("/expdata")
    public ResponseEntity<Resource> expdata(@RequestParam("zformString") String zformString,
                                            @RequestParam("formNo") String formNo,
                                             @RequestParam("path") String path,
                                             @RequestParam("traceFlag") String traceFlag,
                                             @RequestParam("parentId") String parentId) throws IOException {
        zformString = Encodes.urlDecode(zformString);
        Zform zform = JsonConvertUtil.gsonBuilder().fromJson(zformString, new TypeToken<Zform>(){}.getType());
        zform.setFormNo(formNo);
        GenTable genTable = datasourceFeign.getGenTableByFormNo(formNo);
        String filename = genTable.getName() + "_export.xlsx";

        List<List<LinkedHashMap<String, String>>> listResult;
        listResult = datasourceFeign.expdata(zform, path, currentUserName, traceFlag, parentId);

        String exportTemplatePath = datasourceFeign.getExportFilePathByFormNo(formNo, fileRoot);
        ByteArrayInputStream in = ExcelUtil.LinkedHashListToExcel(listResult,1, exportTemplatePath);
        InputStreamResource file = new InputStreamResource(in);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    /**
     * Parse Excel file and insert into genTable from line 2
     * @return List<LinkedHashMap<String, String>>
     */
    @ApiOperation(value = "impExcelIntoGenTable",tags = "Export genTable to Excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formNo", value = "formNo", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentFormNo", value = "parentFormNo", required = false, dataType = "String"),
            @ApiImplicitParam(name = "uniqueId", value = "uniqueId", required = false, dataType = "String"),
            @ApiImplicitParam(name = "toCompany", value = "toCompany", required = false, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String")})
    @PostMapping("/impdata")
    public ResultJson importExcel2Table(@RequestParam("formNo") String formNo,
                                        @RequestParam("parentFormNo") String parentFormNo,
                                        @RequestParam("uniqueId") String uniqueId,
                                        @RequestParam("toCompany") String toCompany,
                                        @RequestParam("fileId") String fileId,
                                        @RequestParam("loginName") String loginName,
                                        @RequestParam("parentId") String parentId) {
        return datasourceFeign.impdata(formNo, parentFormNo, toCompany, parentId,fileId,this.fileRoot,uniqueId, loginName);
    }

    /**
     * Get dict list from table
     */
    @ApiOperation(value = "dictListFromTable",tags = "Get dict list from table")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo",required = true,dataType = "String"),
            @ApiImplicitParam(name = "propertyName", value = "propertyName",required = true,dataType = "String"),
            @ApiImplicitParam(name = "orderBy", value = "orderBy",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/dictListFromTable")
    public String dictList(@RequestParam("formNo") String formNo, @RequestParam("propertyName") String propertyName, @RequestParam("orderBy") String orderBy){
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(0);
            resultJson.setMsg("获取字典成功");
            resultJson.setMsg_en("Got dictionary from table successfully");
            resultJson.setToken(token);
            List<DictResult> list = Lists.newArrayList();
            Zform zform = new Zform();
            zform.setFormNo(formNo);
            PageParam pageParam = new PageParam();
            pageParam.setPageSize(Integer.MAX_VALUE);
            if (StringUtil.isNotEmpty(orderBy)) {
                pageParam.setOrderBy(orderBy);
            } else {
                pageParam.setOrderBy("a.update_date desc");
            }
            zform.setPageParam(pageParam);
            Page<Zform> page = datasourceFeign.findZformData(zform, "path", currentUserName, "", "");
            String meberName;
            int i = 0;
            for(Zform obj : page.getList()) {
                DictResult dict = new DictResult();
                dict.setType(formNo);
                dict.setDictionaryID(obj.getId());
                dict.setHasChildren(false);
                dict.setMember(obj.getId());
                if (propertyName.equalsIgnoreCase("s01")) {
                    meberName = obj.getS01();
                } else if (propertyName.equalsIgnoreCase("s02")) {
                    meberName = obj.getS02();
                } else if (propertyName.equalsIgnoreCase("s03")) {
                    meberName = obj.getS03();
                } else if (propertyName.equalsIgnoreCase("s04")) {
                    meberName = obj.getS04();
                } else if (propertyName.equalsIgnoreCase("s05")) {
                    meberName = obj.getS05();
                } else {
                    break;
                }
                dict.setMemberName(meberName);
                dict.setMemberNameEn(meberName);
                dict.setSort(i++);
                list.add(dict);
            }
            resultJson.put("dict", list);
            return JsonConvertUtil.objectToJson(resultJson);
        } catch (Exception e){
            logger.error("Error occurred while trying to get dictionary from table list: " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Get hash value by key
     */
    @ApiOperation(value = "/getHashByKey", tags = "Get hash value by key")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String")})
    @GetMapping("/getHashByKey")
    public ResultJson getHashByKey(@RequestParam("key") String key) {
        try {
            ResultJson resultJson = new ResultJson();
            String hashValue = datasourceFeign.getHashByKey(key);
            resultJson.put("data", hashValue);
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get hash by key: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
