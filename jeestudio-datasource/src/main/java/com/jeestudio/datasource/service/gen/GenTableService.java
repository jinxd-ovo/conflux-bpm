package com.jeestudio.datasource.service.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.*;
import com.jeestudio.common.entity.system.Dict;
import com.jeestudio.common.entity.system.DictGenView;
import com.jeestudio.common.entity.system.SysFile;
import com.jeestudio.datasource.feign.CacheFeign;
import com.jeestudio.datasource.mapper.base.gen.*;
import com.jeestudio.datasource.service.act.ActModelService;
import com.jeestudio.datasource.service.ai.TransService;
import com.jeestudio.datasource.service.common.ZformService;
import com.jeestudio.datasource.service.system.DictDataService;
import com.jeestudio.datasource.service.system.SysFileService;
import com.jeestudio.datasource.utils.GenUtil;
import com.jeestudio.utils.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: GenTable Service
 * @author: David
 * @Date: 2020-01-19
 */
@Service
public class GenTableService {

    @Autowired
    private GenTableDao genTableDao;

    @Autowired
    private GenTableColumnDao genTableColumnDao;

    @Autowired
    private GenDataBaseDictDao genDataBaseDictDao;

    @Autowired
    private CacheFeign cacheFeign;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private GenSchemeDao genSchemeDao;

    @Autowired
    private GenCodeDao genCodeDao;

    @Autowired
    private TransService transService;

    @Autowired
    private ActModelService actModelService;

    @Autowired
    private SysFileService sysFileService;

    @Resource(name = "taskExecutor")
    private TaskExecutor taskExecutor;

    @Value("${spring.datasource.dbType}")
    private String dbType;

    @Value("${gen.projectPath}")
    private String projectPath;

    @Value("${gen.key}")
    private String genKey;

    @Value("${gen.url}")
    private String genUrl;

    /**
     * Get gentable with defination
     *
     * @param formNo
     * @return gentable
     */
    public GenTable getGenTableWithDefination(String formNo) {
        Object cachedGenTable = cacheFeign.getHash(GenUtil.GENTABLE_CACHE, formNo);
        GenTable genTable = cachedGenTable == null ? null : JsonConvertUtil.gsonBuilder().fromJson((String) cachedGenTable, new TypeToken<GenTable>() {
        }.getType());
        if (genTable == null) {
            genTable = genTableDao.get(formNo);
            if (genTable != null) {
                GenTableColumn genTableColumn = new GenTableColumn();
                genTableColumn.setGenTable(new GenTable(genTable.getId()));
                genTable.setColumnList(this.genTableColumnDao.findList(genTableColumn));
                if (StringUtil.isEmpty(genTable.getSqlColumns())) {
                    GenUtil.buildSqlMapForDynamicTable(genTable, dbType);
                    this.save(genTable);
                }
                cacheFeign.setHash(GenUtil.GENTABLE_CACHE, formNo, JsonConvertUtil.objectToJson(genTable));
            }
        }
        return genTable;
    }

    /**
     * Get gentable by id
     *
     * @param id
     * @return gentable
     */
    public GenTable get(String id) {
        GenTable genTable = this.genTableDao.get(id);
        GenTableColumn genTableColumn = new GenTableColumn();
        genTableColumn.setGenTable(new GenTable(genTable.getId()));
        genTable.setColumnList(this.genTableColumnDao.findList(genTableColumn));
        return genTable;
    }

    /**
     * Get gentable list by page
     *
     * @param page
     * @param genTable
     * @return gentable page
     */
    public Page<GenTable> findPage(Page<GenTable> page, GenTable genTable) {
        genTable.setPage(page);
        List<GenTable> list = this.genTableDao.findList(genTable);
        page.setList(list);
        return page;
    }

    /**
     * Get gentable list
     *
     * @param genTable
     * @return gentable list
     */
    public List<GenTable> findList(GenTable genTable) {
        return this.genTableDao.findList(genTable);
    }

    /**
     * Get all gentable list
     */
    public List<GenTable> findAll() {
        return this.genTableDao.findAllList(new GenTable());
    }

    /**
     * Get gentable list from db
     */
    public List<GenTable> findTableListFormDb(GenTable genTable) {
        genTable.setDbName(dbType);
        return this.genDataBaseDictDao.findTableList(genTable);
    }

    /**
     * Check table name exists
     */
    public boolean existTable(String tableName) {
        if (StringUtil.isBlank(tableName)) {
            return false;
        } else {
            return (1 == this.genTableDao.findCount(tableName));
        }
    }

    /**
     * Check table name exists from db
     */
    public boolean existTableNameInDB(String tableName) {
        if (StringUtil.isBlank(tableName)) {
            return false;
        } else {
            GenTable genTable = new GenTable();
            genTable.setName(tableName);
            genTable.setDbName(dbType);
            return (1 == this.genDataBaseDictDao.findTableList(genTable).size());
        }
    }

    /**
     * Get table from db
     */
    public GenTable getTableFormDb(GenTable genTable, String dbName) {
        if (StringUtil.isNotBlank(genTable.getName())) {
            genTable.setDbName(dbName);
            List<GenTable> list = genDataBaseDictDao.findTableList(genTable);
            if (list != null && list.size() > 0) {
                //If it is new, initialize the table properties
                if (StringUtil.isBlank(genTable.getId())) {
                    genTable = list.get(0);
                    //Set field description
                    if (StringUtil.isBlank(genTable.getComments())) {
                        genTable.setComments(genTable.getName());
                    }
                    genTable.setClassName(StringUtil.toCapitalizeCamelCase(genTable.getName()));
                }

                genTable.setDbName(dbName);
                //Add new column
                List<GenTableColumn> columnList = genDataBaseDictDao.findTableColumnList(genTable);
                for (GenTableColumn column : columnList) {
                    boolean b = false;
                    for (GenTableColumn e : genTable.getColumnList()) {
                        if (e.getName().equals(column.getName())) {
                            b = true;
                        }
                    }
                    if (!b) {
                        genTable.getColumnList().add(column);
                    }
                }

                //Delete deleted columns
                for (GenTableColumn e : genTable.getColumnList()) {
                    boolean b = false;
                    for (GenTableColumn column : columnList) {
                        if (column.getName().equals(e.getName())) {
                            b = true;
                        }
                    }
                    if (!b) {
                        e.setDelFlag(GenTableColumn.DEL_FLAG_DELETE);
                    }
                }
                genTable.setDbName(dbName);
                //Get the primary key
                genTable.setPkList(genDataBaseDictDao.findTablePK(genTable));
                GenUtil.initColumnField(genTable);
            }
        }
        return genTable;
    }

    /**
     * Save gentable
     */
    @Transactional(readOnly = false)
    public void save(GenTable genTable) {
        boolean isSync = true;

        if (StringUtil.isBlank(genTable.getId())) {
            isSync = false;
        } else {
            GenTable oldTable = get(genTable.getId());
            if (oldTable.getColumnList().size() != genTable.getColumnList().size()
                    || (StringUtil.isNotBlank(oldTable.getName())
                    && false == oldTable.getName().equals(genTable.getName()))
                    || (StringUtil.isNotBlank(oldTable.getComments())
                    && false == oldTable.getComments().equals(genTable.getComments()))) {
                isSync = false;
            } else {
                for (GenTableColumn column : genTable.getColumnList()) {
                    GenTableColumn oldColumn = this.genTableColumnDao.get(column.getId());
                    if (oldColumn != null && (StringUtil.isBlank(oldColumn.getId())
                            || (StringUtil.isNotBlank(oldColumn.getName())
                            && false == oldColumn.getName().equals(column.getName()))
                            || (StringUtil.isNotBlank(oldColumn.getJdbcType())
                            && false == oldColumn.getJdbcType().equals(column.getJdbcType()))
                            || (StringUtil.isNotBlank(oldColumn.getIsPk())
                            && false == oldColumn.getIsPk().equals(column.getIsPk()))
                            || (StringUtil.isNotBlank(oldColumn.getComments())
                            && false == oldColumn.getComments().equals(column.getComments())))) {
                        isSync = false;
                    }
                }
            }
        }

        if (false == isSync) {
            genTable.setIsSync(Global.NO);
        }
        if (StringUtil.isBlank(genTable.getId())) {
            genTable.preInsert();
            this.genTableDao.insert(genTable);
        } else {
            genTable.preUpdate();
            this.genTableDao.update(genTable);
        }
        this.genTableColumnDao.deleteByGenTable(genTable);
        for (GenTableColumn column : genTable.getColumnList()) {
            if (StringUtil.isBlank(column.getName())) {
                continue;
            }
            column.setGenTable(genTable);
            column.setId(null);
            column.preInsert();
            this.genTableColumnDao.insert(column);
        }
        cacheFeign.deleteHash(GenUtil.GENTABLE_CACHE, genTable.getName());
    }

