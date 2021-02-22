package com.jeestudio.common.entity.act;

import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.utils.Encodes;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: Task permission
 * @author: houxl
 * @Date: 2020-01-11
 */
public class TaskPermission extends DataEntity<TaskPermission> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String name;
    private String type;
    private String category;
    private String isInuse;
    private String position;
    private String describe;
    private String operation;
    private String tableOperation;
    private String extendOperation;
    private String ruleArgs;
    private String categoryLabel;

    public TaskPermission() {
        super();
    }

    public TaskPermission(String id){
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsInuse() {
        return isInuse;
    }

    public void setIsInuse(String isInuse) {
        this.isInuse = isInuse;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = Encodes.unescapeHtml(operation);
    }

    public String getRuleArgs() {
        if (StringUtils.isNoneBlank(ruleArgs)) {
            ruleArgs = Encodes.unescapeHtml(ruleArgs);
        }
        return ruleArgs;
    }

    public void setRuleArgs(String ruleArgs) {
        this.ruleArgs = ruleArgs;
    }

    public String getTableOperation() {
        return tableOperation;
    }

    public void setTableOperation(String tableOperation) {
        this.tableOperation = tableOperation;
    }

    public String getExtendOperation() {
        return extendOperation;
    }

    public void setExtendOperation(String extendOperation) {
        this.extendOperation = extendOperation;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }
}
