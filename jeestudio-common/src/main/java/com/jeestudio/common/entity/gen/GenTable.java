package com.jeestudio.common.entity.gen;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.StringUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

/**
 * @Description: Gen table
 * @author: houxl
 * @Date: 2020-01-18
 */
public class GenTable extends DataEntity<GenTable> {

    private static final long serialVersionUID = 1L;

    public static final String SQLMAP_SQLCOLUMNS = "sqlColumns";
    public static final String SQLMAP_SQLCOLUMNS_FRIENDLY = "sqlColumnsFriendly";
    public static final String SQLMAP_SQLINSERT = "sqlInsert";
    public static final String SQLMAP_SQLJOINS = "sqlJoins";
    public static final String SQLMAP_SQLUPDATE = "sqlUpdate";
    public static final String SQLMAP_SQLORDERBY = "sqlOrderby";

    private static final String DEFAULT_STATUS_START = "10";
    private static final String DEFAULT_STATUS_BREAK = "00";
    private static final String DEFAULT_STATUS_END = "99";

    public static final String TABLE_TYPE_SIMPLE = "0";
    public static final String TABLE_TYPE_TREE = "3";
    public static final String TABLE_TYPE_RIGHTTABLE = "4";

    private String name;
    private String oldName;
    private String comments;

    @JsonProperty(value = "comments_EN")
    private String commentsEn;

    private String tableType;
    private String className;
    private String parentTable;
    private String parentTableFk;
    private String parentClassName;         //Parent table class name
    private String parentUrlPrefix;         //Parent action url
    private String parentSimpleJavaField;   //Main table simpleJavaField corresponding to foreign key field
    private String parentJavaFieldId;       //Main table parentJavaFieldId corresponding to foreign key field
    private String parentJavaFieldName;     //Main table parentJavaFieldName corresponding to foreign key field
    private String isSync;
    private List<GenTableColumn> columnList = (List) Lists.newArrayList();
    private String nameLike;
    private List<String> pkList;
    private GenTable parent;
    private List<GenTable> childList = (List) Lists.newArrayList();

    private String extJsp;
    private String extJs;
    private String extJava;

    private HashMap<String, String> extJspMap;
    private HashMap<String, String> extJsMap;
    private HashMap<String, String> extJavaMap;

    private String isBuildAdd;
    private String isBuildEdit;
    private String isBuildDel;
    private String isBuildImport;
    private String isBuildOperate;

    private String datasource;
    private String isVersion;

    private String isProcessDefinition;
    private String processDefinitionCategory;
    private String processModelName;

    private String isBuildXform;
    private String isBuildSecret;
    private String isBuildContent;

    private String formType;

    private String scount;    //short text count
    private String mcount;    //middle text count
    private String lcount;    //long text count
    private String dcount;    //date count

    private String isRelease;
    private String isMobile;

    private String sqlColumns;
    private String sqlColumnsFriendly;
    private String sqlJoins;
    private String sqlInsert;
    private String sqlUpdate;
    private String sqlSort;
    private JSONArray json;
    private JSONArray children;
    private String mobileIcon;

    private String statusStart; //the status code as start
    private String statusBreak; //the status code as terminated
    private String statusEnd;   //the status code as end
    private String dbName;
    private boolean hasChildren;

    private String isScroll;
    private String isRowedit;

    private String isBuildExport;
    private String exportList;
    private String exportTemplatePath;
    private String exportRuleName;
    private String importList;
    private String importTemplateFile;
    private String exportTemplateFile;
    private String isCustom;
    private String blockChainParam1;
    private String blockChainParam2;
    private String blockChainParam3;
    private String blockChainParam4;
    private String blockChainParam5;
    private String blockChainParam6;
    private String module;

    public GenTable() {
    }

    public GenTable(String id) {
        super(id);
    }

