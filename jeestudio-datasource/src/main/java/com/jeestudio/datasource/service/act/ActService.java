package com.jeestudio.datasource.service.act;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.jeestudio.common.entity.act.*;
import com.jeestudio.common.entity.system.*;
import com.jeestudio.datasource.annotation.*;
import com.jeestudio.common.entity.common.ActEntity;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.datasource.contextHolder.ApplicationContextHolder;
import com.jeestudio.datasource.service.common.CrudService;
import com.jeestudio.datasource.service.system.*;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.*;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.form.DefaultTaskFormHandler;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: ActService Service
 * @author: David
 * @Date: 2020-01-11
 */
public class ActService<D extends CrudDao<T>, T extends ActEntity<T>> extends CrudService<D, T> {

    @Autowired
    private ProcessEngineFactoryBean processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private AssigneeSettingService assigneeSettingService;

    @Autowired
    private SysMsgService sysMsgService;

    @Autowired
    private UserService userService;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private TaskMessageService taskMessageService;

    @Autowired
    private ActTaskService actTaskService;

    @Autowired
    private ActModelService actModelService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private TaskSettingVersionService taskSettingVersionService;

    @Autowired
    private TaskPermissionService taskPermissionService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private DictDataService dictDataService;

    /**
     * BUTTON_TYPE
     * SAVE
     * SAVEANDSTART
     * SAVEANDCLAIM
     * SAVEANDCOMPLETE
     * SAVEANDTERMINATE
     * SAVEANDREJECT
     * SAVEANDSUPERREJECT Save and reject to a history node
     * SAVEANDCREATENODE
     * SAVEANDDELETENODE
     */
    private enum ButtonType {
        SAVE, SAVEANDSTART, SAVEANDCLAIM, SAVEANDCOMPLETE, SAVEANDTERMINATE,
        SAVEANDREJECT, SAVEANDSUPERREJECT, SAVEANDCREATENODE, SAVEANDDELETENODE,
        SAVEANDSEND, SAVEANDRECEIVE, SAVEANDNOTIFY, SAVEANDDISTRIBUTE, SAVEANDTODISTRIBUTE;
    }

    /**
     * PATH
     * UNSENT
     * SENT
     * TODO_
     * DOING
     * DONE
     * TODOANDDOING
     * TODOANDDOINGANDDONE
     * UNSENTANDSENT filter by create_by
     * QUERY
     */
    private enum Path {
        UNSENT, SENT, TODO, DOING, DONE, TODOANDDOING, TODOANDDOINGANDDONE, UNSENTANDSENT, QUERY;
    }

    /**
     * PROCESS_MAP
     */
    protected enum ProcessMap {
        PROC_INS_ID, PROC_DEF_ID, TASK_DEF_KEY, REQUEST_MAPPING, REST_PARAM, ENTITY_ID,
        PROC_CATEGORY, PROC_CATEGORY_LABEL, PROC_CREATE_USER, PROC_CREATE_TIME,
        PROC_CREATE_USER_LIKE, PROC_CREATE_TIME_BEGIN, PROC_CREATE_TIME_END,
        TASK_ID, TASK_NAME, TASK_CREATE_TIME;
    }

    /**
     * COMMA
     */
    private static final String COMMA = ",";

    /**
     * TASK_DEFINITION_KEY_DEFAULT blank string
     */
    private static final String TASK_DEFINITION_KEY_DEFAULT = "";

    /**
     * TASK_DEFINITION_KEY_END key is "end"
     */
    private static final String TASK_DEFINITION_KEY_END = "end";

    /**
     * START_EVENT_ID
     */
    private static final String START_EVENT_ID = "start";

    /**
     * GRAPHIC_DEFAULT_X
     */
    private static final Double GRAPHIC_DEFAULT_X = 100.0;

    /**
     * GRAPHIC_DEFAULT_Y
     */
    private static final Double GRAPHIC_DEFAULT_Y = 100.0;

    /**
     * PARALLEL_GATEWAY_WIDTH
     */
    private static final Double PARALLEL_GATEWAY_WIDTH = 40.0;

    /**
     * PARALLEL_GATEWAY_HEIGHT
     */
    private static final Double PARALLEL_GATEWAY_HEIGHT = 40.0;

    /**
     * PARALLEL_USERTASK_WIDTH
     */
    private static final Double PARALLEL_USERTASK_WIDTH = 100.0;

    /**
     * PARALLEL_USERTASK_HEIGHT
     */
    private static final Double PARALLEL_USERTASK_HEIGHT = 80.0;

    /**
     * SEQUENCE_FLOW_SOURCE
     */
    private static final String SEQUENCE_FLOW_SOURCE = "source";

    /**
     * SEQUENCE_FLOW_TARGET
     */
    private static final String SEQUENCE_FLOW_TARGET = "target";

    /**
     * FIXED_VALUE_EL_PREFIX
     */
    private static final String FIXED_VALUE_EL_PREFIX = "${";

    /**
     * FIXED_VALUE_EL_SUFFIX
     */
    private static final String FIXED_VALUE_EL_SUFFIX = "}";

    /**
     * ACTIVITI_YES
     */
    private static final String ACTIVITI_YES = "1";

    /**
     * ACTIVITI_NO
     */
    private static final String ACTIVITI_NO = "0";

    /**
     * NODE_MARK_NOTIFY
     */
    private static final String NODE_MARK_NOTIFY = "notify";

    /**
     * NODE_MARK_DISTRIBUTE
     */
    private static final String NODE_MARK_DISTRIBUTE = "distribute";

    /**
     * NODE_MARK_CREATE
     */
    private static final String NODE_MARK_CREATE = "create";

    /**
     * NODE_MARK_BEGIN_INCLUSIVE_GATEWAY
     */
    private static final String NODE_MARK_BEGIN_INCLUSIVE_GATEWAY = "begin_inclusive_gateway";

    /**
     * NODE_MARK_END_INCLUSIVE_GATEWAY
     */
    private static final String NODE_MARK_END_INCLUSIVE_GATEWAY = "end_inclusive_gateway";

    /**
     * UNDERLINE
     */
    private static final String UNDERLINE = "_";

    /**
     * FIXED_VALUE_NOFITY
     */
    private static final String FIXED_VALUE_NOFITY = "知会";

    /**
     * FIXED_VALUE_NOFITY_EN
     */
    private static final String FIXED_VALUE_NOFITY_EN = "Notify";

    /**
     * FIXED_VALUE_DISTRIBUTE
     */
    private static final String FIXED_VALUE_DISTRIBUTE = "分发";

    /**
     * FIXED_VALUE_DISTRIBUTE_EN
     */
    private static final String FIXED_VALUE_DISTRIBUTE_EN = "Distribute";

    /**
     * FIXED_VALUE_CONDITION_100%
     */
    private static final String FIXED_VALUE_CONDITION_100 = "nrOfCompletedInstances/nrOfInstances>=1";

    /**
     * CREATE_NODE_TASKID when add a node in a runtime process instance, get task id by this param.
     */
    private static final String CREATE_NODE_TASKID = "taskId";

    /**
     * CREATE_NODE_ASSIGNEE
     */
    private static final String CREATE_NODE_ASSIGNEE = "assignee";

    /**
     * SPLIT_COMMA
     */
    private static final String SPLIT_COMMA = ",";

    /**
     * DICT_DEFAULT_VALUE
     */
    private static final String DICT_DEFAULT_VALUE = "";

    /**
     * DICT_ACT_CATEGORY
     */
    private static final String DICT_ACT_CATEGORY = "act_category";

    /**
     * DICT_OA_TASK_OPERATION
     */
    private static final String DICT_OA_TASK_OPERATION = "oa_task_operation";

    /**
     * SQUARE_BRACKET_LEFT
     */
    private static final String SQUARE_BRACKET_LEFT = " [ ";

    /**
     * SQUARE_BRACKET_RIGHT
     */
    private static final String SQUARE_BRACKET_RIGHT = " ] ";

    /**
     * AT
     */
    private static final String AT = " ";

    /**
     * SIMPLE_DATE_FORMAT_YMD
     */
    private static final String SIMPLE_DATE_FORMAT_YMD = "yyyy-MM-dd";

    /**
     * SIMPLE_DATE_FORMAT_HM
     */
    private static final String SIMPLE_DATE_FORMAT_HM = "yyyy-MM-dd HH:mm";

    /**
     * SIMPLE_DATE_FORMAT_HMS
     */
    private static final String SIMPLE_DATE_FORMAT_HMS = "yyyy-MM-dd HH:mm:ss";

    private static final String UNSTART = "unstart";
    private static final String END = "end";
    private static final String TERMINATE = "terminate";
    private static final String UNSTARTNAME = "暂存";
    private static final String UNSTARTNAME_EN = "Save";
    private static final String ENDNAME = "结束";
    private static final String ENDNAME_EN = "End";
    private static final String TERMINATENAME = "终止";
    private static final String TERMINATENAME_EN = "Terminate";
    public static final String PATH_QUERY = "query";

