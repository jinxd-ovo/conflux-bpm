package com.jeestudio.common.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @Description: Dict result
 * @author: whl
 * @Date: 2020-01-08
 */
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","fieldHandler"})
public class DictResult  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dictionaryID;
    private String type = "";
    private String member;
    private String memberName;
    @JsonProperty(value = "memberName_EN")
    private String memberNameEn = "";
    private String parentType;
    private boolean hasChildren;
    private int sort;

    public String getDictionaryID() {
        return dictionaryID;
    }

    public void setDictionaryID(String dictionaryID) {
        this.dictionaryID = dictionaryID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberNameEn() {
        return memberNameEn;
    }

    public void setMemberNameEn(String memberNameEn) {
        this.memberNameEn = memberNameEn;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