    /**
     * Save gentable sync flag
     */
    @Transactional(readOnly = false)
    public void syncSave(GenTable genTable) {
        genTable.setIsSync(Global.YES);
        this.genTableDao.update(genTable);
    }

    /**
     * Save gentable from db
     */
    @Transactional(readOnly = false)
    public void saveFromDB(GenTable genTable) {
        genTable.preInsert();
        this.genTableDao.insert(genTable);

        for (GenTableColumn column : genTable.getColumnList()) {
            column.setGenTable(genTable);
            column.setId(null);
            column.preInsert();
            this.genTableColumnDao.insert(column);
        }
    }

    /**
     * Delete gentable
     */
    @Transactional(readOnly = false)
    public void delete(GenTable genTable) {
        this.genTableDao.delete(genTable);
        this.genTableColumnDao.deleteByGenTable(genTable);
        this.genTableDao.delete(genTable);
        this.genTableColumnDao.deleteByGenTable(genTable);
        List<GenTable> childList = this.genTableDao.getChildren(genTable.getName());
        for (GenTable child : childList) {
            child.setParentTable("");
            child.setParentTableFk("");
            child.preUpdate();
            genTableDao.update(child);
        }
    }

    /**
     * Build gentable by sql
     */
    @Transactional(readOnly = false)
    public void buildTable(String sql) {
        this.genTableDao.buildTable(sql);
    }

    /**
     * Copy gentable
     */
    @Transactional(readOnly = false)
    public void copy(GenTable genTable) {
        genTable.setIsSync(Global.NO);

        genTable.setId(IdGen.uuid());
        genTable.setName(genTable.getName() + "2");
        genTable.setClassName(genTable.getClassName());
        this.genTableDao.insert(genTable);

        for (GenTableColumn column : genTable.getColumnList()) {
            column.setGenTable(genTable);
            column.setId(IdGen.uuid());
            this.genTableColumnDao.insert(column);
        }
    }

    /**
     * Get gentable by name
     */
    public GenTable getByName(String name) {
        GenTable genTable = genTableDao.getByName(name);
        return genTable;
    }

