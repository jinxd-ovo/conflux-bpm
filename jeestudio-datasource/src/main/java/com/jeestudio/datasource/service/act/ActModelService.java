package com.jeestudio.datasource.service.act;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.act.TaskSetting;
import com.jeestudio.common.entity.act.TaskSettingVersion;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @Description: ActModel Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class ActModelService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskSettingService taskSettingService;

    @Autowired
    private TaskSettingVersionService taskSettingVersionService;

    private static final int X_AXIS_BEGIN = 0;
    private static final int Y_AXIS_BEGIN = 300;
    private static final int X_AXIS_OFFSET = 150;
    private static final int Y_AXIS_OFFSET = 120;

    /**
     * Get model list
     *
     * @param page
     * @param category
     * @param treeId
     * @return model list
     */
    public Page<org.activiti.engine.repository.Model> modelList(Page<Model> page, String category, String treeId) {
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();
        if (StringUtils.isNotEmpty(category)) {
            modelQuery.modelCategory(category);
        }
        if (StringUtils.isNotBlank(treeId) && false == "0".equals(treeId)) {
            modelQuery.modelTenantId(treeId);
        }
        page.setCount(modelQuery.count());
        page.setList(modelQuery.listPage(page.getFirstResult(), page.getMaxResults()));
        return page;
    }

    /**
     * Create model
     *
     * @param name
     * @param key
     * @param description
     * @param category
     * @param scope
     * @param officeId
     * @return model
     * @throws UnsupportedEncodingException
     */
    @Transactional(readOnly = false)
    public org.activiti.engine.repository.Model create(String name, String key, String description, String category, String scope, String officeId) throws UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        org.activiti.engine.repository.Model modelData = repositoryService.newModel();
        description = StringUtils.defaultString(description);
        modelData.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
        modelData.setName(name);
        modelData.setCategory(category);
        modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count() + 1)));
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelObjectNode.put("scope", scope);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setTenantId(officeId);
        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
        return modelData;
    }

    /**
     * Deploy process model
     *
     * @param id
     * @return deploy message
     */
    @Transactional(readOnly = false)
    public String deploy(String id) {
        String message = "";
        try {
            org.activiti.engine.repository.Model modelData = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            ObjectNode editorNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            String processName = modelData.getName();
            if (!StringUtils.endsWith(processName, ".bpmn20.xml")) {
                processName += ".bpmn20.xml";
            }
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
                    .addInputStream(processName, in).deploy();

            //Set Process Category
            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
            for (ProcessDefinition processDefinition : list) {
                repositoryService.setProcessDefinitionCategory(processDefinition.getId(), modelData.getCategory());
                Map<String, String> metaInfoMap = new Gson().fromJson(modelData.getMetaInfo(), Map.class);
                metaInfoMap.put("procDefKey", bpmnModel.getProcesses().get(0).getId());
                modelData.setMetaInfo(new Gson().toJson(metaInfoMap));
                repositoryService.saveModel(modelData);
                message = "Deployed Success, Process ID=" + processDefinition.getId();

                //Process Def Key
                String procDefKey = editorNode.get("properties").get("process_id").textValue();

                //Process Permission
                List<TaskSetting> taskSettingListTemp = taskSettingService.findListByProcDefKey(procDefKey);
                List<ActivityImpl> activities = ((ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(processDefinition.getId())).getActivities();
                List<String> activityImplIdList = Lists.newArrayList();
                for (ActivityImpl activityImpl : activities) {
                    activityImplIdList.add(activityImpl.getId());
                }
                List<TaskSetting> taskSettingList = Lists.newArrayList();
                List<TaskSetting> taskSettingListDelete = Lists.newArrayList();
                for (TaskSetting taskSetting : taskSettingListTemp) {
                    if (activityImplIdList.contains(taskSetting.getUserTaskId())) {
                        taskSettingList.add(taskSetting);
                    } else {
                        taskSettingListDelete.add(taskSetting);
                    }
                }
                for (TaskSetting taskSetting : taskSettingListDelete) {
                    taskSettingService.delete(taskSetting);
                }
                if (taskSettingList.size() > 0) {
                    //Versioned Process Node Permission Set
                    List<TaskSettingVersion> taskSettingVersionList = Lists.newArrayList();
                    //Versioned Process Def ID
                    String procDefId = processDefinition.getId();
                    for (TaskSetting taskSetting : taskSettingList) {
                        TaskSettingVersion taskSettingVersion = new TaskSettingVersion();
                        taskSettingVersion.setProcDefId(procDefId);
                        taskSettingVersion.setUserTaskId(taskSetting.getUserTaskId());
                        taskSettingVersion.setUserTaskName(taskSetting.getUserTaskName());

                        if (StringUtils.isEmpty(taskSetting.getSettingValue())) {
                            taskSettingVersion.setSettingValue("");
                        } else {
                            taskSettingVersion.setSettingValue(taskSetting.getSettingValue());
                        }

                        if (taskSetting.getTaskPermission() == null || StringUtils.isEmpty(taskSetting.getTaskPermission().getId())) {
                            taskSettingVersion.setPermission("");
                        } else {
                            taskSettingVersion.setPermission(taskSetting.getTaskPermission().getId());
                        }

                        if (StringUtils.isEmpty(taskSetting.getRuleArgs())) {
                            taskSettingVersion.setRuleArgs("");
                        } else {
                            taskSettingVersion.setRuleArgs(taskSetting.getRuleArgs());
                        }
                        taskSettingVersionService.save(taskSettingVersion);
                    }
                }
            }
            if (list.size() == 0) {
                message = "Deploy Failed, No Model.";
            }
        } catch (Exception e) {
            throw new ActivitiException("Incorrect Model, Model ID is " + id, e);
        }
        return message;
    }

    /**
     * Export model
     *
     * @param id
     * @param response
     */
    public void export(String id, HttpServletResponse response) {
        try {
            org.activiti.engine.repository.Model modelData = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            ObjectNode editorNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.flushBuffer();
        } catch (Exception e) {
            throw new ActivitiException("Export Xml File of Model Failed, Model ID is " + id, e);
        }
    }

    /**
     * Update category of the model by id
     *
     * @param id       the model id
     * @param category
     */
    @Transactional(readOnly = false)
    public void updateCategory(String id, String category) {
        org.activiti.engine.repository.Model modelData = repositoryService.getModel(id);
        modelData.setCategory(category);
        repositoryService.saveModel(modelData);
    }

    /**
     * Delete model by id
     *
     * @param id
     */
    @Transactional(readOnly = false)
    public void delete(String id) {
        repositoryService.deleteModel(id);
    }

    /**
     * Adjust model diagram
     *
     * @param modelId
     */
    @Transactional(readOnly = false)
    public void adjust(String modelId) {
        try {
            Model model = repositoryService.getModel(modelId);
            JsonNode jsonNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
            ObjectNode objectNode = (ObjectNode) jsonNode;
            JsonNode js = objectNode.get("childShapes");
            String json = js.toString();
            JSONArray jsonArray = JSONArray.parseArray(json);
            int length = jsonArray.size();
            BpmnJsonConverter bpmn = new BpmnJsonConverter();
            BpmnModel bpmnModel = bpmn.convertToBpmnModel(jsonNode);

            extBpmnModel(bpmnModel);

            BpmnJsonConverter bpmnJson = new BpmnJsonConverter();
            JsonNode objectNodes = bpmnJson.convertToJson(bpmnModel);
            JsonNode jsn = objectNodes.get("childShapes");
            String jsonn = jsn.toString();
            String outGoingId = "";
            String endResourceId = "";
            String endTarget = "";
            JSONArray jsonArrayn = JSONArray.parseArray(jsonn);
            for (int k = 0; k < length; k++) {
                JSONObject jsObject = jsonArray.getJSONObject(k);
                JSONObject jsObjectn = jsonArrayn.getJSONObject(k);
                jsObject.put("bounds", jsObjectn.getString("bounds"));
                JSONArray dockerJson = jsObject.getJSONArray("dockers");
                JSONObject stencil = jsObject.getJSONObject("stencil");
                String id = stencil.getString("id");
                String resourceId = jsObject.getString("resourceId");
                String endOutGoing = jsObject.getString("outgoing");
                JSONArray jsonArrayEndOutgoing = JSONArray.parseArray(endOutGoing);
                if (jsonArrayEndOutgoing.size() > 0) {
                    JSONObject jsonEndOutgoing = (JSONObject) jsonArrayEndOutgoing.get(0);
                    endTarget = jsonEndOutgoing.getString("resourceId");
                }
                int dockerLength = dockerJson.size();
                if (dockerLength > 2) {
                    JSONObject first = (JSONObject) dockerJson.get(0);
                    JSONObject zfinal = (JSONObject) dockerJson.get(dockerLength - 1);
                    JSONArray docker = new JSONArray();
                    docker.add(first);
                    docker.add(zfinal);
                    jsObject.put("dockers", docker);
                }
                if ("StartNoneEvent".equals(id)) {
                    String outgoing = jsObject.getString("outgoing");
                    JSONArray jsonArrayOutgoing = JSONArray.parseArray(outgoing);
                    JSONObject jsonOutgoing = (JSONObject) jsonArrayOutgoing.get(0);
                    outGoingId = jsonOutgoing.getString("resourceId");
                } else if ("EndNoneEvent".equals(id)) {
                    endResourceId = jsObject.getString("resourceId");
                }
                if (outGoingId.equals(resourceId)) {
                    JSONObject first = new JSONObject();
                    first.put("x", 20);
                    first.put("y", 20);
                    JSONObject zfinal = (JSONObject) dockerJson.get(dockerLength - 1);
                    JSONArray docker = new JSONArray();
                    docker.add(first);
                    docker.add(zfinal);
                    jsObject.put("dockers", docker);
                }
                if (endResourceId.equals(endTarget)) {
                    JSONObject first = new JSONObject();
                    first.put("x", 20);
                    first.put("y", 20);
                    JSONObject zfinal = (JSONObject) dockerJson.get(0);
                    JSONArray docker = new JSONArray();
                    docker.add(zfinal);
                    docker.add(first);
                    jsObject.put("dockers", docker);
                }
            }
            String jsonNodeString = jsonArray.toString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonNodeString);
            objectNode.put("childShapes", actualObj);
            jsonNode = objectNode;
            String json_xml = jsonNode.toString();
            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Export model
     *
     * @param bpmnModel
     */
    private void extBpmnModel(BpmnModel bpmnModel) {
        Map<String, Object> locationRecursionMap = locationRecursionMap(bpmnModel);
        //redraw nodes
        offsetLocationMap(locationRecursionMap, bpmnModel);
    }

    /**
     * Offset location
     *
     * @param locationRecursionMap
     * @param bpmnModel
     */
    private void offsetLocationMap(Map<String, Object> locationRecursionMap,
                                   BpmnModel bpmnModel) {
        Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
        offsetLocation(locationRecursionMap, locationMap, X_AXIS_BEGIN, Y_AXIS_BEGIN, X_AXIS_BEGIN, Y_AXIS_BEGIN, X_AXIS_OFFSET, Y_AXIS_OFFSET, 1, 0);
    }

    /**
     * Offset location
     *
     * @param locationRecursionMap
     * @param locationMap
     * @param xBegin
     * @param yBegin
     * @param x
     * @param y
     * @param xAxisOffset
     * @param yAxisOffset
     * @param parallelCount
     * @param countI
     */
    private void offsetLocation(Map<String, Object> locationRecursionMap, Map<String, GraphicInfo> locationMap,
                                int xBegin, int yBegin, int x, int y, int xAxisOffset, int yAxisOffset, int parallelCount, int countI) {
        String key = (String) locationRecursionMap.get("key");
        GraphicInfo graphicInfo = locationMap.get(key);
        double py = 20;
        double px = 0;
        if (x == X_AXIS_BEGIN && y == Y_AXIS_BEGIN) {
            px = 50;
        }

        if (graphicInfo.getWidth() > 70) {
            graphicInfo.setWidth(100);
            graphicInfo.setHeight(80);
            py = 0;
        } else {
            graphicInfo.setWidth(40);
            graphicInfo.setHeight(40);
        }

        graphicInfo.setX(x + px);
        x += xAxisOffset;

        if (parallelCount > 1) {
            int halfCount = parallelCount / 2;
            if (parallelCount % 2 == 0) {
                if (countI < halfCount) {
                    y -= ((halfCount - countI) * yAxisOffset);
                } else {
                    y += ((countI - halfCount + 1) * yAxisOffset);
                }
            } else if (parallelCount % 2 == 1) {
                if (countI < halfCount) {
                    y -= ((halfCount - countI) * yAxisOffset);
                } else if (countI == halfCount) {
                    //y = yBegin;
                } else if (countI > halfCount) {
                    y += ((countI - halfCount) * yAxisOffset);
                }
            }
        } else {
            //y = yBegin;
        }
        graphicInfo.setY(y + py);

        List<Map<String, Object>> list = (List<Map<String, Object>>) locationRecursionMap.get("target");
        for (int i = 0; i < list.size(); i++) {
            offsetLocation(list.get(i), locationMap, xBegin, yBegin, x, y, xAxisOffset, yAxisOffset, list.size(), i);
        }
    }

    /**
     * Get location map
     *
     * @param bpmnModel
     * @return location map
     */
    private Map<String, Object> locationRecursionMap(BpmnModel bpmnModel) {
        Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();

        Map<String, FlowElement> flowElementsMap = Maps.newHashMap();
        Map<String, StartEvent> startEventMap = Maps.newLinkedHashMap();
        Map<String, EndEvent> endEventMap = Maps.newLinkedHashMap();
        Map<String, UserTask> userTaskMap = Maps.newLinkedHashMap();
        Map<String, SequenceFlow> sequenceFlowMap = Maps.newLinkedHashMap();
        Map<String, ExclusiveGateway> exclusiveGatewayMap = Maps.newLinkedHashMap();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                startEventMap.put(flowElement.getId(), (StartEvent) flowElement);
                flowElementsMap.put(flowElement.getId(), flowElement);
            } else if (flowElement instanceof EndEvent) {
                endEventMap.put(flowElement.getId(), (EndEvent) flowElement);
                flowElementsMap.put(flowElement.getId(), flowElement);
            } else if (flowElement instanceof UserTask) {
                userTaskMap.put(flowElement.getId(), (UserTask) flowElement);
                flowElementsMap.put(flowElement.getId(), flowElement);
            } else if (flowElement instanceof SequenceFlow) {
                sequenceFlowMap.put(flowElement.getId(), (SequenceFlow) flowElement);
                flowElementsMap.put(flowElement.getId(), flowElement);
            } else if (flowElement instanceof ExclusiveGateway) {
                exclusiveGatewayMap.put(flowElement.getId(), (ExclusiveGateway) flowElement);
                flowElementsMap.put(flowElement.getId(), flowElement);
            }
        }

        Map<String, Object> locationRecursionMap = Maps.newLinkedHashMap();
        for (Map.Entry<String, StartEvent> entry : startEventMap.entrySet()) {
            locationRecursionMap = locationRecursion(entry.getKey(), flowElementsMap, sequenceFlowMap);
        }

        return locationRecursionMap;
    }

    /**
     * Get location map
     *
     * @param key
     * @param flowElementsMap
     * @param sequenceFlowMap
     * @return Location map
     */
    private Map<String, Object> locationRecursion(String key,
                                                  Map<String, FlowElement> flowElementsMap,
                                                  Map<String, SequenceFlow> sequenceFlowMap) {
        Map<String, Object> locationRecursionMap = Maps.newLinkedHashMap();
        locationRecursionMap.put("key", key);

        List<Map<String, Object>> list = Lists.newLinkedList();
        for (Map.Entry<String, SequenceFlow> entry : sequenceFlowMap.entrySet()) {
            String sourceRef = entry.getValue().getSourceRef();
            if (key.equals(sourceRef)) {
                FlowElement flowElement = flowElementsMap.get(entry.getValue().getTargetRef());
                if (flowElement != null) {
                    Map<String, Object> map = locationRecursion(flowElement.getId(), flowElementsMap, sequenceFlowMap);
                    list.add(map);
                }
            }
        }
        locationRecursionMap.put("target", list);

        return locationRecursionMap;
    }

    /**
     * Get latest version model list
     *
     * @param category
     * @return model list
     */
    @Transactional(readOnly = false)
    public List<Model> findList(String category) {
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();
        if (StringUtils.isNotEmpty(category)) {
            modelQuery.modelCategory(category);
        }
        return modelQuery.list();
    }

    /**
     * Get model by id
     *
     * @param modelId
     * @return model
     */
    @Transactional(readOnly = false)
    public Model get(String modelId) {
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        return model;
    }

    /**
     * Update model
     *
     * @param modelId
     * @param scope
     * @param officeId
     */
    @Transactional(readOnly = false)
    public void update(String modelId, String scope, String officeId) {
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        model.setTenantId(officeId);

        Map<String, String> metaInfoMap = null;
        String metaInfo = model.getMetaInfo();
        if (StringUtils.isBlank(metaInfo)) {
            metaInfoMap = Maps.newHashMap();
        } else {
            metaInfoMap = new Gson().fromJson(metaInfo, Map.class);
        }
        if (metaInfoMap != null) {
            metaInfoMap.put("scope", scope);
            model.setMetaInfo(new Gson().toJson(metaInfoMap));
        }

        repositoryService.saveModel(model);
    }
}
