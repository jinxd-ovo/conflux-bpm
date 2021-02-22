package com.jeestudio.common.entity.act;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Task message
 * @author: houxl
 * @Date: 2020-01-11
 */
public class TaskMessage extends DataEntity<TaskMessage> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String processScope;
    private String operation;

    public TaskMessage() {
        super();
    }

    public TaskMessage(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getProcessScope() {
        return processScope;
    }

    public void setProcessScope(String processScope) {
        this.processScope = processScope;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