    /**
     * Find gentable column list by page
     */
    public Page<GenTableColumn> findTableColumn(Page<GenTableColumn> page, GenTableColumn column) {
        column.setPage(page);
        GenTable gt = column.getGenTable();
        if (gt == null) {
            page.setList(null);
        } else {
            List<GenTableColumn> list = this.genTableColumnDao.findPageList(column);
            for (int i = 0; i < list.size(); i++) {
                GenTableColumn genTableColum = list.get(i);
                String showType = genTableColum.getShowType();
                String javaField = genTableColum.getJavaField();
                switch (showType) {
                    case "treeselectRedio":
                        String[] treeselectRedioIda = javaField.split("\\.");
                        String treeselectRedioId = javaField;
                        if (treeselectRedioIda.length >= 2) {
                            genTableColum.setJavaField(treeselectRedioIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(treeselectRedioId + "Id");
                        }
                        break;
                    case "treeselectCheck":
                        String[] treeselectCheckIda = javaField.split("\\.");
                        String treeselectCheckId = javaField;
                        if (treeselectCheckIda.length >= 2) {
                            genTableColum.setJavaField(treeselectCheckIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(treeselectCheckId + "Id");
                        }
                        break;
                    case "officeselectTree":
                        String[] officeselectTreeIda = javaField.split("\\.");
                        String officeselectTreeId = javaField;
                        if (officeselectTreeIda.length >= 2) {
                            genTableColum.setJavaField(officeselectTreeIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(officeselectTreeId + "Id");
                        }
                        break;
                    case "areaselect":
                        String[] areaselectIda = javaField.split("\\.");
                        String areaselectId = javaField;
                        if (areaselectIda.length >= 2) {
                            genTableColum.setJavaField(areaselectIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(areaselectId + "Id");
                        }
                        break;
                    case "treeselect":
                        String[] treeselectIda = javaField.split("\\.");
                        String treeselectId = javaField;
                        if (treeselectIda.length >= 2) {
                            genTableColum.setJavaField(treeselectIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(treeselectId + "Id");
                        }
                        break;
                    case "gridselect":
                        String[] gridselectIda = javaField.split("\\.");
                        String gridselectId = javaField;
                        if (gridselectIda.length >= 2) {
                            genTableColum.setJavaField(gridselectIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(gridselectId + "Id");
                        }
                        break;
                    case "fileupload":
                        String[] fileuploadIda = javaField.split("\\.");
                        String fileuploadId = javaField;
                        if (fileuploadIda.length >= 2) {
                            genTableColum.setJavaField(fileuploadIda[0] + "File");
                        } else {
                            genTableColum.setJavaField(fileuploadId + "File");
                        }
                        break;
                    case "fileuploadsec":
                        String[] fileuploadsecIda = javaField.split("\\.");
                        String fileuploadsecId = javaField;
                        if (fileuploadsecIda.length >= 2) {
                            genTableColum.setJavaField(fileuploadsecIda[0] + "File");
                        } else {
                            genTableColum.setJavaField(fileuploadsecId + "File");
                        }
                        break;
                    case "fileuploadpic":
                        String[] fileuploadpicIda = javaField.split("\\.");
                        String fileuploadpicId = javaField;
                        if (fileuploadpicIda.length >= 2) {
                            genTableColum.setJavaField(fileuploadpicIda[0] + "File");
                        } else {
                            genTableColum.setJavaField(fileuploadpicId + "File");
                        }
                        break;
                    case "userselect":
                        String[] userselectIda = javaField.split("\\.");
                        String userselectId = javaField;
                        if (userselectIda.length >= 2) {
                            genTableColum.setJavaField(userselectIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(userselectId + "Id");
                        }
                        break;
                    case "officeselect":
                        String[] officeselectIda = javaField.split("\\.");
                        String officeselectId = javaField;
                        if (officeselectIda.length >= 2) {
                            genTableColum.setJavaField(officeselectIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(officeselectId + "Id");
                        }
                        break;
                    case "userselectMulti":
                        String[] userselectMultiIda = javaField.split("\\.");
                        String userselectMultiId = javaField;
                        if (userselectMultiIda.length >= 2) {
                            genTableColum.setJavaField(userselectMultiIda[0] + "Id");
                        } else {
                            genTableColum.setJavaField(userselectMultiId + "Id");
                        }
                        break;
                }
                String jf = genTableColum.getJavaField();
                if (jf.indexOf(".") != -1) {
                    genTableColum.setJavaField(jf.split("\\.")[0]);
                }
            }
            page.setList(list);
        }
        return page;
    }

    /**
     * Find gentable column list
     */
    public List<GenTableColumn> findByColum(GenTableColumn column) {
        GenTable gt = column.getGenTable();
        if (gt == null) {
            return null;
        } else {
            List<GenTableColumn> list = this.genTableColumnDao.findPageList(column);
            return list;
        }
    }

    /**
     * Save edit form with Json
     */
    @Transactional(readOnly = false)
    public void saveEditForm(String json) {
        JSONArray array = JSONArray.parseArray(json);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            genTableColumnDao.saveEditForm(String.valueOf(obj.get("id")),
                    String.valueOf(obj.get("showType") == null ? "" : obj.get("showType")),
                    String.valueOf(obj.get("isOneLine") == null ? "" : obj.get("isOneLine")),
                    String.valueOf(obj.get("isNull") == null ? "" : obj.get("isNull")),
                    String.valueOf(obj.get("comments") == null ? "" : obj.get("comments")),
                    String.valueOf(obj.get("javaField") == null ? "" : obj.get("javaField")),
                    String.valueOf(obj.get("maxLength") == null ? "" : obj.get("maxLength")),
                    String.valueOf(obj.get("minLength") == null ? "" : obj.get("minLength")),
                    String.valueOf(obj.get("min") == null ? "" : obj.get("min")),
                    String.valueOf(obj.get("max") == null ? "" : obj.get("max")),
                    String.valueOf(obj.get("friendlyJdbcType") == null ? "" : obj.get("friendlyJdbcType")),
                    String.valueOf(obj.get("javaType") == null ? "" : obj.get("javaType")),
                    String.valueOf(obj.get("jdbcType") == null ? "" : obj.get("jdbcType")),
                    String.valueOf(obj.get("queryType") == null ? "" : obj.get("queryType")),
                    Integer.valueOf(String.valueOf(obj.get("formSort"))),
                    String.valueOf(obj.get("validateType") == null ? "" : obj.get("validateType")),
                    String.valueOf(obj.get("dictType") == null ? "" : obj.get("dictType")),
                    Integer.valueOf(String.valueOf(obj.get("searchSort"))), String.valueOf(obj.get("isForm")),
                    String.valueOf(obj.get("isQuery")), String.valueOf(obj.get("isList")));
        }
    }

    /**
     * Save edit list
     */
    @Transactional(readOnly = false)
    public void saveEditList(String jsonList) {
        JSONArray array = JSONArray.parseArray(jsonList);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            genTableColumnDao.saveEditList(String.valueOf(obj.get("id")), "1");
        }
    }

    /**
     * Save edit search with Json search
     */
    @Transactional(readOnly = false)
    public void saveEditSearch(String jsonSearch) {
        JSONArray array = JSONArray.parseArray(jsonSearch);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            genTableColumnDao.saveEditSearch(String.valueOf(obj.get("id")), "1", i,
                    String.valueOf(obj.get("queryType") == null ? "" : obj.get("queryType")));
        }
    }

    /**
     * Find gentable column list by gentable id
     */
    public List<GenTableColumn> findGenTableColumnList(String genTableId) {
        List<GenTableColumn> list = genTableColumnDao.findGenTableColumnList(genTableId);
        return list;
    }

    /**
     * Update edit by gentable id
     */
    @Transactional(readOnly = false)
    public void updateEdit(String genTableId) {
        genTableColumnDao.updateEdit(genTableId, "0", "");
    }

    /**
     * Save sql for gentable
     */
    @Transactional(readOnly = false)
    public void saveSql(GenTable genTable) {
        genTableDao.saveSql(genTable.getId(), genTable.getSqlColumns(), genTable.getSqlColumnsFriendly(), genTable.getSqlJoins(), genTable.getSqlInsert(),
                genTable.getSqlUpdate(), genTable.getSqlSort());
    }

    /**
     * Save Jsons
     */
    @Transactional(readOnly = false)
    public void saveJsons(GenTable genTable, boolean b) {
        genTableColumnDao.deleteByGenTableId(genTable.getId());
        GenTableColumn column = new GenTableColumn();
        if (b) {
            column = saveColumnId(column, genTable);
            this.genTableColumnDao.insert(column);

            column = saveColumnDelFlag(column, genTable);
            this.genTableColumnDao.insert(column);
        }
        JSONArray json = genTable.getJson();
        saveColumnPro(json, column, genTable);
        String parentTable = genTable.getParentTable();
        if (StringUtil.isNotEmpty(parentTable) && false == GenTable.TABLE_TYPE_RIGHTTABLE.equals(genTable.getTableType())) {
            saveColumnParentFk(json, column, genTable);
        }
        boolean bId = false;
        boolean bDel = false;
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String field = obj.getString("javaField");
            String str = "procInsId";
            if (str.equals(field)) {
                String cate = genTable.getProcessDefinitionCategory();
                if ("".equals(cate) == true || cate == null) {
                    continue;
                }
            }
            String fieldDef = obj.getString("javaField");
            String strDef = "procDefKey";
            if (strDef.equals(fieldDef)) {
                String cate = genTable.getProcessDefinitionCategory();
                if ("".equals(cate) == true || cate == null) {
                    continue;
                }
            }
            if ("id".equals(obj.getString("javaField"))) {
                column.setIsPk("1");
                bId = true;
            } else {
                column.setIsPk("0");
            }
            if ("delFlag".equals(obj.getString("javaField"))) {
                bDel = true;
            }
            if (!"id".equals(obj.getString("javaField")) && !"createBy.id".equals(obj.getString("javaField"))
                    && !"createDate".equals(obj.getString("javaField"))
                    && !"delFlag".equals(obj.getString("javaField"))) {
                column.setIsEdit("1");
            } else {
                column.setIsEdit("0");
            }
            String javaField = obj.getString("javaField");
            if (javaField.contains("users") && javaField.indexOf(".") != -1 && javaField.indexOf("|") != -1) {
                saveUsersName(json, genTable, javaField);
            }
            if (obj.containsKey("blockChainParam1")) {
                column.setBlockChainParam1(obj.getString("blockChainParam1"));
            } else {
                column.setBlockChainParam1("0");
            }
            if (obj.containsKey("blockChainParam2")) {
                column.setBlockChainParam2(obj.getString("blockChainParam2"));
            } else {
                column.setBlockChainParam2("0");
            }
            if (obj.containsKey("blockChainParam3")) {
                column.setBlockChainParam3(obj.getString("blockChainParam3"));
            } else {
                column.setBlockChainParam3("0");
            }
            column.setGenTable(genTable);
            column.setId(null);
            column.setIsInsert("1");
            column.setShowType(obj.getString("showType"));
            column.setIsOneLine(String.valueOf(obj.getString("isOneLine") == null ? "0" : obj.getString("isOneLine")));
            column.setIsNull(String.valueOf(obj.getString("isNull") == null ? "0" : obj.getString("isNull")));
            column.setComments(obj.getString("comments"));
            column.setCommentsEn(obj.getString("comments_EN"));
            column.setDateType(obj.getString("dateType"));
            column.setJavaField(obj.getString("javaField"));
            if (obj.containsKey("maxLength")) {
                column.setMaxLength(obj.getString("maxLength"));
            } else {
                column.setMaxLength("");
            }
            if (obj.containsKey("minLength")) {
                column.setMinLength(obj.getString("minLength"));
            } else {
                column.setMinLength("");
            }
            if (obj.containsKey("minValue")) {
                column.setMinValue(obj.getString("minValue"));
            } else {
                column.setMinValue("");
            }
            if (obj.containsKey("maxValue")) {
                column.setMaxValue(obj.getString("maxValue"));
            } else {
                column.setMaxValue("");
            }
            column.setJavaType(obj.getString("javaType"));
            column.setJdbcType(obj.getString("jdbcType"));
            if (obj.containsKey("dictType")) {
                column.setDictType(obj.getString("dictType"));
            } else {
                column.setDictType("");
            }
            if (obj.containsKey("queryType")) {
                column.setQueryType(obj.getString("queryType"));
            } else {
                column.setQueryType("");
            }
            if (obj.containsKey("validateType")) {
                column.setValidateType(obj.getString("validateType"));
            } else {
                column.setValidateType("");
            }
            if (obj.containsKey("tableName")) {
                column.setTableName(obj.getString("tableName"));
            } else {
                column.setTableName("");
            }
            if (obj.containsKey("fieldLabels")) {
                column.setFieldLabels(obj.getString("fieldLabels"));
            } else {
                column.setFieldLabels("");
            }
            if (obj.containsKey("fieldKeys")) {
                column.setFieldKeys(obj.getString("fieldKeys"));
            } else {
                column.setFieldKeys("");
            }
            if (obj.containsKey("searchLabel")) {
                column.setSearchLabel(obj.getString("searchLabel"));
            } else {
                column.setSearchLabel("");
            }
            if (obj.containsKey("searchKey")) {
                column.setSearchKey(obj.getString("searchKey"));
            } else {
                column.setSearchKey("");
            }
            column.setIsForm(String.valueOf(obj.getString("isForm") == null ? "0" : obj.getString("isForm")));
            column.setIsQuery(String.valueOf(obj.getString("isQuery") == null ? "0" : obj.getString("isQuery")));
            column.setIsList(String.valueOf(obj.getString("isList") == null ? "0" : obj.getString("isList")));
            if ("".equals(obj.getString("formSort"))) {
                column.setFormSort(100);
            } else {
                String formSort = obj.getString("formSort");
                BigDecimal bigDecimal = new BigDecimal(formSort);
                DecimalFormat df = new DecimalFormat("0");
                formSort = df.format(bigDecimal);
                int num = formSort.indexOf(".");
                if (formSort != null) {
                    if (num != -1) {
                        column.setFormSort(Integer.valueOf(formSort.substring(0, num)));
                    } else {
                        column.setFormSort(Integer.valueOf(formSort));
                    }
                } else {
                    column.setFormSort(100);
                }
            }
            if ("".equals(obj.getString("searchSort"))) {
                column.setSearchSort(100);
            } else {
                String searchSort = obj.getString("searchSort");
                BigDecimal bigDecimal = new BigDecimal(searchSort);
                DecimalFormat df = new DecimalFormat("0");
                searchSort = df.format(bigDecimal);
                int num = searchSort.indexOf(".");
                if (searchSort != null) {
                    if (num != -1) {
                        column.setSearchSort((Integer.valueOf(searchSort.substring(0, num))));
                    } else {
                        column.setSearchSort(Integer.valueOf(searchSort));
                    }
                } else {
                    column.setSearchSort(100);
                }
            }
            column.setIsReadonly(String.valueOf(obj.getString("isReadonly") == null ? "0" : obj.getString("isReadonly")));
            column.setDefaultValue(obj.getString("defaultValue"));
            String name = obj.getString("name");
            if (name.indexOf(".") != -1) {
                String[] names = name.split("\\.");
                name = names[0] + "_" + names[1].split("\\|")[0];
                column.setName(name);
            } else {
                column.setName(obj.getString("name"));
            }
            if ("".equals(obj.getString("listSort"))) {
                column.setListSort(100);
            } else {
                String listSort = obj.getString("listSort");
                BigDecimal bigDecimal = new BigDecimal(listSort);
                DecimalFormat df = new DecimalFormat("0");
                listSort = df.format(bigDecimal);
                int num = listSort.indexOf(".");
                if (listSort != null) {
                    if (num != -1) {
                        column.setListSort((Integer.valueOf(listSort.substring(0, num))));
                    } else {
                        column.setListSort(Integer.valueOf(listSort));
                    }
                } else {
                    column.setListSort(100);
                }
            }
            column.preInsert();
            this.genTableColumnDao.insert(column);
        }
        if (!b) {
            if (!bId) {
                column = saveColumnId(column, genTable);

                this.genTableColumnDao.insert(column);
            }
            if (!bDel) {
                column = saveColumnDelFlag(column, genTable);

                this.genTableColumnDao.insert(column);
            }
        }
    }

    /**
     * Save column parent fk
     */
    private void saveColumnParentFk(JSONArray json, GenTableColumn column, GenTable genTable) {
        boolean b = false;
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String field = obj.getString("javaField");
            String str = "parent.id";
            if (str.equals(field)) {
                b = true;
                break;
            }
        }
        if (!b) {
            column.setGenTable(genTable);
            column.setId(null);
            column.setIsInsert("1");
            column.setShowType("input");
            column.setIsOneLine("");
            column.setIsNull("0");
            column.setIsPk("0");
            column.setComments("FK");
            column.setJavaField("parent.id");
            column.setMaxLength("");
            column.setMinLength("");
            column.setMinValue("");
            column.setMaxValue("");
            column.setJavaType("String");
            column.setJdbcType("varchar(64)");
            column.setQueryType("=");
            column.setValidateType("");
            column.setIsForm("0");
            column.setIsQuery("0");
            column.setIsList("0");
            column.setFormSort(100);
            column.setSearchSort(100);
            column.setIsReadonly("0");
            column.setDefaultValue("");
            column.setName("parent_id");
            column.setListSort(100);
            column.preInsert();
            this.genTableColumnDao.insert(column);
        }

    }

    /**
     * Save column pro
     */
    private void saveColumnPro(JSONArray json, GenTableColumn column, GenTable genTable) {
        String cate = genTable.getProcessDefinitionCategory();
        boolean b = false;
        boolean c = false;
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String field = obj.getString("javaField");
            String str = "procInsId";
            if (str.equals(field)) {
                b = true;
                break;
            }
        }
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String field = obj.getString("javaField");
            String str = "procDefKey";
            if (str.equals(field)) {
                c = true;
                break;
            }
        }
        if (!b) {
            if ("".equals(cate) == false && cate != null) {
                column.setGenTable(genTable);
                column.setId(null);
                column.setIsInsert("1");
                column.setShowType("input");
                column.setIsOneLine("");
                column.setIsNull("0");
                column.setIsEdit("1");
                column.setIsPk("0");
                column.setComments("Process Instance ID");
                column.setJavaField("procInsId");
                column.setMaxLength("");
                column.setMinLength("");
                column.setMinValue("");
                column.setMaxValue("");
                column.setJavaType("String");
                column.setJdbcType("varchar(64)");
                column.setQueryType("=");
                column.setValidateType("");
                column.setIsForm("0");
                column.setIsQuery("0");
                column.setIsList("0");
                column.setFormSort(100);
                column.setSearchSort(100);
                column.setIsReadonly("0");
                column.setDefaultValue("");
                column.setName("proc_ins_id");
                column.setListSort(100);
                column.preInsert();
                this.genTableColumnDao.insert(column);
            }
        }
        if (!c) {
            if ("".equals(cate) == false && cate != null) {
                column.setGenTable(genTable);
                column.setId(null);
                column.setIsInsert("1");
                column.setShowType("input");
                column.setIsOneLine("");
                column.setIsNull("0");
                column.setIsEdit("1");
                column.setIsPk("0");
                column.setComments("Process Def Key");
                column.setJavaField("procDefKey");
                column.setMaxLength("");
                column.setMinLength("");
                column.setMinValue("");
                column.setMaxValue("");
                column.setJavaType("String");
                column.setJdbcType("varchar(64)");
                column.setQueryType("=");
                column.setValidateType("");
                column.setIsForm("0");
                column.setIsQuery("0");
                column.setIsList("0");
                column.setFormSort(100);
                column.setSearchSort(100);
                column.setIsReadonly("0");
                column.setDefaultValue("");
                column.setName("proc_def_key");
                column.setListSort(110);
                column.preInsert();
                this.genTableColumnDao.insert(column);
            }
        }
    }

    /**
     * Save users name
     */
    private void saveUsersName(JSONArray json, GenTable genTable, String javaField) {
        javaField = javaField.split("\\.")[0];
        boolean b = false;
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String field = obj.getString("javaField");
            String str = javaField + "Name";
            if (str.equals(field)) {
                b = true;
                break;
            }
        }
        if (!b) {
            GenTableColumn column = new GenTableColumn();
            column.setGenTable(genTable);
            column.setId(null);
            column.setIsInsert("1");
            column.setShowType("input");
            column.setIsOneLine("");
            column.setIsNull("0");
            column.setIsEdit("1");
            column.setComments("Users name");
            column.setJavaField(javaField + "Name");
            column.setMaxLength("");
            column.setMinLength("");
            column.setMinValue("");
            column.setMaxValue("");
            column.setJavaType("String");
            column.setJdbcType("varchar(2000)");
            column.setQueryType("=");
            column.setValidateType("");
            column.setIsForm("0");
            column.setIsQuery("0");
            column.setIsList("0");
            column.setFormSort(100);
            column.setSearchSort(100);
            column.setIsReadonly("0");
            column.setDefaultValue("");
            column.setName(javaField + "_name");
            column.setListSort(100);
            column.preInsert();
            this.genTableColumnDao.insert(column);
        }
    }