    public GenTable(GenTable parent) {
        this.parent = parent;
        this.parentTable = parent.getName();
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Length(min = 1, max = 200)
    public String getName() {
        return StringUtils.lowerCase(this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCommentsEn() {
        return commentsEn;
    }

    public void setCommentsEn(String commentsEn) {
        this.commentsEn = commentsEn;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParentTable() {
        return StringUtils.lowerCase(this.parentTable);
    }

    public void setParentTable(String parentTable) {
        this.parentTable = parentTable;
    }

    public String getParentTableFk() {
        return StringUtils.lowerCase(this.parentTableFk);
    }

    public void setParentTableFk(String parentTableFk) {
        this.parentTableFk = parentTableFk;
    }

    public String getParentClassName() {
        if (StringUtils.isEmpty(this.parentClassName) && this.getParent() != null) {
            return this.getParent().getClassName();
        } else {
            return this.parentClassName;
        }
    }

    public void setParentClassName(String parentClassName) {
        this.parentClassName = parentClassName;
    }


    public String getParentUrlPrefix() {
        return parentUrlPrefix;
    }

    public void setParentUrlPrefix(String parentUrlPrefix) {
        this.parentUrlPrefix = parentUrlPrefix;
    }


    public String getParentSimpleJavaField() {
        return parentSimpleJavaField;
    }

    public void setParentSimpleJavaField(String parentSimpleJavaField) {
        this.parentSimpleJavaField = parentSimpleJavaField;
    }

    public String getParentJavaFieldId() {
        return parentJavaFieldId;
    }

    public void setParentJavaFieldId(String parentJavaFieldId) {
        this.parentJavaFieldId = parentJavaFieldId;
    }

    public String getParentJavaFieldName() {
        return parentJavaFieldName;
    }

    public void setParentJavaFieldName(String parentJavaFieldName) {
        this.parentJavaFieldName = parentJavaFieldName;
    }

    public List<String> getPkList() {
        return this.pkList;
    }

    public void setPkList(List<String> pkList) {
        this.pkList = pkList;
    }

    public String getNameLike() {
        return this.nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    @JsonBackReference
    public GenTable getParent() {
        return this.parent;
    }

    public void setParent(GenTable parent) {
        this.parent = parent;
    }

    public List<GenTableColumn> getColumnList() {
        return this.columnList;
    }

    public void setColumnList(List<GenTableColumn> columnList) {
        this.columnList = columnList;
    }

    public List<GenTable> getChildList() {
        return this.childList;
    }

    public void setChildList(List<GenTable> childList) {
        this.childList = childList;
    }

    public String getNameAndComments() {
        return getName() + (this.comments == null ? "" : new StringBuilder(":").append(this.comments).toString());
    }

    public List<String> getImportGridJavaList() {
        List importList = (List) Lists.newArrayList();
        for (GenTableColumn column : getColumnList()) {
            if ((column.getTableName() != null) && (false == column.getTableName().equals(""))) {
                if ((StringUtils.indexOf(column.getJavaType(), ".") != -1) && (false == importList.contains(column.getJavaType()))) {
                    importList.add(column.getJavaType());
                }
            }
        }
        return importList;
    }

    public List<String> getImportGridJavaDaoList() {
        boolean isNeedList = false;
        List importList = (List) Lists.newArrayList();
        for (GenTableColumn column : getColumnList()) {
            if ((column.getTableName() != null) && (false == column.getTableName().equals(""))) {
                if ((StringUtils.indexOf(column.getJavaType(), ".") != -1) && (false == importList.contains(column.getJavaType()))) {
                    importList.add(column.getJavaType());
                    isNeedList = true;
                }
            }
        }
        if ((isNeedList) &&
                (false == importList.contains("java.util.List"))) {
            importList.add("java.util.List");
        }

        return importList;
    }

    public Boolean getParentExists() {
        return StringUtils.isNotBlank(this.parentTableFk) ? Boolean.valueOf(true) : Boolean.valueOf(false);
    }

    public Boolean getCreateDateExists() {
        for (GenTableColumn c : this.columnList) {
            if ("create_date".equalsIgnoreCase(c.getName())) {
                return Boolean.valueOf(true);
            }
        }
        return Boolean.valueOf(false);
    }

    public Boolean getUpdateDateExists() {
        for (GenTableColumn c : this.columnList) {
            if ("update_date".equalsIgnoreCase(c.getName())) {
                return Boolean.valueOf(true);
            }
        }
        return Boolean.valueOf(false);
    }

    public Boolean getDelFlagExists() {
        for (GenTableColumn c : this.columnList) {
            if ("del_flag".equalsIgnoreCase(c.getName())) {
                return Boolean.valueOf(true);
            }
        }
        return Boolean.valueOf(false);
    }

    public Boolean getOwnerCodeExists() {
        for (GenTableColumn c : this.columnList) {
            if ("owner_code".equalsIgnoreCase(c.getName())) {
                return Boolean.valueOf(true);
            }
        }
        return Boolean.valueOf(false);
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }

    public String getIsSync() {
        return this.isSync;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getTableType() {
        return this.tableType;
    }

    public String getExtJsp() {
        return extJsp;
    }

    public void setExtJsp(String extJsp) {
        this.extJsp = extJsp;
    }

    public String getExtJs() {
        return extJs;
    }

    public void setExtJs(String extJs) {
        this.extJs = extJs;
    }

    public String getExtJava() {
        return extJava;
    }

    public void setExtJava(String extJava) {
        this.extJava = extJava;
    }

    public HashMap<String, String> getExtJspMap() {
        extJspMap = new HashMap<String, String>();
        this.buildExtMap(extJspMap, this.extJsp);
        return extJspMap;
    }

    public HashMap<String, String> getExtJsMap() {
        extJsMap = new HashMap<String, String>();
        this.buildExtMap(extJsMap, this.extJs);
        return extJsMap;
    }

    public HashMap<String, String> getExtJavaMap() {
        extJavaMap = new HashMap<String, String>();
        this.buildExtMap(extJavaMap, this.extJava);
        return extJavaMap;
    }

    public String getIsBuildAdd() {
        return isBuildAdd;
    }

    public void setIsBuildAdd(String isBuildAdd) {
        this.isBuildAdd = isBuildAdd;
    }

    public String getIsBuildEdit() {
        return isBuildEdit;
    }

    public void setIsBuildEdit(String isBuildEdit) {
        this.isBuildEdit = isBuildEdit;
    }

    public String getIsBuildDel() {
        return isBuildDel;
    }

    public void setIsBuildDel(String isBuildDel) {
        this.isBuildDel = isBuildDel;
    }

    public String getIsBuildImport() {
        return isBuildImport;
    }

    public void setIsBuildImport(String isBuildImport) {
        this.isBuildImport = isBuildImport;
    }

    public String getIsBuildOperate() {
        return isBuildOperate;
    }

    public void setIsBuildOperate(String isBuildOperate) {
        this.isBuildOperate = isBuildOperate;
    }


    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getIsVersion() {
        return isVersion;
    }

    public void setIsVersion(String isVersion) {
        this.isVersion = isVersion;
    }

    public String getIsProcessDefinition() {
        if (StringUtils.isNotEmpty(this.processDefinitionCategory)) {
            return Global.YES;
        } else {
            return isProcessDefinition;
        }
    }

    public void setIsProcessDefinition(String isProcessDefinition) {
        this.isProcessDefinition = isProcessDefinition;
    }

    public String getProcessDefinitionCategory() {
        return processDefinitionCategory;
    }

    public void setProcessDefinitionCategory(String processDefinitionCategory) {
        this.processDefinitionCategory = processDefinitionCategory;
    }

    public String getIsBuildXform() {
        return isBuildXform;
    }

    public void setIsBuildXform(String isBuildXform) {
        this.isBuildXform = isBuildXform;
    }

    public String getIsBuildSecret() {
        return isBuildSecret;
    }

    public void setIsBuildSecret(String isBuildSecret) {
        this.isBuildSecret = isBuildSecret;
    }

    public String getIsBuildContent() {
        return isBuildContent;
    }

    public void setIsBuildContent(String isBuildContent) {
        this.isBuildContent = isBuildContent;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getScount() {
        return scount;
    }

    public void setScount(String scount) {
        this.scount = scount;
    }

    public String getMcount() {
        return mcount;
    }

    public void setMcount(String mcount) {
        this.mcount = mcount;
    }

    public String getLcount() {
        return lcount;
    }

    public void setLcount(String lcount) {
        this.lcount = lcount;
    }

    public String getDcount() {
        return dcount;
    }

    public void setDcount(String dcount) {
        this.dcount = dcount;
    }

    public String getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(String isRelease) {
        this.isRelease = isRelease;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    private void buildExtMap(HashMap<String, String> extMap, String content) {
        if (false == StringUtils.isEmpty(content)) {
            String[] extBlocks = content.split("<!-- ext ");
            for (int i = 1; i < extBlocks.length; i++) {
                String tempBlock = extBlocks[i];
                String tempKey = tempBlock.substring(0, tempBlock.indexOf("-->")).trim();
                String tempValue = tempBlock.substring(tempBlock.indexOf("-->") + 3);
                extMap.put(tempKey, tempValue);
            }
        }
    }

    public String getSqlColumns() {
        return sqlColumns;
    }

    public void setSqlColumns(String sqlColumns) {
        this.sqlColumns = sqlColumns;
    }

    public String getSqlColumnsFriendly() {
        return sqlColumnsFriendly;
    }

    public void setSqlColumnsFriendly(String sqlColumnsFriendly) {
        this.sqlColumnsFriendly = sqlColumnsFriendly;
    }

    public String getSqlJoins() {
        return sqlJoins;
    }

    public void setSqlJoins(String sqlJoins) {
        this.sqlJoins = sqlJoins;
    }

    public String getSqlInsert() {
        return sqlInsert;
    }

    public void setSqlInsert(String sqlInsert) {
        this.sqlInsert = sqlInsert;
    }

    public String getSqlUpdate() {
        return sqlUpdate;
    }

    public void setSqlUpdate(String sqlUpdate) {
        this.sqlUpdate = sqlUpdate;
    }

    public String getSqlSort() {
        return sqlSort;
    }

    public void setSqlSort(String sqlSort) {
        this.sqlSort = sqlSort;
    }

    public JSONArray getJson() {
        return json;
    }

    public void setJson(JSONArray json) {
        this.json = json;
    }

    public JSONArray getChildren() {
        return children;
    }

    public void setChildren(JSONArray children) {
        this.children = children;
    }

    public String getMobileIcon() {
        return mobileIcon;
    }

    public void setMobileIcon(String mobileIcon) {
        this.mobileIcon = mobileIcon;
    }

    public String getStatusStart() {
        if(StringUtil.isEmpty(statusStart)) {
            return DEFAULT_STATUS_START;
        } else {
            return statusStart;
        }
    }

    public void setStatusStart(String statusStart) {
        this.statusStart = statusStart;
    }

    public String getStatusBreak() {
        if(StringUtil.isEmpty(statusBreak)) {
            return DEFAULT_STATUS_BREAK;
        } else {
            return statusBreak;
        }
    }

    public void setStatusBreak(String statusBreak) {
        this.statusBreak = statusBreak;
    }

    public String getStatusEnd() {
        if(StringUtil.isEmpty(statusEnd)) {
            return DEFAULT_STATUS_END;
        } else {
            return statusEnd;
        }
    }

    public void setStatusEnd(String statusEnd) {
        this.statusEnd = statusEnd;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getProcessModelName() {
        return processModelName;
    }

    public void setProcessModelName(String processModelName) {
        this.processModelName = processModelName;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getIsScroll() {
        return isScroll;
    }

    public void setIsScroll(String isScroll) {
        this.isScroll = isScroll;
    }

    public String getIsRowedit() {
        return isRowedit;
    }

    public void setIsRowedit(String isRowedit) {
        this.isRowedit = isRowedit;
    }

    public String getIsBuildExport() {
        return isBuildExport;
    }

    public void setIsBuildExport(String isBuildExport) {
        this.isBuildExport = isBuildExport;
    }

    public String getExportList() {
        return exportList;
    }

    public void setExportList(String exportList) {
        this.exportList = exportList;
    }

    public String getExportTemplatePath() {
        return exportTemplatePath;
    }

    public void setExportTemplatePath(String exportTemplatePath) {
        this.exportTemplatePath = exportTemplatePath;
    }

    public String getExportRuleName() {
        return exportRuleName;
    }

    public void setExportRuleName(String exportRuleName) {
        this.exportRuleName = exportRuleName;
    }

    public String getImportList() {
        return importList;
    }

    public void setImportList(String importList) {
        this.importList = importList;
    }

    public String getImportTemplateFile() {
        return importTemplateFile;
    }

    public void setImportTemplateFile(String importTemplateFile) {
        this.importTemplateFile = importTemplateFile;
    }

    public String getExportTemplateFile() {
        return exportTemplateFile;
    }

    public void setExportTemplateFile(String exportTemplateFile) {
        this.exportTemplateFile = exportTemplateFile;
    }

    public String getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(String isCustom) {
        this.isCustom = isCustom;
    }

    public String getBlockChainParam1() {
        return blockChainParam1;
    }

    public void setBlockChainParam1(String blockChainParam1) {
        this.blockChainParam1 = blockChainParam1;
    }

    public String getBlockChainParam2() {
        return blockChainParam2;
    }

    public void setBlockChainParam2(String blockChainParam2) {
        this.blockChainParam2 = blockChainParam2;
    }

    public String getBlockChainParam3() {
        return blockChainParam3;
    }

    public void setBlockChainParam3(String blockChainParam3) {
        this.blockChainParam3 = blockChainParam3;
    }

    public String getBlockChainParam4() {
        return blockChainParam4;
    }

    public void setBlockChainParam4(String blockChainParam4) {
        this.blockChainParam4 = blockChainParam4;
    }

    public String getBlockChainParam5() {
        return blockChainParam5;
    }

    public void setBlockChainParam5(String blockChainParam5) {
        this.blockChainParam5 = blockChainParam5;
    }

    public String getBlockChainParam6() {
        return blockChainParam6;
    }

    public void setBlockChainParam6(String blockChainParam6) {
        this.blockChainParam6 = blockChainParam6;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}