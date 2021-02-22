package com.jeestudio.datasource.controller.gen;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenScheme;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumnView;
import com.jeestudio.common.entity.gen.GenTableView;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.datasource.service.gen.GenRealmService;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.service.system.DictDataService;
import com.jeestudio.utils.JsonConvertUtil;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: GenTable Controller
 * @author: whl
 * @Date: 2020-01-17
 */
@Api(value = "GenTableController ",tags = "GenTable Controller")
@RestController
@RequestMapping("${datasourcePath}/gen")
public class GenTableController {

    @Autowired
    private GenTableService genTableService;

    @Autowired
    private GenRealmService genRealmService;

    @Autowired
    private DictDataService dictDataService;

    /**
     * Get gentable by id
     */
    @ApiOperation(value = "/getGentable", tags = "Get gentable by id")
    @GetMapping("/getGentable")
    public String getGenTable(String id) {
        return JsonConvertUtil.objectToJson(genTableService.get(id));
    }

    /**
     * Get gentable by formNo
     */
    @ApiOperation(value = "/getGentableByFormNo", tags = "Get gentable by formNo")
    @GetMapping("/getGentableByFormNo")
    public GenTable getGenTableByFormNo(String formNo) {
        return genTableService.getGenTableWithDefination(formNo);
    }

    /**
     * Find gentable list by page
     */
    @ApiOperation(value = "/findPage", tags = "Find genTable list by page")
    @PostMapping("/findPage")
    public Page<GenTable> findPage(@RequestBody GenTable genTable) {
        if (genTable.getPageParam() == null) genTable.setPageParam(new PageParam());
        return genTableService.findPage(new Page<GenTable>(genTable.getPageParam().getPageNo(),
                        genTable.getPageParam().getPageSize(),
                        genTable.getPageParam().getOrderBy()),
                genTable);
    }

    /**
     * Get gentable for edit
     */
    @ApiOperation(value = "/editForm", tags = "Get gentable for edit")
    @PostMapping("/editForm")
    public ResultJson editForm(@RequestBody GenTable genTable) {
        return genTableService.editForm(genTable);
    }

    /**
     * Get realm data
     */
    @ApiOperation(value = "/realmData", tags = "Get realm data")
    @PostMapping("/realmData")
    public ResultJson realmData(@RequestParam("genRealm") String[] genRealm) {
        return genRealmService.realmData(genRealm);
    }

    /**
     * Save gentable
     */
    @ApiOperation(value = "/saveGenTable", tags = "Save gentable")
    @PostMapping("/saveGenTable")
    public ResultJson saveGenTable(@RequestBody GenTable genTable) {
        //genTableService.refreshGenTableCache(genTable.getName());
        return genTableService.saveGenTable(genTable);
    }

    /**
     * Synchronize gentable, rebuild database table
     */
    @ApiOperation(value = "/syncDynamic", tags = "Synchronize gentable")
    @PostMapping("/syncDynamic")
    public ResultJson syncDynamic(@RequestBody GenTable genTable) {
        return genTableService.syncDynamic(genTable);
    }

    /**
     * Remove gentable
     */
    @ApiOperation(value = "/removeDynamic", tags = "Remove gentable")
    @PostMapping("/removeDynamic")
    public ResultJson removeDynamic(@RequestBody GenTable genTable) {
        return genTableService.removeDynamic(genTable);
    }

    /**
     * Delete gentable
     */
    @ApiOperation(value = "/delDynamic", tags = "Delete gentable")
    @PostMapping("/deleteDynamic")
    public ResultJson deleteDynamic(@RequestBody GenTable genTable) {
        return genTableService.deleteDynamic(genTable);
    }

    /**
     * Copy gentable
     */
    @ApiOperation(value = "/copyDynamic", tags = "Copy gentable")
    @PostMapping("/copyDynamic")
    public ResultJson copyDynamic(@RequestBody GenTable genTable) {
        return genTableService.copyDynamic(genTable);
    }