    /**
     * Save column del flag
     */
    private GenTableColumn saveColumnDelFlag(GenTableColumn column, GenTable genTable) {
        column.setGenTable(genTable);
        column.setId(null);
        column.setIsInsert("1");
        column.setShowType("input");
        column.setIsOneLine("");
        column.setIsNull("0");
        column.setIsPk("0");
        column.setComments("DEL FLAG");
        column.setJavaField("delFlag");
        column.setMaxLength("");
        column.setMinLength("");
        column.setMinValue("");
        column.setMaxValue("");
        column.setJavaType("String");
        column.setJdbcType("varchar(64)");
        column.setQueryType("=");
        column.setValidateType("");
        column.setIsForm("0");
        column.setIsQuery("0");
        column.setIsList("0");
        column.setFormSort(100);
        column.setSearchSort(100);
        column.setIsReadonly("0");
        column.setDefaultValue("");
        column.setName("del_flag");
        column.setListSort(7);
        column.preInsert();
        return column;
    }

    /**
     * Save column id
     */
    private GenTableColumn saveColumnId(GenTableColumn column, GenTable genTable) {
        column.setGenTable(genTable);
        column.setId(null);
        column.setShowType("input");
        column.setIsInsert("1");
        column.setIsOneLine("");
        column.setIsNull("0");
        column.setIsPk("1");
        column.setComments("PK");
        column.setJavaField("id");
        column.setMaxLength("");
        column.setMinLength("");
        column.setMinValue("");
        column.setMaxValue("");
        column.setJavaType("String");
        column.setJdbcType("varchar(64)");
        column.setQueryType("=");
        column.setValidateType("");
        column.setIsForm("0");
        column.setIsQuery("0");
        column.setIsList("0");
        column.setFormSort(100);
        column.setSearchSort(100);
        column.setIsReadonly("0");
        column.setDefaultValue("");
        column.setName("id");
        column.setListSort(6);
        column.preInsert();
        return column;
    }

