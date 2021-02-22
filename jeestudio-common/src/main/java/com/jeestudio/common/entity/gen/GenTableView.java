package com.jeestudio.common.entity.gen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: Gen table view
 * @author: whl
 * @Date: 2020-02-07
 */
public class GenTableView implements Serializable {

    private String id;
    private String name;
    private String parentTable;
    private String parentTableFk;
    private String comments;

    @JsonProperty(value = "comments_EN")
    private String commentsEn;

    private String isMobile;
    private String mobileIcon;
    private String processDefinitionCategory;
    private String processModelName;
    private String isScroll;
    private String isBuildSecret;
    private String isRowedit;
    private String isBuildImport;
    private String isBuildExport;
    private String exportList;
    private String exportTemplatePath;
    private String exportRuleName;
    private String importList;
    private String importTemplateFile;
    private String exportTemplateFile;
    private List<GenTableChildren> children;
    private String extJsp;
    private String extJs;
    private String extJava;
    private String isCustom;
    private String blockChainParam1;
    private String blockChainParam2;
    private String blockChainParam3;
    private String blockChainParam4;
    private String blockChainParam5;
    private String blockChainParam6;
    private String module;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentTable(String parentTable) {
        this.parentTable = parentTable;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCommentsEn(String commentsEn) {
        this.commentsEn = commentsEn;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public void setMobileIcon(String mobileIcon) {
        this.mobileIcon = mobileIcon;
    }

    public void setChildren(List<GenTableChildren> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParentTable() {
        return parentTable;
    }

    public String getComments() {
        return comments;
    }

    public String getCommentsEn() {
        return commentsEn;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public String getMobileIcon() {
        return mobileIcon;
    }

    public List<GenTableChildren> getChildren() {
        return children;
    }

    public String getProcessDefinitionCategory() {
        return processDefinitionCategory;
    }

    public void setProcessDefinitionCategory(String processDefinitionCategory) {
        this.processDefinitionCategory = processDefinitionCategory;
    }

    public String getProcessModelName() {
        return processModelName;
    }

    public void setProcessModelName(String processModelName) {
        this.processModelName = processModelName;
    }

    public String getIsScroll() {
        return isScroll;
    }

    public void setIsScroll(String isScroll) {
        this.isScroll = isScroll;
    }

    public String getIsBuildSecret() {
        return isBuildSecret;
    }

    public void setIsBuildSecret(String isBuildSecret) {
        this.isBuildSecret = isBuildSecret;
    }

    public String getIsRowedit() {
        return isRowedit;
    }

    public void setIsRowedit(String isRowedit) {
        this.isRowedit = isRowedit;
    }

    public String getParentTableFk() {
        return parentTableFk;
    }

    public void setParentTableFk(String parentTableFk) {
        this.parentTableFk = parentTableFk;
    }

    public String getIsBuildImport() {
        return isBuildImport;
    }

    public void setIsBuildImport(String isBuildImport) {
        this.isBuildImport = isBuildImport;
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
