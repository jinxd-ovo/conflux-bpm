package com.jeestudio.common.entity.gen;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeestudio.common.entity.system.DictGenView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Gen table column view
 * @author: whl
 * @Date: 2020-02-07
 */
public class GenTableColumnView implements Serializable {

    private String id;
    private String genTableId;
    private String showType;
    private String isOneLine;
    private String isNull;
    private String comments;

    @JsonProperty(value = "comments_EN")
    private String commentsEn;

    private String javaField;
    private String maxLength;
    private String minLength;
    private String minValue;
    private String maxValue;
    private String friendlyJdbcType;
    private String javaType;
    private String jdbcType;
    private String queryType;
    private String validateType;
    private String isForm;
    private String isQuery;
    private String isList;
    private String formSort;
    private String searchSort;
    private String isReadonly;
    private String defaultValue;
    private String name;
    private String listSort;
    private String tableName;
    private String fieldLabels;
    private String fieldKeys;
    private String searchLabel;
    private String searchKey;
    private String dictType;
    private List<DictGenView> dictList;
    private String dateType;
    private String isImport;
    private String isExport;
    private String blockChainParam1;
    private String blockChainParam2;
    private String blockChainParam3;
    private String blockChainParam4;
    private String blockChainParam5;
    private String blockChainParam6;

    public void setId(String id) {
        this.id = id;
    }

    public void setGenTableId(String genTableId) {
        this.genTableId = genTableId;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public void setIsOneLine(String isOneLine) {
        this.isOneLine = isOneLine;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setJavaField(String javaField) {
        this.javaField = javaField;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    public void setIsForm(String isForm) {
        this.isForm = isForm;
    }

    public void setIsQuery(String isQuery) {
        this.isQuery = isQuery;
    }

    public void setIsList(String isList) {
        this.isList = isList;
    }

    public void setFormSort(String formSort) {
        this.formSort = formSort;
    }

    public void setSearchSort(String searchSort) {
        this.searchSort = searchSort;
    }

    public void setIsReadonly(String isReadonly) {
        this.isReadonly = isReadonly;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setListSort(String listSort) {
        this.listSort = listSort;
    }

    public String getId() {
        return id;
    }

    public String getGenTableId() {
        return genTableId;
    }

    public String getShowType() {
        return showType==null?"":showType;
    }

    public String getIsOneLine() {
        return isOneLine==null?"":isOneLine;
    }

    public String getIsNull() {
        return isNull==null?"":isNull;
    }

    public String getComments() {
        return comments;
    }

    public String getCommentsEn() {
        return commentsEn;
    }

    public void setCommentsEn(String commentsEn) {
        this.commentsEn = commentsEn;
    }

    public String getJavaField() {
        return javaField;
    }

    public String getMaxLength() {
        return maxLength==null?"":maxLength;
    }

    public String getMinLength() {
        return minLength==null?"":minLength;
    }

    public String getMinValue() {
        return minValue==null?"":minValue;
    }

    public String getMaxValue() {
        return maxValue==null?"":maxValue;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getValidateType() {
        return validateType==null?"":validateType;
    }

    public String getIsForm() {
        return isForm;
    }

    public String getIsQuery() {
        return isQuery;
    }

    public String getIsList() {
        return isList;
    }

    public String getFormSort() {
        String sort = "";
        if(this.formSort != null){
            int num = this.formSort.indexOf(".");
            if(num != -1){
                sort = this.formSort.substring(0,num);
            }else {
                sort = this.formSort;
            }
        }
        return sort;
    }

    public String getSearchSort() {
        String sort = "";
        if(this.searchSort != null){
            int num = this.searchSort.indexOf(".");
            if(num != -1){
                sort = this.searchSort.substring(0,num);
            }else {
                sort = this.searchSort;
            }
        }
        return sort;
    }

    public String getIsReadonly() {
        return isReadonly;
    }

    public String getDefaultValue() {
        return defaultValue==null?"":defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getListSort() {
        String sort = "";
        if(this.listSort != null){
            int num = this.listSort.indexOf(".");
            if(num != -1){
                sort = this.listSort.substring(0,num);
            }else {
                sort = this.listSort;
            }
        }
        return sort;
    }

    public void setFriendlyJdbcType(String friendlyJdbcType) {
        this.friendlyJdbcType = friendlyJdbcType;
    }

    public String getTableName() {
        return tableName==null?"":tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldLabels() {
        return fieldLabels==null?"":fieldLabels;
    }

    public void setFieldLabels(String fieldLabels) {
        this.fieldLabels = fieldLabels;
    }

    public String getFieldKeys() {
        return fieldKeys==null?"":fieldKeys;
    }

    public void setFieldKeys(String fieldKeys) {
        this.fieldKeys = fieldKeys;
    }

    public String getSearchLabel() {
        return searchLabel==null?"":searchLabel;
    }

    public void setSearchLabel(String searchLabel) {
        this.searchLabel = searchLabel;
    }

    public String getSearchKey() {
        return searchKey==null?"":searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getDictType() {
        return dictType==null?"":dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public List<DictGenView> getDictList() {
        if(this.dictList == null){
            this.dictList = new ArrayList<>();
        }
        return dictList;
    }

    public void setDictList(List<DictGenView> dictList) {
        this.dictList = dictList;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getIsImport() {
        return isImport;
    }

    public void setIsImport(String isImport) {
        this.isImport = isImport;
    }

    public String getIsExport() {
        return isExport;
    }

    public void setIsExport(String isExport) {
        this.isExport = isExport;
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
}