    /**
     * Get gentable column list
     */
    public List<GenTableColumn> getListByGenTableId(String genTableId, String javaType) {
        List<GenTableColumn> list = genTableColumnDao.getListByGenTableId(genTableId, javaType);
        return list;
    }

    /**
     * Save dynamic
     */
    @Transactional(readOnly = false)
    public void saveDynamic(GenTable genTable, int length) {
        genTable.setIsSync(Global.NO);
        if (StringUtil.isBlank(genTable.getId())) {
            genTable.preInsert();
            this.genTableDao.insert(genTable);
        } else {
            genTable.preUpdate();
            this.genTableDao.update(genTable);
        }
        cacheFeign.deleteHash(GenUtil.GENTABLE_CACHE, genTable.getName());
    }

    public ResultJson editForm(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        if (StringUtil.isBlank(genTable.getId())) {
            resultJson.setCode(0);
            resultJson.setMsg("获取动态表单成功");
            resultJson.setMsg_en("Get dynamic form success");
            resultJson.put("genTable", new GenTableView());
            resultJson.put("data", new GenTableColumnView());
        } else {
            GenTableView genTableView = genTableDao.getGengTableViewById(genTable.getId());
            if (genTableView != null) {
                List<GenTableChildren> genTableChildrenList = genTableDao.getGenTableViewByParentTable(genTableView.getName());
                for (GenTableChildren genTableChildren : genTableChildrenList) {
                    List<GenTableColumnView> genTableColumnView = genTableColumnDao.getGenTableColumnViewByGenTableId(genTableChildren.getId());
                    List<GenTableColumnView> genTableColumnViewDict = genTableColumnDao.getGenTableColumnViewByGenTableIdDict(genTableChildren.getId());
                    for (GenTableColumnView genTableColumnView1 : genTableColumnViewDict) {
                        List<DictGenView> dictGenViewList = dictDataService.getDictGenView(genTableColumnView1.getDictType());
                        genTableColumnView1.setDictList(dictGenViewList);
                    }
                    genTableColumnView.addAll(genTableColumnViewDict);
                    genTableChildren.setData(genTableColumnView);
                }
                genTableView.setChildren(genTableChildrenList);
                List<GenTableColumnView> data = genTableColumnDao.getGenTableColumnViewByGenTableId(genTableView.getId());
                List<GenTableColumnView> dataDict = genTableColumnDao.getGenTableColumnViewByGenTableIdDict(genTableView.getId());
                for (GenTableColumnView genTableColumnView : dataDict) {
                    List<DictGenView> dictGenViewList = dictDataService.getDictGenView(genTableColumnView.getDictType());
                    genTableColumnView.setDictList(dictGenViewList);
                }
                data.addAll(dataDict);
                resultJson.setCode(0);
                resultJson.setMsg("获取动态表单成功");
                resultJson.setMsg_en("Get dynamic form success");
                resultJson.put("genTable", genTableView);
                resultJson.put("data", data);
            } else {
                resultJson.setCode(0);
                resultJson.setMsg("获取动态表单失败");
                resultJson.setMsg_en("Get dynamic form fail");
                resultJson.put("genTable", new GenTableView());
                resultJson.put("data", new GenTableColumnView());
            }
        }
        return resultJson;
    }

    @Transactional
    public ResultJson saveGenTable(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        JSONArray array = genTable.getJson();
        boolean needToSave = true;
        if (StringUtil.isBlank(genTable.getId())) {
            //New genTable
            if (this.existTable(genTable.getName())) {
                resultJson.setCode(ResultJson.CODE_FAILED);
                resultJson.setMsg("保存失败，表定义已存在。");
                resultJson.setMsg_en("Saving failed, table definition already exists.");
                resultJson.put("gentableId", "");
                needToSave = false;
            } else if (this.existTableNameInDB(genTable.getName())) {
                resultJson.setCode(ResultJson.CODE_FAILED);
                resultJson.setMsg("保存失败，请从数据库导入表。");
                resultJson.setMsg_en("Saving failed, please import table from database.");
                resultJson.put("gentableId", "");
                needToSave = false;
            } else {
                //Save new genTable
                genTable.setIsNewRecord(true);
            }
        } else {
            //Update ,check oldName and name
            String oldName = genTable.getOldName();
            if (StringUtil.isNotEmpty(oldName) && false == oldName.equalsIgnoreCase(genTable.getName())) {
                if (this.existTable(genTable.getName())) {
                    resultJson.setCode(ResultJson.CODE_FAILED);
                    resultJson.setMsg("保存失败，表定义已存在。");
                    resultJson.setMsg_en("Saving failed, table definition already exists.");
                    resultJson.put("gentableId", "");
                    needToSave = false;
                }
            }
        }
        if (needToSave) {
            genTable.setIsRelease(Global.NO);
            genTable.setIsSync(Global.NO);
            genTable.setClassName("Zform");
            genTable.setFormType("dynamic");
            genTable.setIsBuildXform(Global.YES);
            if (StringUtil.isNotEmpty(genTable.getParentTable())) {
                if (StringUtil.isEmpty(genTable.getParentTableFk())) genTable.setParentTableFk("parent_id");
            }

            this.saveDynamic(genTable, array.size());
            this.saveJsons(genTable, StringUtil.isBlank(genTable.getId()));
            GenTable gent = this.get(genTable.getId());
            GenUtil.buildSqlMapForDynamicTable(gent, dbType);
            this.saveSql(gent);
            this.saveDict(gent);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("保存动态表单成功。");
            resultJson.setMsg_en("Save dynamic success.");
            resultJson.put("gentableId", genTable.getId());
            resultJson.put("genTable", genTable);
            this.refreshGenTableCache(genTable.getName());
        }
        return resultJson;
    }

    @Transactional(readOnly = false)
    public void saveDict(GenTable genTable) {
        //genTable = this.getGenTableWithDefination(genTable.getName());
        String code = genTable.getName();
        String comments = genTable.getComments();
        String commentsEn = genTable.getCommentsEn();
        dictDataService.deleteCascade(code);

        Dict fieldParent = new Dict();
        fieldParent.setId("page-form-field");
        fieldParent.setCode("page-form-field");
        fieldParent.setParentIds("0,");
        Dict cellParent = new Dict();
        cellParent.setId("page-table-cell");
        cellParent.setCode("page-table-cell");
        cellParent.setParentIds("0,");
        Dict fieldDict = new Dict();
        fieldDict.setCode(code);
        fieldDict.setName(comments);
        fieldDict.setNameEn(commentsEn);
        fieldDict.setParent(fieldParent);
        fieldDict.setParentCode(fieldParent.getCode());
        fieldDict.setParentIds(fieldParent.getParentIds() + fieldParent.getId() + ",");
        Dict cellDict = new Dict();
        cellDict.setCode(code);
        cellDict.setName(comments);
        cellDict.setNameEn(commentsEn);
        cellDict.setParent(cellParent);
        cellDict.setParentCode(cellParent.getCode());
        cellDict.setParentIds(cellParent.getParentIds() + cellParent.getId() + ",");
        dictDataService.save(fieldDict);
        dictDataService.save(cellDict);

        List<GenTableColumn> genTableColumnList = genTable.getColumnList();
        for (GenTableColumn genTableColumn : genTableColumnList) {
            String isForm = genTableColumn.getIsForm();
            String isList = genTableColumn.getIsList();
            String isQuery = genTableColumn.getIsQuery();
            if (Global.YES.equals(isForm)) {
                String columnName = genTableColumn.getJavaField();
                if (StringUtil.isNotBlank(columnName) && columnName.indexOf("|") != -1) {
                    columnName = columnName.substring(0, columnName.indexOf("|"));
                }
                String columnComments = genTableColumn.getComments();
                String columnCommentsEn = genTableColumn.getCommentsEn();
                Dict dict = new Dict();
                dict.setCode(columnName);
                dict.setName(columnComments);
                dict.setNameEn(columnCommentsEn);
                dict.setParent(fieldDict);
                dict.setParentCode(fieldDict.getCode());
                dict.setParentIds(fieldDict.getParentIds() + fieldDict.getId() + ",");
                dictDataService.save(dict);
            } else if (Global.YES.equals(isList)) {
                String columnName = genTableColumn.getJavaField();
                if (StringUtil.isNotBlank(columnName) && columnName.indexOf("|") != -1) {
                    columnName = columnName.substring(0, columnName.indexOf("|"));
                }
                String columnComments = genTableColumn.getComments();
                String columnCommentsEn = genTableColumn.getCommentsEn();
                Dict dict = new Dict();
                dict.setCode(columnName);
                dict.setName(columnComments);
                dict.setNameEn(columnCommentsEn);
                dict.setParent(cellDict);
                dict.setParentCode(cellDict.getCode());
                dict.setParentIds(cellDict.getParentIds() + cellDict.getId() + ",");
                dictDataService.save(dict);
            } else if (Global.YES.equals(isQuery)) {
                String columnName = genTableColumn.getJavaField();
                if (StringUtil.isNotBlank(columnName) && columnName.indexOf("|") != -1) {
                    columnName = columnName.substring(0, columnName.indexOf("|"));
                }
                String columnComments = genTableColumn.getComments();
                String columnCommentsEn = genTableColumn.getCommentsEn();
                Dict dict = new Dict();
                dict.setCode(columnName);
                dict.setName(columnComments);
                dict.setNameEn(columnCommentsEn);
                dict.setParent(cellDict);
                dict.setParentCode(cellDict.getCode());
                dict.setParentIds(cellDict.getParentIds() + cellDict.getId() + ",");
                dictDataService.save(dict);
            }
        }
    }

