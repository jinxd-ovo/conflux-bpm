package com.jeestudio.datasource.controller.dynamic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.common.entity.act.Act;
import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.param.GridselectParam;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.datasource.service.common.ZformService;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.service.system.UserService;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.datasource.service.system.SysFileService;
import com.jeestudio.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Description: Zform Controller
 * @author: David
 * @Date: 2020-01-18
 */
@Api(value = "ZformController",tags = "Zform Controller")
@RestController
@RequestMapping("${datasourcePath}/zform")
public class ZformController {

    protected static final Logger logger = LoggerFactory.getLogger(ZformController.class);

    @Value("${oaSecLevelSwitch}")
    protected Boolean oaSecLevelSwitch;

    @Autowired
    protected ZformService zformService;

    @Autowired
    protected GenTableService genTableService;

    @Autowired
    protected UserService userService;

    @Autowired
    private SysFileService sysFileService;

    @PostMapping("/deleteProc")
    public ResultJson deleteProcess(@RequestBody Zform zform) {
        return zformService.deleteProc(zform);
    }

    /**
     * Get zform by id and formNo
     */
    @ApiOperation(value = "/getZform", tags = "Get zform by id and formNo")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getZform")
    public Zform getZform(@RequestParam("formNo") String formNo,
                          @RequestParam("id") String id,
                          @RequestParam("loginName") String loginName) throws Exception {
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        return zformService.get(id, genTable);
    }

    /**
     * Get zform map by id and formNo
     */
    @ApiOperation(value = "/getZformMap", tags = "Get zform map by id and formNo")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getZformMap")
    public LinkedHashMap getZformMap(@RequestParam("formNo") String formNo,
                                     @RequestParam("id") String id,
                                     @RequestParam("loginName") String loginName) throws Exception {
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        return zformService.getMap(id, genTable);
    }

