package com.jeestudio.common.entity.gen;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.utils.StringUtil;
import org.hibernate.validator.constraints.Length;

/**
 * @Description: Gen table column
 * @author: houxl
 * @Date: 2020-01-18
 */
public class GenTableColumn extends DataEntity<GenTableColumn> {

    private static final long serialVersionUID = 1L;

    private GenTable genTable;
    private String name;
    private String comments;

    @JsonProperty(value = "comments_EN")
    private String commentsEn;

    private String jdbcType;
    private String javaType;
    private String javaField;
    private String isPk;
    private String isInsert;
    private String isEdit;
    private String isForm;
    private String isList;
    private String isQuery;
    private String queryType;
    private String showType;
    private String dictType;
    private Integer sort;
    private String tableName;
    private String fieldLabels;
    private String fieldKeys;
    private String searchLabel;
    private String searchKey;
    private String isNull;
    private String validateType;
    private String minLength;
    private String maxLength;
    private String minValue;
    private String maxValue;
    private String isOneLine;
    private String magicLogic;
    private String align;
    private String jdbcNameType;
    private Integer formSort;
    private Integer searchSort;
    private Integer listSort;
    private String visible;
    private String jdbcTypes;
    private String isReadonly;
    private String defaultValue;
    private String dateType;
    private String isImport;
    private String isExport;
    private String blockChainParam1;
    private String blockChainParam2;
    private String blockChainParam3;
    private String blockChainParam4;
    private String blockChainParam5;
    private String blockChainParam6;

    public GenTableColumn() {
    }

    public GenTableColumn(String id) {
        super(id);
    }

    public GenTableColumn(GenTable genTable) {
        this.genTable = genTable;
    }

    public GenTable getGenTable() {
        return this.genTable;
    }

    public void setGenTable(GenTable genTable) {
        this.genTable = genTable;
    }