    @Transactional
    public ResultJson syncDynamic(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        if (StringUtil.isEmpty(genTable.getId())) {
            resultJson.setCode(0);
            resultJson.setMsg("同步数据库失败");
            resultJson.setMsg_en("Sync table failed");
            return resultJson;
        } else {
            genTable = this.get(genTable.getId());
            String tableName = genTable.getName();
            List<GenTableColumn> getTableColumnList = genTable.getColumnList();

            StringBuffer sql;
            //Iterator localIterator;
            if ("mysql".equals(dbType)) {
                sql = new StringBuffer();
                sql.append("drop table if exists " + tableName + " ;");
                this.buildTable(sql.toString());
                sql = new StringBuffer();
                sql.append("create table " + tableName + " (");

                String pk = "";
                for(GenTableColumn column : getTableColumnList) {
                    if (Global.YES.equals(column.getIsPk())) {
                        sql.append("  " + column.getName() + " "
                                + column.getJdbcType() + " comment '"
                                + column.getComments() + "',");
                        pk = pk + column.getName() + ",";
                    } else {
                        sql.append("  " + column.getName() + " "
                                + column.getJdbcType() + " comment '"
                                + column.getComments() + "',");
                    }
                }

                sql.append("primary key (" + pk.substring(0, pk.length() - 1)
                        + ") ");
                sql.append(") comment '" + genTable.getComments() + "'");
                this.buildTable(sql.toString());
            } else {
                if ("oracle".equals(dbType)) {
                    sql = new StringBuffer();
                    try {
                        sql.append("DROP TABLE " + tableName);
                        this.buildTable(sql.toString());
                    } catch (Exception localException) {
                    }

                    sql = new StringBuffer();
                    sql.append("create table " + tableName + " (");

                    String pk = "";
                    for(GenTableColumn column : getTableColumnList) {
                        String jdbctype = column.getJdbcType();
                        if (jdbctype.equalsIgnoreCase("integer"))
                            jdbctype = "number(10,0)";
                        else if (jdbctype.equalsIgnoreCase("datetime"))
                            jdbctype = "TIMESTAMP(0)";
                        else if (jdbctype.contains("nvarchar("))
                            jdbctype = jdbctype.replace("nvarchar", "nvarchar2");
                        else if (jdbctype.contains("varchar("))
                            jdbctype = jdbctype.replace("varchar", "nvarchar2");
                        else if (jdbctype.equalsIgnoreCase("double"))
                            jdbctype = "float(24)";
                        else if (jdbctype.equalsIgnoreCase("longblob"))
                            jdbctype = "blob";
                        else if (jdbctype.equalsIgnoreCase("longtext")) {
                            jdbctype = "nclob";
                        }
                        if (Global.YES.equals(column.getIsPk())) {
                            sql.append("  " + column.getName() + " " + jdbctype
                                    + ",");
                            pk = pk + column.getName();
                        } else {
                            sql.append("  " + column.getName() + " " + jdbctype
                                    + ",");
                        }
                    }

                    sql = new StringBuffer(sql.substring(0, sql.length() - 1) + ")");
                    this.buildTable(sql.toString());
                    this.buildTable("comment on table " + tableName
                            + " is  '" + genTable.getComments() + "'");
                    for (GenTableColumn column : getTableColumnList) {
                        this.buildTable("comment on column "
                                + tableName + "." + column.getName() + " is  '"
                                + column.getComments() + "'");
                    }
                    this.buildTable("alter table " + tableName
                            + " add primary key (" + pk + ") ");
                } else if ("dm".equals(dbType)) {
                    sql = new StringBuffer();
                    DataSourceContextHolder.setDbType(genTable.getDatasource());
                    try {
                        sql.append("DROP TABLE " + tableName + ";");

                        this.buildTable(sql.toString());
                    } catch (Exception localException) {
                    }
                    sql = new StringBuffer();
                    sql.append("create table " + tableName + " (");

                    String pk = "";
                    for(GenTableColumn column : getTableColumnList) {
                        String jdbctype = column.getJdbcType();
                        if (jdbctype.equalsIgnoreCase("integer"))
                            jdbctype = "number(10,0)";
                        else if (jdbctype.equalsIgnoreCase("datetime"))
                            jdbctype = "TIMESTAMP(0)";
                        else if (jdbctype.contains("nvarchar("))
                            jdbctype = jdbctype.replace("nvarchar", "varchar2");
                        else if (jdbctype.contains("varchar("))
                            jdbctype = jdbctype.replace("varchar", "varchar2");
                        else if (jdbctype.equalsIgnoreCase("double"))
                            jdbctype = "number(22,4)";
                        else if (jdbctype.equalsIgnoreCase("longblob"))
                            jdbctype = "blob";
                        else if (jdbctype.equalsIgnoreCase("longtext")) {
                            jdbctype = "text";
                        }
                        if (Global.YES.equals(column.getIsPk())) {
                            sql.append("  " + column.getName() + " " + jdbctype
                                    + ",");
                            pk = pk + column.getName();
                        } else {
                            sql.append("  " + column.getName() + " " + jdbctype
                                    + ",");
                        }
                    }

                    sql = new StringBuffer(sql.substring(0, sql.length() - 1) + ")" + ";");
                    this.buildTable(sql.toString());
                    this.buildTable("comment on table " + tableName
                            + " is  '" + genTable.getComments() + "'" + ";");
                    for (GenTableColumn column : getTableColumnList) {
                        this.buildTable("comment on column "
                                + tableName + "." + column.getName() + " is  '"
                                + column.getComments() + "'" + ";");
                    }

                    this.buildTable("alter table " + tableName
                            + " add constraint PK_" + tableName + "_"
                            + pk.replaceAll(",", "_") + " primary key (" + pk
                            + ") " + ";");
                    DataSourceContextHolder.clearDbType();
                } else if (("mssql".equals(dbType)) || ("sqlserver".equals(dbType))) {
                    // StringBuffer sql;
                    (sql = new StringBuffer())
                            .append("if exists (select * from sysobjects where id = object_id(N'["
                                    + tableName
                                    + "]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)  drop table ["
                                    + tableName + "]");
                    this.buildTable(sql.toString());
                    (sql = new StringBuffer()).append("create table " + tableName
                            + " (");
                    String pk = "";
                    for(GenTableColumn column : getTableColumnList) {
                        String jdbctype = column.getJdbcType();
                        if (jdbctype.equalsIgnoreCase("integer"))
                            jdbctype = "int";
                        else if (jdbctype.equalsIgnoreCase("datetime"))
                            jdbctype = "datetime2";
                        else if (jdbctype.contains("nvarchar("))
                            jdbctype = jdbctype;
                        else if (jdbctype.contains("varchar("))
                            jdbctype = jdbctype.replace("varchar", "nvarchar");
                        else if (jdbctype.equalsIgnoreCase("double"))
                            jdbctype = "decimal(22,4)";
                        else if (jdbctype.equalsIgnoreCase("longblob"))
                            jdbctype = "varbinary(MAX)";
                        else if (jdbctype.equalsIgnoreCase("longtext")) {
                            jdbctype = "ntext";
                        }
                        if ("1".equals(column.getIsPk())) {
                            sql.append("  " + column.getName() + " " + jdbctype
                                    + ",");
                            pk = pk + column.getName();
                        } else {
                            sql.append("  " + column.getName() + " " + jdbctype
                                    + ",");
                        }
                    }
                    sql.append("primary key (" + pk + ") ");
                    sql.append(")");
                    this.buildTable(sql.toString());
                }
            }
            this.syncSave(genTable);
            resultJson.setCode(0);
            resultJson.setMsg("同步数据库成功");
            resultJson.setMsg_en("Sync table success");
            return resultJson;
        }

    }

