package com.jeestudio.services.admin.controller.act;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.reflect.TypeToken;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.JsonConvertUtil;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * @Description: Model Controller
 * @author: David
 * @Date: 2020-01-15
 */
@Api(value = "ModelController ",tags = "Model Controller")
@RestController
@RequestMapping("${adminPath}/service")
public class ModelController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(ModelController.class);
    
    final String MODEL_ID = "modelId";
    final String MODEL_NAME = "name";
    final String MODEL_REVISION = "revision";
    final String MODEL_DESCRIPTION = "description";

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get stencilset
     */
    @ApiOperation(value = "getStencilset",tags = "Get stencilset")
    @RequiresPermissions("user")
    @RequestMapping(value="/editor/stencilset", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getStencilset() {
        InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
        try {
            String stencilset = IOUtils.toString(stencilsetStream, "utf-8");
            stencilset = stencilset.replaceAll("\"","\\\"");
            return stencilset;
        } catch (Exception e) {
            logger.error("Error occurred while trying to load stencil set: " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Get editor json
     */
    @ApiOperation(value = "getEditorJson",tags = "Get editor json")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/{modelId}/json", method = RequestMethod.GET, produces = "application/json")
    public ResultJson getEditorJson(@PathVariable String modelId) {
        logger.info("Model ID: " + modelId);
        try {
            ResultJson resultJson = new ResultJson();
            ObjectNode modelNode = datasourceFeign.getEditorJson(modelId);
            resultJson.put("model", modelNode);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get editor json: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Save model
     */
    @ApiOperation(value = "saveModel",tags = "Save model")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultJson saveModel(@PathVariable String modelId, @RequestParam("json_xml") String json_xml, @RequestParam("svg_xml") String svg_xml, @RequestParam("name") String name, @RequestParam("description") String description) {
        try {
            String result = datasourceFeign.saveModel(modelId, json_xml, svg_xml, name, description);
            ResultJson resultJson = JsonConvertUtil.gsonBuilder().fromJson(result, new TypeToken<ResultJson>(){}.getType());
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to save model: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get model list
     */
    @ApiOperation(value = "getModelList",tags = "Get model list")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/list", method = RequestMethod.GET, produces = "application/json")
    public ResultJson getModelList(String category, String treeId, String pageNo, String pageSize) {
        try {
            ResultJson resultJson = new ResultJson();
            String result = datasourceFeign.findModelList(category, treeId, pageNo, pageSize);
            LinkedHashMap<String, Object> map = JsonConvertUtil.gsonBuilder().fromJson(result, new TypeToken<LinkedHashMap<String, Object>>(){}.getType());
            if (map != null && map.size() > 0) {
                if (map.get("rows") != null) {
                    resultJson.setRows(map.get("rows"));
                }
                if (map.get("total") != null) {
                    resultJson.setTotal(map.get("total"));
                }
            }
            resultJson.setData(map);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get model list:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Create model
     */
    @ApiOperation(value = "createModel",tags = "Create model")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/create", method = RequestMethod.GET, produces = "application/json")
    public ResultJson createModel(String name, String key, String description, String category, String scope, String officeId) {
        try {
            ResultJson resultJson = datasourceFeign.createModel(name, key, description, category, scope, officeId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to create model:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get model data
     */
    @ApiOperation(value = "getModelData",tags = "Get model data")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/data", method = RequestMethod.GET, produces = "application/json")
    public ResultJson getModelData(String modelId) {
        try {
            ResultJson resultJson = datasourceFeign.getModelData(modelId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get model data: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Update model
     */
    @ApiOperation(value = "updateModel",tags = "Update model")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/update", method = RequestMethod.GET, produces = "application/json")
    public ResultJson updateModel(String modelId, String scope, String officeId) {
        try {
            ResultJson resultJson = datasourceFeign.updateModel(modelId, scope, officeId);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to update model: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Deploy model
     */
    @ApiOperation(value = "deployModel",tags = "Deploy model")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/deploy", method = RequestMethod.GET, produces = "application/json")
    public ResultJson deployModel(String id) {
        try {
            ResultJson resultJson = datasourceFeign.deployModel(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to deploy model: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Delete model
     */
    @ApiOperation(value = "deleteModel",tags = "Delete model")
    @RequiresPermissions("user")
    @RequestMapping(value="/model/delete", method = RequestMethod.GET, produces = "application/json")
    public ResultJson deleteModel(String id) {
        try {
            ResultJson resultJson = datasourceFeign.deleteModel(id);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete model: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