    /**
     * Get table list to be imported
     */
    @ApiOperation(value = "/copyDynamic", tags = "Get table list to be imported")
    @PostMapping("/importListDynamic")
    public ResultJson importListDynamic() {
        return genTableService.importListDynamic();
    }

    /**
     * Get dict for gentable
     */
    @ApiOperation(value = "dictDynamic", tags = "Get dict for gentable")
    @GetMapping("/dictDynamic")
    public ResultJson dictDynamic(@RequestParam("key") String key) {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("获取字典成功");
        resultJson.setMsg_en("Get dict success");
        resultJson.put("dictList", dictDataService.findDictListLike(key));
        return resultJson;
    }

    /**
     * Import table from database
     */
    @ApiOperation(value = "/importDynamic", tags = "Import table from database")
    @PostMapping("/importDynamic")
    public ResultJson importDynamic(@RequestBody GenTable genTable) {
        return genTableService.importDynamic(genTable);
    }

    /**
     * Pre-release gentable
     */
    @ApiOperation(value = "buildViewDynamic", tags = "Pre-release gentable")
    @PostMapping("/buildViewDynamic")
    public ResultJson buildViewDynamic(@RequestBody GenScheme genScheme) {
        return genTableService.buildViewDynamic(genScheme);
    }

    /**
     * Release gentable
     */
    @ApiOperation(value = "buildDynamic", tags = "Release gentable")
    @PostMapping("/buildDynamic")
    public ResultJson buildDynamic(@RequestBody GenScheme genScheme, @RequestParam("currentUserName") String currentUserName) {
        return genTableService.buildDynamic(genScheme, currentUserName);
    }

    /**
     * Get dict for view
     */
    @ApiOperation(value = "dictViewDynamic", tags = "Dynamic Dict View")
    @GetMapping("/dictViewDynamic")
    public ResultJson dictViewDynamic(@RequestParam("key") String key) {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("获取字典成功");
        resultJson.setMsg_en("Get dict for view success");
        resultJson.put("dictList", dictDataService.getDictGenView(key));
        return resultJson;
    }

    /**
     * Refresh gentable cache
     */
    @ApiOperation(value = "/refreshGenTableCache", tags = "Refresh gentable cache")
    @GetMapping("/refreshGenTableCache")
    public ResultJson refreshGenTableCache() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        genTableService.refreshGenTableCache("");
        resultJson.setMsg("刷新表单缓存成功");
        resultJson.setMsg_en("Refresh gentable cache success");
        return resultJson;
    }

    /**
     * Get realm column by name
     */
    @ApiOperation(value = "/getRealmColumnByName", tags = "Get realm column by name")
    @PostMapping("/getRealmColumnByName")
    public GenTableColumnView getRealmColumnByName(@RequestParam("name") String name) {
        return genRealmService.getByName(name);
    }

    /**
     * Get export file path by formNo
     */
    @ApiOperation(value = "/getExportFilePathByFormNo", tags = "Get export file path by formNo")
    @PostMapping("/getExportFilePathByFormNo")
    public String getExportFilePathByFormNo(@RequestParam("formNo") String formNo, @RequestParam("fileRoot") String fileRoot) {
        return genTableService.getExportFilePathByFormNo(formNo, fileRoot);
    }

    /**
     * Save import and export settings for gentable
     */
    @ApiOperation(value = "/saveImportAndExport", tags = "Save import and export settings for gentable")
    @PostMapping("/saveImportAndExport")
    public ResultJson saveImportAndExport(@RequestBody GenTableView genTable) {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        genTableService.saveImportAndExport(genTable);
        genTableService.refreshGenTableCache(genTable.getName());
        resultJson.setMsg("保存导入导出设置成功");
        resultJson.setMsg_en("Save import and export setting success");
        return resultJson;
    }
}