    @Transactional
    public ResultJson removeDynamic(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        if (StringUtil.isNotBlank(genTable.getId())) {
            genTable = get(genTable.getId());
        }
        this.delete(genTable);
        genSchemeDao.delete(genSchemeDao.findUniqueByProperty("gen_table_id", genTable.getId()));
        resultJson.setMsg_en("Remove gentable success");
        resultJson.setMsg("移除成功");
        resultJson.setCode(0);
        return resultJson;
    }

    @Transactional
    public ResultJson deleteDynamic(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        if (StringUtil.isNotBlank(genTable.getId())) {
            genTable = get(genTable.getId());
        }
        this.delete(genTable);
        genSchemeDao.delete(genSchemeDao.findUniqueByProperty("gen_table_id", genTable.getId()));
        StringBuffer sql = new StringBuffer();
        if ("mysql".equals(dbType)) {
            sql.append("drop table if exists " + genTable.getName() + " ;");
        } else if ("oracle".equals(dbType) || "dm".equals(dbType))
            sql.append("DROP TABLE " + genTable.getName());
        else if (("mssql".equals(dbType)) || ("sqlserver".equals(dbType))) {
            sql.append("if exists (select * from sysobjects where id = object_id(N'["
                    + genTable.getName()
                    + "]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)  drop table ["
                    + genTable.getName() + "]");
        }
        try {
            this.buildTable(sql.toString());
            resultJson.setMsg_en("Delete gentable success");
            resultJson.setMsg("删除业务表记录和数据库表成功");
            resultJson.setCode(0);
        } catch (Exception localException) {
            resultJson.setMsg_en("Table does not exists, remove gentable success");
            resultJson.setMsg("数据库表不存在，删除业务表记录成功");
            resultJson.setCode(0);
        }
        return resultJson;
    }

    @Transactional
    public ResultJson copyDynamic(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        if (StringUtil.isNotBlank(genTable.getId())) {
            genTable = get(genTable.getId());
        }
        this.copy(genTable);
        resultJson.setMsg_en("Copy gentable success");
        resultJson.setMsg("复制动态表单成功");
        resultJson.setCode(0);
        return resultJson;
    }