    @Length(min = 1, max = 200)
    public String getName() {
        return StringUtil.lowerCase(this.name);
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

    public String getJdbcType() {
        return StringUtil.lowerCase(this.jdbcType);
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaType() {
        return this.javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaField() {
        return this.javaField;
    }

    public void setJavaField(String javaField) {
        this.javaField = javaField;
    }

    public String getIsPk() {
        return this.isPk;
    }

    public void setIsPk(String isPk) {
        this.isPk = isPk;
    }

    public String getIsNull() {
        return this.isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getIsInsert() {
        return this.isInsert;
    }

    public void setIsInsert(String isInsert) {
        this.isInsert = isInsert;
    }

    public String getIsEdit() {
        return this.isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public void setIsForm(String isForm) {
        this.isForm = isForm;
    }

    public String getIsForm() {
        return this.isForm;
    }

    public String getIsList() {
        return this.isList;
    }

    public void setIsList(String isList) {
        this.isList = isList;
    }

    public String getIsQuery() {
        return this.isQuery;
    }

    public void setIsQuery(String isQuery) {
        this.isQuery = isQuery;
    }

    public String getQueryType() {
        return this.queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getShowType() {
        return this.showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getDictType() {
        return this.dictType == null ? "" : this.dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public Integer getSort() {
        return this.sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getNameAndComments() {
        return getName() + (this.comments == null ? "" : new StringBuilder("  :  ").append(this.comments).toString());
    }

    public String getDataLength() {
        String[] ss = StringUtil.split(StringUtil.substringBetween(getJdbcType(), "(", ")"), ",");
        if ((ss != null) && (ss.length == 1)) {
            return ss[0];
        }
        return "0";
    }

    public String getSimpleJavaType() {
        if ("This".equals(getJavaType())) {
            return StringUtil.capitalize(this.genTable.getClassName());
        }
        return StringUtil.indexOf(getJavaType(), ".") != -1 ?
                StringUtil.substringAfterLast(getJavaType(), ".") :
                getJavaType();
    }

    public String getSimpleJavaField() {
        return StringUtil.substringBefore(getJavaField(), ".");
    }

    public String getJavaFieldId() {
        return StringUtil.substringBefore(getJavaField(), "|");
    }

    public String getJavaFieldName() {
        String[][] ss = getJavaFieldAttrs();
        return ss.length > 0 ? getSimpleJavaField() + "." + ss[0][0] : "";
    }

    public String[][] getJavaFieldAttrs() {
        String[] ss = StringUtil.split(StringUtil.substringAfter(getJavaField(), "|"), "|");
        String[][] sss = new String[ss.length][2];
        if (ss != null) {
            for (int i = 0; i < ss.length; i++) {
                sss[i][0] = ss[i];
                sss[i][1] = StringUtil.toUnderScoreCase(ss[i]);
            }
        }
        return sss;
    }

    public List<String> getAnnotationList() {
        List list = (List) Lists.newArrayList();

        if ("This".equals(getJavaType())) {
            list.add("com.fasterxml.jackson.annotation.JsonBackReference");
        }
        if ("java.util.Date".equals(getJavaType())) {
            if (getComments().indexOf("日期") != -1 || getComments().indexOf("date") != -1) {
                list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"yyyy-MM-dd\",timezone=\"GMT+8\")");
            } else {
                list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\",timezone=\"GMT+8\")");
            }
        }

        if ((false == "1".equals(getIsNull())) && (false == "String".equals(getJavaType()))) {
            list.add("javax.validation.constraints.NotNull(message=\"" + getComments() + " can not be empty\")");
        } else if ((false == "1".equals(getIsNull())) && ("String".equals(getJavaType())) &&
                (this.minLength != null) && (!this.minLength.equals(""))) {
            list.add("org.hibernate.validator.constraints.Length(min=" + this.minLength + ", max=" + this.maxLength +
                    ", message=\"" + getComments() + " length must be between " + this.minLength + " and " + this.maxLength + "\")");
        }

        if ("email".equals(this.validateType)) {
            list.add("org.hibernate.validator.constraints.Email(message=\"" + getComments() + " must be a legal mailbox\")");
        }
        if ("url".equals(this.validateType)) {
            list.add("org.hibernate.validator.constraints.URL(message=\"" + getComments() + " must be a legal website URL\")");
        }
        if ("creditcard".equals(this.validateType)) {
            list.add("org.hibernate.validator.constraints.CreditCardNumber(message=\"" + getComments() + " must be a legal Card Number\")");
        }
        if (("number".equals(this.validateType)) || ("digits".equals(this.validateType))) {
            if ((this.minValue != null) && (!this.minValue.equals(""))) {
                if (this.minValue.contains(".")) {
                    String minv = this.minValue.replace(".", "_digitalPoint_");
                    list.add("javax.validation.constraints.Min(value=(long)" + minv + ",message=\"" + getComments() + " minimum value cannot be less than " + minv + "\")");
                } else {
                    list.add("javax.validation.constraints.Min(value=" + this.minValue + ",message=\"" + getComments() + " minimum value cannot be less than " + this.minValue + "\")");
                }
            }
            if ((this.maxValue != null) && (false == this.maxValue.equals(""))) {
                if (this.maxValue.contains(".")) {
                    String maxv = this.maxValue.replace(".", "_digitalPoint_");
                    list.add("javax.validation.constraints.Max(value=(long)" + maxv + ",message=\"" + getComments() + " maximum value cannot exceed " + maxv + "\")");
                } else {
                    list.add("javax.validation.constraints.Max(value=" + this.maxValue + ",message=\"" + getComments() + " maximum value cannot exceed " + this.maxValue + "\")");
                }
            }
        }
        return list;
    }

    public List<String> getSimpleAnnotationList() {
        List list = (List) Lists.newArrayList();
        for (String ann : getAnnotationList()) {
            String anno = StringUtil.substringAfterLast(ann, ".");
            anno = anno.replace("_digitalPoint_", ".");
            list.add(anno);
        }
        return list;
    }

    public Boolean getIsNotBaseField() {
        if (StringUtil.equals(getSimpleJavaField(), "id")
                || StringUtil.equals(getSimpleJavaField(), "remarks")
                || StringUtil.equals(getSimpleJavaField(), "createBy")
                || StringUtil.equals(getSimpleJavaField(), "createDate")
                || StringUtil.equals(getSimpleJavaField(), "updateBy")
                || StringUtil.equals(getSimpleJavaField(), "updateDate")
                || StringUtil.equals(getSimpleJavaField(), "delFlag")) {
            return Boolean.valueOf(false);
        } else {
            return Boolean.valueOf(true);
        }
    }

    public Boolean getIsNotTreeBaseField() {
        if (StringUtil.equals(getSimpleJavaField(), "id")
                || StringUtil.equals(getSimpleJavaField(), "remarks")
                || StringUtil.equals(getSimpleJavaField(), "createBy")
                || StringUtil.equals(getSimpleJavaField(), "createDate")
                || StringUtil.equals(getSimpleJavaField(), "updateBy")
                || StringUtil.equals(getSimpleJavaField(), "updateDate")
                || StringUtil.equals(getSimpleJavaField(), "delFlag")
                || StringUtil.equals(getSimpleJavaField(), "parent")
                || StringUtil.equals(getSimpleJavaField(), "parentIds")
                || StringUtil.equals(getSimpleJavaField(), "name")
                || StringUtil.equals(getSimpleJavaField(), "sort")) {
            return Boolean.valueOf(false);
        } else {
            return Boolean.valueOf(true);
        }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setFieldLabels(String fieldLabels) {
        this.fieldLabels = fieldLabels;
    }

    public String getFieldLabels() {
        return this.fieldLabels;
    }

    public void setFieldKeys(String fieldKeys) {
        this.fieldKeys = fieldKeys;
    }

    public String getFieldKeys() {
        return this.fieldKeys;
    }

    public void setSearchLabel(String searchLabel) {
        this.searchLabel = searchLabel;
    }

    public String getSearchLabel() {
        return this.searchLabel;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return this.searchKey;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMinLength() {
        return this.minLength;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    public String getValidateType() {
        return this.validateType;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getMaxLength() {
        return this.maxLength;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMinValue() {
        return this.minValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMaxValue() {
        return this.maxValue;
    }

    public void setIsOneLine(String isOneLine) {
        this.isOneLine = isOneLine;
    }

    public String getIsOneLine() {
        return this.isOneLine;
    }

    public String getMagicLogic() {
        return magicLogic;
    }

    public void setMagicLogic(String magicLogic) {
        this.magicLogic = magicLogic;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getJdbcNameType() {
        return jdbcNameType;
    }

    public void setJdbcNameType(String jdbcNameType) {
        this.jdbcNameType = jdbcNameType;
    }

    public Integer getFormSort() {
        return formSort;
    }

    public void setFormSort(Integer formSort) {
        this.formSort = formSort;
    }

    public Integer getSearchSort() {
        return searchSort;
    }

    public void setSearchSort(Integer searchSort) {
        this.searchSort = searchSort;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getJdbcTypes() {
        return jdbcTypes;
    }

    public void setJdbcTypes(String jdbcTypes) {
        this.jdbcTypes = jdbcTypes;
    }

    public Integer getListSort() {
        return listSort;
    }

    public void setListSort(Integer listSort) {
        this.listSort = listSort;
    }

    public String getIsReadonly() {
        return isReadonly;
    }

    public void setIsReadonly(String isReadonly) {
        this.isReadonly = isReadonly;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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