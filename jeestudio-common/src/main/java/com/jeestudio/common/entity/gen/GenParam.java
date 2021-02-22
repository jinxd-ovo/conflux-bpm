package com.jeestudio.common.entity.gen;

/**
 * @Description: Gen Param
 * @author: houxl
 * @Date: 2020-06-19
 */
public class GenParam {

    private static final long serialVersionUID = 1L;

    private String genscheme;
    private String code;
    private String isChild;
    private String category;
    private String tplId;

    public String getGenscheme() {
        return genscheme;
    }

    public void setGenscheme(String genscheme) {
        this.genscheme = genscheme;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIsChild() {
        return isChild;
    }

    public void setIsChild(String isChild) {
        this.isChild = isChild;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTplId() {
        return tplId;
    }

    public void setTplId(String tplId) {
        this.tplId = tplId;
    }
}