    /**
     * Get process definition list by category.
     *
     * @param repositoryService
     * @param category
     * @return ProcessDefinition list.
     */
    public static List<ProcessDefinition> getProcessDefinitionList(RepositoryService repositoryService, String category) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionCategory(category).latestVersion().orderByProcessDefinitionKey().asc().list();
    }

    /**
     * Get process definition by key.
     *
     * @param repositoryService
     * @param processDefinitionKey
     * @return ProcessDefinition.
     */
    public static ProcessDefinition getProcessDefinition(RepositoryService repositoryService, String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
    }

    /**
     * Get next task definition agent by next task agent.
     *
     * @param nextTaskAgent
     * @param nextTaskDefinition
     * @return nextTaskAgent
     */
    public static String getNextTaskDefinitionAgent(String nextTaskAgent, ActivityImpl nextTaskDefinition) {
        if (nextTaskDefinition != null) {
            ActivityBehavior activityBehavior = nextTaskDefinition.getActivityBehavior();
            if (activityBehavior instanceof UserTaskActivityBehavior) {
                Set<Expression> candidateUserIds = (((UserTaskActivityBehavior) nextTaskDefinition
                        .getActivityBehavior()).getTaskDefinition()).getCandidateUserIdExpressions();
                for (Expression expression : candidateUserIds) {
                    nextTaskAgent = expression.getExpressionText();
                }
            } else if (activityBehavior instanceof SequentialMultiInstanceBehavior) {
                String assignee = ((SequentialMultiInstanceBehavior) nextTaskDefinition.getActivityBehavior())
                        .getCollectionExpression().getExpressionText();
                nextTaskAgent = assignee;
            } else if (activityBehavior instanceof ParallelMultiInstanceBehavior) {
                String assignee = ((ParallelMultiInstanceBehavior) nextTaskDefinition.getActivityBehavior())
                        .getCollectionExpression().getExpressionText();
                nextTaskAgent = assignee;
            }
            if (StringUtils.isNotBlank(nextTaskAgent) && nextTaskAgent.lastIndexOf("{") != -1
                    && nextTaskAgent.lastIndexOf("}") != -1) {
                nextTaskAgent = nextTaskAgent.substring(nextTaskAgent.lastIndexOf("{") + 1,
                        nextTaskAgent.lastIndexOf("}"));
            }
        }
        return nextTaskAgent;
    }

    /**
     * Get next task definition.
     *
     * @param userTaskName
     * @param activityImpl
     * @param typeName
     * @param activityId
     * @param flag
     * @param procInsId
     * @param gatewayName
     * @param conditionTextName
     * @return ActivityImpl next task definition.
     */
    public static ActivityImpl nextTaskDefinition(String userTaskName, ActivityImpl activityImpl, String typeName, String activityId, String flag, String procInsId, String gatewayName, String conditionTextName) {
        if (userTaskName.equals(activityImpl.getProperty(typeName))
                && !activityId.equals(activityImpl.getId())) {
            return activityImpl;
        } else if (String.valueOf(activityImpl.getProperty(typeName)).indexOf(userTaskName) != -1) {
            List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : outgoingTransitions) {
                PvmActivity destination = pvmTransition.getDestination();
                if (String.valueOf(destination.getProperty(typeName)).indexOf(userTaskName) != -1) {
                    ActivityImpl nextActivityImpl = (ActivityImpl) destination;
                    return nextTaskDefinition(userTaskName, nextActivityImpl, typeName, activityId, flag, procInsId, gatewayName, conditionTextName);
                } else if (String.valueOf(destination.getProperty(typeName)).indexOf(gatewayName) != -1) {
                    List<PvmTransition> nextOutgoingTransitions = destination.getOutgoingTransitions();
                    if (nextOutgoingTransitions.size() == 1) {
                        ActivityImpl nextActivityImpl = (ActivityImpl) nextOutgoingTransitions.get(0).getDestination();
                        return nextTaskDefinition(userTaskName, nextActivityImpl, typeName, activityId, flag, procInsId, gatewayName, conditionTextName);
                    } else if (nextOutgoingTransitions.size() > 1) {
                        for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                            Object property = nextPvmTransition.getProperty(conditionTextName);
                            if (String.valueOf(property).indexOf(flag) != -1) {
                                ActivityImpl nextActivityImpl = (ActivityImpl) nextPvmTransition.getDestination();
                                return nextTaskDefinition(userTaskName, nextActivityImpl, typeName, activityId, flag, procInsId, gatewayName, conditionTextName);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get top key for mssql select sql, eg: select top 100 [column] from [table].
     *
     * @return TOP 100 when mssql
     */
    private String dbType;

    private String getTop() {
        if (StringUtil.isEmpty(this.dbType)) {
            try {
                dbType = ApplicationContextHolder.getBean(DatabaseIdProvider.class).getDatabaseId(ApplicationContextHolder.getBean(DataSource.class));
            } catch (Exception e) {
                dbType = "mysql";
            }
        }
        return dbType.equalsIgnoreCase("mssql") ? " TOP 100 " : "";
    }

    /**
     * Query process instance id list by page
     *
     * @param category
     * @param path
     * @param page
     * @param loginName
     * @return process instance id list.
     */
    public List<String> getProcessInstanceIdList(String category, String path, Page<T> page, String loginName) {
        List<String> processInstanceIdListTemp = getProcessInstanceIdList(category, path, loginName);
        int count = processInstanceIdListTemp.size();
        page.setCount(count);
        int begin = page.getFirstResult();//2.getFirstResult()
        int halfEnd = begin + page.getPageSize();
        int end = halfEnd < count ? halfEnd : count;
        List<String> processInstanceIdList = Lists.newArrayList();
        for (int i = begin; i < end; i++) {
            processInstanceIdList.add(processInstanceIdListTemp.get(i));
        }
        return processInstanceIdList;
    }

    /**
     * Query process id list by category.
     *
     * @param category
     * @param path
     * @param loginName
     * @return process instance id list.
     */
    public List<String> getProcessInstanceIdList(String category, String path, String loginName) {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionCategory(category).latestVersion().list();
        if (processDefinitionList.size() == 0) {
            return Lists.newArrayList();
        }

        List<String> processDefinitionKeyList = Lists.newArrayList();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            processDefinitionKeyList.add(processDefinition.getKey());
        }
        User currentUser = UserUtil.getByLoginName(loginName);
        ;

        LinkedHashMap<String, String> processInstanceIdMap = Maps.newLinkedHashMap();

        if (Path.SENT.name().equalsIgnoreCase(path)) {
            List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery()
                    .processDefinitionKeyIn(processDefinitionKeyList).startedBy(loginName)
                    .orderByProcessInstanceStartTime().desc().list();
            for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
                processInstanceIdMap.put(historicProcessInstance.getId(), historicProcessInstance.getId());
            }
        } else if (Path.TODO.name().equalsIgnoreCase(path)) {
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(currentUser.getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                Date now = new Date();
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(COMMA + category + COMMA)
                        && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().desc().list();
                    for (Task task : taskList) {
                        //Filter out notify tasks of agented uer.
                        if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                            continue;
                        }
                        //Filter out distribute tasks of agented uer.
                        if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                            continue;
                        }
                        processInstanceIdMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
                    }
                }
            }
            List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                    .taskOwner(loginName).active().orderByTaskCreateTime().desc().list();
            for (Task task : taskList) {
                processInstanceIdMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
            }
        } else if (Path.DOING.name().equalsIgnoreCase(path)) {
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(currentUser.getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                Date now = new Date();
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(COMMA + category + COMMA)
                        && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().desc().list();
                    for (Task task : taskList) {
                        processInstanceIdMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
                    }
                }
            }
            List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                    .taskOwner(loginName).active().orderByTaskCreateTime().desc().list();
            for (Task task : taskList) {
                processInstanceIdMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
            }
        } else if (Path.DONE.name().equalsIgnoreCase(path)) {
            List<HistoricTaskInstance> historicTaskInstanceListAs = historyService.createHistoricTaskInstanceQuery()
                    .processDefinitionKeyIn(processDefinitionKeyList).taskAssignee(loginName).finished()
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            List<HistoricTaskInstance> historicTaskInstanceListOw = historyService.createHistoricTaskInstanceQuery()
                    .processDefinitionKeyIn(processDefinitionKeyList).taskOwner(loginName).finished()
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceListAs) {
                processInstanceIdMap.put(historicTaskInstance.getProcessInstanceId(), historicTaskInstance.getProcessInstanceId());
            }
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceListOw) {
                processInstanceIdMap.put(historicTaskInstance.getProcessInstanceId(), historicTaskInstance.getProcessInstanceId());
            }
        } else if (Path.TODOANDDOING.name().equalsIgnoreCase(path)) {
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(currentUser.getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                Date now = new Date();
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(COMMA + category + COMMA)
                        && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().desc().list();
                    for (Task task : taskList) {
                        //Filter out notify tasks of agented uer.
                        if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                            continue;
                        }
                        //Filter out distribute tasks of agented uer.
                        else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                            continue;
                        }
                        processInstanceIdMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
                    }
                }
            }
            List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                    .taskOwner(loginName).active().orderByTaskCreateTime().desc().list();
            for (Task task : taskList) {
                processInstanceIdMap.put(task.getProcessInstanceId(), task.getProcessInstanceId());
            }
        }

        List<String> processInstanceIdList = Lists.newArrayList();
        for (String processInstanceId : processInstanceIdMap.keySet()) {
            processInstanceIdList.add(processInstanceId);
        }

        return processInstanceIdList;
    }

    /**
     * Get user task list by page.
     *
     * @param categoryList
     * @param path
     * @param loginName
     * @param loginNameList
     * @param pageNo
     * @param pageSize
     * @param paramMap
     * @return Task list map.
     */
    public Map<String, Object> getTaskList(List<String> categoryList, String path, String loginName, List<String> loginNameList, int pageNo, int pageSize, Map<String, String> paramMap) {
        StringBuffer processDefinitionQuerySql = new StringBuffer();
        processDefinitionQuerySql.append("SELECT * FROM ACT_RE_PROCDEF RES ");
        if (categoryList != null && categoryList.size() > 0) {
            processDefinitionQuerySql.append("WHERE RES.CATEGORY_ IN ( ");
            for (int i = 0; i < categoryList.size(); i++) {
                if (StringUtils.isNotBlank(categoryList.get(i))) {
                    processDefinitionQuerySql.append("'" + categoryList.get(i) + "'");
                    if (categoryList.size() - 1 > i) {
                        processDefinitionQuerySql.append(", ");
                    } else {
                        processDefinitionQuerySql.append(") ");
                    }
                }
            }
        }
        List<ProcessDefinition> processDefinitionList = repositoryService.createNativeProcessDefinitionQuery().sql(processDefinitionQuerySql.toString()).list();
        if (processDefinitionList.size() == 0) {
            return null;
        }
        List<String> processDefinitionKeyList = Lists.newArrayList();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            processDefinitionKeyList.add(processDefinition.getKey());
        }
        Map<String, ProcessDefinition> processDefinitionMap = Maps.newHashMap();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            processDefinitionMap.put(processDefinition.getId(), processDefinition);
        }

        Map<String, Object> dataMap = Maps.newHashMap();

        if (Path.SENT.name().equalsIgnoreCase(path)) {
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            try {
                if (paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name()) != null
                        && StringUtils.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name()))) {
                    String createTimeBeginString = paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name());
                    createTimeBeginString += " 00:00:00";
                    Date createTimeBegin = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).parse(createTimeBeginString);
                    historicProcessInstanceQuery.startedAfter(createTimeBegin);
                }
                if (paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name()) != null
                        && StringUtils.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name()))) {
                    String createTimeEndString = paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name());
                    createTimeEndString += " 23:59:59";
                    Date createTimeEnd = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).parse(createTimeEndString);
                    historicProcessInstanceQuery.startedBefore(createTimeEnd);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<HistoricProcessInstance> historicProcessInstanceListTemp = historicProcessInstanceQuery.processDefinitionKeyIn(processDefinitionKeyList)
                    .startedBy(loginName).orderByProcessInstanceStartTime().desc().list();
            List<HistoricProcessInstance> historicProcessInstanceList = null;
            if (loginNameList == null) {
                historicProcessInstanceList = historicProcessInstanceListTemp;
            } else {
                historicProcessInstanceList = Lists.newArrayList();
                for (String ln : loginNameList) {
                    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceListTemp) {
                        if (ln.equals(historicProcessInstance.getStartUserId())) {
                            historicProcessInstanceList.add(historicProcessInstance);
                        }
                    }
                }
            }
            if (historicProcessInstanceList.size() == 0) {
                return null;
            }
            Map<String, Object> processInfoMap = getProcessInfoMap(historicProcessInstanceList);
            Map<String, String> requestMappingMap = (Map<String, String>) processInfoMap.get("requestMappingMap");
            Map<String, String> restParamMap = (Map<String, String>) processInfoMap.get("restParamMap");
            Map<String, String> entityIdMap = (Map<String, String>) processInfoMap.get("entityIdMap");
            Map<String, String> userNameMap = (Map<String, String>) processInfoMap.get("userNameMap");
            Map<String, Date> processInstanceStartTimeMap = (Map<String, Date>) processInfoMap.get("processInstanceStartTimeMap");

            StringBuffer nativeHistoricActivitySelectSql = new StringBuffer();
            nativeHistoricActivitySelectSql.append("SELECT " + getTop() + " MAX(START_TIME_) AS START_TIME_, PROC_INST_ID_, MAX(ID_) AS ID_ FROM ACT_HI_ACTINST WHERE PROC_INST_ID_ IN ( ");
            for (int i = 0; i < historicProcessInstanceList.size(); i++) {
                nativeHistoricActivitySelectSql.append("'" + historicProcessInstanceList.get(i).getId() + "'");
                if (historicProcessInstanceList.size() - 1 > i) {
                    nativeHistoricActivitySelectSql.append(", ");
                } else {
                    nativeHistoricActivitySelectSql.append(") ");
                }
            }
            nativeHistoricActivitySelectSql.append(" GROUP BY PROC_INST_ID_ ORDER BY START_TIME_ DESC ");
            List<HistoricActivityInstance> historicActivityInstanceList = null;
            NativeHistoricActivityInstanceQuery nativeHistoricActivityInstanceQuery = historyService.createNativeHistoricActivityInstanceQuery();
            if (pageSize < 0) {
                historicActivityInstanceList = nativeHistoricActivityInstanceQuery.sql(nativeHistoricActivitySelectSql.toString()).list();
            } else {
                historicActivityInstanceList = nativeHistoricActivityInstanceQuery.sql(nativeHistoricActivitySelectSql.toString()).listPage((pageNo - 1) * pageSize, pageSize);
            }
            Map<String, HistoricActivityInstance> endProcessInstanceIdMap = Maps.newHashMap();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                if ("endEvent".equals(historicActivityInstance.getActivityType())) {
                    endProcessInstanceIdMap.put(historicActivityInstance.getProcessInstanceId(), historicActivityInstance);
                }
            }

            StringBuffer nativeHistoricTaskCountSql = new StringBuffer();
            nativeHistoricTaskCountSql.append("SELECT COUNT(a.PROC_INST_ID_) FROM (SELECT  MAX(START_TIME_), PROC_INST_ID_ FROM ACT_HI_TASKINST GROUP BY PROC_INST_ID_) a WHERE a.PROC_INST_ID_ IN ( ");
            StringBuffer nativeHistoricTaskSelectSql = new StringBuffer();
            nativeHistoricTaskSelectSql.append("SELECT " + getTop() + " MAX(START_TIME_) AS START_TIME_, PROC_INST_ID_, MAX(ID_) AS ID_ FROM ACT_HI_TASKINST WHERE PROC_INST_ID_ IN ( ");
            for (int i = 0; i < historicProcessInstanceList.size(); i++) {
                nativeHistoricTaskCountSql.append("'" + historicProcessInstanceList.get(i).getId() + "'");
                nativeHistoricTaskSelectSql.append("'" + historicProcessInstanceList.get(i).getId() + "'");
                if (historicProcessInstanceList.size() - 1 > i) {
                    nativeHistoricTaskCountSql.append(", ");
                    nativeHistoricTaskSelectSql.append(", ");
                } else {
                    nativeHistoricTaskCountSql.append(") ");
                    nativeHistoricTaskSelectSql.append(") ");
                }
            }
            nativeHistoricTaskSelectSql.append(" GROUP BY PROC_INST_ID_ ORDER BY START_TIME_ DESC ");
            List<HistoricTaskInstance> historicTaskInstanceList2 = null;
            NativeHistoricTaskInstanceQuery nativeHistoricTaskInstanceQuery = historyService.createNativeHistoricTaskInstanceQuery();
            if (pageSize < 0) {
                historicTaskInstanceList2 = nativeHistoricTaskInstanceQuery.sql(nativeHistoricTaskSelectSql.toString()).list();
            } else {
                historicTaskInstanceList2 = nativeHistoricTaskInstanceQuery.sql(nativeHistoricTaskSelectSql.toString()).listPage((pageNo - 1) * pageSize, pageSize);
            }
            if (historicTaskInstanceList2.size() == 0) {
                return null;
            }

            StringBuffer nativeHistoricTaskSelectSql2 = new StringBuffer("SELECT " + getTop() + " * FROM ACT_HI_TASKINST WHERE ID_ IN ( ");
            for (int i = 0; i < historicTaskInstanceList2.size(); i++) {
                nativeHistoricTaskSelectSql2.append("'" + historicTaskInstanceList2.get(i).getId() + "'");
                if (historicTaskInstanceList2.size() - 1 > i) {
                    nativeHistoricTaskSelectSql2.append(", ");
                } else {
                    nativeHistoricTaskSelectSql2.append(") ");
                }
            }
            nativeHistoricTaskSelectSql2.append(" ORDER BY START_TIME_ DESC ");
            List<HistoricTaskInstance> historicTaskInstanceList3 = historyService.createNativeHistoricTaskInstanceQuery().sql(nativeHistoricTaskSelectSql2.toString()).list();
            if (historicTaskInstanceList3.size() == 0) {
                return null;
            }
            long count = nativeHistoricTaskInstanceQuery.sql(nativeHistoricTaskCountSql.toString()).count();

            List<ProcessTree> processTreeList = Lists.newArrayList();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList3) {
                String dictValue = processDefinitionMap.get(historicTaskInstance.getProcessDefinitionId()).getCategory();
                //String dictLabel = DictUtil.getDictLabel(dictValue, DICT_ACT_CATEGORY, DICT_DEFAULT_VALUE);
                String dictLabel = dictDataService.getDictLabels(dictValue, DICT_ACT_CATEGORY, DICT_DEFAULT_VALUE, "");

                Map<String, Object> map = Maps.newHashMap();
                map.put(ProcessMap.PROC_INS_ID.name(), historicTaskInstance.getProcessInstanceId());
                map.put(ProcessMap.PROC_DEF_ID.name(), historicTaskInstance.getProcessDefinitionId());
                map.put(ProcessMap.REQUEST_MAPPING.name(), requestMappingMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.REST_PARAM.name(), restParamMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.ENTITY_ID.name(), entityIdMap.get(historicTaskInstance.getProcessInstanceId()));

                map.put(ProcessMap.PROC_CATEGORY.name(), dictValue);
                map.put(ProcessMap.PROC_CATEGORY_LABEL.name(), dictLabel);
                map.put(ProcessMap.PROC_CREATE_USER.name(), userNameMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.PROC_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(processInstanceStartTimeMap.get(historicTaskInstance.getProcessInstanceId())));

                HistoricActivityInstance historicActivityInstance = endProcessInstanceIdMap.get(historicTaskInstance.getProcessInstanceId());
                if (historicActivityInstance != null) {
                    map.put(ProcessMap.TASK_NAME.name(), ENDNAME);
                    map.put(ProcessMap.TASK_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(historicActivityInstance.getStartTime()));
                } else {
                    map.put(ProcessMap.TASK_NAME.name(), historicTaskInstance.getName());
                    map.put(ProcessMap.TASK_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(historicTaskInstance.getStartTime()));
                }

                ProcessTree processTree = new ProcessTree(map);
                processTreeList.add(processTree);
            }
            dataMap.put("total", count);
            dataMap.put("rows", processTreeList);

        } else if (Path.DONE.name().equalsIgnoreCase(path)) {
            Set<String> processInstanceIdSet = Sets.newHashSet();

            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processDefinitionKeyIn(processDefinitionKeyList).taskAssignee(loginName).finished()
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                processInstanceIdSet.add(historicTaskInstance.getProcessInstanceId());
            }
            List<HistoricTaskInstance> historicTaskInstanceListOw = historyService.createHistoricTaskInstanceQuery()
                    .processDefinitionKeyIn(processDefinitionKeyList).taskOwner(loginName).finished()
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceListOw) {
                processInstanceIdSet.add(historicTaskInstance.getProcessInstanceId());
            }
            if (processInstanceIdSet.size() == 0) {
                return null;
            }

            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            try {
                if (paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name()) != null
                        && StringUtils.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name()))) {
                    String createTimeBeginString = paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name());
                    createTimeBeginString += " 00:00:00";
                    Date createTimeBegin = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).parse(createTimeBeginString);
                    historicProcessInstanceQuery.startedAfter(createTimeBegin);
                }
                if (paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name()) != null
                        && StringUtils.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name()))) {
                    String createTimeEndString = paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name());
                    createTimeEndString += " 23:59:59";
                    Date createTimeEnd = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).parse(createTimeEndString);
                    historicProcessInstanceQuery.startedBefore(createTimeEnd);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<HistoricProcessInstance> historicProcessInstanceListTemp = historicProcessInstanceQuery.processInstanceIds(processInstanceIdSet).list();
            List<HistoricProcessInstance> historicProcessInstanceList = null;
            if (loginNameList == null) {
                historicProcessInstanceList = historicProcessInstanceListTemp;
            } else {
                historicProcessInstanceList = Lists.newArrayList();
                for (String ln : loginNameList) {
                    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceListTemp) {
                        if (ln.equals(historicProcessInstance.getStartUserId())) {
                            historicProcessInstanceList.add(historicProcessInstance);
                        }
                    }
                }
            }
            if (historicProcessInstanceList.size() == 0) {
                return null;
            }
            Map<String, Object> processInfoMap = getProcessInfoMap(historicProcessInstanceList);
            Map<String, String> requestMappingMap = (Map<String, String>) processInfoMap.get("requestMappingMap");
            Map<String, String> restParamMap = (Map<String, String>) processInfoMap.get("restParamMap");
            Map<String, String> entityIdMap = (Map<String, String>) processInfoMap.get("entityIdMap");
            Map<String, String> userNameMap = (Map<String, String>) processInfoMap.get("userNameMap");
            Map<String, Date> processInstanceStartTimeMap = (Map<String, Date>) processInfoMap.get("processInstanceStartTimeMap");

            StringBuffer nativeHistoricActivitySelectSql = new StringBuffer();
            nativeHistoricActivitySelectSql.append("SELECT " + getTop() + " MAX(START_TIME_) AS START_TIME_, PROC_INST_ID_, MAX(ID_) AS ID_ FROM ACT_HI_ACTINST WHERE PROC_INST_ID_ IN ( ");
            for (int i = 0; i < historicProcessInstanceList.size(); i++) {
                nativeHistoricActivitySelectSql.append("'" + historicProcessInstanceList.get(i).getId() + "'");
                if (historicProcessInstanceList.size() - 1 > i) {
                    nativeHistoricActivitySelectSql.append(", ");
                } else {
                    nativeHistoricActivitySelectSql.append(") ");
                }
            }
            nativeHistoricActivitySelectSql.append(" GROUP BY PROC_INST_ID_ ORDER BY START_TIME_ DESC ");
            List<HistoricActivityInstance> historicActivityInstanceList = null;
            NativeHistoricActivityInstanceQuery nativeHistoricActivityInstanceQuery = historyService.createNativeHistoricActivityInstanceQuery();
            if (pageSize < 0) {
                historicActivityInstanceList = nativeHistoricActivityInstanceQuery.sql(nativeHistoricActivitySelectSql.toString()).list();
            } else {
                historicActivityInstanceList = nativeHistoricActivityInstanceQuery.sql(nativeHistoricActivitySelectSql.toString()).listPage((pageNo - 1) * pageSize, pageSize);
            }
            Map<String, HistoricActivityInstance> endProcessInstanceIdMap = Maps.newHashMap();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                if ("endEvent".equals(historicActivityInstance.getActivityType())) {
                    endProcessInstanceIdMap.put(historicActivityInstance.getProcessInstanceId(), historicActivityInstance);
                }
            }

            StringBuffer nativeHistoricTaskCountSql = new StringBuffer();
            nativeHistoricTaskCountSql.append("SELECT COUNT(a.PROC_INST_ID_) FROM (SELECT  MAX(START_TIME_), PROC_INST_ID_ FROM ACT_HI_TASKINST GROUP BY PROC_INST_ID_) a WHERE a.PROC_INST_ID_ IN ( ");
            StringBuffer nativeHistoricTaskSelectSql = new StringBuffer();
            nativeHistoricTaskSelectSql.append("SELECT " + getTop() + " MAX(START_TIME_) AS START_TIME_, PROC_INST_ID_, MAX(ID_) AS ID_ FROM ACT_HI_TASKINST WHERE PROC_INST_ID_ IN ( ");
            for (int i = 0; i < historicProcessInstanceList.size(); i++) {
                nativeHistoricTaskCountSql.append("'" + historicProcessInstanceList.get(i).getId() + "'");
                nativeHistoricTaskSelectSql.append("'" + historicProcessInstanceList.get(i).getId() + "'");
                if (historicProcessInstanceList.size() - 1 > i) {
                    nativeHistoricTaskCountSql.append(", ");
                    nativeHistoricTaskSelectSql.append(", ");
                } else {
                    nativeHistoricTaskCountSql.append(") ");
                    nativeHistoricTaskSelectSql.append(") ");
                }
            }
            nativeHistoricTaskSelectSql.append(" GROUP BY PROC_INST_ID_ ORDER BY START_TIME_ DESC ");
            List<HistoricTaskInstance> historicTaskInstanceList2 = null;
            NativeHistoricTaskInstanceQuery nativeHistoricTaskInstanceQuery = historyService.createNativeHistoricTaskInstanceQuery();
            if (pageSize < 0) {
                historicTaskInstanceList2 = nativeHistoricTaskInstanceQuery.sql(nativeHistoricTaskSelectSql.toString()).list();
            } else {
                historicTaskInstanceList2 = nativeHistoricTaskInstanceQuery.sql(nativeHistoricTaskSelectSql.toString()).listPage((pageNo - 1) * pageSize, pageSize);
            }
            if (historicTaskInstanceList2.size() == 0) {
                return null;
            }

            StringBuffer nativeHistoricTaskSelectSql2 = new StringBuffer("SELECT " + getTop() + " * FROM ACT_HI_TASKINST WHERE ID_ IN ( ");
            for (int i = 0; i < historicTaskInstanceList2.size(); i++) {
                nativeHistoricTaskSelectSql2.append("'" + historicTaskInstanceList2.get(i).getId() + "'");
                if (historicTaskInstanceList2.size() - 1 > i) {
                    nativeHistoricTaskSelectSql2.append(", ");
                } else {
                    nativeHistoricTaskSelectSql2.append(") ");
                }
            }
            nativeHistoricTaskSelectSql2.append(" ORDER BY START_TIME_ DESC ");
            List<HistoricTaskInstance> historicTaskInstanceList3 = historyService.createNativeHistoricTaskInstanceQuery().sql(nativeHistoricTaskSelectSql2.toString()).list();
            if (historicTaskInstanceList3.size() == 0) {
                return null;
            }
            long count = nativeHistoricTaskInstanceQuery.sql(nativeHistoricTaskCountSql.toString()).count();

            List<ProcessTree> processTreeList = Lists.newArrayList();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList3) {
                String dictValue = processDefinitionMap.get(historicTaskInstance.getProcessDefinitionId()).getCategory();
                String dictLabel = dictDataService.getDictLabels(dictValue, DICT_ACT_CATEGORY, DICT_DEFAULT_VALUE, "");

                Map<String, Object> map = Maps.newHashMap();
                map.put(ProcessMap.PROC_INS_ID.name(), historicTaskInstance.getProcessInstanceId());
                map.put(ProcessMap.PROC_DEF_ID.name(), historicTaskInstance.getProcessDefinitionId());
                map.put(ProcessMap.REQUEST_MAPPING.name(), requestMappingMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.REST_PARAM.name(), restParamMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.ENTITY_ID.name(), entityIdMap.get(historicTaskInstance.getProcessInstanceId()));

                map.put(ProcessMap.PROC_CATEGORY.name(), dictValue);
                map.put(ProcessMap.PROC_CATEGORY_LABEL.name(), dictLabel);
                map.put(ProcessMap.PROC_CREATE_USER.name(), userNameMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.PROC_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(processInstanceStartTimeMap.get(historicTaskInstance.getProcessInstanceId())));

                HistoricActivityInstance historicActivityInstance = endProcessInstanceIdMap.get(historicTaskInstance.getProcessInstanceId());
                if (historicActivityInstance != null) {
                    map.put(ProcessMap.TASK_NAME.name(), ENDNAME);
                    map.put(ProcessMap.TASK_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(historicActivityInstance.getStartTime()));
                } else {
                    map.put(ProcessMap.TASK_NAME.name(), historicTaskInstance.getName());
                    map.put(ProcessMap.TASK_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(historicTaskInstance.getStartTime()));
                }

                ProcessTree processTree = new ProcessTree(map);
                processTreeList.add(processTree);
            }
            dataMap.put("total", count);
            dataMap.put("rows", processTreeList);

        } else if (Path.TODOANDDOING.name().equalsIgnoreCase(path)) {
            Map<String, String> taskIdMap = Maps.newHashMap();
            Map<String, Task> parentTaskIdMap = Maps.newHashMap();
            Set<String> processInstanceIdSet = Sets.newHashSet();

            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                Date now = new Date();
                if (assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().desc().list();
                    for (Task task : taskList) {
                        //Filter out notify tasks of agented uer.
                        if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                            continue;
                        }
                        //Filter out distribute tasks of agented uer.
                        else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                            continue;
                        }
                        Task t = parentTaskIdMap.get(task.getProcessInstanceId());
                        if (t != null) {
                            taskIdMap.remove(t.getId());
                        }
                        parentTaskIdMap.put(task.getProcessInstanceId(), task);
                        taskIdMap.put(task.getId(), task.getProcessInstanceId());
                        processInstanceIdSet.add(task.getProcessInstanceId());
                    }
                }
            }
            List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                    .taskOwner(loginName).active().orderByTaskCreateTime().desc().list();

            for (Task task : taskList) {
                //notify or distribute task
                if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE) || task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    Task t = parentTaskIdMap.get(task.getProcessInstanceId());
                    if (t != null) {
                        if (t.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE) || t.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                            taskIdMap.remove(t.getId());
                            parentTaskIdMap.put(task.getProcessInstanceId(), task);
                            taskIdMap.put(task.getId(), task.getProcessInstanceId());
                            processInstanceIdSet.add(task.getProcessInstanceId());
                        } else {
                            continue;
                        }
                    } else {
                        parentTaskIdMap.put(task.getProcessInstanceId(), task);
                        taskIdMap.put(task.getId(), task.getProcessInstanceId());
                        processInstanceIdSet.add(task.getProcessInstanceId());
                    }
                }
                //other task
                else {
                    Task t = parentTaskIdMap.get(task.getProcessInstanceId());
                    if (t != null) {
                        taskIdMap.remove(t.getId());
                    }
                    parentTaskIdMap.put(task.getProcessInstanceId(), task);
                    taskIdMap.put(task.getId(), task.getProcessInstanceId());
                    processInstanceIdSet.add(task.getProcessInstanceId());
                }
            }
            if (processInstanceIdSet.size() == 0) {
                return null;
            }

            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            try {
                if (paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name()) != null
                        && StringUtils.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name()))) {
                    String createTimeBeginString = paramMap.get(ProcessMap.PROC_CREATE_TIME_BEGIN.name());
                    createTimeBeginString += " 00:00:00";
                    Date createTimeBegin = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).parse(createTimeBeginString);
                    historicProcessInstanceQuery.startedAfter(createTimeBegin);
                }
                if (paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name()) != null
                        && StringUtils.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name()))) {
                    String createTimeEndString = paramMap.get(ProcessMap.PROC_CREATE_TIME_END.name());
                    createTimeEndString += " 23:59:59";
                    Date createTimeEnd = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).parse(createTimeEndString);
                    historicProcessInstanceQuery.startedBefore(createTimeEnd);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<HistoricProcessInstance> historicProcessInstanceListTemp = historicProcessInstanceQuery.processInstanceIds(processInstanceIdSet).list();
            List<HistoricProcessInstance> historicProcessInstanceList = null;
            if (loginNameList == null) {
                historicProcessInstanceList = historicProcessInstanceListTemp;
            } else {
                historicProcessInstanceList = Lists.newArrayList();
                for (String ln : loginNameList) {
                    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceListTemp) {
                        if (ln.equals(historicProcessInstance.getStartUserId())) {
                            historicProcessInstanceList.add(historicProcessInstance);
                        }
                    }
                }
            }
            if (historicProcessInstanceList.size() == 0) {
                return null;
            }

            List<String> taskIdList = Lists.newArrayList();
            for (Map.Entry<String, String> entry : taskIdMap.entrySet()) {
                for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
                    if (entry.getValue().equals(historicProcessInstance.getId())) {
                        taskIdList.add(entry.getKey());
                    }
                }
            }

            Map<String, Object> processInfoMap = getProcessInfoMap(historicProcessInstanceList);
            Map<String, String> requestMappingMap = (Map<String, String>) processInfoMap.get("requestMappingMap");
            Map<String, String> restParamMap = (Map<String, String>) processInfoMap.get("restParamMap");
            Map<String, String> entityIdMap = (Map<String, String>) processInfoMap.get("entityIdMap");
            Map<String, String> userNameMap = (Map<String, String>) processInfoMap.get("userNameMap");
            Map<String, Date> processInstanceStartTimeMap = (Map<String, Date>) processInfoMap.get("processInstanceStartTimeMap");

            String selectSql = "SELECT " + getTop() + " * ";
            String countSql = "SELECT count(ID_) ";
            String orderSql = " ORDER BY START_TIME_ DESC ";
            StringBuffer fromSql = new StringBuffer("FROM ACT_HI_TASKINST WHERE ID_ IN ( ");
            for (int i = 0; i < taskIdList.size(); i++) {
                fromSql.append("'" + taskIdList.get(i) + "'");
                if (taskIdList.size() - 1 > i) {
                    fromSql.append(", ");
                } else {
                    fromSql.append(") ");
                }
            }
            List<HistoricTaskInstance> historicTaskInstanceList = null;
            NativeHistoricTaskInstanceQuery nativeHistoricTaskInstanceQuery = historyService.createNativeHistoricTaskInstanceQuery();
            if (pageSize < 0) {
                historicTaskInstanceList = nativeHistoricTaskInstanceQuery.sql(selectSql + fromSql.toString() + orderSql).list();
            } else {
                historicTaskInstanceList = nativeHistoricTaskInstanceQuery.sql(selectSql + fromSql.toString() + orderSql).listPage((pageNo - 1) * pageSize, pageSize);
            }
            long count = nativeHistoricTaskInstanceQuery.sql(countSql + fromSql.toString()).count();

            List<ProcessTree> processTreeList = Lists.newArrayList();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                String dictValue = processDefinitionMap.get(historicTaskInstance.getProcessDefinitionId()).getCategory();
                String dictLabel = dictDataService.getDictLabels(dictValue, DICT_ACT_CATEGORY, DICT_DEFAULT_VALUE, "");

                Map<String, Object> map = Maps.newHashMap();
                map.put(ProcessMap.PROC_INS_ID.name(), historicTaskInstance.getProcessInstanceId());
                map.put(ProcessMap.PROC_DEF_ID.name(), historicTaskInstance.getProcessDefinitionId());
                map.put(ProcessMap.TASK_DEF_KEY.name(), historicTaskInstance.getTaskDefinitionKey());
                map.put(ProcessMap.REQUEST_MAPPING.name(), requestMappingMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.REST_PARAM.name(), restParamMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.ENTITY_ID.name(), entityIdMap.get(historicTaskInstance.getProcessInstanceId()));

                map.put(ProcessMap.PROC_CATEGORY.name(), dictValue);
                map.put(ProcessMap.PROC_CATEGORY_LABEL.name(), dictLabel);
                map.put(ProcessMap.PROC_CREATE_USER.name(), userNameMap.get(historicTaskInstance.getProcessInstanceId()));
                map.put(ProcessMap.PROC_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(processInstanceStartTimeMap.get(historicTaskInstance.getProcessInstanceId())));
                map.put(ProcessMap.TASK_NAME.name(), historicTaskInstance.getName());
                map.put(ProcessMap.TASK_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(historicTaskInstance.getCreateTime()));

                ProcessTree processTree = new ProcessTree(map);
                processTreeList.add(processTree);
            }

            dataMap.put("total", count);
            dataMap.put("rows", processTreeList);
        }

        return dataMap;
    }

    /**
     * Get process info map by historic process instance list.
     *
     * @param historicProcessInstanceList
     * @return Process info map.
     */
    private Map<String, Object> getProcessInfoMap(List<HistoricProcessInstance> historicProcessInstanceList) {
        Set<String> loginNameSet = Sets.newHashSet();
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
            loginNameSet.add(historicProcessInstance.getStartUserId());
        }
        List<String> loginNameList = Lists.newArrayList();
        for (String loginName : loginNameSet) {
            loginNameList.add(loginName);
        }
        List<User> userList = userService.findUserListByLoginNameList(loginNameList);
        Map<String, String> userNameMapTemp = Maps.newHashMap();
        for (User user : userList) {
            userNameMapTemp.put(user.getLoginName(), user.getName());
        }

        Map<String, String> requestMappingMap = Maps.newHashMap();
        Map<String, String> restParamMap = Maps.newHashMap();
        Map<String, String> entityIdMap = Maps.newHashMap();
        Map<String, String> userNameMap = Maps.newHashMap();
        Map<String, Date> processInstanceStartTimeMap = Maps.newHashMap();
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
            String businessKey = historicProcessInstance.getBusinessKey();
            //[0]:controllerClassName; [1]:entityId; [2]: formNo
            String[] businessKeyArray = businessKey.split(":");
            //String requestMappingValue = Arrays.asList(Class.forName(businessKeyArray[0]).getAnnotation(RequestMapping.class).value()).toString();
            //[${adminPath}/oa/edoc/edocSend]
            //int beginIndex = requestMappingValue.indexOf("/");
            //int endIndex = requestMappingValue.length() - 1;
            requestMappingMap.put(historicProcessInstance.getId(), businessKeyArray[0]);

            entityIdMap.put(historicProcessInstance.getId(), businessKeyArray[1]);

            if (businessKeyArray.length > 2) {
                restParamMap.put(historicProcessInstance.getId(), "&formNo=" + businessKeyArray[2]);
            } else {
                restParamMap.put(historicProcessInstance.getId(), "");
            }
            userNameMap.put(historicProcessInstance.getId(), userNameMapTemp.get(historicProcessInstance.getStartUserId()));
            processInstanceStartTimeMap.put(historicProcessInstance.getId(), historicProcessInstance.getStartTime());
        }

        Map<String, Object> processInfoMap = Maps.newHashMap();
        processInfoMap.put("requestMappingMap", requestMappingMap);
        processInfoMap.put("restParamMap", restParamMap);
        processInfoMap.put("entityIdMap", entityIdMap);
        processInfoMap.put("userNameMap", userNameMap);
        processInfoMap.put("processInstanceStartTimeMap", processInstanceStartTimeMap);

        return processInfoMap;
    }

    /**
     * Get process definition list by category.
     *
     * @param category
     * @param loginName
     * @return ProcessDefinition list.
     */
    public List<ProcessDefinition> getProcessDefinitionList(String category, String loginName) {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionCategory(category).latestVersion().orderByProcessDefinitionKey().asc().list();

        Map<String, Object> officeMap = null;
        User user = UserUtil.getByLoginName(loginName);
        if (user != null && user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getId())) {
            officeMap = Maps.newHashMap();
            this.searchUpOffice(user.getOffice().getId(), officeMap);
        }

        if (officeMap != null && officeMap.size() > 0) {
            Map<String, Object> processDefinitionKeyMap = Maps.newHashMap();
            List<Model> modelList = repositoryService.createModelQuery().modelCategory(category).list();
            for (Model model : modelList) {
                Map<String, String> metaInfoMap = new Gson().fromJson(model.getMetaInfo(), Map.class);
                if (StringUtils.isNotBlank(model.getTenantId())) {
                    if (officeMap.get(model.getTenantId()) != null) {
                        processDefinitionKeyMap.put(metaInfoMap.get("procDefKey"), metaInfoMap.get("procDefKey"));
                    } else {
                        //Filter
                    }
                } else {
                    processDefinitionKeyMap.put(metaInfoMap.get("procDefKey"), metaInfoMap.get("procDefKey"));
                }
            }

            List<ProcessDefinition> dataList = Lists.newArrayList();
            for (ProcessDefinition processDefinition : processDefinitionList) {
                if (processDefinitionKeyMap.get(processDefinition.getKey()) != null) {
                    dataList.add(processDefinition);
                }
            }
            processDefinitionList = dataList;
        }

        return processDefinitionList;
    }

    /**
     * Search office and put it to map.
     *
     * @param officeId
     * @param officeMap
     */
    private void searchUpOffice(String officeId, Map<String, Object> officeMap) {
        Office office = officeService.get(officeId);
        if (office != null) {
            officeMap.put(office.getId(), office);
            if (StringUtils.isNotBlank(office.getParentId())) {
                searchUpOffice(office.getParentId(), officeMap);
            }
        }
    }

    /**
     * Get process definition by key.
     *
     * @param processDefinitionKey
     * @return ProcessDefinition.
     */
    public ProcessDefinition getProcessDefinition(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
    }

    /**
     * Send message.
     *
     * @param entity
     * @param type
     * @param loginName
     */
    private void sendMessage(T entity, String type, String loginName) {
        boolean result = isNeedSendMessage(entity, type);
        if (result) {
            String processInstanceId = entity.getProcInsId();
            List<String> ownerList = Lists.newArrayList();
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId).list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                String owner = historicTaskInstance.getOwner();
                if (StringUtils.isNotBlank(owner)) {
                    ownerList.add(owner);
                }
            }
            List<User> receiverList = userService.findUserListByLoginNameList(ownerList);

            List<SysMsg> sysMsgLists = getSysMsg(entity, type, receiverList, loginName);
            for (SysMsg sysMsg : sysMsgLists) {
                sysMsgService.save(sysMsg);
                sysMsgService.sendSysMsg(sysMsg.getRecipient().getId());
            }
        }
    }

    /**
     * Get is need to be send message or not.
     *
     * @param entity
     * @param type
     * @return True of false.
     */
    private boolean isNeedSendMessage(T entity, String type) {
        String t = "";
        if (StringUtils.isNotBlank(type) && ButtonType.SAVEANDSTART.name().equalsIgnoreCase(type)) {
            t = ButtonType.SAVEANDCOMPLETE.name();
        } else {
            t = type;
        }

        boolean result = false;
        if (entity.getEdocType() != null) {
            String processDefinitionCategory = entity.getEdocType();
            List<TaskMessage> taskMessageList = taskMessageService.findList(new TaskMessage());
            for (TaskMessage taskMessage : taskMessageList) {
                String[] processScopeArray = taskMessage.getProcessScope().split(SPLIT_COMMA);
                for (String processScope : processScopeArray) {
                    if (processDefinitionCategory.equals(processScope)) {
                        String[] operationArray = taskMessage.getOperation().split(SPLIT_COMMA);
                        for (String operation : operationArray) {
                            if (t.equalsIgnoreCase(operation)) {
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get message list.
     *
     * @param entity
     * @param type
     * @param receiverList
     * @param loginName
     * @return Message list.
     */
    private List<SysMsg> getSysMsg(T entity, String type, List<User> receiverList, String loginName) {
        List<SysMsg> sysMsgList = Lists.newArrayList();

        String record = entity.getId();
        Date sendTime = new Date(System.currentTimeMillis());
        String username = UserUtil.getByLoginName(loginName).getName();
        String dateString = new SimpleDateFormat(SIMPLE_DATE_FORMAT_HMS).format(sendTime);
        String oaTaskOperation = dictDataService.getDictLabels(type, DICT_OA_TASK_OPERATION, DICT_DEFAULT_VALUE, "");
        String actCategory = dictDataService.getDictLabels(entity.getEdocType(), DICT_ACT_CATEGORY, DICT_DEFAULT_VALUE, "");
        String title = SQUARE_BRACKET_LEFT + actCategory + SQUARE_BRACKET_RIGHT
                + username + AT + dateString + oaTaskOperation;
        String content = SQUARE_BRACKET_LEFT + actCategory + SQUARE_BRACKET_RIGHT
                + username + AT + dateString + oaTaskOperation;
        if (false == StringUtils.isBlank(entity.getAct().getComment())) {
            content += SQUARE_BRACKET_LEFT + entity.getAct().getComment() + SQUARE_BRACKET_RIGHT;
        }

        String menuHref = "/admin/oa_done/list";
        String menuName = "已办事项";
        String menuName_EN = "Completed Tasks";
        User currentUser = UserUtil.getByLoginName(loginName);
        for (User user : receiverList) {
            SysMsg sysMsg = new SysMsg();
            sysMsg.setCreateBy(currentUser);
            sysMsg.setUpdateBy(currentUser);
            sysMsg.setOwnerCode(currentUser.getCompany().getCode());
            sysMsg.setTypes("default");
            sysMsg.setTitle(title);
            sysMsg.setContent(content);
            sysMsg.setRecord(record);
            sysMsg.setMenuName(menuName);
            sysMsg.setMenuName_EN(menuName_EN);
            sysMsg.setMenuHref(menuHref);
            sysMsg.setSendTime(sendTime);
            sysMsg.setRecipient(user);
            sysMsg.setStatus(Global.YES);
            //sysMsg.setOwnerCode(Global.ROOT_OWNERCODE);
            sysMsgList.add(sysMsg);
        }
        return sysMsgList;
    }

    /**
     * Get urge process list by category list.
     *
     * @param categoryList
     * @param categoryLimitMap
     * @param lang             EN or Chinese.
     * @return UrgeProcess list.
     */
    public List<UrgeProcess> getUrgeProcessList(List<String> categoryList, Map<String, String> categoryLimitMap, String lang) {
        boolean langIsEN = lang.equalsIgnoreCase(Global.LANG_EN);
        List<UrgeProcess> dataList = null;
        //Urge Process Definition
        if (categoryList.size() > 0) {
            Map<String, String> categoryMap = Maps.newHashMap();
            List<String> keyList = Lists.newArrayList();
            List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionVersion().desc().list();
            for (ProcessDefinition processDefinition : processDefinitionList) {
                String category = processDefinition.getCategory();
                for (String categoryI : categoryList) {
                    if (categoryI.equals(category)) {
                        keyList.add(processDefinition.getKey());
                        categoryMap.put(processDefinition.getId(), category);
                    }
                }
            }
            //Runtime Tasks
            if (keyList.size() > 0) {
                dataList = Lists.newArrayList();

                Map<String, HistoricProcessInstance> historicProcessInstanceMap = Maps.newHashMap();
                List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().unfinished().processDefinitionKeyIn(keyList).list();
                if (historicProcessInstanceList.size() > 0) {
                    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
                        historicProcessInstanceMap.put(historicProcessInstance.getId(), historicProcessInstance);
                    }
                } else {
                    return dataList;
                }

                Map<String, Object> processInfoMap = getProcessInfoMap(historicProcessInstanceList);
                Map<String, String> requestMappingMap = (Map<String, String>) processInfoMap.get("requestMappingMap");
                Map<String, String> entityIdMap = (Map<String, String>) processInfoMap.get("entityIdMap");
                Map<String, String> userNameMap = (Map<String, String>) processInfoMap.get("userNameMap");
                Map<String, Date> processInstanceStartTimeMap = (Map<String, Date>) processInfoMap.get("processInstanceStartTimeMap");

                List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                        .unfinished().processDefinitionKeyIn(keyList).orderByTaskCreateTime().desc().list();
                for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                    String limitHours = categoryLimitMap.get(categoryMap.get(historicTaskInstance.getProcessDefinitionId()));
                    Date startTime = historicTaskInstance.getStartTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTime);
                    calendar.add(Calendar.HOUR, Integer.parseInt(limitHours));
                    Date limitTime = calendar.getTime();
                    Date now = new Date();
                    if (limitTime.before(now)) {
                        UrgeProcess urgeProcess = new UrgeProcess();
                        urgeProcess.setRequestMapping(requestMappingMap.get(historicTaskInstance.getProcessInstanceId()));
                        urgeProcess.setEntityId(entityIdMap.get(historicTaskInstance.getProcessInstanceId()));

                        urgeProcess.setTaskId(historicTaskInstance.getId());
                        String dictValue = categoryMap.get(historicTaskInstance.getProcessDefinitionId());
                        urgeProcess.setProcCategory(dictValue);
                        String userName = userNameMap.get(historicTaskInstance.getProcessInstanceId());
                        if (StringUtils.isNotBlank(userName)) {
                            urgeProcess.setProcCreateUser(userName);
                        }

                        Date processInstanceStartTime = processInstanceStartTimeMap.get(historicTaskInstance.getProcessInstanceId());
                        if (processInstanceStartTime != null) {
                            urgeProcess.setProcCreateTime(new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(processInstanceStartTime));
                        }

                        String durationTimeStr = "";
                        long durationInMillis = now.getTime() - historicTaskInstance.getStartTime().getTime();
                        if (durationInMillis < 1000) {
                            durationTimeStr = langIsEN ? "<1s" : "小于1秒";
                        } else {
                            long dayMillis = 84600000;
                            int day = (int) (durationInMillis / dayMillis);
                            if (day > 0) {
                                durationTimeStr += (day + (langIsEN ? "days" : "天"));
                                durationInMillis -= (dayMillis * day);
                            }
                            long hourMillis = 3600000;
                            int hour = (int) (durationInMillis / hourMillis);
                            if (hour > 0 || (hour == 0 && day > 0)) {
                                durationTimeStr += (hour + (langIsEN ? "hours" : "时"));
                                durationInMillis -= (hourMillis * hour);
                            }
                            long minMillis = 60000;
                            int min = (int) (durationInMillis / minMillis);
                            if (min > 0 || (min == 0 && (day > 0 || hour > 0))) {
                                durationTimeStr += (min + (langIsEN ? "minute" : "分"));
                                durationInMillis -= (minMillis * min);
                            }
                            long secMillis = 1000;
                            int sec = (int) (durationInMillis / secMillis);
                            durationTimeStr += (sec + (langIsEN ? "second" : "秒"));
                        }
                        urgeProcess.setDuringTime(durationTimeStr);
                        urgeProcess.setStartTime(startTime);
                        urgeProcess.setCnode(historicTaskInstance.getName());
                        String owner = historicTaskInstance.getOwner();
                        List<String> ownerList = Lists.newArrayList();
                        if (StringUtils.isBlank(owner)) {
                            List<HistoricIdentityLink> historicIdentityLinks = null;
                            historicIdentityLinks = historyService.getHistoricIdentityLinksForTask(historicTaskInstance.getId());
                            if (historicIdentityLinks == null || historicIdentityLinks.size() == 0) {
                                historicIdentityLinks = historyService.getHistoricIdentityLinksForProcessInstance(historicTaskInstance.getProcessInstanceId());
                            }
                            for (HistoricIdentityLink historicIdentityLink : historicIdentityLinks) {
                                ownerList.add(historicIdentityLink.getUserId());
                            }
                        } else {
                            ownerList.add(owner);
                        }
                        if (ownerList != null && ownerList.size() > 0) {
                            for (String ow : ownerList) {
                                User user = UserUtil.getByLoginName(ow);

                                String cuserId = urgeProcess.getCuserId();
                                if (StringUtils.isBlank(cuserId)) {
                                    urgeProcess.setCuserId(user.getId());
                                    urgeProcess.setCuser(user.getName());
                                } else {
                                    String cid = "," + urgeProcess.getCuserId() + ",";
                                    String uid = "," + user.getId() + ",";
                                    if (cid.indexOf(uid) == -1) {
                                        urgeProcess.setCuserId(urgeProcess.getCuserId() + "," + user.getId());
                                        urgeProcess.setCuser(urgeProcess.getCuser() + "," + user.getName());
                                    }
                                }
                            }
                        }
                        dataList.add(urgeProcess);
                    }
                }
            }
        }
        return dataList;
    }

    /**
     * Get overtime task list by category list.
     *
     * @param categoryList
     * @param categoryLimitMap
     * @param pageNo
     * @param pageSize
     * @param loginName
     * @return Overtime task list map.
     */
    public Map<String, Object> getOvertimeTaskList(List<String> categoryList, Map<String, String> categoryLimitMap, int pageNo, int pageSize, String loginName) {
        StringBuffer processDefinitionQuerySql = new StringBuffer();
        processDefinitionQuerySql.append("SELECT * FROM ACT_RE_PROCDEF RES ");
        if (categoryList != null && categoryList.size() > 0) {
            processDefinitionQuerySql.append("WHERE RES.CATEGORY_ IN ( ");
            for (int i = 0; i < categoryList.size(); i++) {
                if (StringUtils.isNotBlank(categoryList.get(i))) {
                    processDefinitionQuerySql.append("'" + categoryList.get(i) + "'");
                    if (categoryList.size() - 1 > i) {
                        processDefinitionQuerySql.append(", ");
                    } else {
                        processDefinitionQuerySql.append(") ");
                    }
                }
            }
        }
        List<ProcessDefinition> processDefinitionList = repositoryService.createNativeProcessDefinitionQuery().sql(processDefinitionQuerySql.toString()).list();
        if (processDefinitionList.size() == 0) {
            return null;
        }
        List<String> processDefinitionKeyList = Lists.newArrayList();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            processDefinitionKeyList.add(processDefinition.getKey());
        }
        Map<String, ProcessDefinition> processDefinitionMap = Maps.newHashMap();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            processDefinitionMap.put(processDefinition.getId(), processDefinition);
        }

        Map<String, Object> dataMap = Maps.newHashMap();
        Map<String, String> parentTaskIdMap = Maps.newHashMap();
        Map<String, String> taskIdMap = Maps.newHashMap();
        Set<String> processInstanceIdSet = Sets.newHashSet();

        List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
        Date now = new Date();
        for (AssigneeSetting assigneeSetting : assigneeList) {
            if (assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                        .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                for (Task task : taskList) {
                    //Filter out notify tasks of agented user.
                    if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                        continue;
                    }
                    //Filter out distribute tasks of agented user.
                    else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                        continue;
                    }
                    String limitHours = categoryLimitMap.get(processDefinitionMap.get(task.getProcessDefinitionId()).getCategory());
                    Date createTime = task.getCreateTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createTime);
                    calendar.add(Calendar.HOUR, Integer.parseInt(limitHours));
                    Date limitTime = calendar.getTime();
                    //Use before query date
                    if (limitTime.before(now)) {
                        String tid = parentTaskIdMap.get(task.getProcessInstanceId());
                        if (StringUtils.isNotBlank(tid)) {
                            taskIdMap.remove(tid);
                        }
                        parentTaskIdMap.put(task.getProcessInstanceId(), task.getId());
                        taskIdMap.put(task.getId(), task.getProcessInstanceId());
                        processInstanceIdSet.add(task.getProcessInstanceId());
                    }
                }
            }
        }
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKeyIn(processDefinitionKeyList)
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();
        for (Task task : taskList) {
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                continue;
            }
            //Filter out distribute tasks of agented user.
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                continue;
            }
            String limitHours = categoryLimitMap.get(processDefinitionMap.get(task.getProcessDefinitionId()).getCategory());
            if (StringUtils.isNotBlank(limitHours)) {
                Date createTime = task.getCreateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(createTime);
                calendar.add(Calendar.HOUR, Integer.parseInt(limitHours));
                Date limitTime = calendar.getTime();
                if (limitTime.before(now)) {//use before query date
                    String tid = parentTaskIdMap.get(task.getProcessInstanceId());
                    if (StringUtils.isNotBlank(tid)) {
                        taskIdMap.remove(tid);
                    }
                    parentTaskIdMap.put(task.getProcessInstanceId(), task.getId());
                    taskIdMap.put(task.getId(), task.getProcessInstanceId());
                    processInstanceIdSet.add(task.getProcessInstanceId());
                }
            }
        }
        if (processInstanceIdSet.size() == 0) {
            return null;
        }

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> historicProcessInstanceList = historicProcessInstanceQuery.processInstanceIds(processInstanceIdSet).list();

        if (historicProcessInstanceList.size() == 0) {
            return null;
        }

        List<String> taskIdList = Lists.newArrayList();
        for (Map.Entry<String, String> entry : taskIdMap.entrySet()) {
            for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
                if (entry.getValue().equals(historicProcessInstance.getId())) {
                    taskIdList.add(entry.getKey());
                }
            }
        }

        Map<String, Object> processInfoMap = getProcessInfoMap(historicProcessInstanceList);
        Map<String, String> requestMappingMap = (Map<String, String>) processInfoMap.get("requestMappingMap");
        Map<String, String> restParamMap = (Map<String, String>) processInfoMap.get("restParamMap");
        Map<String, String> entityIdMap = (Map<String, String>) processInfoMap.get("entityIdMap");
        Map<String, String> userNameMap = (Map<String, String>) processInfoMap.get("userNameMap");
        Map<String, Date> processInstanceStartTimeMap = (Map<String, Date>) processInfoMap.get("processInstanceStartTimeMap");

        String selectSql = "SELECT " + getTop() + " * ";
        String countSql = "SELECT count(ID_) ";
        String orderSql = " ORDER BY START_TIME_ DESC ";
        StringBuffer fromSql = new StringBuffer("FROM ACT_HI_TASKINST WHERE ID_ IN ( ");
        for (int i = 0; i < taskIdList.size(); i++) {
            fromSql.append("'" + taskIdList.get(i) + "'");
            if (taskIdList.size() - 1 > i) {
                fromSql.append(", ");
            } else {
                fromSql.append(") ");
            }
        }
        List<HistoricTaskInstance> historicTaskInstanceList = null;
        NativeHistoricTaskInstanceQuery nativeHistoricTaskInstanceQuery = historyService.createNativeHistoricTaskInstanceQuery();
        if (pageSize < 0) {
            historicTaskInstanceList = nativeHistoricTaskInstanceQuery.sql(selectSql + fromSql.toString() + orderSql).list();
        } else {
            historicTaskInstanceList = nativeHistoricTaskInstanceQuery.sql(selectSql + fromSql.toString() + orderSql).listPage((pageNo - 1) * pageSize, pageSize);
        }
        long count = nativeHistoricTaskInstanceQuery.sql(countSql + fromSql.toString()).count();

        List<ProcessTree> processTreeList = Lists.newArrayList();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            String dictValue = processDefinitionMap.get(historicTaskInstance.getProcessDefinitionId()).getCategory();
            String dictLabel = dictDataService.getDictLabels(dictValue, DICT_ACT_CATEGORY, DICT_DEFAULT_VALUE, "");

            Map<String, Object> map = Maps.newHashMap();
            map.put(ProcessMap.PROC_INS_ID.name(), historicTaskInstance.getProcessInstanceId());
            map.put(ProcessMap.PROC_DEF_ID.name(), historicTaskInstance.getProcessDefinitionId());
            map.put(ProcessMap.TASK_DEF_KEY.name(), historicTaskInstance.getTaskDefinitionKey());
            map.put(ProcessMap.REQUEST_MAPPING.name(), requestMappingMap.get(historicTaskInstance.getProcessInstanceId()));
            map.put(ProcessMap.REST_PARAM.name(), restParamMap.get(historicTaskInstance.getProcessInstanceId()));
            map.put(ProcessMap.ENTITY_ID.name(), entityIdMap.get(historicTaskInstance.getProcessInstanceId()));

            map.put(ProcessMap.PROC_CATEGORY.name(), dictLabel);
            map.put(ProcessMap.PROC_CREATE_USER.name(), userNameMap.get(historicTaskInstance.getProcessInstanceId()));
            map.put(ProcessMap.PROC_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(processInstanceStartTimeMap.get(historicTaskInstance.getProcessInstanceId())));
            map.put(ProcessMap.TASK_ID.name(), historicTaskInstance.getId());
            map.put(ProcessMap.TASK_NAME.name(), historicTaskInstance.getName());
            map.put(ProcessMap.TASK_CREATE_TIME.name(), new SimpleDateFormat(SIMPLE_DATE_FORMAT_HM).format(historicTaskInstance.getCreateTime()));

            ProcessTree processTree = new ProcessTree(map);
            processTreeList.add(processTree);
        }

        dataMap.put("total", count);
        dataMap.put("rows", processTreeList);

        return dataMap;
    }

    /**
     * Deploy model.
     *
     * @param entity
     */
    private void deployModel(T entity) {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(entity.getAct().getProcDefKey()).latestVersion().list();
        String modelId = processDefinitionList.get(0).getDescription().split(",")[0];
        actModelService.deploy(modelId);
    }

    /**
     * Node personnel range enumeration value.
     */
    private enum SettingValue {
        id, type, name, office, post, level, role, user, relative, org;
    }

    /**
     * Relative role prefix enumeration value.
     */
    private enum RelativeIdPre {
        creater_, creater, createrSuperDept, createrDeptMain, createrDeptOther, createrOrg, createrOrgMain, pre, preSuperDept, preDeptMain, preDeptOther, preOrg, preOrgMain;
    }

    /**
     * Relative role enumeration value.
     */
    private enum RelativeId {
        DeptMember, DeptLeaderMain, DeptLeaderOther, OrgMember, OrgLeaderMain;
    }

    /**
     * Conditional enumeration value.
     */
    private enum Flag {
        yes, no, other, flag;
    }

    /**
     * Rule variable.
     */
    private enum RuleArgs {
        key, value,
        form, content, hand, automatic, extend, operation, formExtend,
        flag, reject;
    }

    /**
     * Get user list.
     *
     * @param entity
     * @param theLoginName
     * @return User list.
     */
    public List<User> getUserList(T entity, String theLoginName) {
        User currentUser = UserUtil.getByLoginName(theLoginName);
        String settingValue = getTaskSettingVersionByAct(entity.getAct()).getSettingValue();
        settingValue = StringEscapeUtils.unescapeHtml(settingValue);
        if (settingValue.startsWith("{")) {
            settingValue = "[" + settingValue;
        }
        if (settingValue.endsWith("}")) {
            settingValue = settingValue + "]";
        }
        @SuppressWarnings("unchecked")
        List<Map<String, String>> settingValueList = new Gson().fromJson(settingValue, List.class);
        List<String> officeIdList = Lists.newArrayList();
        List<String> postIdList = Lists.newArrayList();
        List<String> levelIdList = Lists.newArrayList();
        List<String> roleIdList = Lists.newArrayList();
        List<String> userIdList = Lists.newArrayList();
        List<String> relativeIdList = Lists.newArrayList();
        List<String> orgIdList = Lists.newArrayList();
        for (Map<String, String> map : settingValueList) {
            if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.office.name())) {
                officeIdList.add(map.get(SettingValue.id.name()));
            } else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.level.name())) {
                levelIdList.add(map.get(SettingValue.id.name()));
            } else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.post.name())) {
                postIdList.add(map.get(SettingValue.id.name()));
            } else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.role.name())) {
                roleIdList.add(map.get(SettingValue.id.name()));
            } else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.user.name())) {
                userIdList.add(map.get(SettingValue.id.name()));
            } else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.relative.name())) {
                relativeIdList.add(map.get(SettingValue.id.name()));
            } else if (map.get(SettingValue.type.name()).equalsIgnoreCase(SettingValue.org.name())) {
                orgIdList.add(map.get(SettingValue.id.name()));
            }
        }
        List<User> userListTemp = Lists.newArrayList();
        Map<String, User> userMap = Maps.newHashMap();
        if (officeIdList.size() > 0) {
            userListTemp = userService.findUserListByOfficeIdList(officeIdList);
            for (User user : userListTemp) {
                userMap.put(user.getId(), user);
            }
        }
        if (levelIdList.size() > 0) {
            userListTemp = userService.findUserListByLevelIdList(levelIdList);
            for (User user : userListTemp) {
                userMap.put(user.getId(), user);
            }
        }
        if (postIdList.size() > 0) {
            userListTemp = userService.findUserListByPostIdList(postIdList);
            for (User user : userListTemp) {
                userMap.put(user.getId(), user);
            }
        }
        if (roleIdList.size() > 0) {
            userListTemp = userService.findUserListByRoleIdList(roleIdList);
            for (User user : userListTemp) {
                userMap.put(user.getId(), user);
            }
        }
        if (userIdList.size() > 0) {
            userListTemp = userService.findUserListByUserIdList(userIdList);
            for (User user : userListTemp) {
                userMap.put(user.getId(), user);
            }
        }
        if (orgIdList.size() > 0) {
            userListTemp = userService.findUserListByOrgIdList(orgIdList);
            for (User user : userListTemp) {
                userMap.put(user.getId(), user);
            }
        }
        if (relativeIdList.size() > 0) {
            User applyUser = null;
            //User currentUser = UserUtils.getUser();
            if (entity != null && false == StringUtils.isBlank(entity.getProcInsId())) {
                HistoricVariableInstance historicVariableInstance = historyService
                        .createHistoricVariableInstanceQuery().processInstanceId(entity.getProcInsId())
                        .variableName("applyUserId").singleResult();
                String loginName = historicVariableInstance.getValue().toString();
                if (loginName.indexOf("[") != -1) {
                    applyUser = UserUtil.getByLoginName(loginName.substring(1, loginName.lastIndexOf("]")));
                } else {
                    applyUser = UserUtil.getByLoginName(loginName);
                }
            } else {
                applyUser = currentUser;
            }
            List<Office> officeList = Lists.newArrayList();
            List<Organization> orgList = Lists.newArrayList();
            for (String relativeId : relativeIdList) {
                //Traversing prefix
                if (relativeId.equalsIgnoreCase(RelativeIdPre.creater_.name())) {
                    //Starter
                    userMap.put(applyUser.getId(), applyUser);
                    officeList.add(officeService.get(applyUser.getOffice().getId()));
                } else if (relativeId.indexOf(RelativeIdPre.creater.name()) != -1) {
                    //Starter's department
                    officeList.add(officeService.get(applyUser.getOffice().getId()));
                } else if (relativeId.indexOf(RelativeIdPre.createrSuperDept.name()) != -1) {
                    //Query applyUser's parent department
                    String code = applyUser.getOffice().getCode();
                    Office office = officeService.findUniqueByProperty("ownerCode",
                            code.substring(0, code.length() - 3));
                    officeList.add(office);
                } else if (relativeId.indexOf(RelativeIdPre.createrDeptMain.name()) != -1) {
                    //Query principal person in charge is applyUser
                    Office office = new Office();
                    office.getSqlMap().put("dsf", " AND a.primary_person = '" + applyUser.getId() + "' ");
                    officeList = officeService.findList(office);
                } else if (relativeId.indexOf(RelativeIdPre.createrDeptOther.name()) != -1) {
                    //Query deputy responsible person is applyUser
                    Office office = new Office();
                    office.getSqlMap().put("dsf", " AND a.deputy_person = '" + applyUser.getId() + "' ");
                    officeList = officeService.findList(office);
                } else if (relativeId.indexOf(RelativeIdPre.preSuperDept.name()) != -1) {
                    //Query currentUser parent department
                    String code = currentUser.getOffice().getCode();
                    Office office = officeService.findUniqueByProperty("ownerCode",
                            code.substring(0, code.length() - 3));
                    officeList.add(office);
                } else if (relativeId.indexOf(RelativeIdPre.preDeptMain.name()) != -1) {
                    //Query principal person in charge is current user
                    Office office = new Office();
                    office.getSqlMap().put("dsf", " AND a.primary_person = '" + currentUser.getId() + "' ");
                    officeList = officeService.findList(office);
                } else if (relativeId.indexOf(RelativeIdPre.preDeptOther.name()) != -1) {
                    //Query deputy responsible person is current user
                    Office office = new Office();
                    office.getSqlMap().put("dsf", " AND a.deputy_person = '" + currentUser.getId() + "' ");
                    officeList = officeService.findList(office);
                } else if (relativeId.indexOf(RelativeIdPre.pre.name()) != -1) {
                    officeList.add(currentUser.getOffice());
                }
                //Traversing suffix
                if (relativeId.indexOf(RelativeId.DeptMember.name()) != -1) {
                    officeIdList = Lists.newArrayList();
                    for (Office office : officeList) {
                        officeIdList.add(office.getId());
                    }
                    //Query members of department list
                    if (officeIdList.size() > 0) {
                        List<User> userList = userService.findUserListByOfficeIdList(officeIdList);
                        for (User user : userList) {
                            userMap.put(user.getId(), user);
                        }
                    }
                } else if (relativeId.indexOf(RelativeId.DeptLeaderMain.name()) != -1) {
                    //Query principal person in charge of department
                    userIdList = Lists.newArrayList();
                    for (Office office : officeList) {
                        if (office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                            userIdList.add(office.getPrimaryPerson().getId());
                        }
                    }
                    if (userIdList.size() > 0) {
                        List<User> userList = userService.findUserListByUserIdList(userIdList);
                        for (User user : userList) {
                            userMap.put(user.getId(), user);
                        }
                    }
                } else if (relativeId.indexOf(RelativeId.DeptLeaderOther.name()) != -1) {
                    //Query deputy responsible person of department
                    userIdList = Lists.newArrayList();
                    for (Office office : officeList) {
                        if (office.getDeputyPerson() != null && office.getDeputyPerson().getId() != null) {
                            userIdList.add(office.getDeputyPerson().getId());
                        }
                    }
                    if (userIdList.size() > 0) {
                        List<User> userList = userService.findUserListByUserIdList(userIdList);
                        for (User user : userList) {
                            userMap.put(user.getId(), user);
                        }
                    }
                }
                //Traversing organization prefix
                if (relativeId.indexOf(RelativeIdPre.createrOrg.name()) != -1) {
                    Organization organization = new Organization();
                    organization.getSqlMap().put("dsf", " AND sou.user_id = '" + applyUser.getId() + "' ");
                    orgList = organizationService.findListByUser(organization);
                    Map<String, Organization> orgListTempMap = Maps.newHashMap();
                    for (Organization org : orgList) {
                        orgListTempMap.put(org.getId(), org);
                    }
                    orgList = Lists.newArrayList();
                    for (Map.Entry<String, Organization> entry : orgListTempMap.entrySet()) {
                        orgList.add(entry.getValue());
                    }
                } else if (relativeId.indexOf(RelativeIdPre.createrOrgMain.name()) != -1) {
                    Organization organization = new Organization();
                    organization.getSqlMap().put("dsf", " AND a.primaryperson_id = '" + applyUser.getId() + "' ");
                    orgList = organizationService.findListByUser(organization);
                    Map<String, Organization> orgListTempMap = Maps.newHashMap();
                    for (Organization org : orgList) {
                        orgListTempMap.put(org.getId(), org);
                    }
                    orgList = Lists.newArrayList();
                    for (Map.Entry<String, Organization> entry : orgListTempMap.entrySet()) {
                        orgList.add(entry.getValue());
                    }
                } else if (relativeId.indexOf(RelativeIdPre.preOrg.name()) != -1) {
                    Organization organization = new Organization();
                    organization.getSqlMap().put("dsf", " AND sou.user_id = '" + currentUser.getId() + "' ");
                    orgList = organizationService.findListByUser(organization);
                    Map<String, Organization> orgListTempMap = Maps.newHashMap();
                    for (Organization org : orgList) {
                        orgListTempMap.put(org.getId(), org);
                    }
                    orgList = Lists.newArrayList();
                    for (Map.Entry<String, Organization> entry : orgListTempMap.entrySet()) {
                        orgList.add(entry.getValue());
                    }
                } else if (relativeId.indexOf(RelativeIdPre.preOrgMain.name()) != -1) {
                    Organization organization = new Organization();
                    organization.getSqlMap().put("dsf", " AND a.primaryperson_id = '" + currentUser.getId() + "' ");
                    orgList = organizationService.findListByUser(organization);
                    Map<String, Organization> orgListTempMap = Maps.newHashMap();
                    for (Organization org : orgList) {
                        orgListTempMap.put(org.getId(), org);
                    }
                    orgList = Lists.newArrayList();
                    for (Map.Entry<String, Organization> entry : orgListTempMap.entrySet()) {
                        orgList.add(entry.getValue());
                    }
                }
                //Traversing organization suffix
                if (relativeId.indexOf(RelativeId.OrgMember.name()) != -1) {
                    orgIdList = Lists.newArrayList();
                    for (Organization org : orgList) {
                        orgIdList.add(org.getId());
                    }
                    if (orgIdList.size() > 0) {
                        List<User> userList = userService.findUserListByOrgIdList(orgIdList);
                        for (User user : userList) {
                            userMap.put(user.getId(), user);
                        }
                    }
                } else if (relativeId.indexOf(RelativeId.OrgLeaderMain.name()) != -1) {
                    userIdList = Lists.newArrayList();
                    for (Organization org : orgList) {
                        userIdList.add(org.getPrimaryPerson().getId());
                    }
                    if (userIdList.size() > 0) {
                        List<User> userList = userService.findUserListByUserIdList(userIdList);
                        for (User user : userList) {
                            userMap.put(user.getId(), user);
                        }
                    }
                }
            }
        }
        List<User> userList = Lists.newArrayList();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            //Filter user logic
            if (entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().getIsSys()) && Global.YES.equals(entry.getValue().getIsSys())) {
                //Filter isSys
            } else if (entry.getValue() != null && entry.getValue().isSystem()) {
                //Filter sysadmin/secadmin/auditadmin
            } else if (entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().getUseable())
                    && false == Global.YES.equals(entry.getValue().getUseable())) {
                //Filter useable
            } else {
                //Other user
                User user = entry.getValue();
                StringBuffer name = new StringBuffer();
                name.append(user.getName());
                if (user.getName().length() == 2) {
                    name.append("&emsp;");
                }
                name.append("&emsp;&emsp;");
                String levelName = user.getLevel() != null && StringUtils.isNotBlank(user.getLevel().getName()) ? user.getLevel().getName() : "";
                name.append(levelName);
                for (int i = levelName.length(); i < 6; i++) {
                    name.append("&emsp;");
                }
                String officeName = "";
                String parentIds = user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getParentIds()) ? user.getOffice().getParentIds() : "";
                String[] officeParentIds = parentIds.split(",");
                boolean hasParentName = false;
                for (String pid : officeParentIds) {
                    Office office = officeService.get(pid);
                    if (office != null && StringUtils.isNotBlank(office.getName())) {

                        //if (Global.ROOT_OWNERCODE .equals(office.getCode())) {
                        //    continue;
                        //}
                        if (office.getParentId().equals("0")) continue;

                        if (hasParentName) {
                            officeName += "\\";
                        }
                        officeName += office.getName();
                        hasParentName = true;
                    }
                }
                if (StringUtils.isNotBlank(officeName)) {
                    name.append(officeName);
                    name.append("\\");
                }

                String userOfficeName = user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getName()) ? user.getOffice().getName() : "";
                name.append(userOfficeName);

                user.setName(name.toString());
                userList.add(user);
            }
        }
        return userList;
    }

    /**
     * Get task setting version by act.
     *
     * @param act
     * @return TaskSettingVersion.
     */
    public TaskSettingVersion getTaskSettingVersionByAct(Act act) {
        return taskSettingVersionService.getTaskSettingVersionByAct(act);
    }

    /**
     * Setup act.
     *
     * @param entity
     */
    public void setAct(T entity, String loginName) {
        if (StringUtils.isNotBlank(entity.getProcInsId())) {
            Task task = getCurrentTask(entity.getProcInsId(), loginName);
            ProcessInstance procIns = getProcIns(entity.getProcInsId());
            if (task != null && procIns != null) {
                entity.getAct().setTaskId(task.getId());
                entity.getAct().setTaskName(task.getName());
                entity.getAct().setTaskDefKey(task.getTaskDefinitionKey());
                entity.getAct().setProcInsId(entity.getProcInsId());
                entity.getAct().setProcDefId(procIns.getProcessDefinitionId());
            } else {
                List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(entity.getProcInsId()).taskAssignee(loginName)
                        .finished().includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc().list();
                if (taskList.size() > 0) {
                    HistoricTaskInstance historicTaskInstance = taskList.get(0);
                    entity.getAct().setTaskId(historicTaskInstance.getId());
                    entity.getAct().setTaskName(historicTaskInstance.getName());
                    entity.getAct().setTaskDefKey(historicTaskInstance.getTaskDefinitionKey());
                    entity.getAct().setProcInsId(entity.getProcInsId());
                    entity.getAct().setProcDefId(historicTaskInstance.getProcessDefinitionId());
                } else {
                    List<HistoricTaskInstance> taskListOther = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(entity.getProcInsId()).finished().includeProcessVariables()
                            .orderByHistoricTaskInstanceEndTime().desc().list();
                    HistoricTaskInstance historicTaskInstance = taskListOther.get(0);
                    entity.getAct().setTaskId(historicTaskInstance.getId());
                    entity.getAct().setTaskName(historicTaskInstance.getName());
                    entity.getAct().setTaskDefKey(historicTaskInstance.getTaskDefinitionKey());
                    entity.getAct().setProcInsId(entity.getProcInsId());
                    entity.getAct().setProcDefId(historicTaskInstance.getProcessDefinitionId());
                }
            }
        } else {
            if (entity.getAct() != null && StringUtils.isNotBlank(entity.getAct().getProcDefKey())) {
                List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(entity.getAct().getProcDefKey()).latestVersion().list();
                if (processDefinitionList != null && processDefinitionList.size() > 0) {
                    ProcessDefinition processDefinition = processDefinitionList.get(0);
                    entity.getAct().setProcDefId(processDefinition.getId());
                    entity.getAct().setTaskDefKey(processDefinition.getDescription().split(",")[1]);
                }
            }
        }
    }

    /**
     * Set assignee.
     *
     * @param entity
     */
    public void setAssignee(T entity, String loginName) {
        entity.getAct().setAssignee(loginName);
    }

    /**
     * Set condition for entity.
     *
     * @param entity
     * @param vars
     */
    public void setCondition(T entity, Map<String, Object> vars) {
        vars.put(Flag.flag.name(), entity.getAct().getFlag());
    }

    /**
     * Determine whether the current path is "query".
     *
     * @param path
     * @return True of false.
     */
    public boolean isQuery(String path) {
        return Path.QUERY.name().equalsIgnoreCase(path);
    }

    /**
     * Determine whether the current path is "unsent".
     *
     * @param path
     * @return True of false.
     */
    public boolean isUnsent(String path) {
        return Path.UNSENT.name().equalsIgnoreCase(path);
    }

    /**
     * Determine whether the current path is "unsent and sent".
     *
     * @param path
     * @return True of false.
     */
    public boolean isUnsentandsent(String path) {
        return Path.UNSENTANDSENT.name().equalsIgnoreCase(path);
    }

    /**
     * Determine whether the current path is "unsent and sent".
     *
     * @param path
     * @return True of false.
     */
    public boolean isTodoanddoing(String path) {
        return Path.TODOANDDOING.name().equalsIgnoreCase(path);
    }

    /**
     * Determine whether the current path is "todo_, doing and done".
     *
     * @param path
     * @return True of false.
     */
    public boolean isTodoAndDoingAndDone(String path) {
        return Path.TODOANDDOINGANDDONE.name().equalsIgnoreCase(path);
    }

    /**
     * Determine whether the current path is "done".
     *
     * @param path
     * @return True of false.
     */
    public boolean isDone(String path) {
        return Path.DONE.name().equalsIgnoreCase(path);
    }

    /**
     * Start process.
     *
     * @param procDefKey
     * @param businessTable
     * @param businessId
     * @return The process instance id started.
     */
    public String startProcess(String procDefKey, String businessTable, String businessId, String loginName) {
        return actTaskService.startProcess(procDefKey, businessTable, businessId, loginName);
    }

    /**
     * Start process.
     *
     * @param procDefKey
     * @param businessTable
     * @param businessId
     * @param title
     * @return The process instance id started.
     */
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, String loginName) {
        return actTaskService.startProcess(procDefKey, businessTable, businessId, title, loginName);
    }

    /**
     * Start process.
     *
     * @param procDefKey
     * @param businessTable
     * @param businessId
     * @param title
     * @param vars
     * @return The process instance id started.
     */
    public String startProcess(String procDefKey, String businessTable, String businessId, String title,
                               Map<String, Object> vars, String loginName) {
        return actTaskService.startProcess(procDefKey, businessTable, businessId, title, vars, loginName);
    }

    /**
     * Submit task.
     *
     * @param taskId
     * @param procInsId
     * @param comment
     * @param vars
     */
    public void complete(String taskId, String procInsId, String comment, Map<String, Object> vars) {
        actTaskService.complete(taskId, procInsId, comment, vars);
    }

    /**
     * Submit task.
     *
     * @param taskId
     * @param procInsId
     * @param comment
     * @param title
     * @param vars
     */
    public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars) {
        actTaskService.complete(taskId, procInsId, comment, title, vars);
    }

    /**
     * Submit task.
     *
     * @param taskId
     * @param procInsId
     * @param comment
     * @param title
     * @param vars
     * @param userId
     */
    public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars,
                         String userId) {
        actTaskService.complete(taskId, procInsId, comment, title, vars, userId);
    }

    /**
     * Query process instance by id.
     *
     * @param procInsId
     * @return ProcessInstance.
     */
    public ProcessInstance getProcIns(String procInsId) {
        return actTaskService.getProcIns(procInsId);
    }

    /**
     * Claim task.
     *
     * @param procInsId
     */
    public void claim(String procInsId, String loginName) {
        actTaskService.claim(getCurrentTask(procInsId, loginName).getId(), loginName);
    }

    /**
     * Unclaim task.
     *
     * @param procInsId
     */
    public void unclaim(String procInsId, String loginName) {
        actTaskService.claim(getCurrentTask(procInsId, loginName).getId(), null);
    }

    /**
     * Set auto fill in user name in the form.
     *
     * @param entity
     * @param formMap
     */
    private void setUserName(T entity, Map<String, String> formMap, String loginName) {
        String viewFlag = entity.getViewFlag();
        if (StringUtils.isNotBlank(viewFlag) && "viewForm".equals(viewFlag)) {
            //Query page need not set auto fill in user name.
        } else {
            try {
                Map<String, Field> fieldMap = Maps.newHashMap();
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field : fields) {
                    fieldMap.put(field.getName(), field);
                }
                for (Map.Entry<String, String> entry : formMap.entrySet()) {
                    Field field = fieldMap.get(entry.getKey());
                    if (field == null) {

                    } else {
                        field.setAccessible(true);
                        if (field.getAnnotation(UserName.class) != null) {
                            if (entry.getValue().startsWith("11") && field.get(entity) == null) {
                                //Set user name when visible and editable
                                field.set(entity, UserUtil.getByLoginName(loginName).getName());
                            } else if (entry.getValue().startsWith("10") && StringUtils.isBlank(entity.getProcInsId())) {
                                //Visible, non editable, set to empty
                                field.set(entity, "");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Query the task list defined by the process according to the process classification.
     * This method is used in business centered workflow forms.
     * Only one process definition under a category is required.
     * For list page node status drop-down box.
     *
     * @param category
     * @return Process definition task list map.
     */
    public List<Map<String, String>> getProcessDefinitionTaskList(String category, String loginName, String lang) {
        String processDefinitionId = this.getProcessDefinitionList(category, loginName).get(0).getId();
        TaskSettingVersion taskSettingVersion = new TaskSettingVersion();
        taskSettingVersion.getSqlMap().put("dsf", " AND a.proc_def_id = '" + processDefinitionId + "'");
        List<TaskSettingVersion> taskSettingVersionList = taskSettingVersionService.findList(taskSettingVersion);
        Map<String, String> taskSettingVersionMap = Maps.newHashMap();
        for (TaskSettingVersion t : taskSettingVersionList) {
            taskSettingVersionMap.put(t.getUserTaskId(), t.getUserTaskName());
        }
        List<Map<String, String>> processDefinitionTaskList = Lists.newArrayList();
        Map<String, String> beginMap = Maps.newHashMap();
        beginMap.put("taskId", UNSTART);
        beginMap.put("taskName", lang.equalsIgnoreCase(Global.LANG_EN) ? UNSTARTNAME_EN : UNSTARTNAME);
        processDefinitionTaskList.add(beginMap);
        List<ActivityImpl> activities = ((ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(processDefinitionId)).getActivities();
        for (ActivityImpl activityImpl : activities) {
            if (!StringUtils.isBlank(taskSettingVersionMap.get(activityImpl.getId()))) {
                Map<String, String> m = Maps.newHashMap();
                m.put("taskId", activityImpl.getId());
                m.put("taskName", taskSettingVersionMap.get(activityImpl.getId()));
                processDefinitionTaskList.add(m);
            }
        }
        Map<String, String> endMap = Maps.newHashMap();
        endMap.put("taskId", END);
        endMap.put("taskName", lang.equalsIgnoreCase(Global.LANG_EN) ? ENDNAME_EN : ENDNAME);
        processDefinitionTaskList.add(endMap);
        Map<String, String> terminateMap = Maps.newHashMap();
        terminateMap.put("taskId", TERMINATE);
        terminateMap.put("taskName", lang.equalsIgnoreCase(Global.LANG_EN) ? TERMINATENAME_EN : TERMINATENAME);
        processDefinitionTaskList.add(terminateMap);
        return processDefinitionTaskList;
    }

    /**
     * Replace bookmark, read word file, replace bookmark in word and write file again.
     *
     * @param entity
     * @param fileIn
     * @param fileOut
     * @param response
     */
    public void replaceBookMark(T entity, String fileIn, String fileOut, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        try {
            Map<String, String> bookmarkMap = this.getBookmarkMap(entity);

            fileInputStream = new FileInputStream(new File(fileIn));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileInputStream);
            NodeList elements = document.getElementsByTagName("w:bookmarkStart");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                String wname = element.getAttribute("w:name");
                String bookmark = bookmarkMap.get(wname);
                if (StringUtils.isNotBlank(bookmark)) {
                    Element nextElement = (Element) element.getNextSibling();
                    NodeList wtList = nextElement.getElementsByTagName("w:t");
                    if (wtList.getLength() == 0) {
                        String[] bookmarkArray = bookmark.split("\n");
                        boolean insertWr = false;
                        for (int j = 0; j < bookmarkArray.length; j++) {
                            if (StringUtils.isNotBlank(bookmarkArray[j])) {
                                if (insertWr) {
                                    Node wbr = document.createElement("w:br");
                                    Node wr2 = document.createElement("w:r");
                                    wr2.appendChild(wbr);
                                    element.appendChild(wr2);
                                } else {
                                    insertWr = true;
                                }
                                Node text = document.createTextNode(bookmarkArray[j]);
                                Node wt = document.createElement("w:t");
                                wt.appendChild(text);
                                Node wr = document.createElement("w:r");
                                wr.appendChild(wt);
                                element.appendChild(wr);
                            }
                        }
                    } else {
                        Element wtNode = (Element) wtList.item(0);
                        wtNode.setTextContent(bookmark);
                    }
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileOut.getBytes(), "iso-8859-1"));
            response.getOutputStream().write(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    /**
     * Query bookmark map, used for document red.
     *
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String, String> getBookmarkMap(T entity) throws Exception {
        Map<String, String> bookmarkMap = Maps.newHashMap();
        //Query the properties of the object to be reddened currently
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            //Set to editable
            field.setAccessible(true);
            //Query annotation
            Annotation[] annotations = field.getAnnotations();
            if (annotations.length > 0) {
                //Dictionary annotation, code to name
                if (field.getAnnotation(DictAnnotation.class) != null) {
                    DictAnnotation annotation = field.getAnnotation(DictAnnotation.class);
                    Object object = field.get(entity);
                    if (object != null) {
                        String value = String.valueOf(field.get(entity));
                        if (StringUtils.isNotBlank(value)) {
                            String type = ((DictAnnotation) annotation).type();
                            String dictLabel = dictDataService.getDictLabels(value, type, "", "");
                            bookmarkMap.put(field.getName(), Encodes.unescapeHtml(dictLabel));
                        }
                    }
                }
                //Date annotation, date formatting
                if (field.getAnnotation(DateAnnotation.class) != null) {
                    DateAnnotation annotation = field.getAnnotation(DateAnnotation.class);
                    Date value = (Date) field.get(entity);
                    if (value != null) {
                        String format = ((DateAnnotation) annotation).format();
                        String dateStr = new SimpleDateFormat(format).format(value);
                        bookmarkMap.put(field.getName(), dateStr);
                    }
                }
                //File annotation, take out the file name in the JSON string
                if (field.getAnnotation(FileAnnotation.class) != null) {
                    Object object = field.get(entity);
                    if (object != null) {
                        String value = Encodes.unescapeHtml(String.valueOf(field.get(entity)));
                        if (StringUtils.isNotBlank(value)) {
                            //Upload and analysis of new attachments
                            SysFile sysFile = new SysFile();
                            sysFile.setGroupId(value);
                            List<SysFile> sysFileList = sysFileService.findList(sysFile);
                            String fileList = "";
                            for (SysFile sf : sysFileList) {
                                String filename = sf.getName();
                                fileList += filename;
                                fileList += "\n";
                            }
                            if (StringUtils.isNotBlank(fileList)) {
                                bookmarkMap.put(field.getName(), fileList);
                            }
                        }
                    }
                }
                //Parsing JSON annotations
                if (field.getAnnotation(JsonFormatAnnotation.class) != null) {
                    Object object = field.get(entity);
                    if (object != null) {
                        String value = Encodes.unescapeHtml(String.valueOf(field.get(entity)));
                        if (StringUtils.isNotBlank(value)) {
                            JsonFormatAnnotation annotation = field.getAnnotation(JsonFormatAnnotation.class);
                            String scope = annotation.scope();
                            String[] keys = annotation.keys();
                            String newValue = "";
                            if ("edocSign".equals(scope)) {//{userId:{text:xxx,name:xxx,time:xxx}}
                                @SuppressWarnings("unchecked")
                                Map<String, Map<String, String>> map = new Gson().fromJson(value, Map.class);
                                for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                                    for (String key : keys) {
                                        String valueI = entry.getValue().get(key);
                                        newValue += valueI;
                                        newValue += " ";
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(newValue)) {
                                bookmarkMap.put(field.getName(), newValue);
                            }
                        }
                    }
                }
                //Multi line annotation, which specifies how many words are displayed in one line and how many lines are displayed
                if (field.getAnnotation(MultiLineAnnotation.class) != null) {
                    MultiLineAnnotation annotation = field.getAnnotation(MultiLineAnnotation.class);
                    Object object = field.get(entity);
                    if (object != null) {
                        String value = bookmarkMap.get(field.getName());
                        if (StringUtils.isBlank(value)) {
                            value = Encodes.unescapeHtml(String.valueOf(field.get(entity)));
                        }
                        if (StringUtils.isNotBlank(value)) {
                            String text = "";
                            Integer maxlength = Integer.parseInt(((MultiLineAnnotation) annotation).maxlength());
                            Integer maxline = Integer.parseInt(((MultiLineAnnotation) annotation).maxline());
                            Integer lineCount = 0;
                            String[] valueArray = value.split("\n");
                            for (String valueI : valueArray) {
                                if (StringUtils.isBlank(valueI)) {
                                    continue;
                                }
                                if (valueI.length() > maxlength) {
                                    String textI = "";
                                    boolean add = valueI.length() % maxlength == 0 ? false : true;
                                    int lineCountI = valueI.length() / maxlength;
                                    lineCountI = add ? ++lineCountI : lineCountI;
                                    for (int i = 0; i < lineCountI; i++) {
                                        int beginI = i * maxlength;
                                        int endI = (i + 1) * maxlength;
                                        String subValueI = "";
                                        if (endI > valueI.length()) {
                                            subValueI = valueI.substring(beginI, valueI.length());
                                            int spaceCount = endI - valueI.length();
                                            for (int j = 0; j < spaceCount; j++) {
                                                subValueI += "　";
                                            }
                                        } else {
                                            subValueI = valueI.substring(beginI, endI);
                                        }
                                        textI += subValueI;
                                        textI += "\n";
                                        lineCount++;
                                    }
                                    if (lineCount <= maxline) {
                                        text += textI;
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (lineCount < maxline) {
                                        int spaceCount = maxlength - valueI.length();
                                        for (int j = 0; j < spaceCount; j++) {
                                            valueI += "　";
                                        }
                                        text += valueI;
                                        text += "\n";
                                        lineCount++;
                                    } else {
                                        break;
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(text)) {
                                bookmarkMap.put(field.getName(), text);
                            }
                        }
                    }
                }
                if (field.getAnnotation(UserName.class) != null) {
                    Object object = field.get(entity);
                    if (object != null) {
                        String value = String.valueOf(object);
                        bookmarkMap.put(field.getName(), value);
                    }
                }
            } else {
                //No annotation, direct assignment
                Object object = field.get(entity);
                if (object != null) {
                    String value = String.valueOf(object);
                    bookmarkMap.put(field.getName(), value);
                }
            }
        }

        return bookmarkMap;
    }

    /**
     * Scenario: using in official documents, signing, countersigning and other multiple people operate the same field.
     * Take the value from getname, spell it as a JSON string with the format "content name time" and attach it to the back of setname.
     * getName The non persistent property is the content written by the current user.
     * setName It is a persistent attribute, which stores the multi person JSON list of multi person signing and countersigning.
     *
     * @param entity
     * @param setName Property name corresponding to stored value
     * @param getName Property name corresponding to query value
     */
    public void setFieldValue(T entity, String getName, String setName, String loginName) {
        try {
            User currentUser = UserUtil.getByLoginName(loginName);
            Field getField = entity.getClass().getDeclaredField(getName);
            getField.setAccessible(true); //设置可编辑
            String getValue = (String) getField.get(entity);
            Field setField = entity.getClass().getDeclaredField(setName);
            setField.setAccessible(true);
            String setValue = (String) setField.get(entity);
            String userId = currentUser.getId();
            String userName = currentUser.getName();
            if (StringUtils.isNotBlank(getValue)) {
                //Current user wrote content
                if (StringUtils.isNotBlank(setValue)) {
                    //First, set the value of the name. According to the content filled in by the current login user's ID,
                    // if there is a value in getname, it can be spliced into JSON and overwritten according to the userid.
                    //If the value in getname is not spelled as JSON, it will be added to the list of setname JSON.
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, String>> signMap = new Gson().fromJson(setValue, Map.class);
                    Map<String, String> userSignMap = signMap.get(userId);
                    if (userSignMap != null && userSignMap.size() > 0) {
                        String text = userSignMap.get("text");
                        if (StringUtils.isNotBlank(text) && false == text.equals(getValue)) {
                            userSignMap.put("text", getValue);
                            userSignMap.put("name", userName);
                            userSignMap.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " \n");
                            signMap.put(userId, userSignMap);
                            setField.set(entity, new Gson().toJson(signMap));
                        }
                    } else {
                        userSignMap = Maps.newHashMap();
                        userSignMap.put("text", getValue);
                        userSignMap.put("name", userName);
                        userSignMap.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " \n");
                        signMap.put(userId, userSignMap);
                        setField.set(entity, new Gson().toJson(signMap));
                    }
                } else {
                    //There is no value in setname. Add the value in getname to make JSON
                    Map<String, Map<String, String>> signMap = Maps.newHashMap();
                    Map<String, String> userSignMap = Maps.newHashMap();
                    userSignMap.put("text", getValue);
                    userSignMap.put("name", userName);
                    userSignMap.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " \n");
                    signMap.put(userId, userSignMap);
                    setField.set(entity, new Gson().toJson(signMap));
                }
            } else {
                //The current user has not written or deleted the content
                boolean setNull = false;
                if (StringUtils.isBlank(entity.getProcInsId())) {
                    //Process not started, can be set to null (getValue can be deleted)
                    setNull = true;
                } else {
                    //Process started
                    String ruleArgs = "";
                    Act act = new Act();
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
                    Task currentTask = this.getCurrentTask(processInstance.getId(), loginName);
                    act.setProcDefId(processInstance.getProcessDefinitionId());
                    act.setTaskDefKey(currentTask.getTaskDefinitionKey());
                    TaskSettingVersion taskSettingVersion = taskSettingVersionService.getTaskSettingVersionByAct(act);
                    TaskPermission taskPermission = taskPermissionService.get(taskSettingVersion.getPermission());
                    if (StringUtils.isNotBlank(taskPermission.getRuleArgs())) {
                        ruleArgs = StringEscapeUtils.unescapeHtml(taskPermission.getRuleArgs());
                    }
                    //Get the getname property value from the form field
                    String ruleVlue = getRuleArgs(ruleArgs, "form", getName);
                    //11 is show and edit
                    if (StringUtils.isNotBlank(ruleVlue) && ruleVlue.startsWith("11")) {
                        //Allow setting to empty
                        setNull = true;
                    }
                }
                //The current user has not written or deleted the content. It is allowed to set it to null,
                // and setValue is not null. Set setValue to null
                if (setNull && StringUtils.isNotBlank(setValue)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, String>> signMap = new Gson().fromJson(setValue, Map.class);
                    Map<String, String> userSignMap = signMap.get(userId);
                    if (userSignMap != null && userSignMap.size() > 0) {
                        signMap.remove(userId);
                        if (signMap.size() > 0) {
                            setField.set(entity, new Gson().toJson(signMap));
                        } else {
                            setField.set(entity, "");
                        }
                    } else {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Query rule variable.
     *
     * @param ruleArgs Rule variable string
     * @param scope
     * @param key
     * @return Rule variable string.
     */
    private String getRuleArgs(String ruleArgs, String scope, String key) {
        if (StringUtils.isNotBlank(ruleArgs)) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> ruleArgsList = new Gson().fromJson(ruleArgs, List.class);
            if (ruleArgsList != null && ruleArgsList.size() > 0) {
                for (Map<String, String> map : ruleArgsList) {
                    String k = map.get(RuleArgs.key.name());
                    String n = k.split("_")[1];
                    if (RuleArgs.flag.name().equalsIgnoreCase(scope)) {

                    } else if (RuleArgs.hand.name().equalsIgnoreCase(scope)) {

                    } else if (RuleArgs.form.name().equalsIgnoreCase(scope)) {
                        if (key.equals(n)) {
                            String value = map.get(RuleArgs.value.name());
                            return value;
                        }
                    } else if (RuleArgs.content.name().equalsIgnoreCase(scope)) {

                    }
                }
            }
        }
        return null;
    }

    /**
     * Scenario: using in official documents, signing, countersigning and other multiple people operate the same field.
     * Take value from getname and put it into setname, spell a string in the format of "content name time" and put it into getname.
     * setName Non persistent property, taking value from getname.
     * getName It is a persistent attribute, which stores the multi person JSON list of multi person signing and countersigning.
     *
     * @param entity
     * @param setName Property name corresponding to stored value
     * @param getName Property name corresponding to query value
     */
    public void getFieldValue(T entity, String setName, String getName, String loginName) {
        try {
            User currentUser = UserUtil.getByLoginName(loginName);
            Field getField = entity.getClass().getDeclaredField(getName);
            //Settings can be edited
            getField.setAccessible(true);
            String getValue = (String) getField.get(entity);
            if (StringUtils.isNotBlank(getValue)) {
                String userId = currentUser.getId();
                @SuppressWarnings("unchecked")
                //Query the JSON value of countersignature and signing
                        Map<String, Map<String, String>> signMap = new Gson().fromJson(getValue, Map.class);
                //Query the information written by yourself according to the current login user ID
                Map<String, String> userSignMap = signMap.get(userId);
                //Put getname value in setname
                if (userSignMap != null && userSignMap.size() > 0) {
                    String text = userSignMap.get("text");
                    if (StringUtils.isNotBlank(text)) {
                        Field setField = entity.getClass().getDeclaredField(setName);
                        setField.setAccessible(true);
                        setField.set(entity, text);
                    }
                }

                //Reconstruct the value of getname, format: content name time
                String realValue = "";
                Set<Map.Entry<String, Map<String, String>>> entrySet = signMap.entrySet();
                for (Map.Entry<String, Map<String, String>> entry : entrySet) {
                    Map<String, String> map = entry.getValue();
                    if (map != null && map.size() > 0) {
                        String text = map.get("text");
                        String name = map.get("name");
                        String time = map.get("time");
                        realValue += (text.trim() + " " + name + " " + time);
                    }
                }
                if (StringUtils.isNotBlank(realValue)) {
                    getField.set(entity, realValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Query process page.
     *
     * @param processDefinitionCategory
     * @param processDefinitionKey
     * @return Process page url.
     */
    @SuppressWarnings("unchecked")
    public String getProcessPage(String processDefinitionCategory, String processDefinitionKey) {
        String pageUrl = "";
        List<Model> modelList = repositoryService.createModelQuery().modelCategory(processDefinitionCategory).list();
        for (Model model : modelList) {
            String metaInfo = model.getMetaInfo();
            Map<String, String> metaInfoMap = new Gson().fromJson(metaInfo, Map.class);
            String procDefKey = metaInfoMap.get("procDefKey");
            if (StringUtils.isNotBlank(procDefKey) && procDefKey.equals(processDefinitionKey)) {
                String scope = metaInfoMap.get("scope");
                if (StringUtils.isNotBlank(scope)) {
                    pageUrl = scope;
                    break;
                }
            }
        }
        return pageUrl;
    }

    /**
     * Set rule variable
     *
     * @param entity
     * @param loginName
     */
    @SuppressWarnings("unchecked")
    public void setRuleArgs(T entity, String loginName) {

        //Flow state
        String processInstanceStatus = "";
        if (StringUtils.isBlank(entity.getProcInsId())) {
            processInstanceStatus = "unstart";
        } else {
            processInstanceStatus = "end";
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).active().list();
            for (Task task : taskList) {
                if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

                } else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

                } else {
                    processInstanceStatus = "running";
                    break;
                }
            }
        }
        Map<String, String> extMap = entity.getRuleArgs().get(RuleArgs.extend.name());
        if (extMap == null) {
            extMap = Maps.newHashMap();
        }
        extMap.put("processInstanceStatus", processInstanceStatus);
        entity.getRuleArgs().put(RuleArgs.extend.name(), extMap);

        //Rule variable string
        String ruleArgs = "";

        //Versioning node permissions
        TaskSettingVersion taskSettingVersion = getTaskSettingVersionByAct(entity.getAct());
        if (taskSettingVersion != null) {
            TaskPermission taskPermission = taskPermissionService.get(taskSettingVersion.getPermission());

            //Extended function
            if (StringUtils.isNotBlank(taskPermission.getExtendOperation())) {
                Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                if (extendMap == null) {
                    extendMap = Maps.newHashMap();
                }
                String[] extendOperationArray = taskPermission.getExtendOperation().split(",");
                for (String extendOperation : extendOperationArray) {
                    extendMap.put(extendOperation, extendOperation);
                }
                entity.getRuleArgs().put(RuleArgs.extend.name(), extendMap);
            }

            //Rule permissions
            if (StringUtils.isNotBlank(taskPermission.getRuleArgs())) {
                ruleArgs = StringEscapeUtils.unescapeHtml(taskPermission.getRuleArgs());
            }
        }

        //Form rule | body rule
        if (StringUtils.isNotBlank(ruleArgs)) {
            List<Map<String, String>> ruleArgsList = new Gson().fromJson(ruleArgs, List.class);
            if (ruleArgsList != null && ruleArgsList.size() > 0) {
                Map<String, String> formMap = Maps.newHashMap();        //Form rule
                Map<String, String> formExtendMap = Maps.newHashMap();    //Form rule
                Map<String, String> contentMap = Maps.newHashMap();        //Body rule

                for (Map<String, String> ruleArgsMap : ruleArgsList) {
                    String key = ruleArgsMap.get(RuleArgs.key.name());
                    String value = ruleArgsMap.get(RuleArgs.value.name());
                    String scope = key.split("_")[0];

                    //Form rule
                    if (RuleArgs.form.name().equalsIgnoreCase(scope)) {
                        String keyI = key.split("_")[1];
                        String valueI = value.split("_")[0];
                        formMap.put(keyI, valueI);
                        if (value.split("_").length == 2) {
                            formExtendMap.put(key.split("_")[1], value.split("_")[1]);
                        }
                    }
                    //Body rule
                    else if (RuleArgs.content.name().equalsIgnoreCase(scope)) {
                        String keyI = key.split("_")[1];
                        String valueI = value;
                        contentMap.put(keyI, valueI);

                    }
                }
                entity.getRuleArgs().put(RuleArgs.form.name(), formMap);
                entity.getRuleArgs().put(RuleArgs.formExtend.name(), formExtendMap);
                entity.getRuleArgs().put(RuleArgs.content.name(), contentMap);

                //Load user name
                if (formMap != null && formMap.size() > 0) {
                    setUserName(entity, formMap, loginName);
                }
            }
        }

        Task currentTask = null;
        String processDefinitionId = "";
        //Process not started
        if (StringUtils.isBlank(entity.getProcInsId())) {
            //When initiating a process, using proceessDefinationId
            if (entity.getAct() != null && StringUtils.isNotBlank(entity.getAct().getProcDefId())) {
                processDefinitionId = entity.getAct().getProcDefId();
            }
            //When initiating a process, using processDefinationKey
            else {
                String processDefinationKey = entity.getAct().getProcDefKey();
                processDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinationKey).latestVersion().list().get(0).getId();
            }
        }
        //Process running
        else {
            String processInstanceId = entity.getProcInsId();
            currentTask = this.getCurrentTask(processInstanceId, loginName);
            if (currentTask == null) {
                return;
            } else {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult().getId();
            }
        }

        //ProcessDefinitionEntity
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processDefinitionId);
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Next node
        ActivityImpl nextActivity = null;

        //Process not started
        if (currentTask == null) {
            ActivityImpl startActivity = processDefinitionEntity.findActivity("start");
            ActivityImpl firstActivity = (ActivityImpl) startActivity.getOutgoingTransitions().get(0).getDestination();
            nextActivity = (ActivityImpl) firstActivity.getOutgoingTransitions().get(0).getDestination();
        }
        //Process running
        else {
            ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());
            List<PvmTransition> currentOutgoingTransitions = currentActivity.getOutgoingTransitions();
            if (currentOutgoingTransitions != null && currentOutgoingTransitions.size() > 0) {
                ActivityImpl destinationActivity0 = (ActivityImpl) currentOutgoingTransitions.get(0).getDestination();
                if (destinationActivity0.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                    nextActivity = destinationActivity0;
                } else if (destinationActivity0.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                    //Hand painted containment gateway (start)
                    if (destinationActivity0.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                        nextActivity = destinationActivity0;
                    }
                    //Hand drawn containment gateway (closed)
                    else if (destinationActivity0.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                        nextActivity = destinationActivity0;
                    }
                    //Generated containment gateway (orphaned)
                    else {
                        List<PvmTransition> destination0OutgoingTransitions = destinationActivity0.getOutgoingTransitions();
                        for (PvmTransition destination0PvmTransition : destination0OutgoingTransitions) {
                            ActivityImpl destinationActivity1 = (ActivityImpl) destination0PvmTransition.getDestination();
                            if (destinationActivity1.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                                nextActivity = destinationActivity1;
                                break;
                            } else if (destinationActivity1.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                                //Hand painted containment gateway (start)
                                if (destinationActivity1.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                                    nextActivity = destinationActivity1;
                                }
                                //Hand drawn containment gateway (closed)
                                else if (destinationActivity1.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                                    nextActivity = destinationActivity1;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Branch rule
        boolean b0 = nextActivity != null;
        if (b0) {
            boolean b1 = nextActivity.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior;
            boolean b2 = nextActivity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior;
            boolean b3 = nextActivity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE);
            boolean b4 = nextActivity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE);
            if (b1 || (b2 && (b3 || b4))) {
                //Judge manual branch or automatic branch
                boolean isAutomatic = true;

                List<PvmTransition> nextOutgoingTransitions = nextActivity.getOutgoingTransitions();
                for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                    String documentation = (String) nextPvmTransition.getProperty("documentation");
                    Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                    String mode = customCondditionMap.get("mode");
                    if ("Manual".equals(mode)) {
                        //If the flow mode of one branch is manual, automatic branch is not used
                        isAutomatic = false;
                    } else if ("Automatic".equals(mode)) {
                        //There is a branch whose flow mode is automatic, but if the variable or constant is not configured,
                        // the automatic branch is not used
                        String variable = customCondditionMap.get("variable");
                        String constant = customCondditionMap.get("constant");
                        if (StringUtils.isBlank(variable) || StringUtils.isBlank(constant)) {
                            isAutomatic = false;
                        }
                    }
                }

                //Line sort
                Collections.sort(nextOutgoingTransitions, new Comparator<PvmTransition>() {
                    @Override
                    public int compare(PvmTransition o1, PvmTransition o2) {
                        Map<String, String> map1 = new Gson().fromJson((String) o1.getProperty("documentation"), Map.class);
                        String priority1 = map1.get("priority");
                        Map<String, String> map2 = new Gson().fromJson((String) o2.getProperty("documentation"), Map.class);
                        String priority2 = map2.get("priority");
                        if (StringUtils.isNoneBlank(priority1) && StringUtils.isNoneBlank(priority2)) {
                            return priority1.compareTo(priority2);
                        } else {
                            return 0;
                        }
                    }
                });

                //Manual branch
                if (false == isAutomatic) {
                    Map<String, String> manualMap = Maps.newLinkedHashMap();
                    for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                        ActivityImpl thirdActivityI = (ActivityImpl) nextPvmTransition.getDestination();
                        manualMap.put(thirdActivityI.getId(), (String) thirdActivityI.getProperty("name"));
                    }
                    entity.getRuleArgs().put(RuleArgs.hand.name(), manualMap);

                    Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                    if (extendMap == null) {
                        extendMap = Maps.newHashMap();
                    }
                    extendMap.put("mode", "manual");
                    entity.getRuleArgs().put(RuleArgs.extend.name(), extendMap);
                }
                //Automatic branch
                else if (isAutomatic) {
                    Map<String, String> automaticMap = Maps.newLinkedHashMap();
                    for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                        String conditionText = (String) nextPvmTransition.getProperty("conditionText");
                        int conditionTextBegin = conditionText.indexOf("{") + 1;
                        int conditionTextEnd = conditionText.lastIndexOf("}");
                        conditionText = conditionText.substring(conditionTextBegin, conditionTextEnd);
                        String documentation = (String) nextPvmTransition.getProperty("documentation");
                        Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                        String variable = customCondditionMap.get("variable");

                        try {
                            Field field = entity.getClass().getDeclaredField(variable);
                            if (field != null) {
                                field.setAccessible(true);
                                Object object = field.get(entity);
                                if (object != null) {
                                    String fieldValue = String.valueOf(field.get(entity));
                                    automaticMap.put(variable, fieldValue);
                                } else {
                                    automaticMap.put(variable, "");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    entity.getRuleArgs().put(RuleArgs.automatic.name(), automaticMap);

                    Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                    if (extendMap == null) {
                        extendMap = Maps.newHashMap();
                    }
                    extendMap.put("mode", "automatic");
                    entity.getRuleArgs().put(RuleArgs.extend.name(), extendMap);
                }

                //Whether it's a hand drawn containment gateway (at the beginning)
                if (b3) {
                    Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                    if (extendMap == null) {
                        extendMap = Maps.newHashMap();
                    }
                    extendMap.put("branchType", "multi");
                    entity.getRuleArgs().put(RuleArgs.extend.name(), extendMap);
                } else {
                    Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                    if (extendMap == null) {
                        extendMap = Maps.newHashMap();
                    }
                    extendMap.put("branchType", "single");
                    entity.getRuleArgs().put(RuleArgs.extend.name(), extendMap);
                }
            }
        }
    }

    /**
     * Query the current task list.
     * Priority: user to do task > user notification task > user agent task (no notification task will be delegated).
     * Sorting: task creation time ascending sort.
     *
     * @param processInstanceIdList
     * @param loginName
     * @return Current task list.
     */
    public List<Task> getCurrentTaskList(List<String> processInstanceIdList, String loginName) {
        List<Task> currentTaskList = Lists.newArrayList();
        List<Task> taskList = taskService.createTaskQuery().processInstanceIdIn(processInstanceIdList)
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();
        if (taskList != null && taskList.size() > 0) {
            currentTaskList.addAll(taskList);
        }
        return currentTaskList;
    }

    /**
     * Query the current task list.
     * Priority: user to do task > user notification task > user agent task (no notification task will be delegated).
     * Sorting: task creation time ascending sort.
     *
     * @param processInstanceId
     * @param loginName
     * @return Current task list.
     */
    public Task getCurrentTask(String processInstanceId, String loginName) {
        User currentUser = UserUtil.getByLoginName(loginName);
        Task currentTask = null;

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance != null && StringUtils.isNotBlank(processInstance.getProcessDefinitionId())) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();

            //Current user to do tasks
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                    .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();
            if (taskList != null && taskList.size() > 0) {
                for (Task task : taskList) {
                    currentTask = task;
                    if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                            && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                        break;
                    }
                }
            }
            //Current user agent task
            else if (taskList == null || taskList.size() == 0) {
                Date now = new Date();
                String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
                List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(currentUser.getId());
                for (AssigneeSetting assigneeSetting : assigneeList) {
                    String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                    if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                        taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                                .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                        for (Task task : taskList) {
                            if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                    && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                currentTask = task;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return currentTask;
    }

    /**
     * Query active task.
     * Priority: general task > notification task.
     * Sorting: task creation time ascending sort.
     *
     * @param processInstanceId
     * @return Active task.
     */
    public Task getActiveTask(String processInstanceId) {
        return getActiveTask(processInstanceId, "");
    }

    /**
     * Query active tasks.
     * Priority: general task > notification task.
     * Sorting: task creation time ascending sort.
     *
     * @param processInstanceId
     * @param taskDefinitionKey
     * @return Active task.
     */
    private Task getActiveTask(String processInstanceId, String taskDefinitionKey) {
        Task currentTask = null;

        //Current active task
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (StringUtils.isNotBlank(taskDefinitionKey)) {
            taskQuery.taskDefinitionKey(taskDefinitionKey);
        }
        List<Task> taskList = taskQuery.processInstanceId(processInstanceId).active().orderByTaskCreateTime().asc().list();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                currentTask = task;

                //Notify node
                if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

                }
                //Distribution node
                else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

                }
                //Other nodes
                else {
                    break;
                }
            }
        }

        return currentTask;
    }

    /**
     * Process not started, query the user list of the second node
     *
     * @param entity
     * @param loginName
     * @return User list map.
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Object> getStartingUserList(T entity, String loginName) {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();

        //New record or process not started
        if (entity.getIsNewRecord() || StringUtils.isBlank(entity.getProcInsId())) {
            //ProcessDefinitionEntity
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(entity.getAct().getProcDefKey()).latestVersion().singleResult();
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processDefinition.getId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            ActivityBehavior activityBehavior = null;
            String taskDefinitionKey = "";

            ActivityImpl startEvent = processDefinitionEntity.findActivity(START_EVENT_ID);
            List<PvmTransition> startEventOutgoingTransitions = startEvent.getOutgoingTransitions();
            for (PvmTransition startEventPvmTransition : startEventOutgoingTransitions) {
                ActivityImpl destinationActivity0 = (ActivityImpl) startEventPvmTransition.getDestination();
                List<PvmTransition> destination0OutgoingTransitions = destinationActivity0.getOutgoingTransitions();
                for (PvmTransition destination0PvmTransition : destination0OutgoingTransitions) {
                    ActivityImpl destinationActivity1 = (ActivityImpl) destination0PvmTransition.getDestination();

                    //Exclusive Gateway
                    if (destinationActivity1.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                        //boolean isManual = true;
                        boolean isAutomatic = true;

                        //Judge manual branch, automatic branch
                        List<PvmTransition> nextOutgoingTransitions = destinationActivity1.getOutgoingTransitions();
                        for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                            String documentation = (String) nextPvmTransition.getProperty("documentation");
                            Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                            String mode = customCondditionMap.get("mode");
                            if ("Manual".equals(mode)) {
                                //If the flow mode of one branch is manual, automatic branch is not used
                                isAutomatic = false;
                            } else if ("Automatic".equals(mode)) {
                                //There is a branch whose flow mode is automatic, but if the variable or constant is not configured,
                                // the automatic branch is not used
                                String variable = customCondditionMap.get("variable");
                                String constant = customCondditionMap.get("constant");
                                if (StringUtils.isBlank(variable) || StringUtils.isBlank(constant)) {
                                    isAutomatic = false;
                                }
                            }
                        }

                        //Manual branch
                        if (false == isAutomatic) {
                            if (StringUtils.isBlank(entity.getAct().getFlag())) {
                                map.put("isNeedFlag", true);
                                return map;
                            } else {
                                for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                                    ActivityImpl gatewayDestination = (ActivityImpl) nextPvmTransition.getDestination();
                                    if (entity.getAct().getFlag().equals(gatewayDestination.getId())) {
                                        activityBehavior = gatewayDestination.getActivityBehavior();
                                        taskDefinitionKey = gatewayDestination.getId();
                                        break;
                                    }
                                }
                            }
                        }
                        //Automatic branch
                        else if (isAutomatic) {
                            for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                                String conditionText = (String) nextPvmTransition.getProperty("conditionText");
                                int conditionTextBegin = conditionText.indexOf("{") + 1;
                                int conditionTextEnd = conditionText.lastIndexOf("}");
                                conditionText = conditionText.substring(conditionTextBegin, conditionTextEnd);
                                String documentation = (String) nextPvmTransition.getProperty("documentation");
                                Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                                String variable = customCondditionMap.get("variable");
                                String operator = customCondditionMap.get("operator");
//								String constant = customCondditionMap.get("constant");

                                try {
                                    Field field = entity.getClass().getDeclaredField(variable);
                                    if (field != null) {
                                        field.setAccessible(true);
                                        Object object = field.get(entity);
                                        if (object != null) {
                                            String fieldValue = String.valueOf(field.get(entity));
                                            if (StringUtils.isNotBlank(fieldValue)) {
                                                if ("==".equals(operator)) {

                                                } else {
                                                    conditionText = conditionText.replaceAll("'", "");
                                                }

                                                ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
                                                engine.put(variable, fieldValue);
                                                if ((boolean) engine.eval(conditionText)) {
                                                    ActivityImpl gatewayDestination = (ActivityImpl) nextPvmTransition.getDestination();
                                                    activityBehavior = gatewayDestination.getActivityBehavior();
                                                    taskDefinitionKey = gatewayDestination.getId();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //Containment gateway
                    else if (destinationActivity1.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                        boolean isManual = true;
                        boolean isAutomatic = true;

                        //Judge manual branch or automatic branch
                        List<PvmTransition> nextOutgoingTransitions = destinationActivity1.getOutgoingTransitions();
                        for (PvmTransition nextPvmTransition : nextOutgoingTransitions) {
                            String documentation = (String) nextPvmTransition.getProperty("documentation");
                            Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                            String mode = customCondditionMap.get("mode");
                            if ("Manual".equals(mode)) {
                                //If the flow mode of one branch is manual, automatic branch is not used
                                isAutomatic = false;
                            } else if ("Automatic".equals(mode)) {
                                //There is a branch whose flow mode is automatic, but if the variable or constant is not configured,
                                // the automatic branch is not used
                                String variable = customCondditionMap.get("variable");
                                String constant = customCondditionMap.get("constant");
                                if (StringUtils.isBlank(variable) || StringUtils.isBlank(constant)) {
                                    isAutomatic = false;
                                }
                            }
                        }

                        //Manual branch
                        if (isManual) {
                            if (StringUtils.isBlank(entity.getAct().getFlag())) {
                                map.put("isNeedFlag", true);
                                return map;
                            } else {
                                map.put("isNeedUserList", false);
                                return map;
                            }
                        }
                        //Automatic branch
                        else if (isAutomatic) {
                            map.put("isNeedUserList", false);
                            return map;
                        }
                    }
                    //Second node
                    else {
                        activityBehavior = destinationActivity1.getActivityBehavior();
                        taskDefinitionKey = destinationActivity1.getId();
                    }
                }
            }

            //Multiple user list | single user list
            if (activityBehavior instanceof ParallelMultiInstanceBehavior || activityBehavior instanceof SequentialMultiInstanceBehavior) {
                map.put("type", "multi");
            } else {
                map.put("type", "single");
            }

            //UserList
            entity.getAct().setProcDefId(processDefinition.getId());
            entity.getAct().setTaskDefKey(taskDefinitionKey);
            List<User> userList = getUserList(entity, loginName);
            map.put("userList", userList);

            //Restore Data
            entity.getAct().setProcDefId(null);
            entity.getAct().setTaskDefKey(null);
        }

        return map;
    }

    /**
     * Query next node user list during process running
     *
     * @param entity
     * @param loginName
     * @return Next node user list map.
     */
    public LinkedHashMap<String, Object> getTargetUserList(T entity, String loginName) {
        //Return Data
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        Task currentTask = null;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                currentTask = task;
                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    break;
                }
            }
        }
        //Current user agent task
        else if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    for (Task task : taskList) {
                        if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                            currentTask = task;
                            break;
                        }
                    }
                }
            }
        }

        boolean result = false;
        //Notify node
        if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
            result = false;
        }
        //Distribution node
        else if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
            result = false;
        }
        //Other nodes
        else {
            //Load rule variables
            entity.getAct().setProcDefId(currentTask.getProcessDefinitionId());
            entity.getAct().setTaskDefKey(currentTask.getTaskDefinitionKey());
            this.setRuleArgs(entity, loginName);

            //Whether the current node is completed
            boolean isCompleted = true;
            ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());
            if (currentActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                //Notify node
                if (currentActivity.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                    isCompleted = true;
                }
                //Distribution node
                else if (currentActivity.getId().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    isCompleted = true;
                }
                //Other nodes
                else {
                    isCompleted = isCompleteMultiInstance(currentActivity, currentTask.getExecutionId());
                }
            } else {
                isCompleted = true;
            }

            //Need to select branch or not
            if (isCompleted) {
                boolean b0 = StringUtils.isBlank(entity.getAct().getFlag());
                boolean b1 = entity.getRuleArgs().get(RuleArgs.automatic.name()) == null || entity.getRuleArgs().get(RuleArgs.automatic.name()).size() == 0;
                List<PvmTransition> currentOutgoingTransitions = currentActivity.getOutgoingTransitions();
                for (PvmTransition currentPvmTransition : currentOutgoingTransitions) {
                    ActivityImpl currentDestination = (ActivityImpl) currentPvmTransition.getDestination();
                    if (currentDestination.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior && (b0 && b1)) {
                        map.put("isNeedFlag", true);
                        return map;
                    } else if (currentDestination.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                        //Hand painted containment gateway (start)
                        if (currentDestination.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE) && (b0 && b1)) {
                            map.put("isNeedFlag", true);
                            return map;
                        }
                        //Hand drawn containment gateway (closed)
                        else if (currentDestination.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE) && (b0 && b1)) {
                            map.put("isNeedFlag", true);
                            return map;
                        }
                        //Generated containment gateway (orphaned)
                        else {
                            List<PvmTransition> gatewayOutgoingTransitions = currentDestination.getOutgoingTransitions();
                            for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                                ActivityImpl gatewayDestination = (ActivityImpl) gatewayPvmTransition.getDestination();
                                if (gatewayDestination.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior && (b0 && b1)) {
                                    map.put("isNeedFlag", true);
                                    return map;
                                } else if (gatewayDestination.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                                    //Hand painted containment gateway (start)
                                    if (gatewayDestination.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE) && (b0 && b1)) {
                                        map.put("isNeedFlag", true);
                                        return map;
                                    }
                                    //Hand drawn containment gateway (closed)
                                    else if (gatewayDestination.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE) && (b0 && b1)) {
                                        map.put("isNeedFlag", true);
                                        return map;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //User list required or not
            result = isNeedUserList(entity, currentActivity, currentTask.getExecutionId(), processDefinitionEntity);
            if (result) {
                //Next node collection
                Map<String, Object> nextTaskDefinitionMap = Maps.newHashMap();
                nextTaskDefinitionMap = getNextTaskDefinitionMap(nextTaskDefinitionMap, currentActivity, currentActivity.getId(), entity);
                for (Map.Entry<String, Object> entry : nextTaskDefinitionMap.entrySet()) {
                    //Next node i
                    ActivityImpl nextTaskDefinition = (ActivityImpl) entry.getValue();
                    ActivityBehavior activityBehavior = nextTaskDefinition.getActivityBehavior();

                    //Multiple user list | single user list
                    if (activityBehavior instanceof MultiInstanceActivityBehavior) {
                        map.put("type", "multi");
                    } else {
                        map.put("type", "single");
                    }

                    //User list
                    List<User> userList = Lists.newArrayList();
                    if (nextTaskDefinition.getId().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                        String owner = (String) runtimeService.getVariableLocal(entity.getProcInsId(), nextTaskDefinition.getId());
                        User user = UserUtil.getByLoginName(owner);
                        userList.add(user);
                    } else {
                        entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());
                        entity.getAct().setTaskDefKey(nextTaskDefinition.getId());
                        userList = getUserList(entity, loginName);
                    }
                    map.put("userList", userList);
                    //User list is required for one and only one node
                    break;
                }
            }
        }
        map.put("isNeedUserList", result);

        return map;
    }

    /**
     * Is multi instance complete or not.
     *
     * @param currentActivity
     * @param executionId
     * @return True of false.
     */
    private boolean isCompleteMultiInstance(ActivityImpl currentActivity, String executionId) {
        String variableLocalExecutionId = "";
        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        if (currentActivity.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
            variableLocalExecutionId = execution.getParentId();
        } else if (currentActivity.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
            variableLocalExecutionId = executionId;
        }
        Object nrOfInstancesObejct = runtimeService.getVariableLocal(variableLocalExecutionId, "nrOfInstances");
        int nrOfInstances = nrOfInstancesObejct == null ? 0 : (Integer) nrOfInstancesObejct;
        Object nrOfCompletedInstancesObejct = runtimeService.getVariableLocal(variableLocalExecutionId, "nrOfCompletedInstances");
        int nrOfCompletedInstances = nrOfCompletedInstancesObejct == null ? 0 : (Integer) nrOfCompletedInstancesObejct;

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.put("nrOfInstances", nrOfInstances);
        engine.put("nrOfCompletedInstances", nrOfCompletedInstances + 1);//+1：Assuming this pass

        //Multi instance completion conditions
        MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) currentActivity.getActivityBehavior();
        String expressionText = multiInstanceActivityBehavior.getCompletionConditionExpression().getExpressionText();
        int expressionTextBegin = expressionText.indexOf("{") + 1;
        int expressionTextEnd = expressionText.lastIndexOf("}");
        expressionText = expressionText.substring(expressionTextBegin, expressionTextEnd);

        boolean isCompleteMultiInstance = true;
        try {
            isCompleteMultiInstance = (boolean) engine.eval(expressionText);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return isCompleteMultiInstance;
    }

    /**
     * User list required or not.
     *
     * @param entity
     * @param currentActivity
     * @param executionId
     * @param processDefinitionEntity
     * @return True of false.
     */
    private boolean isNeedUserList(T entity, ActivityImpl currentActivity, String executionId, ProcessDefinitionEntity processDefinitionEntity) {
        boolean result = false;

        //Next node: end node?
        boolean destinationIsEndEvent = false;

        //Next node: hand drawn containment gateway (starting)?
        boolean destinationIsBeginInclusiveGateway = false;

        //Whether all branches are completed
        boolean isCompleteMultiBranch = true;

        //Next node
        ActivityImpl destination0Activity = (ActivityImpl) currentActivity.getOutgoingTransitions().get(0).getDestination();

        //End node
        if (destination0Activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior) {
            destinationIsEndEvent = true;
        }
        //Containment gateway
        else if (destination0Activity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
            //First change to true, and then verify one by one
            destinationIsEndEvent = true;

            //Hand painted containment gateway (start)
            if (destination0Activity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                destinationIsEndEvent = false;
                destinationIsBeginInclusiveGateway = true;
            }
            //Hand drawn containment gateway (closed)
            else if (destination0Activity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                List<PvmTransition> destination0OutgoingTransitions = destination0Activity.getOutgoingTransitions();
                for (PvmTransition destination0PvmTransition : destination0OutgoingTransitions) {
                    ActivityImpl destination1Activity = (ActivityImpl) destination0PvmTransition.getDestination();
                    if (false == (destination1Activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior)) {
                        destinationIsEndEvent = false;
                        break;
                    }
                }

                isCompleteMultiBranch = this.isCompleteMultiBranch(entity, currentActivity, executionId, processDefinitionEntity);
            }
            //Generated containment gateway (orphaned)
            else {
                List<PvmTransition> destination0OutgoingTransitions = destination0Activity.getOutgoingTransitions();
                for (PvmTransition destination0PvmTransition : destination0OutgoingTransitions) {
                    ActivityImpl destination1Activity = (ActivityImpl) destination0PvmTransition.getDestination();
                    //Notify node
                    if (destination1Activity.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

                    }
                    //Distribution node
                    else if (destination1Activity.getId().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

                    }
                    //Other nodes
                    else {
                        boolean b0 = false == (destination1Activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior);
                        boolean b1 = false == (destination1Activity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE));
                        if (b0 && b1) {
                            destinationIsEndEvent = false;
                        }

                        //Hand painted containment gateway (start)
                        if (destination1Activity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                            destinationIsEndEvent = false;
                            destinationIsBeginInclusiveGateway = true;
                        }
                        //Hand drawn containment gateway (closed)
                        else if (destination1Activity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                            List<PvmTransition> destination1OutgoingTransitions = destination1Activity.getOutgoingTransitions();
                            for (PvmTransition destination1PvmTransition : destination1OutgoingTransitions) {
                                ActivityImpl destination2Activity = (ActivityImpl) destination1PvmTransition.getDestination();
                                if (false == (destination2Activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior)) {
                                    destinationIsEndEvent = false;
                                    break;
                                }
                            }

                            isCompleteMultiBranch = this.isCompleteMultiBranch(entity, currentActivity, executionId, processDefinitionEntity);
                        }
                    }
                }
            }
        }

        //Multiple instances
        if (currentActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            //Notify node
            if (currentActivity.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                result = false;
            }
            //Distribution node
            else if (currentActivity.getId().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                result = false;
            }
            //Other nodes
            else {
                boolean isCompleteMultiInstance = isCompleteMultiInstance(currentActivity, executionId);

                /* Next node is not an end node
                 && Multi instance needs user list
                 && The next node is not a hand drawn containment gateway (starting)
                 && Multi branch completion */
                result = (false == destinationIsEndEvent) && isCompleteMultiInstance && (false == destinationIsBeginInclusiveGateway) && isCompleteMultiBranch;
            }
        }
        //Single instance
        else {
            /* Next node is not an end node
             && The next node is not a hand drawn containment gateway (starting)
             && Multi branch completion */
            result = (false == destinationIsEndEvent) && (false == destinationIsBeginInclusiveGateway) && isCompleteMultiBranch;
        }

        return result;
    }

    /**
     * Whether multi branch is completed or not.
     *
     * @param entity
     * @param currentActivity
     * @param executionId
     * @param processDefinitionEntity
     * @return True of false.
     */
    private boolean isCompleteMultiBranch(T entity, ActivityImpl currentActivity, String executionId, ProcessDefinitionEntity processDefinitionEntity) {
        boolean isCompleteMultiBranch = true;

        //Notify node
        if (currentActivity.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
            //true
        }
        //Distribution node
        else if (currentActivity.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
            //true
        }
        //Other nodes
        else {
            Map<String, Object> beginInclusiveGatewayMap = Maps.newHashMap();
            beginInclusiveGatewayMap = this.getBeginInclusiveGatewayMap(currentActivity, beginInclusiveGatewayMap);

            if (beginInclusiveGatewayMap != null && beginInclusiveGatewayMap.size() > 0) {
                List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(entity.getProcInsId()).list();
                for (Execution execution : executionList) {
                    if (isCompleteMultiBranch) {
                        boolean b0 = StringUtils.isNotBlank(execution.getActivityId());
                        if (b0) {
                            boolean b1 = false == currentActivity.getId().equals(execution.getActivityId());
                            boolean b2 = false == execution.getActivityId().startsWith(NODE_MARK_NOTIFY + UNDERLINE);
                            boolean b3 = false == execution.getActivityId().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE);
                            boolean b4 = false == execution.getActivityId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE);
                            boolean b5 = false == execution.getActivityId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE);
                            if (b1 && b2 && b3 && b4 && b5) {
                                ActivityImpl activityI = processDefinitionEntity.findActivity(execution.getActivityId());
                                Map<String, Object> mapI = Maps.newHashMap();
                                mapI = this.getBeginInclusiveGatewayMap(activityI, mapI);
                                if (mapI != null && mapI.size() > 0) {
                                    for (Map.Entry<String, Object> entry : mapI.entrySet()) {
                                        if (beginInclusiveGatewayMap.get(entry.getKey()) != null) {
                                            isCompleteMultiBranch = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isCompleteMultiBranch;
    }

    /**
     * Query next node definition.
     *
     * @param nextTaskDefinitionMap
     * @param activityImpl
     * @param activityId
     * @param entity
     * @return Next node definition map.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getNextTaskDefinitionMap(Map<String, Object> nextTaskDefinitionMap, ActivityImpl activityImpl, String activityId, T entity) {
        if (false == activityImpl.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                && false == activityImpl.getId().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
            if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior
                    || activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior
                    || activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                if (false == activityImpl.getId().equals(activityId)) {
                    nextTaskDefinitionMap.put(activityImpl.getId(), activityImpl);
                } else {
                    List<PvmTransition> userTaskOutgoingTransitions = activityImpl.getOutgoingTransitions();
                    for (PvmTransition userTaskPvmTransition : userTaskOutgoingTransitions) {
                        ActivityImpl userTaskDestination = (ActivityImpl) userTaskPvmTransition.getDestination();
                        if (userTaskDestination.getActivityBehavior() instanceof UserTaskActivityBehavior
                                || userTaskDestination.getActivityBehavior() instanceof ParallelMultiInstanceBehavior
                                || userTaskDestination.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                            getNextTaskDefinitionMap(nextTaskDefinitionMap, userTaskDestination, activityId, entity);
                        } else if (userTaskDestination.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                            List<PvmTransition> gatewayOutgoingTransitions = userTaskDestination.getOutgoingTransitions();
                            for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                                ActivityImpl gatewayDestination = (ActivityImpl) gatewayPvmTransition.getDestination();
                                //TODO Will multiple branches come in? A: the hand drawn containment gateway can't come in
                                getNextTaskDefinitionMap(nextTaskDefinitionMap, gatewayDestination, activityId, entity);
                            }
                        } else if (userTaskDestination.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                            Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                            String mode = extendMap.get("mode");
                            if ("manual".equals(mode)) {
                                List<PvmTransition> gatewayOutgoingTransitions = userTaskDestination.getOutgoingTransitions();
                                for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                                    ActivityImpl gatewayDestination = (ActivityImpl) gatewayPvmTransition.getDestination();
                                    if (entity.getAct().getFlag().equals(gatewayDestination.getId())) {
                                        getNextTaskDefinitionMap(nextTaskDefinitionMap, gatewayDestination, activityId, entity);
                                    }
                                }
                            } else if ("automatic".equals(mode)) {
                                Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());//全部成立的分支的变量和值
                                List<PvmTransition> gatewayOutgoingTransitions = userTaskDestination.getOutgoingTransitions();
                                for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                                    String documentation = (String) gatewayPvmTransition.getProperty("documentation");
                                    Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                                    String variable = customCondditionMap.get("variable");
                                    String operator = customCondditionMap.get("operator");
//									String constant = customCondditionMap.get("constant");
                                    String fieldValue = automaticMap.get(variable);
                                    if (StringUtils.isNotBlank(fieldValue)) {
                                        String conditionText = (String) gatewayPvmTransition.getProperty("conditionText");
                                        int conditionTextBegin = conditionText.indexOf("{") + 1;
                                        int conditionTextEnd = conditionText.lastIndexOf("}");
                                        conditionText = conditionText.substring(conditionTextBegin, conditionTextEnd);
                                        if ("==".equals(operator)) {

                                        } else {
                                            conditionText = conditionText.replaceAll("'", "");
                                        }
                                        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
                                        engine.put(variable, fieldValue);
                                        try {
                                            if ((boolean) engine.eval(conditionText)) {
                                                ActivityImpl gatewayDestination = (ActivityImpl) gatewayPvmTransition.getDestination();
                                                getNextTaskDefinitionMap(nextTaskDefinitionMap, gatewayDestination, activityId, entity);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        } else {
                            //Current or next node is not UserTaskActivityBehavior or GatewayActivityBehavior do not process
                        }
                    }
                }
            } else if (activityImpl.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
                String mode = extendMap.get("mode");
                if ("manual".equals(mode)) {
                    List<PvmTransition> gatewayOutgoingTransitions = activityImpl.getOutgoingTransitions();
                    for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                        ActivityImpl gatewayDestination = (ActivityImpl) gatewayPvmTransition.getDestination();
                        if (entity.getAct().getFlag().equals(gatewayDestination.getId())) {
                            getNextTaskDefinitionMap(nextTaskDefinitionMap, gatewayDestination, activityId, entity);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    //Variables and values of all established branches
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    List<PvmTransition> gatewayOutgoingTransitions = activityImpl.getOutgoingTransitions();
                    for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                        String documentation = (String) gatewayPvmTransition.getProperty("documentation");
                        Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                        String variable = customCondditionMap.get("variable");
                        String operator = customCondditionMap.get("operator");
//						String constant = customCondditionMap.get("constant");
                        String fieldValue = automaticMap.get(variable);
                        if (StringUtils.isNotBlank(fieldValue)) {
                            String conditionText = (String) gatewayPvmTransition.getProperty("conditionText");
                            int conditionTextBegin = conditionText.indexOf("{") + 1;
                            int conditionTextEnd = conditionText.lastIndexOf("}");
                            conditionText = conditionText.substring(conditionTextBegin, conditionTextEnd);
                            if ("==".equals(operator)) {

                            } else {
                                conditionText = conditionText.replaceAll("'", "");
                            }
                            ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
                            engine.put(variable, fieldValue);
                            try {
                                if ((boolean) engine.eval(conditionText)) {
                                    ActivityImpl gatewayDestination = (ActivityImpl) gatewayPvmTransition.getDestination();
                                    getNextTaskDefinitionMap(nextTaskDefinitionMap, gatewayDestination, activityId, entity);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                //Current or next node is not UserTaskActivityBehavior do not process
            }
        } else {
            //Notify node does not handle
            //Distribution node does not process
        }

        return nextTaskDefinitionMap;
    }

    /**
     * Save (workflow core)
     *
     * @param entity
     * @param title
     * @param processDefinationKey
     * @param tableName
     * @param vars
     * @param loginName
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    public void saveAct(T entity, String title, String processDefinationKey, String tableName, Map<String, Object> vars, String loginName) {//TODO 简化参数
        //ParamMap: {button,type,flag}
        Map<String, String> paramMap = new Gson().fromJson(Encodes.unescapeHtml(entity.getAct().getParam()), Map.class);

        //Button & Type
        String button = " [ " + paramMap.get("button") + " ] " + (entity.getAct().getComment() == null ? "" : entity.getAct().getComment());
        String type = paramMap.get("type");

        //Process not started: temp save
        if (ButtonType.SAVE.name().equalsIgnoreCase(type)) {
            this.save(entity);
        }
        //Save and start process
        else if (ButtonType.SAVEANDSTART.name().equalsIgnoreCase(type)) {
            //Deployment model
            deployModel(entity);

            //Update process initiation time and object ID
            entity.setCreateDate(new Date());
            this.save(entity);

            //Dynamic form formNo
            String businessId = entity.getId();
            try {
                Field[] declaredFields = entity.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    if ("formNo".equals(field.getName())) {
                        Object object = field.get(entity);
                        if (object != null) {
                            String value = (String) object;
                            if (StringUtils.isNotBlank(value)) {
                                businessId += (":" + value);
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Startup process
            String processInstanceId = this.startProcess(processDefinationKey, tableName, businessId, loginName);
            entity.setProcInsId(processInstanceId);

            //Set task owner and handler of the first node
            String startUser = loginName; //UserUtils.getUser().getLoginName();
            List<String> startUserList = Lists.newArrayList();
            startUserList.add(startUser);
            this.setOwner(processInstanceId, startUserList);

            //Set task handler of the first node
            Task currentTask = this.getCurrentTask(processInstanceId, loginName);
            currentTask.setAssignee(startUser);
            taskService.saveTask(currentTask);

            //Load rule variables
            entity.getAct().setProcDefId(this.getProcIns(processInstanceId).getProcessDefinitionId());
            entity.getAct().setTaskDefKey(currentTask.getTaskDefinitionKey());
            this.setRuleArgs(entity, loginName);

            //Branch defaults
            String branchType = "";
            String mode = "";
            Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
            if (extendMap != null) {
                branchType = extendMap.get("branchType");
                mode = extendMap.get("mode");
            }

            //User selected when submitting form - zero branches
            if (StringUtils.isBlank(branchType)) {
                //Second node handler list
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }

                //Complete the first node
                this.complete(currentTask.getId(), processInstanceId, button, title, vars);
                this.save(entity);

                //Set second node task owner
                this.setOwner(processInstanceId, loginNameList);
            }
            //User selected when submitting form - a branch
            else if ("single".equals(branchType)) {
                //Set branch conditions
                if ("manual".equals(mode)) {
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (entity.getAct().getFlag().equals(entry.getKey())) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                //Second node handler list
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }

                //Complete the first node
                this.complete(currentTask.getId(), processInstanceId, button, title, vars);
                this.save(entity);

                //Set second node task owner
                this.setOwner(processInstanceId, loginNameList);
            }
            //No user selected when submitting form - multiple branches
            else if ("multi".equals(branchType)) {
                //Set branch conditions
                if ("manual".equals(mode)) {
                    Map<String, String> flagMap = Maps.newHashMap();
                    String[] flagArray = entity.getAct().getFlag().split(COMMA);
                    for (String flag : flagArray) {
                        flagMap.put(flag, flag);
                    }
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (flagMap.get(entry.getKey()) != null) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                //Second node handler list
                Map<String, Object> branchInfoMap = this.getBranchInfoMap(entity, currentTask, loginName);
                for (Map.Entry<String, Object> entry : branchInfoMap.entrySet()) {
                    Map<String, Object> branchInfo = (Map<String, Object>) entry.getValue();
                    String assigneeExpressionText = (String) branchInfo.get("assigneeExpressionText");
                    List<String> loginNameList = (List<String>) branchInfo.get("loginNameList");
                    vars.put(assigneeExpressionText, loginNameList);
                }

                //Complete the first node
                this.complete(currentTask.getId(), processInstanceId, button, title, vars);
                this.save(entity);

                //Set second node task owner
                this.setOwner(processInstanceId, branchInfoMap);
            }
        }
        //Process running: temp save
        else if (ButtonType.SAVEANDCLAIM.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.claim(entity.getProcInsId(), loginName);
        }
        //Save and submit tasks
        else if (ButtonType.SAVEANDCOMPLETE.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.claim(entity.getProcInsId(), loginName);

            Task currentTask = this.getCurrentTask(entity.getProcInsId(), loginName);
            entity.getAct().setProcDefId(this.getProcIns(entity.getProcInsId()).getProcessDefinitionId());
            entity.getAct().setTaskDefKey(currentTask.getTaskDefinitionKey());
            this.setRuleArgs(entity, loginName);

            String branchType = "";
            String mode = "";
            Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
            if (extendMap != null) {
                branchType = extendMap.get("branchType");
                mode = extendMap.get("mode");
            }

            if (StringUtils.isBlank(branchType)) {
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }

                this.complete(currentTask.getId(), entity.getProcInsId(), button, title, vars);

                ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
                if (processInstance != null) {
                    this.setOwner(processInstance.getId(), loginNameList);
                }
            } else if ("single".equals(branchType)) {
                if ("manual".equals(mode)) {
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (entity.getAct().getFlag().equals(entry.getKey())) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                //Second node handler list
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }

                this.complete(currentTask.getId(), entity.getProcInsId(), button, title, vars);

                ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
                if (processInstance != null) {
                    this.setOwner(processInstance.getId(), loginNameList);
                }
            } else if ("multi".equals(branchType)) {
                if ("manual".equals(mode)) {
                    Map<String, String> flagMap = Maps.newHashMap();
                    String[] flagArray = entity.getAct().getFlag().split(COMMA);
                    for (String flag : flagArray) {
                        flagMap.put(flag, flag);
                    }
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (flagMap.get(entry.getKey()) != null) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                Map<String, Object> branchInfoMap = this.getBranchInfoMap(entity, currentTask, loginName);
                for (Map.Entry<String, Object> entry : branchInfoMap.entrySet()) {
                    Map<String, Object> branchInfo = (Map<String, Object>) entry.getValue();
                    String assigneeExpressionText = (String) branchInfo.get("assigneeExpressionText");
                    List<String> loginNameList = (List<String>) branchInfo.get("loginNameList");
                    vars.put(assigneeExpressionText, loginNameList);
                }

                this.complete(currentTask.getId(), entity.getProcInsId(), button, title, vars);

                ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
                if (processInstance != null) {
                    this.setOwner(processInstance.getId(), branchInfoMap);

                    //Multi branch execution instance correction
                    Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).executionId(processInstance.getId()).singleResult();
                    if (execution != null && StringUtils.isNotBlank(execution.getActivityId())) {
                        //ProcessDefinitionEntity
                        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
                        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
                        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

                        ActivityImpl activityImpl = processDefinitionEntity.findActivity(execution.getActivityId());
                        if (activityImpl != null && activityImpl.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                            //Containment gateway (started)
                            if (activityImpl.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY)) {
                                //Correct, do nothing
                            }
                            //Containment gateway (generated)
                            else {
                                //correct it
                                List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
                                for (PvmTransition pvmTransition : outgoingTransitions) {
                                    ActivityImpl destination = (ActivityImpl) pvmTransition.getDestination();
                                    if (destination.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY)) {
                                        final ExecutionEntity executionEntity = (ExecutionEntity) execution;
                                        executionEntity.setActivity(destination);
                                        managementService.executeCommand(new Command<T>() {
                                            @Override
                                            public T execute(CommandContext commandContext) {
                                                commandContext.getDbSqlSession().update(executionEntity);
                                                return null;
                                            }
                                        });
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //Save and fallback
        else if (ButtonType.SAVEANDREJECT.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);

            this.rollBack(entity, button, title, loginName);
        }
        //Save and specify fallback
        else if (ButtonType.SAVEANDSUPERREJECT.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);

            this.rollBackTo(entity, button, title, loginName);
        }
        //Save and terminate
        else if (ButtonType.SAVEANDTERMINATE.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);

            entity.setTempNodeKey(TASK_DEFINITION_KEY_END);
            this.rollBackTo(entity, button, title, loginName);
        }

        //Send system message
        sendMessage(entity, type, loginName);
    }

    /**
     * Save (business flow core)
     *
     * @param entity
     * @param title
     * @param processDefinationKey
     * @param tableName
     * @param vars
     * @param loginName
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    public void saveServiceAct(T entity, String title, String processDefinationKey, String tableName, Map<String, Object> vars, String loginName) {
        //ParamMap: {button,type,flag}
        Map<String, String> paramMap = new Gson().fromJson(Encodes.unescapeHtml(entity.getAct().getParam()), Map.class);

        //Button & Type
        String button = " [ " + paramMap.get("button") + " ] " + (entity.getAct().getComment() == null ? "" : entity.getAct().getComment());
        String type = paramMap.get("type");

        //Process not started: temp save
        if (ButtonType.SAVE.name().equalsIgnoreCase(type)) {
            entity.setCurrentAssignee(COMMA + loginName + COMMA);
            entity.setCurrentStatus(UNSTART);
            this.save(entity);
        }
        //Save and start process
        else if (ButtonType.SAVEANDSTART.name().equalsIgnoreCase(type)) {
            //Deployment model
            deployModel(entity);

            //Update process initiation time and object ID
            entity.setCreateDate(new Date());
            this.save(entity);

            //Dynamic form formNo
            String businessId = entity.getId();
            try {
                Field[] declaredFields = entity.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    if ("formNo".equals(field.getName())) {
                        Object object = field.get(entity);
                        if (object != null) {
                            String value = (String) object;
                            if (StringUtils.isNotBlank(value)) {
                                businessId += (":" + value);
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Startup process
            String processInstanceId = this.startProcess(processDefinationKey, tableName, businessId, title);
            entity.setProcInsId(processInstanceId);

            //Set task owner and handler of the first node
            String startUser = loginName; //UserUtils.getUser().getLoginName();
            List<String> startUserList = Lists.newArrayList();
            startUserList.add(startUser);
            this.setOwner(processInstanceId, startUserList);

            //Set task handler of the first node
            Task currentTask = this.getCurrentTask(processInstanceId, loginName);
            currentTask.setAssignee(startUser);
            taskService.saveTask(currentTask);

            //Load rule variables
            entity.getAct().setProcDefId(this.getProcIns(processInstanceId).getProcessDefinitionId());
            entity.getAct().setTaskDefKey(currentTask.getTaskDefinitionKey());
            this.setRuleArgs(entity, loginName);

            //Branch defaults
            String branchType = "";
            String mode = "";
            Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
            if (extendMap != null) {
                branchType = extendMap.get("branchType");
                mode = extendMap.get("mode");
            }

            //User selected when submitting form - zero branches
            if (StringUtils.isBlank(branchType)) {
                //Second node handler list
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }
                String[] tempLoginNameArray = new String[loginNameList.size()];
                for (int i = 0; i < loginNameList.size(); i++) {
                    tempLoginNameArray[i] = loginNameList.get(i);
                }
                entity.setTempLoginName(tempLoginNameArray);

                //Complete the first node
                this.complete(currentTask.getId(), processInstanceId, button, title, vars);
                this.save(entity);

                //Set second node task owner
                this.setOwner(processInstanceId, loginNameList);
            }
            //User selected when submitting form - a branch
            else if ("single".equals(branchType)) {
                //Set branch conditions
                if ("manual".equals(mode)) {
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (entity.getAct().getFlag().equals(entry.getKey())) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                //Second node handler list
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }
                String[] tempLoginNameArray = new String[loginNameList.size()];
                for (int i = 0; i < loginNameList.size(); i++) {
                    tempLoginNameArray[i] = loginNameList.get(i);
                }
                entity.setTempLoginName(tempLoginNameArray);

                //Complete the first node
                this.complete(currentTask.getId(), processInstanceId, button, title, vars);
                this.save(entity);

                //Set second node task owner
                this.setOwner(processInstanceId, loginNameList);
            }
            //No user selected when submitting form - multiple branches
            else if ("multi".equals(branchType)) {
                //Set branch conditions
                if ("manual".equals(mode)) {
                    Map<String, String> flagMap = Maps.newHashMap();
                    String[] flagArray = entity.getAct().getFlag().split(COMMA);
                    for (String flag : flagArray) {
                        flagMap.put(flag, flag);
                    }
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (flagMap.get(entry.getKey()) != null) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                //Second node handler list
                List<String> allLoginNameList = Lists.newArrayList();
                Map<String, Object> branchInfoMap = this.getBranchInfoMap(entity, currentTask, loginName);
                for (Map.Entry<String, Object> entry : branchInfoMap.entrySet()) {
                    Map<String, Object> branchInfo = (Map<String, Object>) entry.getValue();
                    String assigneeExpressionText = (String) branchInfo.get("assigneeExpressionText");
                    List<String> loginNameList = (List<String>) branchInfo.get("loginNameList");
                    vars.put(assigneeExpressionText, loginNameList);
                    allLoginNameList.addAll(loginNameList);
                }
                String[] tempLoginNameArray = new String[allLoginNameList.size()];
                for (int i = 0; i < allLoginNameList.size(); i++) {
                    tempLoginNameArray[i] = allLoginNameList.get(i);
                }
                entity.setTempLoginName(tempLoginNameArray);

                //Complete the first node
                this.complete(currentTask.getId(), processInstanceId, button, title, vars);
                this.save(entity);

                //Set second node task owner
                this.setOwner(processInstanceId, branchInfoMap);
            }

            ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
            if (processInstance != null) {
                entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());

                Task task = getActiveTask(processInstance.getId());
                entity.getAct().setTaskDefKey(task.getTaskDefinitionKey());
                entity.setCurrentStatus(task.getTaskDefinitionKey());
                entity.setProcTaskName(task.getTaskDefinitionKey());

                String tempLoginNameStr = COMMA;
                for (String tempLoginName : entity.getTempLoginName()) {
                    tempLoginNameStr += tempLoginName;
                    tempLoginNameStr += COMMA;
                }
                entity.setCurrentAssignee(tempLoginNameStr);

                TaskPermission taskPermission = new TaskPermission();
                taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
                entity.setProcTaskPermission(taskPermission);

                this.save(entity);
            }
        }
        //Process running: temp save
        else if (ButtonType.SAVEANDCLAIM.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.claim(entity.getProcInsId(), loginName);
        }
        //Save and submit tasks
        else if (ButtonType.SAVEANDCOMPLETE.name().equalsIgnoreCase(type)) {
            String oldStatus = entity.getCurrentStatus();

            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.claim(entity.getProcInsId(), loginName);

            Task currentTask = this.getCurrentTask(entity.getProcInsId(), loginName);
            entity.getAct().setProcDefId(this.getProcIns(entity.getProcInsId()).getProcessDefinitionId());
            entity.getAct().setTaskDefKey(currentTask.getTaskDefinitionKey());
            this.setRuleArgs(entity, loginName);
//			this.setAssignee(entity);

            String branchType = "";
            String mode = "";
            Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
            if (extendMap != null) {
                branchType = extendMap.get("branchType");
                mode = extendMap.get("mode");
            }

            if (StringUtils.isBlank(branchType)) {
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }
                String[] tempLoginNameArray = new String[loginNameList.size()];
                for (int i = 0; i < loginNameList.size(); i++) {
                    tempLoginNameArray[i] = loginNameList.get(i);
                }
                entity.setTempLoginName(tempLoginNameArray);

                this.complete(currentTask.getId(), entity.getProcInsId(), button, title, vars);

                ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
                if (processInstance != null) {
                    this.setOwner(processInstance.getId(), loginNameList);
                }
            } else if ("single".equals(branchType)) {
                if ("manual".equals(mode)) {
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (entity.getAct().getFlag().equals(entry.getKey())) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                //Second node handler list
                List<String> userIdList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && theLoginName.startsWith("userId_")) {
                            userIdList.add(theLoginName.split("_")[1]);
                        }
                    }
                }
                Set<String> loginNameSet = Sets.newHashSet();
                if (userIdList != null && userIdList.size() > 0) {
                    List<User> userList = userService.findUserListByUserIdList(userIdList);
                    for (User user : userList) {
                        loginNameSet.add(user.getLoginName());
                    }
                }
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName) && false == theLoginName.startsWith("userId_")) {
                            loginNameSet.add(theLoginName);
                        }
                    }
                }
                List<String> loginNameList = Lists.newArrayList();
                for (String theLoginName : loginNameSet) {
                    loginNameList.add(theLoginName);
                }
                String assigneeExpressionText = this.getNextTaskDefinitionAssigneeExpressionText(entity, loginName);
                if (StringUtils.isNotBlank(assigneeExpressionText)) {
                    vars.put(assigneeExpressionText, loginNameList);
                }
                String[] tempLoginNameArray = new String[loginNameList.size()];
                for (int i = 0; i < loginNameList.size(); i++) {
                    tempLoginNameArray[i] = loginNameList.get(i);
                }
                entity.setTempLoginName(tempLoginNameArray);

                this.complete(currentTask.getId(), entity.getProcInsId(), button, title, vars);

                ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
                if (processInstance != null) {
                    this.setOwner(processInstance.getId(), loginNameList);
                }
            } else if ("multi".equals(branchType)) {
                if ("manual".equals(mode)) {
                    Map<String, String> flagMap = Maps.newHashMap();
                    String[] flagArray = entity.getAct().getFlag().split(COMMA);
                    for (String flag : flagArray) {
                        flagMap.put(flag, flag);
                    }
                    Map<String, String> manualMap = entity.getRuleArgs().get(RuleArgs.hand.name());
                    for (Map.Entry<String, String> entry : manualMap.entrySet()) {
                        if (flagMap.get(entry.getKey()) != null) {
                            vars.put(entry.getKey(), ACTIVITI_YES);
                        } else {
                            vars.put(entry.getKey(), ACTIVITI_NO);
                        }
                    }
                } else if ("automatic".equals(mode)) {
                    Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());
                    for (Map.Entry<String, String> entry : automaticMap.entrySet()) {
                        vars.put(entry.getKey(), entry.getValue());
                    }
                }

                List<String> allLoginNameList = Lists.newArrayList();
                Map<String, Object> branchInfoMap = this.getBranchInfoMap(entity, currentTask, loginName);
                for (Map.Entry<String, Object> entry : branchInfoMap.entrySet()) {
                    Map<String, Object> branchInfo = (Map<String, Object>) entry.getValue();
                    String assigneeExpressionText = (String) branchInfo.get("assigneeExpressionText");
                    List<String> loginNameList = (List<String>) branchInfo.get("loginNameList");
                    vars.put(assigneeExpressionText, loginNameList);
                    allLoginNameList.addAll(loginNameList);
                }
                String[] tempLoginNameArray = new String[allLoginNameList.size()];
                for (int i = 0; i < allLoginNameList.size(); i++) {
                    tempLoginNameArray[i] = allLoginNameList.get(i);
                }
                entity.setTempLoginName(tempLoginNameArray);

                this.complete(currentTask.getId(), entity.getProcInsId(), button, title, vars);

                ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
                if (processInstance != null) {
                    this.setOwner(processInstance.getId(), branchInfoMap);

                    //Multi branch execution instance correction
                    Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).executionId(processInstance.getId()).singleResult();
                    if (execution != null && StringUtils.isNotBlank(execution.getActivityId())) {
                        //ProcessDefinitionEntity
                        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
                        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
                        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

                        ActivityImpl activityImpl = processDefinitionEntity.findActivity(execution.getActivityId());
                        if (activityImpl != null && activityImpl.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                            //Containment gateway (started)
                            if (activityImpl.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY)) {
                                //Correct
                            }
                            //Containment gateway (generated)
                            else {
                                //To correct
                                List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
                                for (PvmTransition pvmTransition : outgoingTransitions) {
                                    ActivityImpl destination = (ActivityImpl) pvmTransition.getDestination();
                                    if (destination.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY)) {
                                        final ExecutionEntity executionEntity = (ExecutionEntity) execution;
                                        executionEntity.setActivity(destination);
                                        managementService.executeCommand(new Command<T>() {
                                            @Override
                                            public T execute(CommandContext commandContext) {
                                                commandContext.getDbSqlSession().update(executionEntity);
                                                return null;
                                            }
                                        });
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
            if (processInstance != null) {
                entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());

                List<String> loginNameList = Lists.newArrayList();
                if (entity.getTempLoginName() != null && entity.getTempLoginName().length > 0) {
                    for (String theLoginName : entity.getTempLoginName()) {
                        if (StringUtils.isNotBlank(theLoginName)) {
                            loginNameList.add(theLoginName);
                        }
                    }
                }

                this.setOwner(processInstance.getId(), loginNameList);

                Task task = getActiveTask(processInstance.getId());
                entity.getAct().setTaskDefKey(task.getTaskDefinitionKey());
                entity.setCurrentStatus(task.getTaskDefinitionKey());
                entity.setProcTaskName(task.getTaskDefinitionKey());

                String tempLoginNameStr = COMMA;
                if (oldStatus.equals(entity.getCurrentStatus())) {
                    String[] currentAssigneeArray = entity.getCurrentAssignee().split(",");
                    for (String ca : currentAssigneeArray) {
                        if (StringUtils.isNotBlank(ca) && false == ca.equals(loginName)) {
                            tempLoginNameStr += ca;
                            tempLoginNameStr += COMMA;
                        }
                    }
                } else {
                    for (String tempLoginName : loginNameList) {
                        tempLoginNameStr += tempLoginName;
                        tempLoginNameStr += ",";
                    }
                }
                entity.setCurrentAssignee(tempLoginNameStr);

                TaskPermission taskPermission = new TaskPermission();
                taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
                entity.setProcTaskPermission(taskPermission);

                this.save(entity);
            } else {
                TaskPermission taskPermission = new TaskPermission();
                taskPermission.setId(END);
                entity.setProcTaskPermission(taskPermission);
                entity.setCurrentStatus(END);
                entity.setCurrentAssignee("-");
                this.save(entity);
            }
        }
        //Save and fallback
        else if (ButtonType.SAVEANDREJECT.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.rollBack(entity, button, title, loginName);

            ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
            if (processInstance != null) {
                entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());

                Task task = this.getActiveTask(entity.getProcInsId());
                entity.getAct().setTaskDefKey(task.getTaskDefinitionKey());
                entity.setCurrentStatus(task.getTaskDefinitionKey());
                entity.setProcTaskName(task.getTaskDefinitionKey());

                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(entity.getProcInsId()).taskDefinitionKey(task.getTaskDefinitionKey())
                        .orderByTaskCreateTime().desc().list();
                String assignee = null;
                for (HistoricTaskInstance historicTaskInstance : list) {
                    if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                        assignee = historicTaskInstance.getOwner();
                        break;
                    }
                }
                entity.setCurrentAssignee("," + assignee + ",");

                TaskPermission taskPermission = new TaskPermission();
                taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
                entity.setProcTaskPermission(taskPermission);

                this.save(entity);
            }
        }
        //Save and specify fallback
        else if (ButtonType.SAVEANDSUPERREJECT.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.rollBackTo(entity, button, title, loginName);

            ProcessInstance processInstance = this.getProcIns(entity.getProcInsId());
            if (processInstance != null) {
                entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());

                Task task = this.getActiveTask(entity.getProcInsId());
                entity.getAct().setTaskDefKey(task.getTaskDefinitionKey());
                entity.setCurrentStatus(task.getTaskDefinitionKey());
                entity.setProcTaskName(task.getTaskDefinitionKey());

                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(entity.getProcInsId()).taskDefinitionKey(task.getTaskDefinitionKey())
                        .orderByTaskCreateTime().desc().list();
                String assignee = null;
                for (HistoricTaskInstance historicTaskInstance : list) {
                    if (!StringUtils.isBlank(historicTaskInstance.getOwner())) {
                        assignee = historicTaskInstance.getOwner();
                        break;
                    }
                }
                entity.setCurrentAssignee("," + assignee + ",");

                TaskPermission taskPermission = new TaskPermission();
                taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
                entity.setProcTaskPermission(taskPermission);

                this.save(entity);
            }
        }
        //Save and terminate
        else if (ButtonType.SAVEANDTERMINATE.name().equalsIgnoreCase(type)) {
            this.unclaim(entity.getProcInsId(), loginName);
            this.save(entity);
            this.rollBackTo(entity, button, title, loginName);

            TaskPermission taskPermission = new TaskPermission();
            taskPermission.setId(END);
            entity.setProcTaskPermission(taskPermission);
            entity.setCurrentStatus(TERMINATE);
            entity.setCurrentAssignee("-");
            this.save(entity);
        }
        sendMessage(entity, type, loginName);
    }

    /**
     * Set task owner (submit - a branch).
     *
     * @param processInstanceId
     * @param loginNameList
     */
    public void setOwner(String processInstanceId, List<String> loginNameList) {
        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current active task
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        for (Task task : taskList) {
            if (StringUtils.isBlank(task.getOwner())) {
                ActivityImpl activityImpl = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
                if (activityImpl.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {//多人并行、串行（包含知会）
                    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityImpl.getActivityBehavior();
                    String collectionElementVariable = multiInstanceActivityBehavior.getCollectionElementVariable();
                    String loginName = (String) runtimeService.getVariableLocal(task.getExecutionId(), collectionElementVariable);
                    taskService.setOwner(task.getId(), loginName);
                } else {
                    taskService.setOwner(task.getId(), loginNameList.get(0));
                }
            } else {
                //Existence of OWNER
            }
        }
    }

    /**
     * Set task owner (submit - multiple branches).
     *
     * @param processInstanceId
     * @param branchInfoMap
     */
    @SuppressWarnings("unchecked")
    public void setOwner(String processInstanceId, Map<String, Object> branchInfoMap) {
        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current active task
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        for (Task task : taskList) {
            if (StringUtils.isBlank(task.getOwner())) {
                ActivityImpl activityImpl = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
                if (activityImpl.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {//多人并行、串行（包含知会）
                    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityImpl.getActivityBehavior();
                    String collectionElementVariable = multiInstanceActivityBehavior.getCollectionElementVariable();
                    String loginName = (String) runtimeService.getVariableLocal(task.getExecutionId(), collectionElementVariable);
                    taskService.setOwner(task.getId(), loginName);
                } else {
                    Map<String, Object> branchInfo = (Map<String, Object>) branchInfoMap.get(task.getTaskDefinitionKey());
                    List<String> loginNameList = (List<String>) branchInfo.get("loginNameList");
                    taskService.setOwner(task.getId(), loginNameList.get(0));
                }
            } else {
                //Existence of OWNER
            }
        }
    }

    /**
     * Set task owner (fallback, specified fallback, retrieve).
     *
     * @param processDefinitionEntity
     * @param processInstanceId
     */
    public void setOwner(ProcessDefinitionEntity processDefinitionEntity, String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        for (Task task : taskList) {
            //Notify node
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                ActivityImpl activityImpl = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
                if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    if (activityImpl.getId().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                        String owner = (String) runtimeService.getVariableLocal(processInstanceId, activityImpl.getId());
                        taskService.setOwner(task.getId(), owner);
                    } else {
                        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskDefinitionKey(task.getTaskDefinitionKey()).orderByTaskCreateTime().desc().list();
                        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                            if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                                taskService.setOwner(task.getId(), historicTaskInstance.getOwner());
                                break;
                            }
                        }
                    }
                } else if (activityImpl.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    //To be tested: is loginname the owner or assignee in history? A: owner
                    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityImpl.getActivityBehavior();
                    String collectionElementVariable = multiInstanceActivityBehavior.getCollectionElementVariable();
                    String loginName = (String) runtimeService.getVariableLocal(task.getExecutionId(), collectionElementVariable);
                    taskService.setOwner(task.getId(), loginName);
                }
            }
        }
    }

    /**
     * Query next node handler variable.
     *
     * @param entity
     * @param loginName
     * @return Next node handler var.
     */
    private String getNextTaskDefinitionAssigneeExpressionText(T entity, String loginName) {
        //Next node handler variable
        String assigneeExpressionText = "";

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        Task currentTask = null;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                currentTask = task;
                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    break;
                }
            }
        }
        //Current user agent task
        else if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    for (Task task : taskList) {
                        if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                            currentTask = task;
                            break;
                        }
                    }
                }
            }
        }

        //Notify node
        if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
            //Notify node does not handle
        }
        //Distribution node
        else if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
            //Distribution node does not process
        }
        //Other nodes
        else {
            //Load rule variables
            entity.getAct().setProcDefId(currentTask.getProcessDefinitionId());
            entity.getAct().setTaskDefKey(currentTask.getTaskDefinitionKey());
            this.setRuleArgs(entity, loginName);

            //Current node
            ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

            //Next node
            Map<String, Object> nextTaskDefinitionMap = Maps.newHashMap();
            nextTaskDefinitionMap = getNextTaskDefinitionMap(nextTaskDefinitionMap, currentActivity, currentActivity.getId(), entity);
            for (Map.Entry<String, Object> entry : nextTaskDefinitionMap.entrySet()) {
                ActivityImpl nextTaskDefinition = (ActivityImpl) entry.getValue();
                if (nextTaskDefinition.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) nextTaskDefinition.getActivityBehavior();
                    assigneeExpressionText = multiInstanceActivityBehavior.getCollectionExpression().getExpressionText();
                } else if (nextTaskDefinition.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    if (nextTaskDefinition.getId().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                        assigneeExpressionText = (String) runtimeService.getVariableLocal(entity.getProcInsId(), nextTaskDefinition.getId());
                    } else {
                        UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) nextTaskDefinition.getActivityBehavior();

                        //Candidate user
                        Set<Expression> candidateUserIdExpressions = userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions();
                        //candidateUserIdExpressions.size() == 1
                        for (Expression expression : candidateUserIdExpressions) {
                            assigneeExpressionText = expression.getExpressionText();
                        }

                        if (StringUtils.isBlank(assigneeExpressionText)) {
                            //Agent user
                            assigneeExpressionText = userTaskActivityBehavior.getTaskDefinition().getAssigneeExpression().getExpressionText();
                        }
                    }
                }
                int assigneeExpressionTextBegin = assigneeExpressionText.lastIndexOf("{");
                int assigneeExpressionTextEnd = assigneeExpressionText.lastIndexOf("}");
                if (assigneeExpressionTextBegin != -1 && assigneeExpressionTextEnd != -1) {
                    assigneeExpressionText = assigneeExpressionText.substring(assigneeExpressionTextBegin + 1, assigneeExpressionTextEnd);
                }
            }
        }

        return assigneeExpressionText;
    }

    /**
     * Query branch information (submit).
     *
     * @param entity
     * @param currentTask
     * @param loginName
     * @return Branch info.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getBranchInfoMap(T entity, Task currentTask, String loginName) {
        //Return Data
        Map<String, Object> branchInfoMap = Maps.newHashMap();

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(currentTask.getProcessInstanceId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current activity
        ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

        //Hand painted containment gateway (start)
        ActivityImpl beginInclusiveGateway = null;

        List<PvmTransition> currentOutgoingTransitions = currentActivity.getOutgoingTransitions();
        for (PvmTransition currentPvmTransition : currentOutgoingTransitions) {
            ActivityImpl destinationActivity0 = (ActivityImpl) currentPvmTransition.getDestination();
            //Containment gateway
            if (destinationActivity0.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                //Hand painted containment gateway (start)
                if (destinationActivity0.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                    beginInclusiveGateway = destinationActivity0;
                    break;
                }
                //Hand drawn containment gateway (closed)
                else if (destinationActivity0.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                    //Non-existent
                }
                //Generated containment gateway (orphaned)
                else {
                    boolean isBreak = false;
                    List<PvmTransition> destination0OutgoingTransitions = destinationActivity0.getOutgoingTransitions();
                    for (PvmTransition destination0PvmTransition : destination0OutgoingTransitions) {
                        ActivityImpl destinationActivity1 = (ActivityImpl) destination0PvmTransition.getDestination();
                        //Hand painted containment gateway (start)
                        if (destinationActivity1.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                            beginInclusiveGateway = destinationActivity1;
                            isBreak = true;
                            break;
                        }
                        //Other nodes
                        else {
                            //Out of scope
                        }
                    }
                    if (isBreak) {
                        break;
                    }
                }
            }
            //Exclusive Gateway
            else if (destinationActivity0.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                boolean isBreak = false;
                List<PvmTransition> destination0OutgoingTransitions = destinationActivity0.getOutgoingTransitions();
                for (PvmTransition destination0PvmTransition : destination0OutgoingTransitions) {
                    ActivityImpl destinationActivity1 = (ActivityImpl) destination0PvmTransition.getDestination();
                    //Hand painted containment gateway (start)
                    if (destinationActivity1.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                        beginInclusiveGateway = destinationActivity1;
                        isBreak = true;
                        break;
                    }
                    //Other nodes
                    else {
                        //Out of scope
                    }
                }
                if (isBreak) {
                    break;
                }
            }
        }

        if (beginInclusiveGateway == null) {
            //Reserve
        } else {
            //The collection of nodes in circulation
            List<ActivityImpl> activityImplList = Lists.newArrayList();

            //Circulation mode
            String mode = "manual";
            Map<String, String> extendMap = entity.getRuleArgs().get(RuleArgs.extend.name());
            if (extendMap != null) {
                mode = extendMap.get("mode");
            }

            //Manual branch
            if ("manual".equals(mode)) {
                Map<String, String> flagMap = Maps.newHashMap();
                String[] flagArray = entity.getAct().getFlag().split(COMMA);
                for (String flag : flagArray) {
                    flagMap.put(flag, flag);
                }

                List<PvmTransition> gatewayOutgoingTransitions = beginInclusiveGateway.getOutgoingTransitions();
                for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                    ActivityImpl destinationActivity0 = (ActivityImpl) gatewayPvmTransition.getDestination();
                    if (destinationActivity0.getActivityBehavior() instanceof MultiInstanceActivityBehavior
                            || destinationActivity0.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                        //Branches of circulation
                        if (flagMap.get(destinationActivity0.getId()) != null) {
                            activityImplList.add(destinationActivity0);
                        }
                        //Non circulating branch
                        else {
                            //Out of scope
                        }
                    }
                }
            }
            //Automatic branch
            else if ("automatic".equals(mode)) {
                Map<String, String> automaticMap = entity.getRuleArgs().get(RuleArgs.automatic.name());

                List<PvmTransition> gatewayOutgoingTransitions = beginInclusiveGateway.getOutgoingTransitions();
                for (PvmTransition gatewayPvmTransition : gatewayOutgoingTransitions) {
                    ActivityImpl destinationActivity0 = (ActivityImpl) gatewayPvmTransition.getDestination();
                    if (destinationActivity0.getActivityBehavior() instanceof MultiInstanceActivityBehavior
                            || destinationActivity0.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                        String conditionText = (String) gatewayPvmTransition.getProperty("conditionText");
                        int conditionTextBegin = conditionText.indexOf("{") + 1;
                        int conditionTextEnd = conditionText.lastIndexOf("}");
                        conditionText = conditionText.substring(conditionTextBegin, conditionTextEnd);
                        String documentation = (String) gatewayPvmTransition.getProperty("documentation");
                        Map<String, String> customCondditionMap = new Gson().fromJson(documentation, Map.class);
                        String variable = customCondditionMap.get("variable");
//						String operator = customCondditionMap.get("operator");
//						String constant = customCondditionMap.get("constant");

                        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
                        engine.put(variable, automaticMap.get(variable));

                        try {
                            if ((boolean) engine.eval(conditionText)) {
                                activityImplList.add(destinationActivity0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (ActivityImpl activityImpl : activityImplList) {
                //Branch information
                Map<String, Object> branchInfo = Maps.newHashMap();

                //Next node handler variable
                String assigneeExpressionText = "";
                if (activityImpl.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityImpl.getActivityBehavior();
                    assigneeExpressionText = multiInstanceActivityBehavior.getCollectionExpression().getExpressionText();
                } else if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    //It's always a node of hand painting, and it's never a custom node?
                    UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityImpl.getActivityBehavior();

                    //Candidate user
                    Set<Expression> candidateUserIdExpressions = userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions();
                    //candidateUserIdExpressions.size() == 1
                    for (Expression expression : candidateUserIdExpressions) {
                        assigneeExpressionText = expression.getExpressionText();
                    }

                    if (StringUtils.isBlank(assigneeExpressionText)) {
                        //Agent user
                        assigneeExpressionText = userTaskActivityBehavior.getTaskDefinition().getAssigneeExpression().getExpressionText();
                    }
                }
                int assigneeExpressionTextBegin = assigneeExpressionText.lastIndexOf("{");
                int assigneeExpressionTextEnd = assigneeExpressionText.lastIndexOf("}");
                if (assigneeExpressionTextBegin != -1 && assigneeExpressionTextEnd != -1) {
                    assigneeExpressionText = assigneeExpressionText.substring(assigneeExpressionTextBegin + 1, assigneeExpressionTextEnd);
                }
                branchInfo.put("assigneeExpressionText", assigneeExpressionText);

                entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());
                entity.getAct().setTaskDefKey(activityImpl.getId());
                List<User> userList = getUserList(entity, loginName);
                List<String> loginNameList = Lists.newArrayList();
                if (userList != null && userList.size() > 0) {
                    if (activityImpl.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                        for (User user : userList) {
                            loginNameList.add(user.getLoginName());
                        }
                    } else if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                        loginNameList.add(userList.get(0).getLoginName());
                    }
                }
                branchInfo.put("loginNameList", loginNameList);

                branchInfoMap.put(activityImpl.getId(), branchInfo);
            }
        }

        return branchInfoMap;
    }

    /**
     * Check whether the data exists during processing
     *
     * @param processInstanceId
     * @param loginName
     * @return Checked message.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> isCacheData(String processInstanceId, String loginName) {
        //Return Data
        Map<String, Object> data = Maps.newHashMap();

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            data.clear();
            data.put("result", true);
            data.put("message", "任务已被撤销");
            data.put("message_en", "Task has been revoked");
        } else {
            Task currentTask = this.getCurrentTask(processInstanceId, loginName);
            if (currentTask == null) {
                data.clear();
                data.put("result", true);
                data.put("message", "任务已过期");
                data.put("message_en", "Task expired");
            } else {
                //Notify node permission
                if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                    TaskPermission taskPermission = new TaskPermission();
                    //TODO：英文提交信息处理
                    String notifyOperation = "提交_saveAndComplete,知会_saveAndNotify,取消_cancel";
                    taskPermission.setOperation(notifyOperation);

                    data.clear();
                    data.put("result", false);
                    data.put("taskPermission", taskPermission);
                }
                //Distribute node permissions
                else if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    TaskPermission taskPermission = new TaskPermission();
                    //TODO：英文提交信息处理
                    String distributeOperation = "提交_saveAndComplete,分发_saveAndDistribute,转发文_saveAndSend,转收文_saveAndReceive,取消_cancel";
                    taskPermission.setOperation(distributeOperation);

                    data.clear();
                    data.put("result", false);
                    data.put("taskPermission", taskPermission);
                }
                //Add node permissions
                else if (currentTask.getTaskDefinitionKey().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                    TaskPermission taskPermission = new TaskPermission();
                    //TODO：英文提交信息处理
                    String distributeOperation = "提交_saveAndComplete,回退_saveAndReject,加签_saveAndCreateNode,减签_saveAndDeleteNode,知会_saveAndNotify,取消_cancel";
                    taskPermission.setOperation(distributeOperation);

                    data.clear();
                    data.put("result", false);
                    data.put("taskPermission", taskPermission);
                }
                //Other node permissions
                else {
                    Act act = new Act();
                    act.setProcDefId(processInstance.getProcessDefinitionId());
                    act.setTaskDefKey(currentTask.getTaskDefinitionKey());
                    TaskSettingVersion taskSettingVersion = taskSettingVersionService.getTaskSettingVersionByAct(act);
                    TaskPermission taskPermission = taskPermissionService.get(taskSettingVersion.getPermission());

                    //Node button name customization
                    if (taskPermission != null && StringUtils.isNotBlank(taskPermission.getRuleArgs())) {
                        List<Map<String, String>> ruleArgsList = new Gson().fromJson(taskPermission.getRuleArgs(), List.class);
                        if (ruleArgsList != null && ruleArgsList.size() > 0) {
                            for (Map<String, String> ruleArgsMap : ruleArgsList) {
                                String key = ruleArgsMap.get(RuleArgs.key.name());
                                String value = ruleArgsMap.get(RuleArgs.value.name());
                                String scope = key.split("_")[0];

                                //Button rule
                                if (RuleArgs.operation.name().equalsIgnoreCase(scope)) {
                                    String operation = key.split("_")[1];
                                    String oldName = value.split("_")[0];
                                    String newName = value.split("_")[1];
                                    String newOperation = taskPermission.getOperation().replaceAll(oldName + "_" + operation, newName + "_" + operation);
                                    taskPermission.setOperation(newOperation);
                                }
                            }
                        }
                    }

                    data.clear();
                    data.put("result", false);
                    data.put("taskPermission", taskPermission);
                }
            }
        }

        return data;
    }

    /**
     * Check whether the data exists when viewing.
     *
     * @param processInstanceId
     * @return Checked message.
     */
    public Map<String, Object> isCacheView(String processInstanceId) {
        //Return Data
        Map<String, Object> data = Maps.newHashMap();

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            data.clear();
            data.put("result", true);
            data.put("message", "任务已被撤销");
            data.put("message_en", "Task has been revoked");
        } else {
            data.clear();
            data.put("result", false);
        }

        return data;
    }

    /**
     * Notify node.
     *
     * @param entity
     * @param loginName
     * @return Node map.
     */
    @Transactional(readOnly = false)
    public LinkedHashMap<String, Object> notifyNode(T entity, String loginName) {
        LinkedHashMap<String, Object> dataMap = createBranch(entity, NODE_MARK_NOTIFY + UNDERLINE, FIXED_VALUE_NOFITY, loginName);
        return dataMap;
    }

    /**
     * Notify node for English.
     *
     * @param entity
     * @param loginName
     * @return Node map.
     */
    @Transactional(readOnly = false)
    public Map<String, Object> notifyNodeEN(T entity, String loginName) {
        Map<String, Object> dataMap = createBranch(entity, NODE_MARK_NOTIFY + UNDERLINE, FIXED_VALUE_NOFITY_EN, loginName);
        return dataMap;
    }

    /**
     * Distribution node.
     *
     * @param entity
     * @param loginName
     * @return Node map.
     */
    @Transactional(readOnly = false)
    public LinkedHashMap<String, Object> distributeNode(T entity, String loginName) {
        LinkedHashMap<String, Object> dataMap = createBranch(entity, NODE_MARK_DISTRIBUTE + UNDERLINE, FIXED_VALUE_DISTRIBUTE, loginName);
        return dataMap;
    }

    /**
     * Distribution node for English.
     *
     * @param entity
     * @param loginName
     * @return Node map.
     */
    @Transactional(readOnly = false)
    public Map<String, Object> distributeNodeEN(T entity, String loginName) {
        Map<String, Object> dataMap = createBranch(entity, NODE_MARK_DISTRIBUTE + UNDERLINE, FIXED_VALUE_DISTRIBUTE_EN, loginName);
        return dataMap;
    }

    /**
     * Increase branch.
     *
     * @param entity
     * @param nodeMark_
     * @param nodeName
     * @param loginName
     * @return Message.
     */
    @Transactional(readOnly = false)
    public LinkedHashMap<String, Object> createBranch(T entity, String nodeMark_, String nodeName, String loginName) {
        User currentUser = UserUtil.getByLoginName(loginName);
        //Return Data
        LinkedHashMap<String, Object> dataMap = Maps.newLinkedHashMap();

        //BranchUserLoginNameList
        List<String> branchUserLoginNameList = Lists.newArrayList();
        //List<User> branchUserList = userService.findUserListByUserIdList(Arrays.asList(entity.getTempLoginName()));
        //for (User user : branchUserList) {
        //branchUserLoginNameList.add(user.getLoginName());
        for (String targetLoginName : entity.getTempLoginName()) {
            branchUserLoginNameList.add(targetLoginName);
        }

        //Execution instance ExecutionList
        final String processInstanceId = entity.getProcInsId();
        List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        if (executionList != null && executionList.size() > 0) {
            final List<ActivityImpl> updateActivityList = Lists.newArrayList();

            //ProcessDefinitionEntity
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            //Current user to do tasks
            Task currentTask = null;
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                    .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();
            if (taskList != null && taskList.size() > 0) {
                for (Task task : taskList) {
                    if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                            && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                        currentTask = task;
                        break;
                    } else if (task.getTaskDefinitionKey().startsWith(nodeMark_)) {
                        currentTask = task;
                    }
                }
            }
            //Current user agent task
            else if (taskList == null || taskList.size() == 0) {
                Date now = new Date();
                String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
                List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(currentUser.getId());
                for (AssigneeSetting assigneeSetting : assigneeList) {
                    boolean isBreak = false;
                    String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                    if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                        taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                                .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                        if (taskList != null && taskList.size() >= 0) {
                            if (isBreak) {
                                break;
                            } else {
                                for (Task task : taskList) {
                                    if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                            && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                        currentTask = task;
                                        isBreak = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Current activity
            ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

            //Execution instance
            Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();

            //Scenario 0: notify nodes to add node and distribute nodes to add node
            if (currentActivity.getId().startsWith(nodeMark_)) {
                //ExecutionParentEntity
                Execution executionParent = runtimeService.createExecutionQuery().executionId(execution.getParentId()).singleResult();
                ExecutionEntity executionParentEntity = (ExecutionEntity) executionParent;

                //ExistUserLoginNameMap
                Map<String, String> existUserLoginNameMap = Maps.newHashMap();
                List<Task> existTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(currentActivity.getId()).active().list();
                for (Task task : existTaskList) {
                    existUserLoginNameMap.put(task.getOwner(), task.getOwner());
                }
                //Add node：Non update user list, only create non repeated user notification sub task or sub task distribution
                //The user list not created by the previous node will not be added to the user list of the original notification or distribution.
                //The user list of the original notification or distribution will be selected or updated by the previous node

                //Add node
                for (String branchUserLoginName : branchUserLoginNameList) {
                    if (StringUtils.isBlank(existUserLoginNameMap.get(branchUserLoginName))) {
                        //NewExecutionEntity
                        final ExecutionEntity newExecutionEntity = new ExecutionEntity();
                        newExecutionEntity.setActive(true);     //activation
                        newExecutionEntity.setConcurrent(true); //branch
                        newExecutionEntity.setScope(false);
                        newExecutionEntity.setParent(executionParentEntity);
                        newExecutionEntity.setParentId(executionParentEntity.getId());
                        newExecutionEntity.setProcessInstance((InterpretableExecution) processInstance);
                        newExecutionEntity.setProcessDefinitionId(processInstance.getProcessDefinitionId());
                        newExecutionEntity.setActivity(currentActivity);

                        managementService.executeCommand(new Command<T>() {
                            @Override
                            public T execute(CommandContext commandContext) {
                                commandContext.getDbSqlSession().insert(newExecutionEntity);
                                return null;
                            }
                        });

                        //NewTaskEntity
                        TaskEntity newTaskEntity = new TaskEntity();
                        final String newTaskEntityId = UUID.randomUUID().toString();
                        newTaskEntity.setId(newTaskEntityId);
                        newTaskEntity.setName(currentTask.getName());
                        newTaskEntity.setCreateTime(new Date());
                        newTaskEntity.setOwner(branchUserLoginName);
                        newTaskEntity.setAssignee(branchUserLoginName);
                        newTaskEntity.setExecution(newExecutionEntity);
                        newTaskEntity.setExecutionId(newExecutionEntity.getId());
                        newTaskEntity.setTaskDefinitionKey(currentTask.getTaskDefinitionKey());
                        newTaskEntity.setProcessDefinitionId(currentTask.getProcessDefinitionId());
                        newTaskEntity.setProcessInstanceId(currentTask.getProcessInstanceId());
                        taskService.saveTask(newTaskEntity);
                        taskSettingVersionService.updateHistoricTask(newTaskEntity);

                        //NrOfInstances + 1, NrOfActiveInstances + 1 Is it necessary?
                        Object nrOfInstancesObejct = runtimeService.getVariableLocal(executionParentEntity.getId(), "nrOfInstances");
                        Integer nrOfInstances = nrOfInstancesObejct == null ? 0 : (Integer) nrOfInstancesObejct;
                        runtimeService.setVariableLocal(executionParentEntity.getId(), "nrOfInstances", nrOfInstances + 1);
                        Object nrOfActiveInstancesObejct = runtimeService.getVariableLocal(executionParentEntity.getId(), "nrOfActiveInstances");
                        Integer nrOfActiveInstances = nrOfActiveInstancesObejct == null ? 0 : (Integer) nrOfActiveInstancesObejct;
                        runtimeService.setVariableLocal(executionParentEntity.getId(), "nrOfActiveInstances", nrOfActiveInstances + 1);
                    } else {
                        //Users have been informed or distributed
                    }
                }
            } else {
                //DestinationActivity0
                List<PvmTransition> currentActivityOutgoingTransitions = currentActivity.getOutgoingTransitions();
                ActivityImpl destinationActivity0 = (ActivityImpl) currentActivityOutgoingTransitions.get(0).getDestination();

                //Scenario 1: update or create a notification node | update or create a distribution node
                if (destinationActivity0.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior
                        && false == destinationActivity0.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)
                        && false == destinationActivity0.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                    ActivityImpl inclusiveGatewayActivity = destinationActivity0;
                    boolean startWithNodeMark = false;

                    //Scenario 1.1: update notification node | update distribution node
                    List<PvmTransition> inclusiveGatewayOutgoingTransitions = inclusiveGatewayActivity.getOutgoingTransitions();
                    for (PvmTransition inclusiveGatewayPvmTransition : inclusiveGatewayOutgoingTransitions) {
                        ActivityImpl destinationActivityI = (ActivityImpl) inclusiveGatewayPvmTransition.getDestination();
                        if (destinationActivityI.getId().startsWith(nodeMark_)) {
                            startWithNodeMark = true;

                            MultiInstanceActivityBehavior multiInstanceActivitiBehavior = (MultiInstanceActivityBehavior) destinationActivityI.getActivityBehavior();
                            //CollectionExpression: The last notification task or distribution task that the informed user or distribution user did not complete should not be repeated this time
                            //CollectionExpression: The last notification task or distribution task not completed by the notified user or distribution user has nothing to do with this notification task or distribution task (right)
                            //Principle: update the user list of the notification node or distribution node, not related to "whether there are unfinished notification tasks or distribution tasks"
                            //That is to say, whether the users of this notification or the users of distribution have or have not completed the notification task or distribution task,
                            // they will create a new notification task or distribution task, and retain the original notification task or distribution task (if any)
                            multiInstanceActivitiBehavior.setCollectionExpression(new FixedValue(branchUserLoginNameList));
                            updateActivityList.add(destinationActivityI);

                            //Update Activity
                            final ExecutionEntity finalExecutionEntity = (ExecutionEntity) execution;
                            ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<T>() {
                                @Override
                                public T execute(CommandContext commandContext) {
                                    try {
                                        for (ActivityImpl activityImpl : updateActivityList) {
                                            finalExecutionEntity.setActivity(activityImpl);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            });
                        }
                    }

                    //Scenario 1.2: create a notification node and create a distribution node
                    if (false == startWithNodeMark) {
                        //NewUserTaskActivity
                        String newParallelUserTaskActivityId = nodeMark_ + UUID.randomUUID().toString();
                        ActivityImpl newParallelUserTaskActivity = processDefinitionEntity.createActivity(newParallelUserTaskActivityId);
                        TaskDefinition newUserTaskDefinition = new TaskDefinition(new DefaultTaskFormHandler());
                        newUserTaskDefinition.setKey(newParallelUserTaskActivityId);
                        newUserTaskDefinition.setNameExpression(new FixedValue(nodeName));

                        UserTaskActivityBehavior userTaskActivityBehavior = new UserTaskActivityBehavior(newParallelUserTaskActivityId, newUserTaskDefinition);
                        ParallelMultiInstanceBehavior parallelMultiInstanceBehavior = new ParallelMultiInstanceBehavior(newParallelUserTaskActivity, userTaskActivityBehavior);
                        parallelMultiInstanceBehavior.setCollectionExpression(new FixedValue(branchUserLoginNameList));
                        parallelMultiInstanceBehavior.setCollectionElementVariable(UUID.randomUUID().toString());
                        newParallelUserTaskActivity.setScope(true);
                        newParallelUserTaskActivity.setActivityBehavior(parallelMultiInstanceBehavior);

                        //Redirect InclusiveGatewayActivity --> NewUserTaskActivity
                        Map<String, Map<String, String>> inclusiveGateway2UserTaskMap = Maps.newHashMap();
                        String newInclusiveGatewaySequenceFlowId = UUID.randomUUID().toString();
                        TransitionImpl newInclusiveGatewayOutgoingTransition = inclusiveGatewayActivity.createOutgoingTransition(newInclusiveGatewaySequenceFlowId);
                        newInclusiveGatewayOutgoingTransition.setSkipExpression(new FixedValue("1==1"));
                        newInclusiveGatewayOutgoingTransition.setDestination(newParallelUserTaskActivity);

                        //SequenceFlow Data
                        Map<String, String> map = Maps.newHashMap();
                        map.put(SEQUENCE_FLOW_SOURCE, inclusiveGatewayActivity.getId());
                        map.put(SEQUENCE_FLOW_TARGET, newParallelUserTaskActivity.getId());
                        inclusiveGateway2UserTaskMap.put(newInclusiveGatewaySequenceFlowId, map);

                        updateActivityList.add(inclusiveGatewayActivity);
                        updateActivityList.add(newParallelUserTaskActivity);

                        //Update Activity
                        final ExecutionEntity finalExecutionEntity = (ExecutionEntity) execution;
                        ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<T>() {
                            @Override
                            public T execute(CommandContext commandContext) {
                                try {
                                    for (ActivityImpl activityImpl : updateActivityList) {
                                        finalExecutionEntity.setActivity(activityImpl);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        });

                        //BpmnModel
                        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
                        Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
                        Map<String, FlowElement> flowElementsMap = Maps.newHashMap();
                        for (FlowElement flowElement : flowElements) {
                            flowElementsMap.put(flowElement.getId(), flowElement);
                        }

                        //UserTask
                        UserTask userTask = new UserTask();
                        userTask.setId(newParallelUserTaskActivityId);
                        userTask.setName(nodeName);
                        flowElements.add(userTask);

                        //SequenceFlow
                        for (Map.Entry<String, Map<String, String>> entry : inclusiveGateway2UserTaskMap.entrySet()) {
                            String sequenceFlowId = entry.getKey();
                            Map<String, String> m = entry.getValue();

                            SequenceFlow sequenceFlow = new SequenceFlow();
                            sequenceFlow.setId(sequenceFlowId);
                            sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                            sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));
                            flowElements.add(sequenceFlow);

                            //InclusiveGateway
                            InclusiveGateway inclusiveGateway = (InclusiveGateway) flowElementsMap.get(m.get(SEQUENCE_FLOW_SOURCE));

                            //InclusiveGateway Outgoing
                            inclusiveGateway.getOutgoingFlows().add(sequenceFlow);

                            //UserTask Incoming
                            userTask.getIncomingFlows().add(sequenceFlow);
                        }

                        //BpmnAutoLayout
                        new BpmnAutoLayout(bpmnModel).execute();

                        //Update BpmnModel
                        try {
                            taskSettingVersionService.updateActByte(new BpmnXMLConverter().convertToXML(bpmnModel), processInstance.getDeploymentId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //Scenario 2: create containment gateway and notification node | create containment gateway and distribution node
                else {
                    //NewInclusiveGatewayActivity
                    String newInclusiveGatewayActivityId = UUID.randomUUID().toString();
                    ActivityImpl newInclusiveGatewayActivity = processDefinitionEntity.createActivity(newInclusiveGatewayActivityId);
                    newInclusiveGatewayActivity.setExclusive(false);
                    newInclusiveGatewayActivity.setActivityBehavior(new InclusiveGatewayActivityBehavior());

                    //Redirect CurrentActivity --> NewInclusiveGatewayActivity --> DestinationActivity
                    Map<String, Map<String, String>> current2InclusiveGatewayMap = Maps.newHashMap();
                    Map<String, Map<String, String>> inclusiveGateway2DestinationMap = Maps.newHashMap();
                    for (PvmTransition currentPvmTransition : currentActivityOutgoingTransitions) {
                        //DestinationActivity
                        ActivityImpl destinationActivity = (ActivityImpl) currentPvmTransition.getDestination();
                        destinationActivity.getIncomingTransitions().remove(currentPvmTransition);

                        String newInclusiveGatewaySequenceFlowId = UUID.randomUUID().toString();
                        TransitionImpl newInclusiveGatewayOutgoingTransition = newInclusiveGatewayActivity.createOutgoingTransition(newInclusiveGatewaySequenceFlowId);
                        newInclusiveGatewayOutgoingTransition.setSkipExpression(new FixedValue("1==1"));//new FixedValue(true)
                        newInclusiveGatewayOutgoingTransition.setDestination(destinationActivity);

                        TransitionImpl currentTransitionImpl = (TransitionImpl) currentPvmTransition;
                        currentTransitionImpl.setDestination(newInclusiveGatewayActivity);

                        //SequenceFlow Data
                        Map<String, String> map1 = Maps.newHashMap();
                        map1.put(SEQUENCE_FLOW_SOURCE, currentActivity.getId());
                        map1.put(SEQUENCE_FLOW_TARGET, newInclusiveGatewayActivity.getId());
                        current2InclusiveGatewayMap.put(currentPvmTransition.getId(), map1);

                        Map<String, String> map2 = Maps.newHashMap();
                        map2.put(SEQUENCE_FLOW_SOURCE, newInclusiveGatewayActivity.getId());
                        map2.put(SEQUENCE_FLOW_TARGET, destinationActivity.getId());
                        inclusiveGateway2DestinationMap.put(newInclusiveGatewaySequenceFlowId, map2);

                        updateActivityList.add(destinationActivity);
                    }
                    updateActivityList.add(currentActivity);

                    //NewUserTaskActivity
                    String newParallelUserTaskActivityId = nodeMark_ + UUID.randomUUID().toString();
                    ActivityImpl newParallelUserTaskActivity = processDefinitionEntity.createActivity(newParallelUserTaskActivityId);
                    TaskDefinition newUserTaskDefinition = new TaskDefinition(new DefaultTaskFormHandler());
                    newUserTaskDefinition.setKey(newParallelUserTaskActivityId);
                    newUserTaskDefinition.setNameExpression(new FixedValue(nodeName));

                    UserTaskActivityBehavior userTaskActivityBehavior = new UserTaskActivityBehavior(newParallelUserTaskActivityId, newUserTaskDefinition);
                    ParallelMultiInstanceBehavior parallelMultiInstanceBehavior = new ParallelMultiInstanceBehavior(newParallelUserTaskActivity, userTaskActivityBehavior);
                    parallelMultiInstanceBehavior.setCollectionExpression(new FixedValue(branchUserLoginNameList));
                    parallelMultiInstanceBehavior.setCollectionElementVariable(UUID.randomUUID().toString());
                    newParallelUserTaskActivity.setScope(true);
                    newParallelUserTaskActivity.setActivityBehavior(parallelMultiInstanceBehavior);

                    //Redirect NewInclusiveGatewayActivity --> NewUserTaskActivity
                    Map<String, Map<String, String>> inclusiveGateway2UserTaskMap = Maps.newHashMap();
                    String newInclusiveGatewaySequenceFlowId = UUID.randomUUID().toString();
                    TransitionImpl newInclusiveGatewayOutgoingTransition = newInclusiveGatewayActivity.createOutgoingTransition(newInclusiveGatewaySequenceFlowId);
                    newInclusiveGatewayOutgoingTransition.setSkipExpression(new FixedValue("1==1"));
                    newInclusiveGatewayOutgoingTransition.setDestination(newParallelUserTaskActivity);

                    //SequenceFlow Data
                    Map<String, String> map = Maps.newHashMap();
                    map.put(SEQUENCE_FLOW_SOURCE, newInclusiveGatewayActivity.getId());
                    map.put(SEQUENCE_FLOW_TARGET, newParallelUserTaskActivity.getId());
                    inclusiveGateway2UserTaskMap.put(newInclusiveGatewaySequenceFlowId, map);

                    updateActivityList.add(newInclusiveGatewayActivity);
                    updateActivityList.add(newParallelUserTaskActivity);

                    //Update Activity
                    final ExecutionEntity finalExecutionEntity = (ExecutionEntity) execution;
                    ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<T>() {
                        @Override
                        public T execute(CommandContext commandContext) {
                            try {
                                for (ActivityImpl activityImpl : updateActivityList) {
                                    finalExecutionEntity.setActivity(activityImpl);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });

                    //BpmnModel
                    BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
                    Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
                    Map<String, FlowElement> flowElementsMap = Maps.newHashMap();
                    for (FlowElement flowElement : flowElements) {
                        flowElementsMap.put(flowElement.getId(), flowElement);
                    }

                    //InclusiveGateway
                    InclusiveGateway inclusiveGateway = new InclusiveGateway();
                    inclusiveGateway.setId(newInclusiveGatewayActivityId);
                    flowElements.add(inclusiveGateway);

                    //UserTask
                    UserTask userTask = new UserTask();
                    userTask.setId(newParallelUserTaskActivityId);
                    userTask.setName(nodeName);
                    flowElements.add(userTask);

                    //SequenceFlow
                    for (Map.Entry<String, Map<String, String>> entry : current2InclusiveGatewayMap.entrySet()) {
                        String sequenceFlowId = entry.getKey();
                        Map<String, String> m = entry.getValue();

                        FlowElement flowElement = flowElementsMap.get(sequenceFlowId);
                        if (flowElement != null && flowElement instanceof SequenceFlow) {
                            SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                            sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                            sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));

                            //InclusiveGateway Incoming
                            inclusiveGateway.getIncomingFlows().add(sequenceFlow);
                        }
                    }

                    //SequenceFlow
                    for (Map.Entry<String, Map<String, String>> entry : inclusiveGateway2DestinationMap.entrySet()) {
                        String sequenceFlowId = entry.getKey();
                        Map<String, String> m = entry.getValue();

                        SequenceFlow sequenceFlow = new SequenceFlow();
                        sequenceFlow.setId(sequenceFlowId);
                        sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                        sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));
                        flowElements.add(sequenceFlow);

                        //InclusiveGateway Outgoing
                        inclusiveGateway.getOutgoingFlows().add(sequenceFlow);

                        //Target Incoming
                        FlowElement targetFlowElement = flowElementsMap.get(m.get(SEQUENCE_FLOW_TARGET));
                        FlowNode targetFlowNode = (FlowNode) targetFlowElement;

                        List<SequenceFlow> targetFlowNodeIncomingFlows = targetFlowNode.getIncomingFlows();
                        List<SequenceFlow> inclusiveGatewayIncomingFlows = inclusiveGateway.getIncomingFlows();

                        List<SequenceFlow> targetFlowNodeIncomingFlowsRemoveList = Lists.newArrayList();
                        for (SequenceFlow targetSequenceFlow : targetFlowNodeIncomingFlows) {
                            for (SequenceFlow gatewaySequenceFlow : inclusiveGatewayIncomingFlows) {
                                if (targetSequenceFlow.getId().equals(gatewaySequenceFlow.getId())) {
                                    targetFlowNodeIncomingFlowsRemoveList.add(targetSequenceFlow);
                                }
                            }
                        }
                        for (SequenceFlow removeSequenceFlow : targetFlowNodeIncomingFlowsRemoveList) {
                            targetFlowNode.getIncomingFlows().remove(removeSequenceFlow);
                        }
                        targetFlowNode.getIncomingFlows().add(sequenceFlow);
                    }

                    //SequenceFlow
                    for (Map.Entry<String, Map<String, String>> entry : inclusiveGateway2UserTaskMap.entrySet()) {
                        String sequenceFlowId = entry.getKey();
                        Map<String, String> m = entry.getValue();

                        SequenceFlow sequenceFlow = new SequenceFlow();
                        sequenceFlow.setId(sequenceFlowId);
                        sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                        sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));
                        flowElements.add(sequenceFlow);

                        //InclusiveGateway Outgoing
                        inclusiveGateway.getOutgoingFlows().add(sequenceFlow);

                        //UserTask Incoming
                        userTask.getIncomingFlows().add(sequenceFlow);
                    }

                    //BpmnAutoLayout
                    new BpmnAutoLayout(bpmnModel).execute();

                    //Update BpmnModel
                    try {
                        taskSettingVersionService.updateActByte(new BpmnXMLConverter().convertToXML(bpmnModel), processInstance.getDeploymentId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            dataMap.put("success", true);
            dataMap.put("message", nodeName + "操作成功");
            dataMap.put("message_en", nodeName + "Operation success");
        } else {
            /* 流程结束（executionList == null || executionList.size() == 0） */
            dataMap.put("success", true);
            dataMap.put("message", nodeName + "操作失败");
            dataMap.put("message_en", nodeName + "Operation failed");
        }

        return dataMap;
    }

    /**
     * Add node.
     *
     * @param entity
     * @param loginName
     * @return Result message.
     */
    @Transactional(readOnly = false)
    public LinkedHashMap<String, Object> createNode(T entity, String loginName) {
        //Return Data
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();

        //Execution instance ExecutionList
        String processInstanceId = entity.getProcInsId();
        List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        if (executionList != null && executionList.size() > 0) {
            final List<ActivityImpl> updateActivityList = Lists.newArrayList();

            //ProcessDefinitionEntity
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            //Current user to do tasks
            String currentTaskDefinitionKey = "";
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                    .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

            //Current user agent tasks
            if (taskList == null || taskList.size() == 0) {
                if (taskList == null || taskList.size() == 0) {
                    Date now = new Date();
                    String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
                    List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
                    for (AssigneeSetting assigneeSetting : assigneeList) {
                        boolean isBreak = false;
                        String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                        if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                            taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                                    .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                            if (taskList != null && taskList.size() >= 0) {
                                if (isBreak) {
                                    break;
                                } else {
                                    for (Task task : taskList) {
                                        if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                                && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                            isBreak = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (Task task : taskList) {
                String taskDefinitionKey = task.getTaskDefinitionKey();
                if (false == taskDefinitionKey.startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    currentTaskDefinitionKey = taskDefinitionKey;
                    break;
                }
            }
            Execution execution = null;
            for (Execution executionI : executionList) {
                if (StringUtils.isNotBlank(executionI.getActivityId()) && executionI.getActivityId().equals(currentTaskDefinitionKey)) {
                    execution = executionI;
                    break;
                }
            }

            //CurrentActivity
            String currentActivityId = execution.getActivityId();
            ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentActivityId);

            //NewUserTaskActivity
            String newUserTaskActivityId = NODE_MARK_CREATE + UNDERLINE + UUID.randomUUID().toString();
            ActivityImpl newUserTaskActivity = processDefinitionEntity.createActivity(newUserTaskActivityId);
            TaskDefinition newUserTaskDefinition = new TaskDefinition(new DefaultTaskFormHandler());
            newUserTaskDefinition.setKey(newUserTaskActivityId);
            newUserTaskDefinition.setNameExpression(new FixedValue(entity.getCustomNodeName()));

            //Added node: setting user
            Set<Expression> candidateUserIdExpressionSet = Sets.newHashSet();
            candidateUserIdExpressionSet.add(new FixedValue(entity.getTempLoginName()[0]));
            newUserTaskDefinition.setCandidateUserIdExpressions(candidateUserIdExpressionSet);

            //Added node: store user
            runtimeService.setVariableLocal(entity.getProcInsId(), newUserTaskActivityId, entity.getTempLoginName()[0]);

            UserTaskActivityBehavior userTaskActivityBehavior = new UserTaskActivityBehavior(newUserTaskActivityId, newUserTaskDefinition);
            newUserTaskActivity.setActivityBehavior(userTaskActivityBehavior);

            //Redirect CurrentActivity --> NewUserTaskActivity --> DestinationActivity
            Map<String, Map<String, String>> current2NewUserTaskMap = Maps.newHashMap();
            Map<String, Map<String, String>> newUserTask2DestinationMap = Maps.newHashMap();
            List<PvmTransition> currentOutgoingTransitions = currentActivity.getOutgoingTransitions();
            for (PvmTransition currentPvmTransition : currentOutgoingTransitions) {
                //DestinationActivity
                ActivityImpl destinationActivity = (ActivityImpl) currentPvmTransition.getDestination();
                destinationActivity.getIncomingTransitions().remove(currentPvmTransition);

                String newUserTaskSequenceFlowId = UUID.randomUUID().toString();
                TransitionImpl newUserTaskOutgoingTransition = newUserTaskActivity.createOutgoingTransition(newUserTaskSequenceFlowId);
                newUserTaskOutgoingTransition.setDestination(destinationActivity);

                TransitionImpl currentTransitionImpl = (TransitionImpl) currentPvmTransition;
                currentTransitionImpl.setDestination(newUserTaskActivity);

                //SequenceFlow Data
                Map<String, String> map1 = Maps.newHashMap();
                map1.put(SEQUENCE_FLOW_SOURCE, currentActivity.getId());
                map1.put(SEQUENCE_FLOW_TARGET, newUserTaskActivity.getId());
                current2NewUserTaskMap.put(currentPvmTransition.getId(), map1);

                Map<String, String> map2 = Maps.newHashMap();
                map2.put(SEQUENCE_FLOW_SOURCE, newUserTaskActivity.getId());
                map2.put(SEQUENCE_FLOW_TARGET, destinationActivity.getId());
                newUserTask2DestinationMap.put(newUserTaskSequenceFlowId, map2);

                updateActivityList.add(destinationActivity);
            }
            updateActivityList.add(currentActivity);
            updateActivityList.add(newUserTaskActivity);

            //Update Activity
            final ExecutionEntity executionEntity = (ExecutionEntity) execution;
            ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<T>() {
                @Override
                public T execute(CommandContext commandContext) {
                    try {
                        for (ActivityImpl activityImpl : updateActivityList) {
                            executionEntity.setActivity(activityImpl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            //BpmnModel
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
            Map<String, FlowElement> flowElementsMap = Maps.newHashMap();
            for (FlowElement flowElement : flowElements) {
                flowElementsMap.put(flowElement.getId(), flowElement);
            }

            //UserTask
            UserTask userTask = new UserTask();
            userTask.setId(newUserTaskActivityId);
            userTask.setName(entity.getCustomNodeName());
            flowElements.add(userTask);

            //SequenceFlow
            for (Map.Entry<String, Map<String, String>> entry : current2NewUserTaskMap.entrySet()) {
                String sequenceFlowId = entry.getKey();
                Map<String, String> m = entry.getValue();

                FlowElement flowElement = flowElementsMap.get(sequenceFlowId);
                if (flowElement != null && flowElement instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                    sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));

                    //UserTask Incoming
                    userTask.getIncomingFlows().add(sequenceFlow);
                }
            }

            //SequenceFlow
            for (Map.Entry<String, Map<String, String>> entry : newUserTask2DestinationMap.entrySet()) {
                String sequenceFlowId = entry.getKey();
                Map<String, String> m = entry.getValue();

                SequenceFlow sequenceFlow = new SequenceFlow();
                sequenceFlow.setId(sequenceFlowId);
                sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));
                flowElements.add(sequenceFlow);

                //UserTask Outgoing
                userTask.getOutgoingFlows().add(sequenceFlow);

                //Target Incoming
                FlowElement targetFlowElement = flowElementsMap.get(m.get(SEQUENCE_FLOW_TARGET));
                FlowNode targetFlowNode = (FlowNode) targetFlowElement;

                List<SequenceFlow> targetFlowNodeIncomingFlows = targetFlowNode.getIncomingFlows();
                List<SequenceFlow> userTaskIncomingFlows = userTask.getIncomingFlows();

                List<SequenceFlow> targetFlowNodeIncomingFlowsRemoveList = Lists.newArrayList();
                for (SequenceFlow targetSequenceFlow : targetFlowNodeIncomingFlows) {
                    for (SequenceFlow userTaskSequenceFlow : userTaskIncomingFlows) {
                        if (targetSequenceFlow.getId().equals(userTaskSequenceFlow.getId())) {
                            targetFlowNodeIncomingFlowsRemoveList.add(targetSequenceFlow);
                        }
                    }
                }
                for (SequenceFlow removeSequenceFlow : targetFlowNodeIncomingFlowsRemoveList) {
                    targetFlowNode.getIncomingFlows().remove(removeSequenceFlow);
                }
                targetFlowNode.getIncomingFlows().add(sequenceFlow);
            }

            //BpmnAutoLayout
            new BpmnAutoLayout(bpmnModel).execute();

            //Update BpmnModel
            try {
                taskSettingVersionService.updateActByte(new BpmnXMLConverter().convertToXML(bpmnModel), processInstance.getDeploymentId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Return Data
            map.put(CREATE_NODE_TASKID, newUserTaskActivityId);
            map.put(CREATE_NODE_ASSIGNEE, entity.getTempLoginName()[0]);
        } else {
            //End of process（executionList == null || executionList.size() == 0）
            //TODO
        }
        //return new Gson().toJson(map);
        return map;
    }

    /**
     * Reduce node.
     *
     * @param entity
     * @param loginName
     * @return Result message.
     */
    @Transactional(readOnly = false)
    public LinkedHashMap<String, Object> deleteNode(T entity, String loginName) {
        //Return Data
        LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        //Whether the next node can be deleted
        boolean isDeletable = isDeletable(entity, loginName);
        if (isDeletable) {
            //ProcessDefinitionEntity
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            //Current user to do tasks
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                    .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

            //Current user agent tasks
            if (taskList == null || taskList.size() == 0) {
                Date now = new Date();
                String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
                List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
                for (AssigneeSetting assigneeSetting : assigneeList) {
                    boolean isBreak = false;
                    String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                    if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                        taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                                .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                        if (taskList != null && taskList.size() >= 0) {
                            if (isBreak) {
                                break;
                            } else {
                                for (Task task : taskList) {
                                    if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                            && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                        isBreak = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Current task
            Task currentTask = null;
            for (Task task : taskList) {
                //Notify node
                if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

                }
                //Distribution node
                else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

                }
                //Other nodes
                else {
                    currentTask = task;
                    break;
                }
            }

            //Current activity
            final ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

            //Next node
            final ActivityImpl destinationActivity0 = (ActivityImpl) currentActivity.getOutgoingTransitions().get(0).getDestination();

            //Next next node
            final ActivityImpl destinationActivity1 = (ActivityImpl) destinationActivity0.getOutgoingTransitions().get(0).getDestination();

            //Redirect CurrentActivity --> DestinationActivity1
            TransitionImpl currentTransitionImpl = (TransitionImpl) currentActivity.getOutgoingTransitions().get(0);
            currentTransitionImpl.setDestination(destinationActivity1);
            destinationActivity1.getIncomingTransitions().clear();
            destinationActivity1.getIncomingTransitions().add(currentTransitionImpl);

            Map<String, Map<String, String>> currentActivity2DestinationActivity1Map = Maps.newHashMap();
            Map<String, String> map1 = Maps.newHashMap();
            map1.put(SEQUENCE_FLOW_SOURCE, currentTransitionImpl.getSource().getId());
            map1.put(SEQUENCE_FLOW_TARGET, currentTransitionImpl.getDestination().getId());
            currentActivity2DestinationActivity1Map.put(currentTransitionImpl.getId(), map1);

            String deleteElementId = destinationActivity0.getId();
            String deleteFlowElementId = destinationActivity0.getOutgoingTransitions().get(0).getId();

            destinationActivity0.getOutgoingTransitions().clear();
            destinationActivity0.getIncomingTransitions().clear();

            //Reduce node
            final TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).taskId(destinationActivity0.getId()).singleResult();
            taskService.deleteTask(destinationActivity0.getId());//TODO To be tested

            //ExecutionEntity
            Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
            final ExecutionEntity executionEntity = (ExecutionEntity) execution;

            //Update Activity
            ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<T>() {
                @Override
                public T execute(CommandContext commandContext) {
                    try {
                        executionEntity.setActivity(currentActivity);
                        executionEntity.setActivity(destinationActivity1);
                        executionEntity.removeTask(taskEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            //BpmnModel
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
            Map<String, List<GraphicInfo>> flowLocationMap = bpmnModel.getFlowLocationMap();
            Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
            Map<String, FlowElement> flowElementsMap = Maps.newHashMap();
            for (FlowElement flowElement : flowElements) {
                flowElementsMap.put(flowElement.getId(), flowElement);
            }

            //SequenceFlow
            for (Map.Entry<String, Map<String, String>> entry : currentActivity2DestinationActivity1Map.entrySet()) {
                String sequenceFlowId = entry.getKey();
                Map<String, String> m = entry.getValue();

                FlowElement flowElement = flowElementsMap.get(sequenceFlowId);
                if (flowElement != null && flowElement instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    sequenceFlow.setSourceRef(m.get(SEQUENCE_FLOW_SOURCE));
                    sequenceFlow.setTargetRef(m.get(SEQUENCE_FLOW_TARGET));

                    //Target Incoming
                    FlowElement targetFlowElement = flowElementsMap.get(m.get(SEQUENCE_FLOW_TARGET));
                    FlowNode targetFlowNode = (FlowNode) targetFlowElement;
                    List<SequenceFlow> targetFlowNodeIncomingFlows = targetFlowNode.getIncomingFlows();
                    for (SequenceFlow targetSequenceFlow : targetFlowNodeIncomingFlows) {
                        if (deleteFlowElementId.equals(targetSequenceFlow.getId())) {
                            targetSequenceFlow = sequenceFlow;
                        }
                    }
                }
            }

            //Remove graphic | remove line segment
            FlowElement deleteElement = flowElementsMap.get(deleteElementId);
            FlowElement deleteFlowElement = flowElementsMap.get(deleteFlowElementId);
            flowElements.remove(deleteElement);
            flowElements.remove(deleteFlowElement);
            locationMap.remove(deleteElementId);
            flowLocationMap.remove(deleteFlowElementId);

            //BpmnAutoLayout
            new BpmnAutoLayout(bpmnModel).execute();

            //Update BpmnModel
            try {
                taskSettingVersionService.updateActByte(new BpmnXMLConverter().convertToXML(bpmnModel), processInstance.getDeploymentId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Return Data
            data.put("success", true);
            data.put("message", "减少节点成功");
            data.put("message_en", "Node reduction succeeded");
        } else {
            //Return Data
            data.put("success", false);
            data.put("message", "减少节点失败");
            data.put("message_en", "Node reduction failed");
        }
        return data;
    }

    /**
     * Check whether the node could be reduced.
     *
     * @param entity
     * @param loginName
     * @return True of false.
     */
    private boolean isDeletable(T entity, String loginName) {
        //Return Data
        boolean deletable = true;

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

        //Current user agent tasks
        if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                boolean isBreak = false;
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    if (taskList != null && taskList.size() >= 0) {
                        if (isBreak) {
                            break;
                        } else {
                            for (Task task : taskList) {
                                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                    isBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Current task
        Task currentTask = null;
        for (Task task : taskList) {
            //Notify node
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                currentTask = task;
                break;
            }
        }

        //Current activity
        ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());
        List<PvmTransition> currentOutgoingTransitions = currentActivity.getOutgoingTransitions();
        for (PvmTransition currentPvmTransition : currentOutgoingTransitions) {
            ActivityImpl destinationActivity0 = (ActivityImpl) currentPvmTransition.getDestination();
            //Next node: end node
            if (TASK_DEFINITION_KEY_END.equals(destinationActivity0.getId())) {
                deletable = false;
                break;
            }
            //Other nodes
            else {
                if (false == destinationActivity0.getActivityBehavior() instanceof UserTaskActivityBehavior
                        && false == destinationActivity0.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    deletable = false;
                    break;
                }
            }
        }

        //Branch confluence node
        if (deletable) {
            Map<String, String> map = Maps.newHashMap();
            for (PvmTransition currentPvmTransition : currentOutgoingTransitions) {
                String destinationActivityId = currentPvmTransition.getDestination().getId();//Next node ID
                String currentPvmTransitionId = currentPvmTransition.getId();//Current export ID
                map.put(destinationActivityId, currentPvmTransitionId);
            }
            List<ActivityImpl> activities = processDefinitionEntity.getActivities();
            for (ActivityImpl activityImpl : activities) {
                List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
                for (PvmTransition pvmTransition : outgoingTransitions) {
                    String did = pvmTransition.getDestination().getId();
                    String pid = pvmTransition.getId();
                    if (map.get(did) != null && false == map.get(did).equals(pid)) {
                        deletable = false;
                        break;
                    }
                }
            }

        }

        return deletable;
    }

    /**
     * Check whether the node could be fallbacked.
     *
     * @param processInstanceId
     * @param loginName
     * @return Result message.
     */
    public LinkedHashMap<String, Object> rollBackCheck(String processInstanceId, String loginName) {
        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

        //Current user agent tasks
        if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                boolean isBreak = false;
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    if (taskList != null && taskList.size() >= 0) {
                        if (isBreak) {
                            break;
                        } else {
                            for (Task task : taskList) {
                                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                    isBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Current task
        Task currentTask = null;
        for (Task task : taskList) {
            //Notify node
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                currentTask = task;
                break;
            }
        }
        taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();

        //Current activity
        ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

        LinkedHashMap<String, Object> dataMap = Maps.newLinkedHashMap();
        Map<String, Object> sourceActivityInfoMap = this.getSourceActivityInfoMap(currentActivity, currentTask.getProcessInstanceId(), currentTask.getId());
        if (sourceActivityInfoMap.get("success") != null && (boolean) sourceActivityInfoMap.get("success")) {
            dataMap.put("success", true);
        } else {
            dataMap.put("success", false);
            dataMap.put("message", "不允许回退");
            dataMap.put("message_en", "Fallback not allowed");
        }

        return dataMap;
    }

    /**
     * Query the information collection of the last node (the last executed node that meets the conditions).
     *
     * @param currentActivity
     * @param processInstanceId
     * @param taskId
     * @return Result message.
     */
    private Map<String, Object> getSourceActivityInfoMap(ActivityImpl currentActivity, String processInstanceId, String taskId) {
        //Return Data
        Map<String, Object> sourceActivityInfoMap = Maps.newHashMap();

        List<PvmTransition> currentIncomingTransitions = currentActivity.getIncomingTransitions();
        if (currentIncomingTransitions != null) {
            if (currentIncomingTransitions.size() > 0) {
                for (PvmTransition currentPvmTransition : currentIncomingTransitions) {
                    ActivityImpl source0Activity = (ActivityImpl) currentPvmTransition.getSource();
                    //Previous node: exclusive gateway
                    if (source0Activity.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                        List<PvmTransition> source0IncomingTransitions = source0Activity.getIncomingTransitions();
                        for (PvmTransition source0pvmTransition : source0IncomingTransitions) {
                            ActivityImpl source1Activity = (ActivityImpl) source0pvmTransition.getSource();
                            //Previous previous node: containment gateway (generated)
                            if (source1Activity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior
                                    && false == source1Activity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)
                                    && false == source1Activity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                                List<PvmTransition> source1IncomingTransitions = source1Activity.getIncomingTransitions();
                                ActivityImpl source2Activity = (ActivityImpl) source1IncomingTransitions.get(0).getSource();
                                //Previous previous previous node: user task (single person)
                                if (source2Activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                                    sourceActivityInfoMap.put(source2Activity.getId(), source2Activity);
                                }
                                //Previous previous previous node: user tasks (multiple)
                                else if (source2Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                                    sourceActivityInfoMap.put(source2Activity.getId(), source2Activity);
                                }
                                //Previous previous previous node: other nodes
                                else {
                                    //If it continues to expand, it is recommended to get the result through recursive algorithm
                                }
                            }
                            //Previous previous node: user task (single person)
                            else if (source1Activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                                sourceActivityInfoMap.put(source1Activity.getId(), source1Activity);
                            }
                            //Previous previous node: user tasks (multiple)
                            else if (source1Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                                sourceActivityInfoMap.put(source1Activity.getId(), source1Activity);
                            }
                            //Previous previous node: other nodes
                            else {

                            }
                        }
                    }
                    //Previous node: containment gateway (generated)
                    else if (source0Activity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior
                            && false == source0Activity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)
                            && false == source0Activity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                        List<PvmTransition> source0IncomingTransitions = source0Activity.getIncomingTransitions();
                        ActivityImpl source1Activity = (ActivityImpl) source0IncomingTransitions.get(0).getSource();
                        //Previous previous node: user task (single person)
                        if (source1Activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                            sourceActivityInfoMap.put(source1Activity.getId(), source1Activity);
                        }
                        //Previous previous node: user tasks (multiple)
                        else if (source1Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                            sourceActivityInfoMap.put(source1Activity.getId(), source1Activity);
                        }
                        //Previous previous node: other nodes
                        else {
                            //If it continues to expand, it is recommended to get the result through recursive algorithm
                        }
                    }
                    //Previous node: user task (single person)
                    else if (source0Activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                        sourceActivityInfoMap.put(source0Activity.getId(), source0Activity);
                    }
                    //Previous node: user tasks (multiple)
                    else if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                        sourceActivityInfoMap.put(source0Activity.getId(), source0Activity);
                    }
                    //Previous node: other nodes
                    else {

                    }
                }

                //Previous node set that meets the criteria
                if (sourceActivityInfoMap != null && sourceActivityInfoMap.size() > 0) {
                    boolean begin = false;
                    List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(processInstanceId).orderByTaskCreateTime().desc().orderByTaskDefinitionKey().desc().list();
                    for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                        if (begin) {
                            //Last previous node in history
                            if (sourceActivityInfoMap.get(historicTaskInstance.getTaskDefinitionKey()) != null) {
                                sourceActivityInfoMap.put("success", true);
                                sourceActivityInfoMap.put("sourceTaskId", historicTaskInstance.getId());
                                sourceActivityInfoMap.put("sourceActivityId", historicTaskInstance.getTaskDefinitionKey());
                                sourceActivityInfoMap.put("historicTaskInstanceList", historicTaskInstanceList);
                                sourceActivityInfoMap.put(historicTaskInstance.getTaskDefinitionKey(), sourceActivityInfoMap.get(historicTaskInstance.getTaskDefinitionKey()));
                                break;
                            }
                        }
                        if (taskId.equals(historicTaskInstance.getId())) {
                            begin = true;
                        }
                    }
                } else {
                    sourceActivityInfoMap.put("success", false);
                    sourceActivityInfoMap.put("message", "不允许回退");
                    sourceActivityInfoMap.put("message_en", "Fallback not allowed");
                }
            } else {
                sourceActivityInfoMap.put("success", false);
                sourceActivityInfoMap.put("message", "不允许回退");
                sourceActivityInfoMap.put("message_en", "Fallback not allowed");
            }
        } else {
            sourceActivityInfoMap.put("success", false);
            sourceActivityInfoMap.put("message", "不允许回退");
            sourceActivityInfoMap.put("message_en", "Fallback not allowed");
        }

        return sourceActivityInfoMap;
    }

    /**
     * Fallback.
     *
     * @param entity
     * @param button
     * @param title
     * @param loginName
     */
    @SuppressWarnings("unchecked")
    private void rollBack(T entity, String button, String title, String loginName) {
        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

        //Current user agent tasks
        if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                boolean isBreak = false;
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    if (taskList != null && taskList.size() >= 0) {
                        if (isBreak) {
                            break;
                        } else {
                            for (Task task : taskList) {
                                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                    isBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Current task
        Task currentTask = null;
        for (Task task : taskList) {
            //Notify node
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                currentTask = task;
                break;
            }
        }
        taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).active().list();

        //Current activity
        ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

        //Serial multiplayer
        if (currentActivity.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
            //Adjust multi instance completion condition variable
            Object nrOfInstancesObejct = runtimeService.getVariableLocal(currentTask.getExecutionId(), "nrOfInstances");
            int nrOfInstances = nrOfInstancesObejct == null ? 0 : (Integer) nrOfInstancesObejct;
            runtimeService.setVariableLocal(currentTask.getExecutionId(), "nrOfCompletedInstances", nrOfInstances - 1);
        }
        //Multiplayer parallelism
        else if (currentActivity.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
            //ExecutionParentEntity
            Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
            for (Task task : taskList) {
                //Notify node
                if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

                }
                //Distribution node
                else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

                }
                //Other nodes
                else {
                    if (currentTask.getId().equals(task.getId())) {
                        //The current user task will be completed later
                    } else {
                        Object nrOfInstancesObejct = runtimeService.getVariableLocal(execution.getParentId(), "nrOfInstances");
                        int nrOfInstances = nrOfInstancesObejct == null ? 0 : (Integer) nrOfInstancesObejct;
                        Object nrOfCompletedInstancesObejct = runtimeService.getVariableLocal(execution.getParentId(), "nrOfCompletedInstances");
                        int nrOfCompletedInstances = nrOfCompletedInstancesObejct == null ? 0 : (Integer) nrOfCompletedInstancesObejct;

                        ScriptEngineManager manager = new ScriptEngineManager();
                        ScriptEngine engine = manager.getEngineByName("js");
                        engine.put("nrOfInstances", nrOfInstances);
                        engine.put("nrOfCompletedInstances", nrOfCompletedInstances + 1);//+1：Assuming this pass

                        //Multi instance completion conditions
                        MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) currentActivity.getActivityBehavior();
                        String expressionText = multiInstanceActivityBehavior.getCompletionConditionExpression().getExpressionText();
                        int expressionTextBegin = expressionText.indexOf("{") + 1;
                        int expressionTextEnd = expressionText.lastIndexOf("}");
                        expressionText = expressionText.substring(expressionTextBegin, expressionTextEnd);

                        boolean result = false;
                        try {
                            result = (boolean) engine.eval(expressionText);
                        } catch (ScriptException e) {
                            e.printStackTrace();
                        }
                        if (result) {
                            break;
                        } else {
                            this.complete(task.getId(), entity.getProcInsId(), button, title, processInstance.getProcessVariables());
                            final String deletedTaskId = task.getId();
                            managementService.executeCommand(new Command<T>() {
                                @Override
                                public T execute(CommandContext commandContext) {
                                    HistoricTaskInstanceEntity historicTaskInstanceEntity = commandContext.getDbSqlSession().selectById(HistoricTaskInstanceEntity.class, deletedTaskId);
                                    historicTaskInstanceEntity.markEnded("deleted");
                                    return null;
                                }
                            });
                        }
                    }
                }
            }
        }

        //Previous node information
        Map<String, Object> source0ActivityInfoMap = this.getSourceActivityInfoMap(currentActivity, entity.getProcInsId(), currentTask.getId());
        String source0TaskId = (String) source0ActivityInfoMap.get("sourceTaskId");
        String source0ActivityId = (String) source0ActivityInfoMap.get("sourceActivityId");
        ActivityImpl source0Activity = (ActivityImpl) source0ActivityInfoMap.get(source0ActivityId);
        List<HistoricTaskInstance> historicTaskInstanceList = (List<HistoricTaskInstance>) source0ActivityInfoMap.get("historicTaskInstanceList");

        //Previous previous node information
        Map<String, Object> source1ActivityInfoMap = null;
        if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            source1ActivityInfoMap = this.getSourceActivityInfoMap(source0Activity, entity.getProcInsId(), source0TaskId);
        }

        //Redirect CurrentActivity --> SourceActivity
        TransitionImpl transitionImpl = (TransitionImpl) currentActivity.getOutgoingTransitions().get(0);
        ActivityImpl originalDestination = transitionImpl.getDestination();
        originalDestination.getIncomingTransitions().remove(transitionImpl);
        transitionImpl.setDestination(source0Activity);

        //Claim the current task
        this.claim(entity.getProcInsId(), loginName);
        this.setRuleArgs(entity, loginName);

        //Previous node : task owner set
        List<String> ownerList = Lists.newArrayList();
        boolean isBreak = false;
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            //Notify node
            if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                //Multiplayer node
                if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    //Previous node : last execution history
                    if (source0Activity.getId().equals(historicTaskInstance.getTaskDefinitionKey())) {
                        if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                            ownerList.add(historicTaskInstance.getOwner());
                        }
                    }
                    if (source1ActivityInfoMap != null && (boolean) source1ActivityInfoMap.get("success")) {
                        String source1ActivityId = (String) source1ActivityInfoMap.get("sourceActivityId");
                        if (source1ActivityId.equals(historicTaskInstance.getTaskDefinitionKey())) {
                            break;
                        }
                    }
                }
                //Single node
                else {
                    //Previous node : last execution history
                    if (source0Activity.getId().equals(historicTaskInstance.getTaskDefinitionKey())) {
                        isBreak = true;
                        if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                            ownerList.add(historicTaskInstance.getOwner());
                        }
                    }
                    //Other nodes
                    else {
                        if (isBreak) {
                            break;
                        }
                    }
                }
            }
        }

        //Previous node : handler | candidate
        boolean isMultiInstanceActivityBehavior = false;
        String sourceExpressionText = "";
        if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            isMultiInstanceActivityBehavior = true;
            MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) source0Activity.getActivityBehavior();
            sourceExpressionText = multiInstanceActivityBehavior.getCollectionExpression().getExpressionText();
        } else if (source0Activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            if (source0Activity.getId().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                sourceExpressionText = (String) runtimeService.getVariableLocal(entity.getProcInsId(), source0Activity.getId());
            } else {
                UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) source0Activity.getActivityBehavior();
                Set<Expression> candidateUserIdExpressions = userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions();
                //candidateUserIdExpressions.size() == 1
                for (Expression expression : candidateUserIdExpressions) {
                    sourceExpressionText = expression.getExpressionText();
                }
            }
        }
        int sourceExpressionTextBegin = sourceExpressionText.lastIndexOf("{");
        int sourceExpressionTextEnd = sourceExpressionText.lastIndexOf("}");
        if (sourceExpressionTextBegin != -1 && sourceExpressionTextEnd != -1) {
            sourceExpressionText = sourceExpressionText.substring(sourceExpressionTextBegin + 1, sourceExpressionTextEnd);
        }
        if (isMultiInstanceActivityBehavior) {
            processInstance.getProcessVariables().put(sourceExpressionText, ownerList);
        } else {
            processInstance.getProcessVariables().put(sourceExpressionText, ownerList.get(0));
        }

        //Fallback
        this.complete(currentTask.getId(), entity.getProcInsId(), button, title, processInstance.getProcessVariables());
        this.setOwner(processDefinitionEntity, entity.getProcInsId());

        //Remove the previous node activity entry
        source0Activity.getIncomingTransitions().remove(transitionImpl);

        //Resume current activity exit
        transitionImpl.setDestination(originalDestination);
    }

    /**
     * Specified rollback.
     *
     * @param entity
     * @param button
     * @param title
     * @param loginName
     */
    private void rollBackTo(T entity, String button, String title, String loginName) {
        String sourceTaskDefinitionKey = "";
        if (StringUtils.isBlank(entity.getTempNodeKey())) {
            sourceTaskDefinitionKey = TASK_DEFINITION_KEY_END;
        } else {
            sourceTaskDefinitionKey = entity.getTempNodeKey();
        }

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

        //Current user agent tasks
        if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                boolean isBreak = false;
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    if (taskList != null && taskList.size() >= 0) {
                        if (isBreak) {
                            break;
                        } else {
                            for (Task task : taskList) {
                                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                    isBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Current task
        Task currentTask = null;
        for (Task task : taskList) {
            //Notify node
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                currentTask = task;
                break;
            }
        }
        taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).active().list();

        //Current activity
        ActivityImpl currentActivity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

        //Serial multiplayer
        if (currentActivity.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
            //Adjust multi instance completion condition variable
            Object nrOfInstancesObejct = runtimeService.getVariableLocal(currentTask.getExecutionId(), "nrOfInstances");
            int nrOfInstances = nrOfInstancesObejct == null ? 0 : (Integer) nrOfInstancesObejct;
            runtimeService.setVariableLocal(currentTask.getExecutionId(), "nrOfCompletedInstances", nrOfInstances - 1);
        }
        //Multiplayer parallelism
        else if (currentActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            //ExecutionParentEntity
            Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
            for (Task task : taskList) {
                if (currentTask.getId().equals(task.getId())) {
                    //The current user task will be completed later
                } else {
                    Object nrOfInstancesObejct = runtimeService.getVariableLocal(execution.getParentId(), "nrOfInstances");
                    int nrOfInstances = nrOfInstancesObejct == null ? 0 : (Integer) nrOfInstancesObejct;
                    Object nrOfCompletedInstancesObejct = runtimeService.getVariableLocal(execution.getParentId(), "nrOfCompletedInstances");
                    int nrOfCompletedInstances = nrOfCompletedInstancesObejct == null ? 0 : (Integer) nrOfCompletedInstancesObejct;

                    ScriptEngineManager manager = new ScriptEngineManager();
                    ScriptEngine engine = manager.getEngineByName("js");
                    engine.put("nrOfInstances", nrOfInstances);
                    engine.put("nrOfCompletedInstances", nrOfCompletedInstances + 1);//+1：Assuming this pass

                    //Multi instance completion conditions
                    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) currentActivity.getActivityBehavior();
                    String expressionText = multiInstanceActivityBehavior.getCompletionConditionExpression().getExpressionText();
                    int expressionTextBegin = expressionText.indexOf("{") + 1;
                    int expressionTextEnd = expressionText.lastIndexOf("}");
                    expressionText = expressionText.substring(expressionTextBegin, expressionTextEnd);

                    boolean result = false;
                    try {
                        result = (boolean) engine.eval(expressionText);
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    if (result) {
                        break;
                    } else {
                        this.complete(task.getId(), entity.getProcInsId(), button, title, processInstance.getProcessVariables());
                        final String deletedTaskId = task.getId();
                        managementService.executeCommand(new Command<T>() {
                            @Override
                            public T execute(CommandContext commandContext) {
                                HistoricTaskInstanceEntity historicTaskInstanceEntity = commandContext.getDbSqlSession().selectById(HistoricTaskInstanceEntity.class, deletedTaskId);
                                historicTaskInstanceEntity.markEnded("deleted");
                                return null;
                            }
                        });
                    }
                }
            }
        }

        //Specify fallback node
        ActivityImpl source0Activity = processDefinitionEntity.findActivity(sourceTaskDefinitionKey);

        //Specify the last node information of fallback node
        Map<String, Object> source1ActivityInfoMap = null;
        List<HistoricTaskInstance> historicTaskInstanceList = null;
        if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcInsId()).orderByTaskCreateTime().desc().list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                if (source0Activity.getId().equals(historicTaskInstance.getTaskDefinitionKey())) {
                    source1ActivityInfoMap = this.getSourceActivityInfoMap(source0Activity, entity.getProcInsId(), historicTaskInstance.getId());
                    break;
                }
            }
        }

        //Redirect CurrentActivity --> SourceActivity
        TransitionImpl transitionImpl = (TransitionImpl) currentActivity.getOutgoingTransitions().get(0);
        ActivityImpl originalDestination = transitionImpl.getDestination();
        originalDestination.getIncomingTransitions().remove(transitionImpl);
        transitionImpl.setDestination(source0Activity);

        //Claim the current task
        this.claim(entity.getProcInsId(), loginName);
        this.setRuleArgs(entity, loginName);

        //Specified fallback node: task owner set
        List<String> ownerList = Lists.newArrayList();

        boolean isBreak = false;
        if (historicTaskInstanceList == null) {
            historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcInsId()).orderByTaskCreateTime().desc().list();
        }
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            //Notify node
            if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                //Multiplayer node
                if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    //Previous node: history of last execution
                    if (source0Activity.getId().equals(historicTaskInstance.getTaskDefinitionKey())) {
                        if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                            ownerList.add(historicTaskInstance.getOwner());
                        }
                    }
                    if (source1ActivityInfoMap != null && (boolean) source1ActivityInfoMap.get("success")) {
                        String source1ActivityId = (String) source1ActivityInfoMap.get("sourceActivityId");
                        if (source1ActivityId.equals(historicTaskInstance.getTaskDefinitionKey())) {
                            break;
                        }
                    }
                }
                //Single node
                else {
                    //Previous node: history of last execution
                    if (source0Activity.getId().equals(historicTaskInstance.getTaskDefinitionKey())) {
                        isBreak = true;
                        if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                            ownerList.add(historicTaskInstance.getOwner());
                        }
                    }
                    //Other nodes
                    else {
                        if (isBreak) {
                            break;
                        }
                    }
                }
            }
        }

        //Designated fallback node: handler | candidate
        boolean isMultiInstanceActivityBehavior = false;
        String sourceExpressionText = "";
        if (source0Activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            isMultiInstanceActivityBehavior = true;
            MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) source0Activity.getActivityBehavior();
            sourceExpressionText = multiInstanceActivityBehavior.getCollectionExpression().getExpressionText();
        } else if (source0Activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            if (source0Activity.getId().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                sourceExpressionText = (String) runtimeService.getVariableLocal(entity.getProcInsId(), source0Activity.getId());
            } else {
                UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) source0Activity.getActivityBehavior();
                Set<Expression> candidateUserIdExpressions = userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions();
                //candidateUserIdExpressions.size() == 1
                for (Expression expression : candidateUserIdExpressions) {
                    sourceExpressionText = expression.getExpressionText();
                }
            }
        }
        int sourceExpressionTextBegin = sourceExpressionText.lastIndexOf("{");
        int sourceExpressionTextEnd = sourceExpressionText.lastIndexOf("}");
        if (sourceExpressionTextBegin != -1 && sourceExpressionTextEnd != -1) {
            sourceExpressionText = sourceExpressionText.substring(sourceExpressionTextBegin + 1, sourceExpressionTextEnd);
        }
        if (isMultiInstanceActivityBehavior) {
            processInstance.getProcessVariables().put(sourceExpressionText, ownerList);
        } else {
            if (StringUtils.isNotBlank(sourceExpressionText) && ownerList.size() > 0) {
                processInstance.getProcessVariables().put(sourceExpressionText, ownerList.get(0));
            }
        }

        //Specified rollback
        this.complete(currentTask.getId(), entity.getProcInsId(), button, title, processInstance.getProcessVariables());
        this.setOwner(processDefinitionEntity, entity.getProcInsId());

        //Remove the specified fallback node activity entry
        source0Activity.getIncomingTransitions().remove(transitionImpl);

        //Resume current activity exit
        transitionImpl.setDestination(originalDestination);
    }

    /**
     * Specify fallback node list.
     *
     * @param entity
     * @param loginName
     * @return Node list map.
     */
    public LinkedHashMap<String, Object> getNodeList(T entity, String loginName) {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();

        //Execution instance ExecutionList
        String processInstanceId = entity.getProcInsId();

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Current user to do tasks
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                .taskOwner(loginName).active().orderByTaskCreateTime().asc().list();

        //Current user agent tasks
        if (taskList == null || taskList.size() == 0) {
            Date now = new Date();
            String processDefinitionCategory = COMMA + processDefinition.getCategory() + COMMA;
            List<AssigneeSetting> assigneeList = assigneeSettingService.getAssigneeListByUserId(UserUtil.getByLoginName(loginName).getId());
            for (AssigneeSetting assigneeSetting : assigneeList) {
                boolean isBreak = false;
                String processScopeArrayStr = COMMA + assigneeSetting.getProcessScope() + COMMA;
                if (processScopeArrayStr.contains(processDefinitionCategory) && assigneeSetting.getBeginTime().before(now) && assigneeSetting.getEndTime().after(now)) {
                    taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId())
                            .taskOwner(assigneeSetting.getCreateBy().getLoginName()).active().orderByTaskCreateTime().asc().list();
                    if (taskList != null && taskList.size() >= 0) {
                        if (isBreak) {
                            break;
                        } else {
                            for (Task task : taskList) {
                                if (false == task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)
                                        && false == task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                                    isBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Current task
        Task currentTask = null;
        for (Task task : taskList) {
            //Notify node
            if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                currentTask = task;
                break;
            }
        }

        ActivityImpl currentActiviti = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());
        Map<String, Object> historyActivityMap = Maps.newLinkedHashMap();
        Map<String, Object> beginEndScopeInfoMap = Maps.newHashMap();
        this.setHistoryActivityMap(historyActivityMap, currentActiviti, beginEndScopeInfoMap, false);

        if (historyActivityMap != null && historyActivityMap.size() > 0) {
            LinkedHashMap<String, HistoricTaskInstance> tempNodeMap = Maps.newLinkedHashMap();
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcInsId()).orderByHistoricTaskInstanceEndTime().asc().list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                if (historyActivityMap.get(historicTaskInstance.getTaskDefinitionKey()) != null) {
                    tempNodeMap.put(historicTaskInstance.getTaskDefinitionKey(), historicTaskInstance);
                }
            }
            List<HistoricTaskInstance> nodeList = Lists.newArrayList();
            for (Map.Entry<String, HistoricTaskInstance> entry : tempNodeMap.entrySet()) {
                nodeList.add(entry.getValue());
            }
            map.put("success", true);
            map.put("nodeList", nodeList);
        } else {
            map.put("success", false);
            map.put("message", "不允许指定回退");
            map.put("message_en", "Specifying fallback is not allowed");
        }

        return map;
    }

    /**
     * Current node finds all nodes forward.
     *
     * @param historyActivityMap
     * @param currentActivity
     * @param beginEndScopeInfoMap
     * @param hasEndInclusiveGateway
     */
    private void setHistoryActivityMap(Map<String, Object> historyActivityMap, ActivityImpl currentActivity, Map<String, Object> beginEndScopeInfoMap, boolean hasEndInclusiveGateway) {
        List<PvmTransition> incomingTransitions = currentActivity.getIncomingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();

            //Inclusive gateway start and end scope node filtering
            if (beginEndScopeInfoMap != null && beginEndScopeInfoMap.get(sourceActivity.getId()) != null) {
                setHistoryActivityMap(historyActivityMap, sourceActivity, beginEndScopeInfoMap, hasEndInclusiveGateway);
                continue;
            }

            if (sourceActivity.getActivityBehavior() instanceof StartEvent) {
                continue;
            } else if (sourceActivity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                historyActivityMap.put(sourceActivity.getId(), sourceActivity);
                setHistoryActivityMap(historyActivityMap, sourceActivity, beginEndScopeInfoMap, hasEndInclusiveGateway);
            } else if (sourceActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                historyActivityMap.put(sourceActivity.getId(), sourceActivity);
                setHistoryActivityMap(historyActivityMap, sourceActivity, beginEndScopeInfoMap, hasEndInclusiveGateway);
            } else if (sourceActivity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
                if (hasEndInclusiveGateway) {
                    setHistoryActivityMap(historyActivityMap, sourceActivity, beginEndScopeInfoMap, hasEndInclusiveGateway);
                } else {
                    continue;
                }
            } else if (sourceActivity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
                beginEndScopeInfoMap = this.getBeginEndScopeInfoMap(sourceActivity, beginEndScopeInfoMap);
                hasEndInclusiveGateway = true;
                setHistoryActivityMap(historyActivityMap, sourceActivity, beginEndScopeInfoMap, hasEndInclusiveGateway);
            } else {
                setHistoryActivityMap(historyActivityMap, sourceActivity, beginEndScopeInfoMap, hasEndInclusiveGateway);
            }
        }
    }

    /**
     * Query the scope information collection at the beginning and end of containment gateway (refer to the following 4 methods).
     *
     * @param currentActivity
     * @param beginEndScopeInfoMap
     * @return Scope info map.
     */
    private Map<String, Object> getBeginEndScopeInfoMap(ActivityImpl currentActivity, Map<String, Object> beginEndScopeInfoMap) {
        Map<String, Object> beginInclusiveGatewayMap = Maps.newHashMap();
        beginInclusiveGatewayMap = this.getBeginInclusiveGatewayMap(currentActivity, beginInclusiveGatewayMap);
        ActivityImpl beginInclusiveGateway = this.getBeginInclusiveGateway(beginInclusiveGatewayMap);

        beginEndScopeInfoMap = this.getBeginEndScopeInfoMap(currentActivity, beginInclusiveGateway, beginEndScopeInfoMap);
        return beginEndScopeInfoMap;
    }

    /**
     * Query the latest containment gateway on the current branch (started).
     *
     * @param beginInclusiveGatewayMap
     * @return ActivityImpl the latest containment gateway.
     */
    private ActivityImpl getBeginInclusiveGateway(Map<String, Object> beginInclusiveGatewayMap) {
        if (beginInclusiveGatewayMap.size() == 1) {
            ActivityImpl beginInclusiveGateway = null;
            for (Map.Entry<String, Object> entry : beginInclusiveGatewayMap.entrySet()) {
                beginInclusiveGateway = (ActivityImpl) entry.getValue();
            }
            return beginInclusiveGateway;
        } else if (beginInclusiveGatewayMap.size() > 1) {
            Map<String, Object> parentBeginInclusiveGatewayMap = Maps.newHashMap();
            Map<String, Object> childBeginInclusiveGatewayMap = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : beginInclusiveGatewayMap.entrySet()) {
                for (Map.Entry<String, Object> entry2 : beginInclusiveGatewayMap.entrySet()) {
                    if (false == entry.getKey().equals(entry2.getKey())) {
                        Map<String, Object> allBeginInclusiveGatewayMap = Maps.newHashMap();
                        allBeginInclusiveGatewayMap = this.getAllBeginInclusiveGatewayMap((ActivityImpl) entry2.getValue(), allBeginInclusiveGatewayMap);
                        if (allBeginInclusiveGatewayMap.get(entry.getKey()) != null) {
                            childBeginInclusiveGatewayMap.put(entry2.getKey(), entry2.getValue());
                        }
                    }
                }
            }
            if (childBeginInclusiveGatewayMap.size() > 0) {
                for (Map.Entry<String, Object> entry : beginInclusiveGatewayMap.entrySet()) {
                    if (childBeginInclusiveGatewayMap.get(entry.getKey()) != null) {
                        ActivityImpl beginInclusiveGatwayI = (ActivityImpl) entry.getValue();
                        List<PvmTransition> incomingTransitions = beginInclusiveGatwayI.getIncomingTransitions();
                        for (PvmTransition pvmTransition : incomingTransitions) {
                            ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
                            parentBeginInclusiveGatewayMap = this.getBeginInclusiveGatewayMap(sourceActivity, parentBeginInclusiveGatewayMap);

                        }
                    } else {
                        parentBeginInclusiveGatewayMap.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                for (Map.Entry<String, Object> entry : beginInclusiveGatewayMap.entrySet()) {
                    ActivityImpl beginInclusiveGatwayI = (ActivityImpl) entry.getValue();
                    List<PvmTransition> incomingTransitions = beginInclusiveGatwayI.getIncomingTransitions();
                    for (PvmTransition pvmTransition : incomingTransitions) {
                        ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
                        parentBeginInclusiveGatewayMap = this.getBeginInclusiveGatewayMap(sourceActivity, parentBeginInclusiveGatewayMap);

                    }
                }
            }
            return getBeginInclusiveGateway(parentBeginInclusiveGatewayMap);
        } else {
            return null;
        }
    }

    /**
     * Query the last containment gateway (starting) collection on all branches.
     *
     * @param currentActivity
     * @param beginInclusiveGatewayMap
     * @return The last containment gateway map.
     */
    private Map<String, Object> getBeginInclusiveGatewayMap(ActivityImpl currentActivity, Map<String, Object> beginInclusiveGatewayMap) {
        if (currentActivity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
            beginInclusiveGatewayMap.put(currentActivity.getId(), currentActivity);
        } else {
            List<PvmTransition> incomingTransitions = currentActivity.getIncomingTransitions();
            for (PvmTransition pvmTransition : incomingTransitions) {
                ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
                beginInclusiveGatewayMap = getBeginInclusiveGatewayMap(sourceActivity, beginInclusiveGatewayMap);
            }
        }
        return beginInclusiveGatewayMap;
    }

    /**
     * Queries all forward inclusive gateway (start) collections on the current branch.
     *
     * @param currentActivity
     * @param allBeginInclusiveGatewayMap
     * @return All forward inclusive gateway map.
     */
    private Map<String, Object> getAllBeginInclusiveGatewayMap(ActivityImpl currentActivity, Map<String, Object> allBeginInclusiveGatewayMap) {
        if (currentActivity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
            allBeginInclusiveGatewayMap.put(currentActivity.getId(), allBeginInclusiveGatewayMap);
        } else {
            List<PvmTransition> incomingTransitions = currentActivity.getIncomingTransitions();
            for (PvmTransition pvmTransition : incomingTransitions) {
                ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
                allBeginInclusiveGatewayMap = getAllBeginInclusiveGatewayMap(sourceActivity, allBeginInclusiveGatewayMap);
            }
        }
        return allBeginInclusiveGatewayMap;
    }

    /**
     * Query containment gateway (start) and containment gateway (end) scope information sets.
     *
     * @param currentActivity
     * @param beginInclusiveGateway
     * @param beginEndScopeInfoMap
     * @return Scope info map.
     */
    private Map<String, Object> getBeginEndScopeInfoMap(ActivityImpl currentActivity, ActivityImpl beginInclusiveGateway, Map<String, Object> beginEndScopeInfoMap) {
        List<PvmTransition> incomingTransitions = currentActivity.getIncomingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
            beginEndScopeInfoMap.put(sourceActivity.getId(), sourceActivity);
            if (sourceActivity.getId().equals(beginInclusiveGateway.getId())) {
                beginEndScopeInfoMap.put(currentActivity.getId(), currentActivity);
            } else {
                beginEndScopeInfoMap = getBeginEndScopeInfoMap(sourceActivity, beginInclusiveGateway, beginEndScopeInfoMap);
            }
        }
        return beginEndScopeInfoMap;
    }

    /**
     * Retrieve check.
     *
     * @param entity
     * @param userId
     * @param loginName
     * @param resultMap
     */
    @Transactional(readOnly = false)
    public void backward(T entity, String userId, String loginName, LinkedHashMap<String, Object> resultMap) {
        boolean b0 = true;
        boolean b1 = true;
        boolean b2 = true;
        boolean b3 = true;
        boolean b4 = true;
        boolean b5 = true;
        boolean b6 = true;

        Task currentTask = null;
        ActivityImpl currentActivity = null;
        //Previous node
        ActivityImpl sourceActivity = null;

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        if (processInstance == null) {
            b0 = false;
        } else {
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            //Current active task list
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).active().list();
            for (Task task : taskList) {
                //Notify node
                if (task.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                    //Notify node, do not handle
                }
                //Distribution node
                else if (task.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    //Distribution node, do not process
                }
                //Other nodes
                else {
                    currentTask = task;
                    currentActivity = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
                    if (currentActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                        b1 = false;
                        continue;
                    } else {
                        //Current node entry
                        List<PvmTransition> currentIncomingTransitions = currentActivity.getIncomingTransitions();

                        //Historical task list
                        boolean isBreak = false;
                        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcInsId()).orderByTaskCreateTime().desc().list();
                        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                            if (isBreak) {
                                break;
                            }
                            //Notify node
                            else if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

                            }
                            //Distribution node
                            else if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

                            }
                            //Other nodes
                            else {
                                for (PvmTransition currentPvmTransition : currentIncomingTransitions) {
                                    //Previous node ID
                                    String sourceActivityId = "";

                                    //Previous node i
                                    sourceActivity = (ActivityImpl) currentPvmTransition.getSource();
                                    if (sourceActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                                        //Multiple instances do not process
                                    } else if (sourceActivity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                                        //Containment gateway does not process
                                    } else if (sourceActivity.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                                        List<PvmTransition> sourceIncomingTransitions = sourceActivity.getIncomingTransitions();
                                        for (PvmTransition sourcePvmTransition : sourceIncomingTransitions) {
                                            //Containment gateway does not process
                                            sourceActivityId = sourcePvmTransition.getSource().getId();

                                            //History of last previous node execution
                                            if (historicTaskInstance.getTaskDefinitionKey().equals(sourceActivityId)) {
                                                sourceActivity = processDefinitionEntity.findActivity(sourceActivityId);
                                                isBreak = true;
                                                break;
                                            }
                                        }
                                        if (isBreak) {
                                            break;
                                        }
                                    } else if (sourceActivity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                                        sourceActivityId = sourceActivity.getId();

                                        //History of last previous node execution
                                        if (historicTaskInstance.getTaskDefinitionKey().equals(sourceActivityId)) {
                                            sourceActivity = processDefinitionEntity.findActivity(sourceActivityId);
                                            isBreak = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        //The previous node is a multi instance or containment gateway
                        if (sourceActivity == null) {
                            b2 = false;
                            continue;
                        } else {
                            //Previous node activity type of mutually exclusive gateway
                            if (sourceActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                                b3 = false;
                                continue;
                            } else if (sourceActivity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                                b3 = false;
                                continue;
                            } else {
                                Map<String, String> historicTaskInstanceMap = Maps.newHashMap();
                                for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                                    historicTaskInstanceMap.put(historicTaskInstance.getTaskDefinitionKey(), historicTaskInstance.getAssignee());
                                }

                                //The handler of the previous node is the current user
                                String sourceAssignee = historicTaskInstanceMap.get(sourceActivity.getId());
                                if (StringUtils.isBlank(sourceAssignee) || false == loginName.equals(sourceAssignee)) {
                                    b4 = false;
                                    continue;
                                } else {
                                    //Record that the last updated user is the current user
                                    if (false == userId.equals(entity.getUpdateBy().getId())) {
                                        b5 = false;
                                        continue;
                                    } else {
                                        Map<String, String> tableOperationMap = Maps.newHashMap();
                                        Act act = new Act();
                                        act.setProcDefId(processDefinitionEntity.getId());
                                        act.setTaskDefKey(currentTask.getTaskDefinitionKey());
                                        TaskSettingVersion taskSettingVersion = taskSettingVersionService.getTaskSettingVersionByAct(act);
                                        if (taskSettingVersion != null && StringUtils.isNotBlank(taskSettingVersion.getPermission())) {
                                            TaskPermission taskPermission = taskPermissionService.get(taskSettingVersion.getPermission());
                                            if (taskPermission != null && StringUtils.isNotBlank(taskPermission.getTableOperation())) {
                                                String[] tableOperationArray = taskPermission.getTableOperation().split(",");
                                                for (String tableOperation : tableOperationArray) {
                                                    String key = tableOperation.split("_")[1];
                                                    tableOperationMap.put(key, key);
                                                }
                                            }
                                        }

                                        //Permission management allow to retrieve or not
                                        if (StringUtils.isBlank(tableOperationMap.get("backward"))) {
                                            b6 = false;
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;//With and only one node
                }
            }
        }

        if (b0 && b1 && b2 && b3 && b4 && b5 && b6) {
            String button = " [ 取回 ] ";
            String title = "";
            backward(entity, button, title, currentTask, currentActivity, sourceActivity, loginName);
            resultMap.put("message", "取回成功");
            resultMap.put("message_en", "Get back success");
            resultMap.put("success", true);
        } else {
            resultMap.put("message", "不允许取回");
            resultMap.put("message_en", "Get back not allowed");
            resultMap.put("success", false);
        }
    }

    /**
     * Get back.
     *
     * @param entity
     * @param button
     * @param title
     * @param currentTask
     * @param currentActivity
     * @param sourceActivity
     * @param loginName
     */
    private void backward(T entity, String button, String title, Task currentTask, ActivityImpl currentActivity, ActivityImpl sourceActivity, String loginName) {
        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        //Redirect CurrentActivity --> SourceActivity
        TransitionImpl transitionImpl = (TransitionImpl) currentActivity.getOutgoingTransitions().get(0);
        ActivityImpl originalDestination = transitionImpl.getDestination();
        originalDestination.getIncomingTransitions().remove(transitionImpl);
        transitionImpl.setDestination(sourceActivity);

        //Claim the current task
        taskService.claim(currentTask.getId(), loginName);
        this.setRuleArgs(entity, loginName);

        //Previous node: task owner set
        List<String> ownerList = Lists.newArrayList();

        boolean isBreak = false;
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcInsId()).orderByTaskCreateTime().desc().list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            //Notify node
            if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                //The notification node (multiple instances without exits) and the multiple instance node will cross during execution, so filter here
            }
            //Distribution node
            else if (historicTaskInstance.getTaskDefinitionKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                //The distribution node (multi instance without exit) and multi instance node will cross each other during execution, so filter here
            }
            //Retrieve node: last execution history
            else if (sourceActivity.getId().equals(historicTaskInstance.getTaskDefinitionKey())) {
                isBreak = true;
                if (StringUtils.isNotBlank(historicTaskInstance.getOwner())) {
                    ownerList.add(historicTaskInstance.getOwner());
                }
            }
            //Other nodes
            else {
                if (isBreak) {
                    break;
                }
            }
        }

        //Previous node: handler | candidate
        boolean isMultiInstanceActivityBehavior = false;
        String sourceExpressionText = "";
        if (sourceActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
            isMultiInstanceActivityBehavior = true;
            MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) sourceActivity.getActivityBehavior();
            sourceExpressionText = multiInstanceActivityBehavior.getCollectionExpression().getExpressionText();
        } else if (sourceActivity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            if (sourceActivity.getId().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                sourceExpressionText = (String) runtimeService.getVariable(entity.getProcInsId(), sourceActivity.getId());
            } else {
                UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) sourceActivity.getActivityBehavior();
                Set<Expression> candidateUserIdExpressions = userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions();
                //candidateUserIdExpressions.size() == 1
                for (Expression expression : candidateUserIdExpressions) {
                    sourceExpressionText = expression.getExpressionText();
                }
            }
        }
        int sourceExpressionTextBegin = sourceExpressionText.lastIndexOf("{");
        int sourceExpressionTextEnd = sourceExpressionText.lastIndexOf("}");
        if (sourceExpressionTextBegin != -1 && sourceExpressionTextEnd != -1) {
            sourceExpressionText = sourceExpressionText.substring(sourceExpressionTextBegin + 1, sourceExpressionTextEnd);
        }
        if (isMultiInstanceActivityBehavior) {
            processInstance.getProcessVariables().put(sourceExpressionText, ownerList);
        } else {
            processInstance.getProcessVariables().put(sourceExpressionText, ownerList.get(0));
        }

        //Get back
        this.complete(currentTask.getId(), entity.getProcInsId(), button, title, processInstance.getProcessVariables());
        this.setOwner(processDefinitionEntity, entity.getProcInsId());

        //Remove the previous node activity entry
        sourceActivity.getIncomingTransitions().remove(transitionImpl);

        //Resume current activity exit
        transitionImpl.setDestination(originalDestination);

        //Update Object
        processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        if (processInstance != null) {
            entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());
            entity.getAct().setTaskDefKey(sourceActivity.getId());
            entity.setProcTaskName(sourceActivity.getId());
            TaskPermission taskPermission = new TaskPermission();
            taskPermission.setId(this.getTaskSettingVersionByAct(entity.getAct()).getPermission());
            entity.setProcTaskPermission(taskPermission);
            User updateBy = new User();
            updateBy.setId("");
            entity.setUpdateBy(updateBy);
            dao.update(entity);
        }
    }

    /**
     * Revoke.
     *
     * @param entity
     * @param resultMap
     */
    @Transactional(readOnly = false)
    public void revoke(T entity, LinkedHashMap<String, Object> resultMap) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
        if (processInstance == null) {
            resultMap.put("message", "撤销失败：流程已结束");
            resultMap.put("message_en", "Revoke failed: process ended");
            resultMap.put("success", false);
        } else {
            //ProcessDefinitionEntity
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            //Current active task
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).active().list();

            //Current task collection
            Map<String, Task> currentTaskMap = Maps.newHashMap();
            for (Task task : taskList) {
                currentTaskMap.put(task.getTaskDefinitionKey(), task);
            }

            //Revoke verification (if a node does not meet the conditions, it cannot be revoked)
            boolean result = true;

            for (Map.Entry<String, Task> entry : currentTaskMap.entrySet()) {
                //Notify node
                if (entry.getKey().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {
                    //cancelable
                }
                //Distribution node
                else if (entry.getKey().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {
                    //cancelable
                }
                //Add node
                else if (entry.getKey().startsWith(NODE_MARK_CREATE + UNDERLINE)) {
                    //cancelable
                }
                //Other nodes
                else {
                    //Current task permissions
                    entity.getAct().setProcDefId(processDefinitionEntity.getId());
                    entity.getAct().setTaskDefKey(entry.getKey());
                    TaskSettingVersion taskSettingVersion = taskSettingVersionService.getTaskSettingVersionByAct(entity.getAct());
                    String permissionId = taskSettingVersion.getPermission();
                    TaskPermission taskPermission = taskPermissionService.get(permissionId);
                    if (taskPermission != null && StringUtils.isNotBlank(taskPermission.getTableOperation())) {
                        Map<String, String> tableOperationMap = Maps.newHashMap();
                        String[] tableOperationArray = taskPermission.getTableOperation().split(",");
                        for (String tableOperation : tableOperationArray) {
                            //tableOperation格式：撤销_revoke,取回_backward
                            String tableOperationKey = tableOperation.split("_")[1];
                            tableOperationMap.put(tableOperationKey, tableOperation);
                        }
                        //No permission to revoke
                        if (StringUtils.isBlank(tableOperationMap.get("revoke"))) {
                            //If one node does not meet the conditions, it cannot be revoked
                            result = false;
                            break;
                        }
                        //Have the right to revoke
                        else {
                            //cancelable
                        }
                    } else {
                        //If one node does not meet the conditions, it cannot be revoked
                        result = false;
                        break;
                    }
                }
            }

            if (result) {
                deleteAct(entity);
                entity.setProcInsId("");
                entity.setProcTaskName("");
                entity.setProcTaskPermission(new TaskPermission());
                super.save(entity);
                resultMap.put("message", "撤销成功");
                resultMap.put("message_en", "Revoke success");
                resultMap.put("success", true);
            } else {
                resultMap.put("message", "撤销失败：没有撤销权限");
                resultMap.put("message_en", "Revoke failed: process ended");
                resultMap.put("success", false);
            }
        }
    }

    /**
     * Query multi branch information set (done list multi branch).
     *
     * @param processInstanceId
     * @param loginName
     * @return Multi branch info map.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMultiBranchInfoMap(String processInstanceId, String loginName) {
        Map<String, Object> dataMap = Maps.newHashMap();

        //ProcessDefinitionEntity
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        Map<String, Object> activeSourceInfoMap = Maps.newHashMap();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime().desc().list();
        for (Task task : taskList) {
            ActivityImpl activityImpl = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());

            //Notify node
            if (activityImpl.getId().startsWith(NODE_MARK_NOTIFY + UNDERLINE)) {

            }
            //Distribution node
            else if (activityImpl.getId().startsWith(NODE_MARK_DISTRIBUTE + UNDERLINE)) {

            }
            //Other nodes
            else {
                activeSourceInfoMap = this.getBeginInclusiveGatewaySourceInfoMap(activityImpl, activityImpl, null, false, activeSourceInfoMap);
            }
        }

        if (activeSourceInfoMap != null && activeSourceInfoMap.size() > 0) {
            Map<String, Object> activeTargetMap = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : activeSourceInfoMap.entrySet()) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                List<Map<String, Object>> targetList = (List<Map<String, Object>>) map.get("targetList");
                for (Map<String, Object> map2 : targetList) {
                    String targetActivityId = (String) map2.get("targetId");
                    activeTargetMap.put(targetActivityId, targetActivityId);
                }
            }

            Map<String, Object> inactiveSourceInfoMap = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : activeSourceInfoMap.entrySet()) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                String sourceActivityId = (String) map.get("sourceId");
                String sourceActivityName = (String) map.get("sourceName");
                String beginInclusiveGatewayId = (String) map.get("beginInclusiveGatewayId");
                ActivityImpl beginInclusiveGateway = processDefinitionEntity.findActivity(beginInclusiveGatewayId);
                List<PvmTransition> outgoingTransitions = beginInclusiveGateway.getOutgoingTransitions();
                for (PvmTransition pvmTransition : outgoingTransitions) {
                    ActivityImpl targetActivity = (ActivityImpl) pvmTransition.getDestination();
                    if (activeTargetMap.get(targetActivity.getId()) == null) {
                        Map<String, Object> map2 = null;
                        List<Map<String, Object>> targetList = null;
                        if (inactiveSourceInfoMap.get(entry.getKey()) != null) {
                            map2 = (Map<String, Object>) inactiveSourceInfoMap.get(entry.getKey());
                            targetList = (List<Map<String, Object>>) map2.get("targetList");
                        } else {
                            map2 = Maps.newHashMap();
                            map2.put("sourceId", sourceActivityId);
                            map2.put("sourceName", sourceActivityName);
                            map2.put("beginInclusiveGatewayId", beginInclusiveGatewayId);
                            targetList = Lists.newArrayList();
                        }
                        Map<String, Object> map3 = Maps.newHashMap();
                        map3.put("targetId", targetActivity.getId());
                        map3.put("targetName", targetActivity.getProperty("name"));
                        map3.put("parentSource", true);
                        targetList.add(map3);
                        map2.put("targetList", targetList);
                        inactiveSourceInfoMap.put(sourceActivityId, map2);
                    }
                }
            }

            Map<String, Map<String, HistoricTaskInstance>> historicTaskInstanceMap = Maps.newHashMap();
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                if (historicTaskInstanceMap.get(historicTaskInstance.getTaskDefinitionKey()) != null) {
                    Map<String, HistoricTaskInstance> htiMap = historicTaskInstanceMap.get(historicTaskInstance.getTaskDefinitionKey());
                    htiMap.put(historicTaskInstance.getId(), historicTaskInstance);
                } else {
                    Map<String, HistoricTaskInstance> htiMap = Maps.newHashMap();
                    htiMap.put(historicTaskInstance.getId(), historicTaskInstance);
                    historicTaskInstanceMap.put(historicTaskInstance.getTaskDefinitionKey(), htiMap);
                }
            }

            Map<String, Object> newActiveSourceInfoMap = Maps.newHashMap();
            Map<String, Object> newInactiveSourceInfoMap = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : activeSourceInfoMap.entrySet()) {
                Map<String, HistoricTaskInstance> map = historicTaskInstanceMap.get(entry.getKey());
                boolean isOwner = false;
                boolean isAssignee = false;
                for (Map.Entry<String, HistoricTaskInstance> entry2 : map.entrySet()) {
                    HistoricTaskInstance hisctoricTaskInstance = entry2.getValue();
                    if (StringUtils.isNotBlank(hisctoricTaskInstance.getOwner()) && loginName.equals(hisctoricTaskInstance.getOwner())) {
                        isOwner = true;
                        break;
                    } else if (StringUtils.isNotBlank(hisctoricTaskInstance.getAssignee()) && loginName.equals(hisctoricTaskInstance.getAssignee())) {
                        isAssignee = true;
                        break;
                    }

                }
                if (isOwner || isAssignee) {
                    newActiveSourceInfoMap.put(entry.getKey(), entry.getValue());
                    Object object = inactiveSourceInfoMap.get(entry.getKey());
                    if (object != null) {
                        newInactiveSourceInfoMap.put(entry.getKey(), object);
                    }
                } else {
                    //Filter
                }
            }

            dataMap.put("activeSourceInfoMap", newActiveSourceInfoMap);
            dataMap.put("inactiveSourceInfoMap", newInactiveSourceInfoMap);
            dataMap.put("success", true);
        } else {
            dataMap.put("success", false);
            dataMap.put("message", "没有运行中的多分支");
            dataMap.put("message_en", "There are no running multi branches");
        }
        return dataMap;
    }

    /**
     * Query the last containment gateway (start) collection (containment gateway, previous node, next node) on all branches.
     *
     * @param activeActivity
     * @param currentActivity
     * @param targetActivity
     * @param hasParentSource
     * @param sourceInfoMap
     * @return The last containment gateway map.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getBeginInclusiveGatewaySourceInfoMap(ActivityImpl activeActivity, ActivityImpl currentActivity, ActivityImpl targetActivity, boolean hasParentSource, Map<String, Object> sourceInfoMap) {
        if (currentActivity.getId().startsWith(NODE_MARK_BEGIN_INCLUSIVE_GATEWAY + UNDERLINE)) {
            ActivityImpl sourceActivity = (ActivityImpl) currentActivity.getIncomingTransitions().get(0).getSource();
            if (sourceActivity.getActivityBehavior() instanceof UserTaskActivityBehavior || sourceActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                //sourceActivity = sourceActivity;
            } else if (sourceActivity.getActivityBehavior() instanceof InclusiveGatewayActivityBehavior) {
                sourceActivity = (ActivityImpl) sourceActivity.getIncomingTransitions().get(0).getSource();
            }

            Map<String, Object> map = null;
            List<Map<String, Object>> targetList = null;
            if (sourceInfoMap.get(sourceActivity.getId()) != null) {
                map = (Map<String, Object>) sourceInfoMap.get(sourceActivity.getId());
                targetList = (List<Map<String, Object>>) map.get("targetList");
            } else {
                map = Maps.newHashMap();
                map.put("sourceId", sourceActivity.getId());
                map.put("sourceName", sourceActivity.getProperty("name"));
                map.put("beginInclusiveGatewayId", currentActivity.getId());
                targetList = Lists.newArrayList();
            }
            Map<String, Object> map2 = Maps.newHashMap();
            map2.put("targetId", targetActivity.getId());
            map2.put("targetName", targetActivity.getProperty("name"));
            map2.put("activeId", activeActivity.getId());
            map2.put("activeName", activeActivity.getProperty("name"));
            if (hasParentSource) {
                map2.put("parentSource", false);
            } else {
                map2.put("parentSource", true);
                hasParentSource = true;
            }
            targetList.add(map2);
            map.put("targetList", targetList);

            sourceInfoMap.put(sourceActivity.getId(), map);
            sourceInfoMap = getBeginInclusiveGatewaySourceInfoMap(activeActivity, sourceActivity, currentActivity, hasParentSource, sourceInfoMap);
        } else if (currentActivity.getId().startsWith(NODE_MARK_END_INCLUSIVE_GATEWAY + UNDERLINE)) {
            //Stop recursion, equivalent to continue;
        } else {
            List<PvmTransition> incomingTransitions = currentActivity.getIncomingTransitions();
            for (PvmTransition pvmTransition : incomingTransitions) {
                ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
                if (currentActivity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    sourceInfoMap = getBeginInclusiveGatewaySourceInfoMap(activeActivity, sourceActivity, currentActivity, hasParentSource, sourceInfoMap);
                } else if (currentActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    sourceInfoMap = getBeginInclusiveGatewaySourceInfoMap(activeActivity, sourceActivity, currentActivity, hasParentSource, sourceInfoMap);
                } else {
                    sourceInfoMap = getBeginInclusiveGatewaySourceInfoMap(activeActivity, sourceActivity, targetActivity, hasParentSource, sourceInfoMap);
                }
            }
        }
        return sourceInfoMap;
    }

    /**
     * Set Branch Information (activate or destroy).
     *
     * @param entity
     * @param addIds
     * @param deleteIds
     * @param loginName
     * @return Result message.
     */
    public Map<String, Object> setMultiBranchInfo(T entity, String addIds, String deleteIds, String loginName) {
        Map<String, Object> dataMap = Maps.newHashMap();

        //Active branch
        if (StringUtils.isNotBlank(addIds)) {
            //ProcessDefinitionEntity
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
            RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
            ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

            String[] addIdArray = addIds.split(",");
            for (String addId : addIdArray) {
                ActivityImpl currentActivity = processDefinitionEntity.findActivity(addId);
                ActivityImpl beginInclusiveGateway = (ActivityImpl) currentActivity.getIncomingTransitions().get(0).getSource();//get(0): 原目标一定是beginInclusiveGateway

                Execution beginInclusiveGatewayExecution = null;
                List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(entity.getProcInsId()).list();
                for (Execution execution : executionList) {
                    if (beginInclusiveGateway.getId().equals(execution.getActivityId())) {
                        beginInclusiveGatewayExecution = execution;
                    }
                }
                ExecutionEntity beginInclusiveGatewayExecutionEntity = (ExecutionEntity) beginInclusiveGatewayExecution;

                //NewExecutionEntity
                final ExecutionEntity newExecutionEntity = new ExecutionEntity();
                newExecutionEntity.setActive(true);//Active
                newExecutionEntity.setConcurrent(false);//branch
                newExecutionEntity.setScope(false);
                newExecutionEntity.setParent(beginInclusiveGatewayExecutionEntity);
                newExecutionEntity.setParentId(beginInclusiveGatewayExecutionEntity.getId());
                newExecutionEntity.setProcessInstance((InterpretableExecution) processInstance);
                newExecutionEntity.setProcessDefinitionId(processInstance.getProcessDefinitionId());
                newExecutionEntity.setActivity(currentActivity);

                managementService.executeCommand(new Command<T>() {
                    @Override
                    public T execute(CommandContext commandContext) {
                        commandContext.getDbSqlSession().insert(newExecutionEntity);
                        return null;
                    }
                });

                entity.getAct().setProcDefId(processInstance.getProcessDefinitionId());
                entity.getAct().setTaskDefKey(currentActivity.getId());
                List<User> userList = getUserList(entity, loginName);
                List<String> loginNameList = Lists.newArrayList();
                if (userList != null && userList.size() > 0) {
                    loginNameList.add(userList.get(0).getLoginName());
                }

                //NewTaskEntity
                TaskEntity newTaskEntity = new TaskEntity();
                final String newTaskEntityId = UUID.randomUUID().toString();
                newTaskEntity.setId(newTaskEntityId);
                newTaskEntity.setName((String) currentActivity.getProperty("name"));
                newTaskEntity.setCreateTime(new Date());
                newTaskEntity.setOwner(loginNameList.get(0));
//				newTaskEntity.setAssignee(loginNameList.get(0));
                newTaskEntity.setExecution(newExecutionEntity);
                newTaskEntity.setExecutionId(newExecutionEntity.getId());
                newTaskEntity.setTaskDefinitionKey(currentActivity.getId());
                newTaskEntity.setProcessDefinitionId(processInstance.getProcessDefinitionId());
                newTaskEntity.setProcessInstanceId(processInstance.getId());
                taskService.saveTask(newTaskEntity);
                taskSettingVersionService.updateHistoricTask(newTaskEntity);
            }

            dataMap.put("add", true);
        }
        //Destruction branch
        if (StringUtils.isNotBlank(deleteIds)) {
            Map<String, String> taskDefinitionKeyMap = Maps.newHashMap();
            String[] deleteIdArray = deleteIds.split(",");
            for (String deleteId : deleteIdArray) {
                taskDefinitionKeyMap.put(deleteId, deleteId);
            }

            Map<String, Execution> executionMap = Maps.newHashMap();
            List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(entity.getProcInsId()).list();
            for (Execution execution : executionList) {
                executionMap.put(execution.getId(), execution);
            }

            List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).active().list();
            for (Task task : taskList) {
                String taskDefinitionKey = taskDefinitionKeyMap.get(task.getTaskDefinitionKey());
                if (StringUtils.isNotBlank(taskDefinitionKey)) {
                    final ExecutionEntity execution = (ExecutionEntity) executionMap.get(task.getExecutionId());
                    managementService.executeCommand(new Command<T>() {
                        @Override
                        public T execute(CommandContext commandContext) {
                            commandContext.getDbSqlSession().delete(execution);
                            return null;
                        }
                    });
                    TaskEntity taskEntity = (TaskEntity) task;
                    taskEntity.setExecutionId("");
                    taskService.saveTask(taskEntity);
                    taskService.deleteTask(task.getId(), true);//true: cascade
                }
            }

            dataMap.put("delete", true);
        }

        return dataMap;
    }

    /**
     * Delete workflow.
     *
     * @param entity
     */
    @Transactional(readOnly = false)
    public void deleteAct(T entity) {
        if (StringUtils.isNotBlank(entity.getProcInsId())) {
            String processDefinitionId = "";

            //Runtime
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
            if (processInstance != null) {
                processDefinitionId = processInstance.getProcessDefinitionId();
                runtimeService.deleteProcessInstance(entity.getProcInsId(), "DeleteCascade");
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(entity.getProcInsId()).list();
                for (Task task : taskList) {
                    taskService.deleteComments(task.getId(), entity.getProcInsId());
                    taskService.deleteTask(task.getId(), true);
                }
            }

            //History
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(entity.getProcInsId()).singleResult();
            if (historicProcessInstance != null) {
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
                historyService.deleteHistoricProcessInstance(entity.getProcInsId());
                List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcInsId()).list();
                for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                    historyService.deleteHistoricTaskInstance(historicTaskInstance.getId());
                }
            }

            //Deploy
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
        }
    }

    /**
     * Delete all deployed processes under the classification (use carefully, not allowed in the production environment).
     *
     * @param processDefinitionCategory
     */
    public void deleteDeploymentProcess(String processDefinitionCategory) {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().processDefinitionCategory(processDefinitionCategory).list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
        }
    }

    public InputStream tracePhoto(String processDefinitionId, String executionId) {
        boolean processInstanceIsEnd = false;
        if (StringUtils.isBlank(processDefinitionId) || Global.NO.equals(processDefinitionId)) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
            if (processInstance == null) {
                processInstanceIsEnd = true;
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(executionId).singleResult();
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            } else {
                processDefinitionId = processInstance.getProcessDefinitionId();
            }
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //extBpmnModel(processInstance, bpmnModel);
        new BpmnAutoLayout(bpmnModel).execute();

        List<String> activeActivityIds = Lists.newArrayList();
        if (runtimeService.createExecutionQuery().executionId(executionId).count() > 0) {
            activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        }

        /* 2019-07-17 流程图流转路线 JINXD —————— 开始*/
        RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl) repositoryService;
        ReadOnlyProcessDefinition deployedProcessDefinition = repositoryServiceImpl.getDeployedProcessDefinition(processDefinitionId);
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) deployedProcessDefinition;

        String processInstanceId = executionId;

        Map<String, Object> historicTaskInstanceMap = Maps.newHashMap();
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            historicTaskInstanceMap.put(historicTaskInstance.getTaskDefinitionKey(), historicTaskInstance.getTaskDefinitionKey());
        }

        Set<String> highLightedFlowSet = Sets.newHashSet();
        for (Map.Entry<String, Object> entry : historicTaskInstanceMap.entrySet()) {
            ActivityImpl activityImpl = processDefinitionEntity.findActivity(entry.getKey());
            List<PvmTransition> incomingTransitions = activityImpl.getIncomingTransitions();
            for (PvmTransition pvmTransition : incomingTransitions) {
                ActivityImpl sourceActivity = (ActivityImpl) pvmTransition.getSource();
                if (sourceActivity.getActivityBehavior() instanceof UserTaskActivityBehavior
                        || sourceActivity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                    if (historicTaskInstanceMap.get(sourceActivity.getId()) == null) {
                        continue;
                    }
                    if (processDefinitionEntity.findActivity(sourceActivity.getId()) != null) {
                        highLightedFlowSet.add(pvmTransition.getId());
                    }
                } else if (sourceActivity.getActivityBehavior() instanceof GatewayActivityBehavior) {
                    List<PvmTransition> sourceIncomingTransitions = sourceActivity.getIncomingTransitions();
                    for (PvmTransition sourcePvmTransition : sourceIncomingTransitions) {
                        ActivityImpl sourceActivity1 = (ActivityImpl) sourcePvmTransition.getSource();
                        if (sourceActivity1.getActivityBehavior() instanceof UserTaskActivityBehavior
                                || sourceActivity1.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                            if (historicTaskInstanceMap.get(sourceActivity1.getId()) == null) {
                                continue;
                            }
                            if (processDefinitionEntity.findActivity(sourceActivity1.getId()) != null) {
                                highLightedFlowSet.add(pvmTransition.getId());
                                highLightedFlowSet.add(sourcePvmTransition.getId());
                            }
                        } else if (sourceActivity1.getActivityBehavior() instanceof GatewayActivityBehavior) {
                            List<PvmTransition> source1IncomingTransitions = sourceActivity1.getIncomingTransitions();
                            for (PvmTransition source1PvmTransition : source1IncomingTransitions) {
                                ActivityImpl sourceActivity2 = (ActivityImpl) source1PvmTransition.getSource();
                                if (sourceActivity2.getActivityBehavior() instanceof UserTaskActivityBehavior
                                        || sourceActivity2.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
                                    if (historicTaskInstanceMap.get(sourceActivity2.getId()) == null) {
                                        continue;
                                    }
                                    if (processDefinitionEntity.findActivity(sourceActivity2.getId()) != null) {
                                        highLightedFlowSet.add(pvmTransition.getId());
                                        highLightedFlowSet.add(sourcePvmTransition.getId());
                                        highLightedFlowSet.add(source1PvmTransition.getId());
                                    }
                                } else if (sourceActivity2.getActivityBehavior() instanceof NoneStartEventActivityBehavior) {
                                    highLightedFlowSet.add(pvmTransition.getId());
                                    highLightedFlowSet.add(sourcePvmTransition.getId());
                                    highLightedFlowSet.add(source1PvmTransition.getId());
                                }
                            }
                        } else if (sourceActivity1.getActivityBehavior() instanceof NoneStartEventActivityBehavior) {
                            highLightedFlowSet.add(pvmTransition.getId());
                            highLightedFlowSet.add(sourcePvmTransition.getId());
                        }
                    }
                } else if (sourceActivity.getActivityBehavior() instanceof NoneStartEventActivityBehavior) {
                    highLightedFlowSet.add(pvmTransition.getId());
                }
            }
            List<PvmTransition> outgoingTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : outgoingTransitions) {
                ActivityImpl destinationActivity = (ActivityImpl) pvmTransition.getDestination();
                if (destinationActivity.getActivityBehavior() instanceof NoneEndEventActivityBehavior && processInstanceIsEnd) {
                    highLightedFlowSet.add(pvmTransition.getId());
                }
            }
        }
        List<String> highLightedFlows = Lists.newArrayList();
        for (String lineId : highLightedFlowSet) {
            highLightedFlows.add(lineId);
        }
        /* 2019-07-17 流程图流转路线 JINXD —————— 结束*/

        // 使用spring注入引擎请使用下面的这行代码
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        DefaultProcessDiagramGenerator processDiagramGeneratornew = new DefaultProcessDiagramGenerator();
        //return processDiagramGeneratornew.generateDiagram(bpmnModel, "png", activeActivityIds);

        for (FlowNode flowNode : bpmnModel.getProcesses().get(0).findFlowElementsOfType(FlowNode.class)) {
            List<SequenceFlow> normalSequence = Lists.newArrayList();
            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                String sourceRef = sequenceFlow.getSourceRef();
                String targetRef = sequenceFlow.getTargetRef();
                FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
                FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
                if (sourceElement != null && targetElement != null) {
                    normalSequence.add(sequenceFlow);
                }
            }
            flowNode.getOutgoingFlows().clear();
            for (SequenceFlow sequenceFlow : normalSequence) {
                flowNode.getOutgoingFlows().add(sequenceFlow);
            }
        }

        return processDiagramGeneratornew.generateDiagram(bpmnModel, "png",
                activeActivityIds, highLightedFlows,
                processEngine.getProcessEngineConfiguration().getActivityFontName(),
                processEngine.getProcessEngineConfiguration().getLabelFontName(),
                processEngine.getProcessEngineConfiguration().getClassLoader(), 1.0);
    }

    public List<Act> histoicFlowList(String procInsId) {
        List<Act> actList = Lists.newArrayList();
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(procInsId).orderByTaskCreateTime().desc().orderByHistoricTaskInstanceEndTime().desc().list();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            if ("deleted".equals(historicTaskInstance.getDeleteReason())) {
                continue;
            }
            Act act = new Act();
            act.setHistTask(historicTaskInstance);
            act.getActMap().put("histTaskName", historicTaskInstance.getName());
            String assignee = historicTaskInstance.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {

            } else {
                assignee = historicTaskInstance.getOwner();
            }
//            User user = UserUtils.getUserMessageByLoginName(assignee);
            assignee = userService.getByLoginName(assignee).getName();
            act.getActMap().put("assignee", assignee);

//            act.getActMap().put("userMessage", user);

            if (historicTaskInstance.getStartTime() != null) {
                act.getActMap().put("startTime", simpleDateFormat.format(historicTaskInstance.getStartTime()));
                if (historicTaskInstance.getEndTime() != null) {
                    act.getActMap().put("endTime", simpleDateFormat.format(historicTaskInstance.getEndTime()));
                    String durationTimeStr = "";
                    long durationInMillis = historicTaskInstance.getEndTime().getTime() - historicTaskInstance.getStartTime().getTime();
                    if (durationInMillis < 1000) {
                        durationTimeStr = "小于1秒";
                    } else {
                        long dayMillis = 84600000;
                        int day = (int) (durationInMillis / dayMillis);
                        if (day > 0) {
                            durationTimeStr += (day + "天");
                            durationInMillis -= (dayMillis * day);
                        }
                        long hourMillis = 3600000;
                        int hour = (int) (durationInMillis / hourMillis);
                        if (hour > 0 || (hour == 0 && day > 0)) {
                            durationTimeStr += (hour + "时");
                            durationInMillis -= (hourMillis * hour);
                        }
                        long minMillis = 60000;
                        int min = (int) (durationInMillis / minMillis);
                        if (min > 0 || (min == 0 && (day > 0 || hour > 0))) {
                            durationTimeStr += (min + "分");
                            durationInMillis -= (minMillis * min);
                        }
                        long secMillis = 1000;
                        int sec = (int) (durationInMillis / secMillis);
                        durationTimeStr += (sec + "秒");
                    }
                    act.getActMap().put("durationTime", durationTimeStr);
                }
            }
            if (StringUtils.isNotBlank(historicTaskInstance.getId())) {
                List<Comment> commentList = taskService.getTaskComments(historicTaskInstance.getId());
                if (commentList.size() > 0) {
                    act.setComment(commentList.get(0).getFullMessage());
                }
            }
            actList.add(act);
        }
        Collections.reverse(actList);
        return actList;
    }
}
