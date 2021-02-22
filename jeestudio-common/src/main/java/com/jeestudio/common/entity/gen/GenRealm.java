/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.jeestudio.common.entity.gen;

import javax.validation.constraints.NotNull;
import java.util.List;
import com.google.common.collect.Lists;
import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Gen realm
 * @author: houxl
 * @Date: 2020-01-18
 */
public class GenRealm extends DataEntity<GenRealm> {

	private static final long serialVersionUID = 1L;

	private String ownerCode;
	private String name;
	private Integer sort;
	private List<GenRealmProperty> genRealmPropertyList = Lists.newArrayList();
	private String type;
	private String desc;
	private String isRealm;
	private String editable;
	private String processDefinitionCategory;
	private String previewImg;
	
	public GenRealm() {
		super();
	}

	public GenRealm(String id){
		super(id);
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
	
	@NotNull(message="Sort cannot be empty")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public List<GenRealmProperty> getGenRealmPropertyList() {
		return genRealmPropertyList;
	}

	public void setGenRealmPropertyList(List<GenRealmProperty> genRealmPropertyList) {
		this.genRealmPropertyList = genRealmPropertyList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIsRealm() {
		return isRealm;
	}

	public void setIsRealm(String isRealm) {
		this.isRealm = isRealm;
	}

	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getProcessDefinitionCategory() {
		return processDefinitionCategory;
	}

	public void setProcessDefinitionCategory(String processDefinitionCategory) {
		this.processDefinitionCategory = processDefinitionCategory;
	}

	public String getPreviewImg() {
		return previewImg;
	}

	public void setPreviewImg(String previewImg) {
		this.previewImg = previewImg;
	}
}