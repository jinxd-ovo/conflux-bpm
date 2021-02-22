package com.jeestudio.services.admin.controller.gen;

import com.google.common.collect.Lists;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenScheme;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumnView;
import com.jeestudio.common.entity.gen.GenTableView;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.common.view.common.TreeView;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: GenTable Controller
 * @author: whl
 * @Date: 2020-01-17
 */
@Api(value = "GenTableController ",tags = "GenTable Controller")
@RestController
@RequestMapping("${adminPath}/gen/genTable")
public class GenTableController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(GenTableController.class);

    @Autowired
    private DatasourceFeign datasourceFeign;

    /**
     * Find dynamic list
     */
    @ApiOperation(value = "findDynamicList", tags = "Find dynamic list")
    @RequiresPermissions("user")
    @PostMapping("/findDynamicList")
    public ResultJson findDynamicList(@RequestBody GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        try {
            Page<GenTable> page = datasourceFeign.findPage(genTable); //genTableService.findDynamicList(genTable);
            resultJson.setRows(page.getList());
            resultJson.setTotal(page.getCount());
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("查询动态表单成功");
            resultJson.setMsg_en("Dynamic list was found successfully");
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to find dynamic list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get tree table list
     */
    @ApiOperation(value = "getTreeTableList", tags = "Get tree table list")
    @RequiresPermissions("user")
    @GetMapping("/getTreeTableList")
    public ResultJson getTreeTableList() {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTable = new GenTable();
            genTable.setTableType(GenTable.TABLE_TYPE_TREE);
            genTable.setPageParam(new PageParam());
            genTable.getPageParam().setPageSize(Integer.MAX_VALUE);
            Page<GenTable> page = datasourceFeign.findPage(genTable);
            List<TreeView> list = Lists.newArrayList();
            int i = 0;
            for (GenTable theGenTable : page.getList()) {
                TreeView treeView = new TreeView();
                treeView.setId(theGenTable.getName());
                treeView.setName(theGenTable.getNameAndComments());
                treeView.setParentId(Global.DEFAULT_ROOT_CODE);
                treeView.setHasChildren(false);
                treeView.setSort(i++);
                list.add(treeView);
            }
            resultJson.put("data", list);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get tree table list: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get gentable for edit
     */
    @ApiOperation(value = "editForm", tags = "Get gentable for edit")
    @RequiresPermissions("user")
    @PostMapping("/editForm")
    public ResultJson editForm(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.editForm(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get zform for editing: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get realm data
     */
    @ApiOperation(value = "realmData", tags = "Get realm data")
    @RequiresPermissions("user")
    @PostMapping("/realmData")
    public ResultJson realmData(@RequestParam("genRealm") String[] genRealm) {
        try {
            ResultJson resultJson = datasourceFeign.realmData(genRealm);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get realm data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save gentable
     */
    @ApiOperation(value = "saveGenTable", tags = "Save gentable")
    @RequiresPermissions("user")
    @PostMapping("/saveGenTable")
    public ResultJson saveGenTable(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.saveGenTable(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Synchronize gentable, rebuild database table
     */
    @ApiOperation(value = "syncDynamic", tags = "Synchronize gentable")
    @RequiresPermissions("user")
    @PostMapping("/syncDynamic")
    public ResultJson syncDynamic(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.syncDynamic(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to sync gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Remove gentable
     */
    @ApiOperation(value = "removeDynamic", tags = "Remove gentable")
    @RequiresPermissions("user")
    @PostMapping("/removeDynamic")
    public ResultJson removeDynamic(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.removeDynamic(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to remove gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Delete gentable
     */
    @ApiOperation(value = "deleteDynamic", tags = "Delete gentable")
    @RequiresPermissions("user")
    @PostMapping("/deleteDynamic")
    public ResultJson deleteDynamic(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.deleteDynamic(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Copy gentable
     */
    @ApiOperation(value = "copyDynamic", tags = "Copy gentable")
    @RequiresPermissions("user")
    @PostMapping("/copyDynamic")
    public ResultJson copyDynamic(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.copyDynamic(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to copy gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get table list to be imported
     */
    @ApiOperation(value = "importListDynamic", tags = "Get table list to be imported")
    @RequiresPermissions("user")
    @PostMapping("/importListDynamic")
    public ResultJson importListDynamic() {
        try {
            ResultJson resultJson = datasourceFeign.importListDynamic();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get table list to be imported: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get dict for gentable
     */
    @ApiOperation(value = "dictDynamic", tags = "Get dict for gentable")
    @RequiresPermissions("user")
    @GetMapping("/dictDynamic")
    public ResultJson dictDynamic(@RequestParam("key") String key) {
        try {
            ResultJson resultJson = datasourceFeign.dictDynamic(key);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get dictionary list by key: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Import table from database
     */
    @ApiOperation(value = "importDynamic", tags = "Import table from database")
    @RequiresPermissions("user")
    @PostMapping("/importDynamic")
    public ResultJson importDynamic(@RequestBody GenTable genTable) {
        try {
            ResultJson resultJson = datasourceFeign.importDynamic(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to import table: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Pre-release gentable
     */
    @ApiOperation(value = "buildViewDynamic", tags = "Pre-release gentable")
    @RequiresPermissions("user")
    @PostMapping("/buildViewDynamic")
    public ResultJson buildViewDynamic(@RequestBody GenScheme genScheme) {
        try {
            ResultJson resultJson = datasourceFeign.buildViewDynamic(genScheme);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to pre-release gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Release gentable
     */
    @ApiOperation(value = "buildDynamic", tags = "Release gentable")
    @RequiresPermissions("user")
    @PostMapping("/buildDynamic")
    public ResultJson buildDynamic(@RequestBody GenScheme genScheme) {
        try {
            ResultJson resultJson = datasourceFeign.buildDynamic(genScheme, currentUserName);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to release gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get dict for view
     */
    @ApiOperation(value = "dictViewDynamic", tags = "Get dict for view")
    @RequiresPermissions("user")
    @GetMapping("/dictViewDynamic")
    public ResultJson dictViewDynamic(@RequestParam("key") String key) {
        try {
            ResultJson resultJson = datasourceFeign.dictViewDynamic(key);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get dictionary for view: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Refresh gentable cache
     */
    @ApiOperation(value = "/refreshGenTableCache", tags = "Refresh gentable cache")
    @RequiresPermissions("user")
    @GetMapping("/refreshGenTableCache")
    public ResultJson refreshGenTableCache() {
        try {
            ResultJson resultJson = datasourceFeign.refreshGenTableCache();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to refresh gentable cache: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get realm columns by names
     */
    @ApiOperation(value = "/getRealmColumnsByNames", tags = "Get realm columns by names")
    @RequiresPermissions("user")
    @PostMapping("/getRealmColumnsByNames")
    public ResultJson getRealmColumnsByNames(@RequestParam("names") String names) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(0);
            resultJson.setMsg("获取领域成功");
            resultJson.setMsg_en("Got realm columns successfully");
            if (StringUtil.isNotEmpty(names)) {
                names = names.replaceAll("，", ",");
                names = names.replaceAll(" ", ",");
                String[] nameArray = names.split(",");
                List<GenTableColumnView> list = Lists.newArrayList();
                for(int i = 0; i < nameArray.length; i++) {
                    String sort = String.valueOf(10 * i);
                    GenTableColumnView column = datasourceFeign.getRealmColumnByName(nameArray[i]);
                    if (column != null) {
                        column.setFormSort(sort);
                        column.setListSort(sort);
                        column.setSearchSort(sort);
                    } else {
                        String nameEn = datasourceFeign.getTransResult(nameArray[i], "auto", "en");
                        if (nameEn.indexOf("dst") != -1) {
                            nameEn = nameEn.substring(nameEn.indexOf("dst") + 6, nameEn.lastIndexOf("\""));
                        } else {
                            nameEn = "";
                        }
                        column = new GenTableColumnView();
                        column.setFormSort(sort);
                        column.setListSort(sort);
                        column.setSearchSort(sort);
                        column.setIsForm(Global.YES);
                        column.setIsQuery(Global.NO);
                        column.setIsList(Global.YES);
                        column.setComments(nameArray[i]);
                        column.setName(StringUtil.toLowerCaseWith_(nameEn));
                        column.setCommentsEn(nameEn);
                        column.setIsOneLine(Global.NO);
                        column.setIsReadonly(Global.NO);
                        column.setIsNull(Global.YES);

                        if (nameArray[i].indexOf("日期") != -1) {
                            column.setJavaField("d");
                            column.setShowType("dateselect");
                            column.setJdbcType("datetime");
                            column.setJavaType("java.util.Date");
                            column.setDateType("yyyy-MM-dd");
                        } else if (nameArray[i].indexOf("时间") != -1) {
                            column.setJavaField("d");
                            column.setShowType("dateselect");
                            column.setJdbcType("datetime");
                            column.setJavaType("java.util.Date");
                            column.setDateType("yyyy-MM-dd HH:mm:ss");
                        } else if (nameArray[i].indexOf("复选") != -1 || nameArray[i].indexOf("多项") != -1 || nameArray[i].indexOf("多选") != -1) {
                            column.setJavaField("c");
                            column.setShowType("checkbox");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("String");
                            column.setDictType("yes_no");
                        } else if (nameArray[i].indexOf("单选") != -1 || nameArray[i].indexOf("单项") != -1) {
                            column.setJavaField("s");
                            column.setShowType("radiobox");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("String");
                            column.setDictType("yes_no");
                            column.setIsNull(Global.NO);
                        } else if (nameArray[i].indexOf("下拉") != -1 || nameArray[i].indexOf("选择") != -1 || nameArray[i].indexOf("选项") != -1) {
                            column.setJavaField("s");
                            column.setShowType("select");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("String");
                            column.setDictType("yes_no");
                        } else if (nameArray[i].indexOf("人员") != -1) {
                            column.setJavaField("user");
                            column.setShowType("treeselectRedio");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("com.jeestudio.common.entity.system.User");
                        } else if (nameArray[i].indexOf("部门") != -1 || nameArray[i].indexOf("机构") != -1) {
                            column.setJavaField("office");
                            column.setShowType("officeselectTree");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("com.jeestudio.common.entity.system.Office");
                        } else if (nameArray[i].indexOf("区域") != -1 || nameArray[i].indexOf("地区") != -1 || nameArray[i].indexOf("行政区") != -1) {
                            column.setJavaField("area");
                            column.setShowType("areaselect");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("com.jeestudio.common.entity.system.Area");
                        } else if (nameArray[i].indexOf("附件") != -1 || nameArray[i].indexOf("文件") != -1 || nameArray[i].indexOf("上传") != -1) {
                            column.setJavaField("s");
                            column.setShowType("fileupload");
                            column.setJdbcType("longtext");
                            column.setJavaType("String");
                            column.setIsOneLine(Global.YES);
                        } else {
                            column.setJavaField("s");
                            column.setShowType("input");
                            column.setJdbcType("varchar(64)");
                            column.setJavaType("String");
                        }
                    }
                    list.add(column);
                }
                resultJson.put("realm", list);
                resultJson.setRows(list);
                resultJson.setTotal(list.size());
            }
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get realm columns by names: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save import and export settings for gentable
     */
    @ApiOperation(value = "saveImportAndExport", tags = "Save import and export settings for gentable")
    @RequiresPermissions("user")
    @PostMapping("/saveImportAndExport")
    public ResultJson saveGenTable(@RequestBody GenTableView genTable) {
        try {
            ResultJson resultJson = datasourceFeign.saveImportAndExport(genTable);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save import and export settings for gentable: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
