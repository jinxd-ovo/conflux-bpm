package com.jeestudio.datasource.controller.act;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.datasource.service.act.ActModelService;
import com.jeestudio.datasource.service.system.OfficeService;
import com.jeestudio.utils.JsonConvertUtil;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Model Controller
 * @author: David
 * @Date: 2020-01-15
 */
@Api(value = "ModelController ",tags = "Model")
@RestController
@RequestMapping("act/model")
public class ModelController implements ModelDataJsonConstants {

    protected static final Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActModelService actModelService;

    @Autowired
    private OfficeService officeService;

    /**
     * Save model
     *
     * @param modelId
     * @param json_xml
     * @param svg_xml
     * @param name
     * @param description
     * @return reulst message
     */
    @ApiOperation(value = "/save", tags = "Save model")
    @PostMapping("/save")
    public String saveModel(@RequestParam String modelId,
                            @RequestParam("json_xml") String json_xml,
                            @RequestParam("svg_xml") String svg_xml,
                            @RequestParam("name") String name,
                            @RequestParam("description") String description) {
        ResultJson resultJson = new ResultJson();
        try {
            Model model = repositoryService.getModel(modelId);
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put(MODEL_NAME, name);
            modelJson.put(MODEL_DESCRIPTION, description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));
            InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
            TranscoderInput input = new TranscoderInput(svgStream);
            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();
        } catch (Exception e) {
            logger.error("Error while saving model:" + ExceptionUtils.getStackTrace(e));
            resultJson.setMsg("保存模型失败");
            resultJson.setMsg_en("Save model failed");
            resultJson.setCode(ResultJson.CODE_FAILED);
        }
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("保存模型成功");
        resultJson.setMsg_en("Save model success");
        return JsonConvertUtil.objectToJson(resultJson);
    }

    /**
     * Get editor Json
     *
     * @param modelId
     * @return Json string
     */
    @ApiOperation(value = "json", tags = "Get editor Json")
    @GetMapping("/json")
    public ObjectNode getEditorJson(@RequestParam("modelId") String modelId) {
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(MODEL_NAME, model.getName());
                }
                modelNode.put(MODEL_ID, model.getId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                        new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                modelNode.put("model", editorJsonNode);

            } catch (Exception e) {
                logger.error("Error while getting editor Json:" + ExceptionUtils.getStackTrace(e));
            }
        } else {
            logger.info("Editor Json model is null, model id:" + modelId);
        }
        return modelNode;
    }

    /**
     * Get model list
     *
     * @param category
     * @param treeId
     * @param pageNo
     * @param pageSize
     * @return model list map
     */
    @ApiOperation(value = "list", tags = "Get model list")
    @GetMapping("/list")
    public LinkedHashMap<String, Object> getModelList(@RequestParam("category") String category, @RequestParam("treeId") String treeId, @RequestParam("pageNo") String pageNo, @RequestParam("pageSize") String pageSize) {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        try {
            Page<org.activiti.engine.repository.Model> page = new Page<>();
            page.setPageNo(Integer.parseInt(pageNo));
            page.setPageSize(Integer.parseInt(pageSize));
            page = actModelService.modelList(page, category, treeId);
            List<org.activiti.engine.repository.Model> modelList = page.getList();
            long modelListCount = page.getCount();

            List<Map<String, Object>> dataList = Lists.newArrayList();
            for (org.activiti.engine.repository.Model m : modelList) {
                Map<String, Object> modelMap = Maps.newHashMap();
                modelMap.put("id", m.getId());
                modelMap.put("category", m.getCategory());
                modelMap.put("key", m.getKey());
                modelMap.put("name", m.getName());
                modelMap.put("version", m.getVersion());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                modelMap.put("createTime", simpleDateFormat.format(m.getCreateTime()));
                modelMap.put("lastUpdateTime", simpleDateFormat.format(m.getLastUpdateTime()));
                modelMap.put("metaInfo", new Gson().fromJson(m.getMetaInfo(), Map.class));
                if (StringUtils.isNotBlank(m.getTenantId())) {
                    modelMap.put("officeName", officeService.get(m.getTenantId()).getName());
                }
                dataList.add(modelMap);
            }

            map.put("rows", dataList);
            map.put("total", modelListCount);
        } catch (Exception e) {
            logger.error("Error while getting model list:" + ExceptionUtils.getStackTrace(e));
        }
        return map;
    }

    /**
     * Get model data
     *
     * @param modelId
     * @return result message
     */
    @ApiOperation(value = "data", tags = "Get model data")
    @GetMapping("/data")
    public ResultJson getModelData(@RequestParam("modelId") String modelId) {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(0);
            resultJson.setMsg("获取模型数据成功");
            resultJson.setMsg_en("Get model data success");
            org.activiti.engine.repository.Model m = actModelService.get(modelId);
            LinkedHashMap<String, Object> modelMap = Maps.newLinkedHashMap();
            modelMap.put("id", m.getId());
            modelMap.put("category", m.getCategory());
            modelMap.put("key", m.getKey());
            modelMap.put("name", m.getName());
            modelMap.put("version", m.getVersion());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            modelMap.put("createTime", simpleDateFormat.format(m.getCreateTime()));
            modelMap.put("lastUpdateTime", simpleDateFormat.format(m.getLastUpdateTime()));
            modelMap.put("metaInfo", new Gson().fromJson(m.getMetaInfo(), Map.class));
            if (StringUtils.isNotBlank(m.getTenantId())) {
                Office office = new Office();
                office.setId(m.getTenantId());
                office.setName(officeService.get(m.getTenantId()).getName());
                modelMap.put("office", office);
                modelMap.put("officeId", m.getTenantId());
                modelMap.put("officeName", officeService.get(m.getTenantId()).getName());
            } else {
                modelMap.put("office", null);
                modelMap.put("officeId", "");
                modelMap.put("officeName", "");
            }
            resultJson.setData(modelMap);
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("获取模型数据失败");
            resultJson.setMsg_en("Get model data failed");
            logger.error("Error while getting model data:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
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
     * @return result message
     */
    @ApiOperation(value = "create", tags = "Create model")
    @GetMapping("/create")
    public ResultJson createModel(@RequestParam("name") String name, @RequestParam("key") String key,
                                  @RequestParam("description") String description, @RequestParam("category") String category,
                                  @RequestParam("scope") String scope, @RequestParam("officeId") String officeId) {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(0);
            resultJson.setMsg("创建模型数据成功");
            resultJson.setMsg_en("Create model data success");
            actModelService.create(name, key, description, category, scope, officeId);
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("创建模型数据失败");
            resultJson.setMsg_en("Create model data failed");
            logger.error("Error while creating model data:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }

    /**
     * Update model
     *
     * @param modelId
     * @param scope
     * @param officeId
     * @return result message
     */
    @ApiOperation(value = "update", tags = "Update model")
    @GetMapping("/update")
    public ResultJson updateModel(@RequestParam("modelId") String modelId, @RequestParam("scope") String scope, @RequestParam("officeId") String officeId) {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(0);
            resultJson.setMsg("更新模型数据成功");
            resultJson.setMsg_en("Update model data success");
            actModelService.update(modelId, scope, officeId);
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("更新模型数据失败");
            resultJson.setMsg_en("Update model data failed");
            logger.error("Error while updating model data:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }

    /**
     * Deploy model
     *
     * @param id
     * @return result message
     */
    @ApiOperation(value = "deploy", tags = "Deploy model")
    @GetMapping("/deploy")
    public ResultJson deployModel(@RequestParam("id") String id) {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(0);
            resultJson.setMsg("部署模型数据成功");
            resultJson.setMsg_en("Deploy model data success");
            actModelService.deploy(id);
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("部署模型数据失败");
            resultJson.setMsg_en("Deploy model data failed");
            logger.error("Error while deploying model data:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }

    /**
     * Delete model
     *
     * @param id
     * @return result message
     */
    @ApiOperation(value = "delete", tags = "Delete model")
    @GetMapping("/delete")
    public ResultJson deleteModel(@RequestParam("id") String id) {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(0);
            resultJson.setMsg("删除模型数据成功");
            resultJson.setMsg_en("Delete model data success");
            actModelService.delete(id);
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("删除模型数据失败");
            resultJson.setMsg_en("Delete model data failed");
            logger.error("Error while deleting model data:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }
}