    /**
     * Get zform with act
     */
    @ApiOperation(value = "/getZformWithAct", tags = "Get zform by id and formNo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "procDefKey", value = "procDefKey", required = false, dataType = "String")})
    @GetMapping("/getZformWithAct")
    public Zform getZformWithAct(@RequestParam("formNo") String formNo,
                                 @RequestParam("id") String id,
                                 @RequestParam("procDefKey") String procDefKey,
                                 @RequestParam("loginName") String loginName) throws Exception {
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        Zform zform = null;
        if (StringUtil.isEmpty(id)) {
            zform = new Zform();
            zform.setFormNo(formNo);
            zform.setProcDefKey(procDefKey);
        } else {
            zform = zformService.get(id, genTable);
        }
        //Act form
        if (false == StringUtil.isEmpty(genTable.getProcessDefinitionCategory())) {
            if (StringUtil.isBlank(zform.getProcInsId())) {
                procDefKey = zform.getProcDefKey().replaceAll("'", "");
                ProcessDefinition processDefinition = zformService.getProcessDefinition(procDefKey);
                zform.getAct().setProcDefId(processDefinition.getId());
                zform.getAct().setTaskDefKey(processDefinition.getDescription().split(",")[1]);
            } else {
                zformService.setAct(zform, loginName);
            }
            zformService.setRuleArgs(zform, loginName);
        }
        return zform;
    }

    /**
     * Get zform list
     *
     * @param path
     * @param zform
     * @param loginName
     * @return zform list
     */
    @ApiOperation(value = "/data", tags = "Get zform list JSON data")
    @ApiImplicitParams({@ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "traceFlag", value = "traceFlag", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String")})
    @PostMapping("/data")
    public Page<Zform> data(@RequestBody Zform zform,
                            @RequestParam("path") String path,
                            @RequestParam("loginName") String loginName,
                            @RequestParam("traceFlag") String traceFlag,
                            @RequestParam("parentId") String parentId) throws Exception {
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        if (zform.getPageParam() == null) {
            zform.setPageParam(new PageParam());
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

    /**
     * Get zform map
     *
     * @param path
     * @param zform
     * @param loginName
     * @return zform map
     */
    @ApiOperation(value = "/datamap", tags = "Get zform list JSON data")
    @ApiImplicitParams({@ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "traceFlag", value = "traceFlag", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String")})
    @PostMapping("/datamap")
    public Page<Zform> datamap(@RequestBody Zform zform,
                            @RequestParam("path") String path,
                            @RequestParam("loginName") String loginName,
                            @RequestParam("traceFlag") String traceFlag,
                            @RequestParam("parentId") String parentId) throws Exception {
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        if (zform.getPageParam() == null) {
            zform.setPageParam(new PageParam());
        }
        Page<Zform> page = zformService.findPageMap(new Page<Zform>(zform.getPageParam().getPageNo(), zform.getPageParam().getPageSize(), zform.getPageParam().getOrderBy()),
                zform,
                path,
                loginName,
                genTable,
                traceFlag,
                parentId);
        return page;
    }

    /**
     * Get zform list
     *
     * @param path
     * @param zform
     * @param loginName
     * @return zform list
     */
    @ApiOperation(value = "/expdata", tags = "Export list data to excel")
    @ApiImplicitParams({@ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "traceFlag", value = "traceFlag", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String")})
    @PostMapping("/expdata")
    public List<List<LinkedHashMap<String, String>>> expdata(@RequestBody Zform zform,
                            @RequestParam("path") String path,
                            @RequestParam("loginName") String loginName,
                            @RequestParam("traceFlag") String traceFlag,
                            @RequestParam("parentId") String parentId) throws Exception {

        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());

        if (zform.getPageParam() == null) {
            zform.setPageParam(new PageParam());
        }

        Page<Zform> page = zformService.findPage(new Page<Zform>(
                zform.getPageParam().getPageNo(),
                Integer.MAX_VALUE,
                zform.getPageParam().getOrderBy()),
                zform,
                path,
                loginName,
                genTable,
                traceFlag,
                parentId);
        List<List<LinkedHashMap<String, String>>> listResult;

        listResult = zformService.exportData(page, zform, genTable, loginName);

        return listResult;

    }

    @ApiOperation(value = "/impdata", tags = "Retrieve excel data into zform")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formNo", value = "formNo", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentFormNo", value = "parentFormNo", required = false, dataType = "String"),
            @ApiImplicitParam(name = "toCompany", value = "toCompany", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "parentId", required = false, dataType = "String"),
            @ApiImplicitParam(name = "fileId", value = "fileId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileRoot", value = "fileRoot", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")
    })
    @PostMapping("/impdata")
    public ResultJson impdata(
                       @RequestParam("formNo") String formNo,
                       @RequestParam("parentFormNo") String parentFormNo,
                       @RequestParam("toCompany") String toCompany,
                       @RequestParam("parentId") String parentId,
                       @RequestParam("fileId") String fileId,
                       @RequestParam("fileRoot") String fileRoot,
                       @RequestParam("uniqueId") String uniqueId,
                       @RequestParam("loginName") String loginName
                       ){
        ResultJson resultJson = new ResultJson();
        File file = sysFileService.getFirstByGroupId(fileId, fileRoot);

        if (file.exists()) {
            try {
                int passRowNumber = 2;
                if (StringUtil.isNotEmpty(formNo)) {
                    passRowNumber = 0;
                }
                GenTable genTable = genTableService.getGenTableWithDefination(formNo);
                String columns = genTable.getImportList();
                LinkedHashMap<String, List<LinkedHashMap<String, String>>> listResult;
                listResult = ExcelUtil.excelToLinkedHashList(new DataInputStream(new FileInputStream(file)),
                        passRowNumber,
                        formNo,
                        parentFormNo,
                        uniqueId,
                        columns);

                String keyString = listResult.entrySet().iterator().next().getKey();

                User currentUser = UserUtil.getByLoginName(loginName);
                String ownerCode = currentUser.getOffice().getCode();
                if (StringUtil.isNotEmpty(toCompany)) {
                    ownerCode = currentUser.getCompany().getCode();
                }
                columns = columns.replaceAll(",", "!");
                zformService.importData(ownerCode, columns, formNo, parentFormNo, parentId, uniqueId, listResult.entrySet().iterator().next().getValue(), currentUser);

                resultJson.setMsg("成功导入" + listResult.get(keyString).size() + "条数据");
                resultJson.setMsg_en("Import data success");
                resultJson.setCode(ResultJson.CODE_SUCCESS);
            } catch (Exception e) {
                resultJson.setMsg("导入数据错误：" + e.getMessage());
                resultJson.setMsg_en("Import data error:" + e.getMessage());
                resultJson.setCode(ResultJson.CODE_FAILED);
            }
        }
        else {
            resultJson.setMsg("读取文件错误");
            resultJson.setMsg_en("Reading file error");
            resultJson.setCode(ResultJson.CODE_FAILED);
        }
        return resultJson;
    }

    /**
     * Get task list
     */
    @ApiOperation(value = "/getTaskList", tags = "Get task list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/getTaskList")
    public Map<String, Object> getTaskList(@RequestBody Zform zform,
                                           @RequestParam("path") String path,
                                           @RequestParam("loginName") String loginName) {
        List<String> categoryList = Lists.newArrayList();
        String parentid = zform.getParent().getId();
        Zform temp = new Zform();
        temp.setFormNo("oa_process_tree_setting");
        GenTable genTable = genTableService.getGenTableWithDefination("oa_process_tree_setting");
        List<Zform> zformList = zformService.findList(temp, genTable);
        if (parentid != "") {
            for (int i = 0; i < zformList.size(); i++) {
                if (zformList.get(i).getId().equals(parentid) || zformList.get(i).getParentIds().contains(parentid)) {
                    if (zformList.get(i).getS02() != null && !"".equals(zformList.get(i).getS02())) {
                        categoryList.add(zformList.get(i).getS02());
                    }
                }
            }
        } else {
            for (int i = 0; i < zformList.size(); i++) {
                if (zformList.get(i).getS02() != null && !"".equals(zformList.get(i).getS02())) {
                    categoryList.add(zformList.get(i).getS02());
                }
            }
        }
        if (zform.getPageParam() == null) {
            zform.setPageParam(new PageParam());
            zform.getPageParam().setParamMap(Maps.newHashMap());
        }
        Map<String, Object> taskMap = zformService.getTaskList(categoryList,
                path,
                loginName,
                zform.getPageParam().getPageNo(),
                zform.getPageParam().getPageSize(),
                zform.getPageParam().getParamMap());
        return taskMap;
    }

    /**
     * Get gridselect data
     */
    @ApiOperation(value = "/gridselectData", tags = "Get gridselect data")
    @PostMapping("/gridselectData")
    public ResultJson gridselectData(@RequestBody GridselectParam gridselectParam) {
        return zformService.gridselectData(gridselectParam);
    }

    /**
     * Get zform list data count
     */
    @ApiOperation(value = "/dataCount", tags = "Get zform list data count")
    @ApiImplicitParams({@ApiImplicitParam(name = "path", value = "path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/dataCount")
    public String dataCount(@RequestBody Zform zform,
                            @RequestParam("path") String path,
                            @RequestParam("loginName") String loginName) {
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        if (zform.getPageParam() == null) {
            zform.setPageParam(new PageParam());
        }
        String countValue = zformService.findCount(new Page<Zform>(zform.getPageParam().getPageNo(), zform.getPageParam().getPageSize(), zform.getPageParam().getOrderBy()),
                zform,
                path,
                loginName,
                genTable);
        return countValue;
    }

    /**
     * Special logic here
     */
    private void preSave(Zform zform, String loginName, String businessKey) {
        if ("prt_information".equals(zform.getFormNo()) && "20".equals(zform.getStatus())) {
            Date theDate = new Date();
            zform.setS10(loginName);
            zform.setS11(loginName);
            zform.setD02(theDate);
            zform.setD03(theDate);
        }
    }

    /**
     * Special logic here
     */
    private void preSave(Zform zform, Zform t, String loginName, String businessKey) {
        if ("prt_information".equals(zform.getFormNo()) && "20".equals(zform.getStatus())) {
            Date theDate = new Date();
            t.setS10(loginName);
            t.setS11(loginName);
            t.setD02(theDate);
            t.setD03(theDate);
        }
    }

    /**
     * Save zform
     */
    @ApiOperation(value = "/save", tags = "Save zform")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String"),
            @ApiImplicitParam(name = "businessKey", value = "businessKey", required = true, dataType = "String")})
    @PostMapping("/save")
    public ResultJson save(@RequestBody Zform zform, @RequestParam("loginName") String loginName, @RequestParam("businessKey") String businessKey) {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
            //Start process, deploy model
            User currentUser = UserUtil.getByLoginName(loginName);
            zform.setTempRuleArgsClass(TaskPermission.class.getSimpleName());
            if (false == zform.getIsNewRecord()) {
                //Update
                Zform t = zformService.get(zform.getId(), genTable);
                BeanUtil.copyBeanNotNull2Bean(zform, t);
                t.setUpdateBy(currentUser);
                this.preSave(zform, t, loginName, businessKey);
                zformService.saveAct(businessKey, t, loginName, genTable);
                resultJson.put("data", t.getParentIds());
            } else {
                //Insert
                zform.setCreateBy(currentUser);
                zform.setUpdateBy(currentUser);
                zform.setOwnerCode(currentUser.getOffice().getCode());
                this.preSave(zform, loginName, businessKey);
                zformService.saveAct(businessKey, zform, loginName, genTable);
                resultJson.put("data", zform.getParentIds());
            }
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("保存成功");
            resultJson.setMsg_en("Save success");
        } catch (Exception e) {
            logger.info("Zform name:" + zform.getName());
            logger.error("Save zform error:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("保存失败");
            resultJson.setMsg_en("Save failed");
        }
        return resultJson;
    }

    /**
     * Save zform before saving children
     */
    @ApiOperation(value = "/beforeSave", tags = "Save zform before saving children")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/beforeSave")
    public ResultJson beforeSave(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
            if (false == zform.getIsNewRecord()) {
                resultJson.setCode(ResultJson.CODE_FAILED);
                resultJson.setMsg("操作异常");
                resultJson.setMsg_en("Unexpected error");
            } else {
                //Insert
                User currentUser = UserUtil.getByLoginName(loginName);
                zform.setOwnerCode(currentUser.getCompany().getCode());
                zform.setCreateBy(currentUser);
                zformService.beforeSave(zform, genTable);
                resultJson.setCode(ResultJson.CODE_SUCCESS);
                resultJson.setInsertedId(zform.getId());
            }
        } catch (Exception e) {
            logger.info("Zform name:" + zform.getName());
            logger.error("Error while doing beforeSave zform:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("保存失败");
            resultJson.setMsg_en("Save failed");
        }
        return resultJson;
    }

    /**
     * Delete zform
     */
    @ApiOperation(value = "/delete", tags = "Delete zform by ID")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String")})
    @PostMapping("/delete")
    public ResultJson delete(@RequestParam("formNo") String formNo, @RequestParam("id") String id) {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            Zform zform = zformService.get(id, genTable);
            zformService.delete(zform);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("删除成功");
            resultJson.setMsg_en("Delete success");
        } catch (Exception e) {
            logger.error("Error while deleting zform:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("删除失败");
            resultJson.setMsg_en("Delete failed");
        }
        return resultJson;
    }

    /**
     * Delete zform by ids
     *
     * @param ids
     * @param formNo
     * @return result message
     */
    @ApiOperation(value = "/deleteAll", tags = "Delete zform by ids")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "String")})
    @PostMapping("/deleteAll")
    public ResultJson deleteAll(@RequestParam("formNo") String formNo, @RequestParam("ids") String ids) {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            zformService.deleteAll(ids, formNo, genTable);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("删除成功");
            resultJson.setMsg_en("Delete success");
        } catch (Exception e) {
            logger.error("Error while deleting zform:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("删除失败");
            resultJson.setMsg_en("Delete failed");
        }
        return resultJson;
    }

    /**
     * Get user list
     */
    @ApiOperation(value = "/getUserList", tags = "Get user list")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/getUserList")
    public LinkedHashMap<String, Object> getUserList(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        LinkedHashMap<String, Object> targetUserInfo = zformService.getTargetUserList(zform, loginName);
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        List<User> userList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(zform.getSecLevel()) && this.oaSecLevelSwitch) {
            List<User> unfilterUserList = (List<User>) targetUserInfo.get("userList");
            if (unfilterUserList != null && unfilterUserList.size() > 0) {
                for (User user : unfilterUserList) {
                    if (StringUtil.isNotEmpty(user.getSecLevel())  && zform.getSecLevel().compareTo(user.getSecLevel()) <= 0) {
                        userList.add(user);
                    }
                }
            }
            targetUserInfo.put("userList", userList);
        } else {
            //do not need filter
        }
        return targetUserInfo;
    }

    /**
     * Get node list
     *
     * @param zform
     * @param loginName
     * @return node list
     */
    @ApiOperation(value = "/getNodeList", tags = "Get node list")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/getNodeList")
    public LinkedHashMap<String, Object> getNodeList(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        zform.setTempRuleArgsClass(TaskPermission.class.getSimpleName());
        LinkedHashMap<String, Object> targetNodeInfo = zformService.getNodeList(zform, loginName);
        return targetNodeInfo;
    }

    /**
     * Get current user name
     *
     * @param loginName
     * @return name of current user
     */
    @ApiOperation(value = "/getCurrentUser", tags = "Get current user name")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getCurrentUser")
    public String getCurrentUser(@RequestParam("loginName") String loginName) {
        User currentUser = UserUtil.getByLoginName(loginName);
        return currentUser.getName();
    }

    /**
     * Get rule variables and return zform. Read location of supplementary rule variable, which must be executed before setting rule variable.
     *
     * @param zform
     * @param procDefKey
     * @param loginName
     * @return zform with rule variables
     */
    @ApiOperation(value = "/getRuleArgs", tags = "Get rule variables and return zform")
    @ApiImplicitParams({@ApiImplicitParam(name = "procDefKey", value = "procDefKey", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/getRuleArgs")
    public Zform getRuleArgs(@RequestBody Zform zform, @RequestParam("procDefKey") String procDefKey, @RequestParam("loginName") String loginName) {
        zform.setTempRuleArgsClass(TaskPermission.class.getSimpleName());
        ProcessDefinition processDefinition = zformService.getProcessDefinition(procDefKey);
        zform.getAct().setProcDefId(processDefinition.getId());
        zform.getAct().setTaskDefKey(processDefinition.getDescription().split(",")[1]);
        zformService.setRuleArgs(zform, loginName);
        return zform;
    }

    /**
     * Create a node
     *
     * @param zform
     * @param loginName
     * @return node map created
     */
    @ApiOperation(value = "/createNode", tags = "Create a node")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/createNode")
    public LinkedHashMap<String, Object> createNode(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        LinkedHashMap<String, Object> map = zformService.createNode(zform, loginName);
        map.put("tempRuleArgsClass", TaskPermission.class.getSimpleName());
        return map;
    }

    /**
     * Delete a node
     *
     * @param zform
     * @param loginName
     * @return result message
     */
    @ApiOperation(value = "/deleteNode", tags = "Delete a node")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/deleteNode")
    public LinkedHashMap<String, Object> deleteNode(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        return zformService.deleteNode(zform, loginName);
    }

    /**
     * Get back tasks by ids
     *
     * @param ids
     * @param formNo
     * @param loginName
     * @return result message
     */
    @ApiOperation(value = "/backward", tags = "Get back tasks by ids")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/backward")
    public LinkedHashMap<String, Object> backward(@RequestParam("formNo") String formNo,
                                                  @RequestParam("ids") String ids,
                                                  @RequestParam("loginName") String loginName) throws Exception {
        LinkedHashMap<String, Object> resultMap = Maps.newLinkedHashMap();
        User currentUser = UserUtil.getByLoginName(loginName);
        for (String id : ids.split(",")) {
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            Zform zform = zformService.get(id, genTable);
            zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLINSERT, genTable.getSqlInsert());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLUPDATE, genTable.getSqlUpdate());
            zformService.backward(zform, currentUser.getId(), loginName, resultMap);
        }
        return resultMap;
    }

    /**
     * Revoke tasks by ids
     *
     * @param ids
     * @param formNo
     * @return result message
     */
    @ApiOperation(value = "/revoke", tags = "Revoke tasks by IDs")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "String"),
    })
    @PostMapping("/revoke")
    public LinkedHashMap<String, Object> revoke(@RequestParam("formNo") String formNo, @RequestParam("ids") String ids) throws Exception {
        LinkedHashMap<String, Object> resultMap = Maps.newLinkedHashMap();
        for (String id : ids.split(",")) {
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            Zform zform = zformService.get(id, genTable);
            zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLINSERT, genTable.getSqlInsert());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLUPDATE, genTable.getSqlUpdate());
            zformService.revoke(zform, resultMap);
        }
        return resultMap;
    }


    /**
     * Create a notify node
     *
     * @param zform
     * @param loginName
     * @return result message
     */
    @ApiOperation(value = "/notifyNode", tags = "Create a notify node")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/notifyNode")
    public LinkedHashMap<String, Object> notifyNode(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        return zformService.notifyNode(zform, loginName);
    }

    /**
     * Create a distribute node
     *
     * @param zform
     * @param loginName
     * @return result message
     */
    @ApiOperation(value = "/distributeNode", tags = "Create a distribute node")
    @ApiImplicitParams({@ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/distributeNode")
    public LinkedHashMap<String, Object> distributeNode(@RequestBody Zform zform, @RequestParam("loginName") String loginName) {
        return zformService.distributeNode(zform, loginName);
    }

    /**
     * Check whether a node could be fallen back
     *
     * @param procInsId
     * @param loginName
     * @return result message
     */
    @ApiOperation(value = "/rollBackCheck", tags = "Check whether a node could be fallback")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsId", value = "procInsId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @PostMapping("/rollBackCheck")
    public LinkedHashMap<String, Object> rollBackCheck(@RequestParam("procInsId") String procInsId, @RequestParam("loginName") String loginName) {
        return zformService.rollBackCheck(procInsId, loginName);
    }

    /**
     * Get process definition list
     */
    @ApiOperation(value = "/getProcDefList", tags = "Get process definition list")
    @ApiImplicitParams({@ApiImplicitParam(name = "formNo", value = "formNo", required = true, dataType = "String"),
            @ApiImplicitParam(name = "currentUserName", value = "currentUserName", required = true, dataType = "String")})
    @GetMapping("/getProcDefList")
    public ResultJson getProcDefList(@RequestParam("formNo") String formNo, @RequestParam("currentUserName") String currentUserName) {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            List<ProcessDefinition> procDefList = zformService.getProcessDefinitionList(genTable.getProcessDefinitionCategory(), currentUserName);
            List<Map<String, String>> list = Lists.newArrayList();
            for (ProcessDefinition processDefinition : procDefList) {
                Map<String, String> map = Maps.newHashMap();
                map.put("procDefKey", processDefinition.getKey());
                map.put("procDefName", processDefinition.getName());
                list.add(map);
            }
            resultJson.put("procDefList", list);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("获取流程定义列表成功");
            resultJson.setMsg_en("Get process definition list success");
        } catch (Exception e) {
            logger.error("Error while getting definition list:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("获取流程定义列表失败");
            resultJson.setMsg_en("Get process definition list failed");
        }
        return resultJson;

    }

    /**
     * Is cache view
     */
    @ApiOperation(value = "/isCacheView", tags = "Is cache view")
    @ApiImplicitParams({@ApiImplicitParam(name = "processInstanceId", value = "processInstanceId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/isCacheView")
    public ResultJson isCacheView(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("loginName") String loginName) {
        ResultJson resultJson = new ResultJson();
        try {
            Map<String, Object> cacheView = zformService.isCacheView(processInstanceId);
            resultJson.put("data", cacheView);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
        } catch (Exception e) {
            logger.error("Error while getting is cache view:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("操作失败");
            resultJson.setMsg_en("Operation failed");
        }
        return resultJson;
    }

    /**
     * Is cache data
     */
    @ApiOperation(value = "/isCacheData", tags = "Is cache data")
    @ApiImplicitParams({@ApiImplicitParam(name = "processInstanceId", value = "processInstanceId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/isCacheData")
    public ResultJson isCacheData(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("loginName") String loginName) {
        ResultJson resultJson = new ResultJson();
        try {
            Map<String, Object> cacheData = zformService.isCacheData(processInstanceId, loginName);
            resultJson.put("data", cacheData);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
        } catch (Exception e) {
            logger.error("Error while getting is cache data:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("操作失败");
            resultJson.setMsg_en("Operation failed");
        }
        return resultJson;
    }

    /**
     * Trace photo
     */
    @ApiOperation(value = "/tracePhoto", tags = "Trace photo")
    @ApiImplicitParams({@ApiImplicitParam(name = "procDefId", value = "procDefId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "procInsId", value = "procInsId", required = true, dataType = "String")})
    @GetMapping("/tracePhoto")
    public ResultJson tracePhoto(@RequestParam("procDefId") String procDefId, @RequestParam("procInsId") String procInsId) {
        ResultJson resultJson = new ResultJson();
        try {
            InputStream imageStream = zformService.tracePhoto(procDefId, procInsId);
            byte[] bytes = IOUtils.toByteArray(imageStream);
            String encoded = Base64.getEncoder().encodeToString(bytes);
            resultJson.put("data", encoded);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
        } catch (Exception e) {
            logger.error("Error while getting is cache data:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("操作失败");
            resultJson.setMsg_en("Operation failed");
        }
        return resultJson;
    }

    /**
     * History flow
     */
    @ApiOperation(value = "/histoicFlow", tags = "Histoic flow")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsId", value = "procInsId", required = true, dataType = "String")})
    @GetMapping("/histoicFlow")
    public ResultJson histoicFlow(@RequestParam("procInsId") String procInsId) {
        ResultJson resultJson = new ResultJson();
        try {
            List<Act> list = zformService.histoicFlowList(procInsId);
            resultJson.put("data", list);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
        } catch (Exception e) {
            logger.error("Error while getting is cache data:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("操作失败");
            resultJson.setMsg_en("Operation failed");
        }
        return resultJson;
    }

    /**
     * Get hash value by key
     */
    @ApiOperation(value = "/getHashByKey", tags = "Get hash value by key")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String")})
    @GetMapping("/getHashByKey")
    public String getHashByKey(@RequestParam("key") String key) throws Exception {
        return zformService.getHashByKey(key);
    }
}
