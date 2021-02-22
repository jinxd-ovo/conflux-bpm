package com.jeestudio.common.entity.cms;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Prt Model
 * @author: houxl
 * @Date: 2020-07-02
 */
public class PrtModel extends DataEntity<PrtModel> {

    private static final long serialVersionUID = 1L;
    private String ownerCode;
    private String name;
    private String types;
    private String modelPath;
    private String picPath;
    private String ifDefault;
    private String useable;
    private String label;
    private PrtSite site;

    public PrtModel() {
        super();
    }

    public PrtModel(String id){
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

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getIfDefault() {
        return ifDefault;
    }

    public void setIfDefault(String ifDefault) {
        this.ifDefault = ifDefault;
    }

    public String getUseable() {
        return useable;
    }

    public void setUseable(String useable) {
        this.useable = useable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PrtSite getSite() {
        return site;
    }

    public void setSite(PrtSite site) {
        this.site = site;
    }
}
