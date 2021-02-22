package com.jeestudio.datasource.service.act;

import com.google.common.collect.Maps;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Description: ActTask Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class ActTaskService {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * Start a new process
     *
     * @param procDefKey    the process defination key
     * @param businessTable the table name
     * @param businessId    the table id
     * @return process instance id
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String loginName) {
        return startProcess(procDefKey, businessTable, businessId, "", loginName);
    }

    /**
     * Start a new process
     *
     * @param procDefKey    the process defination key
     * @param businessTable the table name
     * @param businessId    the table id
     * @param title         the todo_ task title
     * @return process instance id
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, String loginName) {
        Map<String, Object> vars = Maps.newHashMap();
        return startProcess(procDefKey, businessTable, businessId, title, vars, loginName);
    }

    /**
     * Start a new process
     *
     * @param procDefKey    the process defination key
     * @param businessTable the table name
     * @param businessId    the table id
     * @param title         the todo_ task title
     * @param vars          variables for process
     * @return process instance id
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars, String loginName) {
        //Setup variables for process
        if (vars == null) {
            vars = Maps.newHashMap();
        }
        String userId = (String) vars.get("applyUserId");
        if (userId == null) {
            userId = loginName;
        }
        //Setup the user id who start the process
        identityService.setAuthenticatedUserId(userId);
        //Setup process title
        if (StringUtils.isNotBlank(title)) {
            vars.put("title", title);
        }
        //Start process
        ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable + ":" + businessId, vars);
        return procIns.getId();
    }

    /**
     * Submit task and save comments
     *
     * @param taskId    the task id
     * @param procInsId the process instance id, do not save comments when it's blank
     * @param comment   the comments when submit task
     * @param vars      variables of process
     */
    @Transactional(readOnly = false)
    public void complete(String taskId, String procInsId, String comment, Map<String, Object> vars) {
        complete(taskId, procInsId, comment, "", vars);
    }

    /**
     * Submit task and save comments
     *
     * @param taskId    the task id
     * @param procInsId the process instance id, do not save comments when it's blank
     * @param comment   the comments when submit task
     * @param title     the todo_ task title
     * @param vars      variables of process
     */
    @Transactional(readOnly = false)
    public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars) {
        //Add comments
        if (StringUtils.isNotBlank(procInsId) && StringUtils.isNotBlank(comment)) {
            taskService.addComment(taskId, procInsId, comment);
        }
        //Init process variables
        if (vars == null) {
            vars = Maps.newHashMap();
        }
        //Setup process title
        if (StringUtils.isNotBlank(title)) {
            vars.put("title", title);
        }
        //Submit task
        taskService.complete(taskId, vars);
    }

    /**
     * Submit task and save comments
     *
     * @param taskId    the task id
     * @param procInsId the process instance id, do not save comments when it's blank
     * @param comment   the comments when submit task
     * @param title     the todo_ task title
     * @param vars      variables of process
     * @param userId    assignee user id
     */
    @Transactional(readOnly = false)
    public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars, String userId) {
        //Add comments
        if (StringUtils.isNotBlank(procInsId) && StringUtils.isNotBlank(comment)) {
            taskService.addComment(taskId, procInsId, comment);
        }
        //Init process variables
        if (vars == null) {
            vars = Maps.newHashMap();
        }
        //Setup process title
        if (StringUtils.isNotBlank(title)) {
            vars.put("title", title);
        }
        //Submit task
        taskService.setAssignee(taskId, userId);
        taskService.complete(taskId, vars);
    }

    /**
     * Get process instance by id
     *
     * @param procInsId the process instance id
     * @return process instance
     */
    @Transactional(readOnly = false)
    public ProcessInstance getProcIns(String procInsId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
    }

    /**
     * Claim task
     *
     * @param taskId the task id
     * @param userId the user id who claimed the task(login name)
     */
    @Transactional(readOnly = false)
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }
}
