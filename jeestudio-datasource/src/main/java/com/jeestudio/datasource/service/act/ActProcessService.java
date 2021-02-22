package com.jeestudio.datasource.service.act;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.view.act.ProcessView;
import com.jeestudio.utils.Global;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.GatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

@Service
public class ActProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngineFactoryBean processEngine;

    public Page<ProcessView> processList(Page<ProcessView> page, String category) {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion().orderByProcessDefinitionKey().asc();

        if (StringUtils.isNotEmpty(category)){
            processDefinitionQuery.processDefinitionCategory(category);
        }

        page.setCount(processDefinitionQuery.count());
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(page.getFirstResult(), page.getMaxResults());
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            ProcessView processView = new ProcessView();
            processView.setId(processDefinition.getId());
            processView.setCategory(processDefinition.getCategory());
            processView.setKey(processDefinition.getKey());
            processView.setName(processDefinition.getName());
            processView.setVersion(processDefinition.getVersion());
            processView.setSuspended(processDefinition.isSuspended());
            processView.setDeploymentId(processDefinition.getDeploymentId());
            processView.setDeploymentTime(deployment.getDeploymentTime());

            page.getList().add(processView);
        }

        return page;
    }

    public String updateState(String state, String procDefId) {
        if (state.equals("active")) {
            repositoryService.activateProcessDefinitionById(procDefId, true, null);
            return "已激活ID为[" + procDefId + "]的流程定义。";
        } else if (state.equals("suspend")) {
            repositoryService.suspendProcessDefinitionById(procDefId, true, null);
            return "已挂起ID为[" + procDefId + "]的流程定义。";
        }
        return "无操作";
    }

    public void delete(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
    }

    public Model toModel(String procDefId) throws UnsupportedEncodingException, XMLStreamException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

        BpmnJsonConverter converter = new BpmnJsonConverter();
        ObjectNode modelNode = converter.convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setCategory(processDefinition.getCategory());//.getDeploymentId());
        modelData.setDeploymentId(processDefinition.getDeploymentId());
        modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count()+1)));

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

        return modelData;
    }

    public String deploy(String category, MultipartFile file) {
        String message = "";
        String fileName = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();
            Deployment deployment = null;
            String extension = FilenameUtils.getExtension(fileName);
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else if (extension.equals("png")) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (fileName.indexOf("bpmn20.xml") != -1) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (extension.equals("bpmn")) {
                String baseName = FilenameUtils.getBaseName(fileName);
                deployment = repositoryService.createDeployment().addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
            } else {
                message = "不支持的文件类型：" + extension;
            }

            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

            for (ProcessDefinition processDefinition : list) {
                repositoryService.setProcessDefinitionCategory(processDefinition.getId(), category);
                message += "部署成功，流程ID=" + processDefinition.getId() + "<br/>";
            }

            if (list.size() == 0){
                message = "部署失败，没有流程。";
            }

        } catch (Exception e) {
            throw new ActivitiException("部署失败！", e);
        }
        return message;
    }

    public Page<ProcessInstance> runningList(Page<ProcessInstance> page, String procInsId, String procDefKey) {
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

        if (StringUtils.isNotBlank(procInsId)){
            processInstanceQuery.processInstanceId(procInsId);
        }

        if (StringUtils.isNotBlank(procDefKey)){
            processInstanceQuery.processDefinitionKey(procDefKey);
        }

        page.setCount(processInstanceQuery.count());
        page.setList(processInstanceQuery.listPage(page.getFirstResult(), page.getMaxResults()));
        return page;
    }

    public Page<HistoricProcessInstance> historyList(Page<HistoricProcessInstance> page, String procInsId, String procDefKey) {

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().finished()
                .orderByProcessInstanceEndTime().desc();

        if (StringUtils.isNotBlank(procInsId)){
            query.processInstanceId(procInsId);
        }

        if (StringUtils.isNotBlank(procDefKey)){
            query.processDefinitionKey(procDefKey);
        }

        page.setCount(query.count());
        page.setList(query.listPage(page.getFirstResult(), page.getMaxResults()));
        return page;
    }

    public InputStream tracePhoto(String processDefinitionId, String executionId) {
        if (StringUtils.isBlank(processDefinitionId) || Global.NO.equals(processDefinitionId)) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
            processDefinitionId = processInstance.getProcessDefinitionId();
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        new BpmnAutoLayout(bpmnModel).execute();

        List<String> activeActivityIds = Lists.newArrayList();
        if (runtimeService.createExecutionQuery().executionId(executionId).count() > 0){
            activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        }

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
                    if (processDefinitionEntity.findActivity(sourceActivity.getId()) != null) {
                        highLightedFlowSet.add(pvmTransition.getId());
                    }
                } else if (sourceActivity.getActivityBehavior() instanceof GatewayActivityBehavior) {
                    List<PvmTransition> sourceIncomingTransitions = sourceActivity.getIncomingTransitions();
                    for (PvmTransition sourcePvmTransition : sourceIncomingTransitions) {
                        ActivityImpl sourceActivity1 = (ActivityImpl) sourcePvmTransition.getSource();
                        if (sourceActivity1.getActivityBehavior() instanceof UserTaskActivityBehavior
                                || sourceActivity1.getActivityBehavior() instanceof MultiInstanceActivityBehavior) {
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
        }
        List<String> highLightedFlows  = Lists.newArrayList();
        for (String lineId : highLightedFlowSet) {
            highLightedFlows.add(lineId);
        }

        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        DefaultProcessDiagramGenerator processDiagramGeneratornew = new DefaultProcessDiagramGenerator();

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

    public void deleteProcIns(String processInstanceId, String deleteReason) {
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }
}