    @Transactional
    public ResultJson importListDynamic() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg_en("Get table success");
        resultJson.setMsg("获取数据表成功");
        resultJson.put("tableList", this.findTableListFormDb(new GenTable()));
        return resultJson;
    }

    @Transactional
    public ResultJson importDynamic(GenTable genTable) {
        ResultJson resultJson = new ResultJson();
        if (!StringUtil.isBlank(genTable.getName())) {
            if (this.existTable(genTable.getName())) {
                resultJson.setCode(0);
                resultJson.setMsg_en("Table exists");
                resultJson.setMsg("表已经添加！");
                return resultJson;
            }
            (genTable = this.getTableFormDb(genTable, dbType))
                    .setTableType("0");
            genTable.setIsBuildXform(Global.YES);
            //save
            genTable.setClassName("Zform");
            genTable.setFormType("dynamic");
            genTable.setTableType("0");
            genTable.setIsRelease(Global.NO);
            genTable.setIsSync(Global.NO);
            genTable.preInsert();
            if (StringUtil.isNotEmpty(genTable.getComments())) {
                genTable.setCommentsEn(transService.getTransResultString(genTable.getComments(), "auto", "en"));
            }
            this.genTableDao.insert(genTable);
            for (GenTableColumn column : genTable.getColumnList()) {
                if (StringUtil.isNotEmpty(column.getComments())) {
                    column.setCommentsEn(transService.getTransResultString(column.getComments(), "auto", "en"));
                }
                column.setGenTable(genTable);
                column.setId(null);
                column.preInsert();
                this.genTableColumnDao.insert(column);
            }
            resultJson.setCode(0);
            resultJson.setMsg_en("Import table success");
            resultJson.setMsg("数据库导入表单成功！");
        }
        return resultJson;
    }

    @Transactional
    public ResultJson buildViewDynamic(GenScheme genScheme) {
        ResultJson resultJson = new ResultJson();
        if (StringUtil.isBlank(genScheme.getPackageName()))
            genScheme.setPackageName("com.gt_plus.modules");
        GenScheme oldGenScheme;
        if ((oldGenScheme = genSchemeDao.findUniqueByProperty(
                "gen_table_id", genScheme.getGenTable().getId())) != null) {
            genScheme = oldGenScheme;
        }
        genScheme.setGenTable(get(genScheme.getGenTable().getId()));
        resultJson.setCode(0);
        resultJson.setMsg("获取发布信息成功");
        resultJson.setMsg_en("Get build message success");
        resultJson.put("genScheme", genScheme);
        return resultJson;
    }

    @Transactional
    public ResultJson buildDynamic(GenScheme genScheme, String currentUserName) {
        ResultJson resultJson = new ResultJson();
        GenTable genTable = this.get(genScheme.getGenTable().getId());
        genTable.setIsRelease(Global.YES);
        this.save(genTable);
        if (StringUtil.isBlank(genScheme.getId())) {
            genScheme.setIsNewRecord(true);
            genScheme.preInsert();
            this.genSchemeDao.insert(genScheme);
        } else {
            genScheme.preUpdate();
            this.genSchemeDao.update(genScheme);
        }
        String result = this.xgenerateCode(genScheme, currentUserName);
        resultJson.setCode(0);
        resultJson.setMsg("发布成功");
        resultJson.setMsg_en("Build Success");
        return resultJson;
    }

    private String xgenerateCode(GenScheme genScheme, String currentUserName) {

        StringBuilder result = new StringBuilder();
        String timePath = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        GenTable genTable = genTableDao.get(genScheme.getGenTable().getId());
        genScheme.setGenTable(genTable);
        genTable.setColumnList(genTableColumnDao.findList(new GenTableColumn(
                new GenTable(genTable.getId()))));
        GenConfig config = GenUtil.getConfig();

        boolean isMobile = Global.YES.equals(genTable.getIsMobile()) ? true : false;
        List<GenTemplate> templateList = GenUtil.getTemplateList(config, genScheme.getGenTable().getFormType(), false, isMobile);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String username = currentUserName;
        String groupId = UUID.randomUUID().toString();
        String modName = genScheme.getModuleName() + File.separator + genTable.getName();
        String fileId = "";
        String fileName = "";
        String path = "";
        if (genTable.getChildList() == null || genTable.getChildList().size() == 0) {
            if (false == genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
                GenTable parentTable = new GenTable();
                parentTable.setParentTable(genTable.getName());
                List<GenTable> childList = genTableDao.findList(parentTable);
                for (GenTable subTable : childList) {
                    GenTableColumn genTableColumn = new GenTableColumn();
                    genTableColumn.setGenTable(new GenTable(subTable.getId()));
                    subTable.setColumnList(this.genTableColumnDao.findList(genTableColumn));
                }
                genTable.setChildList(childList);
            }
        }
        genScheme.setGenTable(genTable);
        Map<String, Object> model = GenUtil.getDataModel(genScheme, dbType);

        for (GenTemplate tpl : templateList) {
            result.append(GenUtil.xgenerateToFile(false,
                        genScheme.getGenTable().getFormType(),
                    tpl,
                    genScheme,
                    genScheme.getReplaceFile(),
                    timePath,
                    projectPath,
                    genKey,
                    genUrl,
                    dbType));

            fileName = FreeMarkerUtil.renderString(tpl.getFileName(), model);
            fileId = UUID.randomUUID().toString();
            path = projectPath + File.separator + timePath + File.separator
                    + StringUtil.replaceEach(FreeMarkerUtil.renderString(tpl.getFilePath() + "/", model),
                    new String[]{"//", "/", "."}, new String[]{File.separator, File.separator, File.separator})
                    + FreeMarkerUtil.renderString(tpl.getFileName(), model);
            GenCode genCode = new GenCode();
            genCode.setCreateTime(date);
            genCode.setUsername(username);
            genCode.setGroupId(groupId);
            genCode.setModuleName(modName);
            genCode.setFileId(fileId);
            genCode.setFileName(fileName);
            genCode.setPath(path);
            if (genCode.getIsNewRecord()) {
                genCode.preInsert();
                genCodeDao.insert(genCode);
            } else {
                genCode.preUpdate();
                genCodeDao.update(genCode);
            }
        }
        return result.toString();
    }

    @Async
    public void refreshGenTableCache(String formNo) {
        if (StringUtil.isNotEmpty(formNo)) {
            cacheFeign.deleteLikeHash(GenUtil.GENTABLE_CACHE, formNo);
            this.getGenTableWithDefination(formNo);
        } else {
            Set<String> hashKeySet = cacheFeign.getHashKeys(GenUtil.GENTABLE_CACHE);
            for (String hashKey : hashKeySet) {
                cacheFeign.deleteLikeHash(GenUtil.GENTABLE_CACHE, hashKey);
            }
            List<GenTable> list = this.findAll();
            for (GenTable genTable : list) {
                this.getGenTableWithDefination(genTable.getName());
            }
        }
    }

    @Transactional(readOnly = false)
    public String getImportTemplateFileGroupIdByFormNo(String formNo, String fileRoot) {
        String groupId = "";
        GenTable genTable = this.getGenTableWithDefination(formNo);
        if (StringUtil.isNotEmpty(genTable.getImportTemplateFile())) {
            groupId = genTable.getImportTemplateFile();
        } else {
            //create import template file by isImport by genTableColumn
            List<String> columnList = Lists.newArrayList();
            if (StringUtil.isNotEmpty(genTable.getImportList())) {
                String[] importList = genTable.getImportList().split(",");
                for(int i=0; i<importList.length; i++) {
                    for(GenTableColumn column : genTable.getColumnList()) {
                        if (column.getJavaField().equals(importList[i])) {
                            columnList.add(column.getComments());
                            break;
                        }
                    }
                }
            } else {
                for (GenTableColumn column : genTable.getColumnList()) {
                    if (Global.YES.equals(column.getIsForm())) {
                        columnList.add(column.getComments());
                    }
                }
            }
            String fileName = genTable.getName() + "_import.xlsx";
            createTemplate(columnList, fileRoot, fileName);
            groupId = saveSysFile(fileRoot, fileName);
            GenTableView theGenTable = new GenTableView();
            theGenTable.setId(genTable.getId());
            theGenTable.setImportTemplateFile(groupId);
            genTableDao.saveImport(theGenTable);
            this.refreshGenTableCache(formNo);
        }
        return groupId;
    }

    @Transactional(readOnly = false)
    public String getExportFilePathByFormNo(String formNo, String fileRoot) {
        String filePath ;
        GenTable genTable = this.getGenTableWithDefination(formNo);
        if (StringUtil.isEmpty(genTable.getExportTemplateFile())) {
            //create export template file by isExport in genTableColumn
            List<String> columnList = Lists.newArrayList();
            if (StringUtil.isNotEmpty(genTable.getExportList())) {
                String[] exportList = genTable.getExportList().split(",");
                for (int i = 0; i < exportList.length; i++) {
                    for (GenTableColumn column : genTable.getColumnList()) {
                        if (column.getJavaField().equals(exportList[i])) {
                            columnList.add(column.getComments());
                            break;
                        }
                    }
                }
            } else {
                for (GenTableColumn column : genTable.getColumnList()) {
                    if (Global.YES.equals(column.getIsForm())) {
                        columnList.add(column.getComments());
                    }
                }
            }
            String fileName = genTable.getName() + "_export.xlsx";
            createTemplate(columnList, fileRoot, fileName);
            String groupId = saveSysFile(fileRoot, fileName);
            GenTableView theGenTable = new GenTableView();
            theGenTable.setId(genTable.getId());
            theGenTable.setExportTemplateFile(groupId);
            genTableDao.saveExport(theGenTable);
            this.refreshGenTableCache(formNo);
            filePath = fileRoot + "/" + fileName;
        } else {
            filePath = sysFileService.getFirstFilePathByGroupId(genTable.getExportTemplateFile(), fileRoot);
        }
        return filePath;
    }

    private String saveSysFile(String fileRoot, String fileName) {
        SysFile sysFile = new SysFile();
        String groupId = UUID.randomUUID().toString().replaceAll("-", "");
        sysFile.setGroupId(groupId);
        sysFile.setName(fileName);
        sysFile.setExt("xlsx");
        File file = new File(fileRoot + "/" + fileName);
        long size = file.length();
        String sizeStr;
        if (size / 1024 < 1024) {
            sizeStr = "(" + new DecimalFormat("0.0").format(size / 1024D) + "K)";
        } else {
            sizeStr = "(" + new DecimalFormat("0.0").format(size / 1024D / 1024D) + "M)";
        }
        sysFile.setSize(sizeStr);
        sysFile.setPath("/" + fileName);
        sysFile.setUploadTime(new Date());
        sysFile.setType("FILE");
        sysFile.setSort(1);
        sysFile.setSecFlag(Global.NO);
        sysFile.setVisitCount(0);
        sysFile.setToPdf(Global.NO);
        sysFileService.save(sysFile);
        return groupId;
    }

    private void createTemplate(List<String> columnList, String outPath, String outName) {
        String inPath = (new ApplicationHome(getClass())).getSource().getParentFile().toString();
        String inName = "/classes/templates/modules/excel/template_blank.xlsx";
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(inPath + inName);
            XSSFWorkbook xssfWorkbook = createExcel(columnList, fileInputStream);
            fileOutputStream = new FileOutputStream(outPath + "/" + outName);
            xssfWorkbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private XSSFWorkbook createExcel(List<String> columnList, FileInputStream fileInputStream) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFSheet sheet0 = xssfWorkbook.getSheetAt(0);
        XSSFRow row0 = sheet0.createRow(0);
        for (int i = 0; i < columnList.size(); i++) {
            String value = columnList.get(i);
            XSSFCell cell = row0.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(value);
        }
        return xssfWorkbook;
    }

    @Transactional(readOnly = false)
    public  void saveImportAndExport(GenTableView genTable) {
        if(StringUtil.isNotEmpty(genTable.getImportTemplateFile())) {
            LinkedHashMap<String, Object> map = sysFileService.getFileList(genTable.getImportTemplateFile());
            if (((List<SysFile>)map.get("files")).size() == 0) {
                genTable.setImportTemplateFile("");
            }
        }
        if(StringUtil.isNotEmpty(genTable.getExportTemplateFile())) {
            LinkedHashMap<String, Object> map = sysFileService.getFileList(genTable.getExportTemplateFile());
            if (((List<SysFile>)map.get("files")).size() == 0) {
                genTable.setExportTemplateFile("");
            }
        }
        //sort list
        GenTable theGenTable = this.getGenTableWithDefination(genTable.getName());
        String sortedImportList = "";
        String sortedExportList = "";
        String importList = "," + (genTable.getImportList() == null ? "" : genTable.getImportList()) + ",";
        String exportList = "," + (genTable.getExportList() == null ? "" : genTable.getExportList()) + ",";
        boolean noImportList = true;
        boolean noExportList = true;
        if (StringUtil.isNotEmpty(genTable.getImportList())) {
            noImportList = false;
        }
        if (StringUtil.isNotEmpty(genTable.getExportList())) {
            noExportList = false;
        }
        for(GenTableColumn column : theGenTable.getColumnList()) {
            if (importList.indexOf("," + column.getJavaField() + ",") != -1
                    || noImportList && Global.YES.equals(column.getIsForm())) {
                sortedImportList += "," + column.getJavaField();
            }
            if (exportList.indexOf("," + column.getJavaField() + ",") != -1
                    || noExportList && Global.YES.equals(column.getIsForm())) {
                sortedExportList += "," + column.getJavaField();
            }
        }
        if (sortedImportList.indexOf(",") == 0) sortedImportList = sortedImportList.substring(1);
        if (sortedExportList.indexOf(",") == 0) sortedExportList = sortedExportList.substring(1);

        genTable.setImportList(sortedImportList);
        genTable.setExportList(sortedExportList);
        genTableDao.saveImportAndExport(genTable);
        refreshGenTableCache(genTable.getName());
    }
}
