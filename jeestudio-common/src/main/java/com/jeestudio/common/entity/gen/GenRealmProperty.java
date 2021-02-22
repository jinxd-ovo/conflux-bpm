/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.jeestudio.common.entity.gen;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Gen realm property
 * @author: houxl
 * @Date: 2020-01-18
 */
public class GenRealmProperty extends DataEntity<GenRealmProperty> {

	private static final long serialVersionUID = 1L;

	private String ownerCode;
	private String name;
	private String comments;
	private String jdbcType;
	private Integer sort;
	private String javaType;
	private String javaField;
	private String isForm;
	private String isList;
	private String isQuery;
	private String queryType;
	private String showType;
	private String dictType;
	private GenRealm realmId;
	private String isReadonly;
	private String isOneline;
	private String validatetype;
	private String minLength;
	private String maxLength;
	private String minValue;
	private String maxValue;
	private String tablename;
	private String fieldlabels;
	private String fieldkeys;
	private String searchlabel;
	private String searchkey;
	private String isNull;
	
	public GenRealmProperty() {
		super();
	}

	public GenRealmProperty(String id){
		super(id);
	}

	public GenRealmProperty(GenRealm realmId){
		this.realmId = realmId;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}
	
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	
	public String getJavaField() {
		return javaField;
	}

	public void setJavaField(String javaField) {
		this.javaField = javaField;
	}
	
	public String getIsForm() {
		return isForm;
	}

	public void setIsForm(String isForm) {
		this.isForm = isForm;
	}
	
	public String getIsList() {
		return isList;
	}

	public void setIsList(String isList) {
		this.isList = isList;
	}
	
	public String getIsQuery() {
		return isQuery;
	}

	public void setIsQuery(String isQuery) {
		this.isQuery = isQuery;
	}
	
	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}
	
	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}
	
	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	
	public GenRealm getRealmId() {
		return realmId;
	}

	public void setRealmId(GenRealm realmId) {
		this.realmId = realmId;
	}

	public String getIsReadonly() {
		return isReadonly;
	}

	public void setIsReadonly(String isReadonly) {
		this.isReadonly = isReadonly;
	}

	public String getIsOneline() {
		return isOneline;
	}

	public void setIsOneline(String isOneline) {
		this.isOneline = isOneline;
	}

	public String getValidatetype() {
		return validatetype;
	}

	public void setValidatetype(String validatetype) {
		this.validatetype = validatetype;
	}

	public String getMinLength() {
		return minLength;
	}

	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getFieldlabels() {
		return fieldlabels;
	}

	public void setFieldlabels(String fieldlabels) {
		this.fieldlabels = fieldlabels;
	}

	public String getFieldkeys() {
		return fieldkeys;
	}

	public void setFieldkeys(String fieldkeys) {
		this.fieldkeys = fieldkeys;
	}

	public String getSearchlabel() {
		return searchlabel;
	}

	public void setSearchlabel(String searchlabel) {
		this.searchlabel = searchlabel;
	}

	public String getSearchkey() {
		return searchkey;
	}

	public void setSearchkey(String searchkey) {
		this.searchkey = searchkey;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}
}