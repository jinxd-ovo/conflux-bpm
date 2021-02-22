package com.jeestudio.services.admin.controller.dict;

import com.google.common.collect.Lists;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.system.Dict;
import com.jeestudio.common.entity.system.DictResult;
import com.jeestudio.common.view.common.TreeView;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.JsonConvertUtil;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: Dict Controller
 * @author: whl
 * @Date: 2019-12-31
 */
@Api(value = "DictController ",tags = "Dict Controller")
@RestController
@RequestMapping("${adminPath}/sys/dict")
public class DictController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(DictController.class);

    @Autowired
    private DatasourceFeign datasourceFeign;

    /**
     * Get dict list
     */
    @ApiOperation(value = "dictList",tags = "Get dict list")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/dictList")
    public String dictList(@RequestParam("type") String type){
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(0);
            resultJson.setMsg("获取字典成功");
            resultJson.setMsg_en("Got dictionary successfully");
            resultJson.setToken(token);
            resultJson.put("dict",datasourceFeign.dictList(type));
            return JsonConvertUtil.objectToJson(resultJson);
        }catch (Exception e){
            logger.error("Error occurred while trying to get dictionary list: " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Save dict
     */
    @ApiOperation(value = "saveDict",tags = "Save dict")
    @ApiImplicitParams({@ApiImplicitParam(name = "dict", value = "dict",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/saveDict")
    public ResultJson saveDict(Dict dict){
        try {
            ResultJson resultJson = datasourceFeign.saveDict(dict);
            resultJson.setToken(token);
            return resultJson;
        }catch (Exception e){
            logger.error("Error while saving dict:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get dict labels by values and type
     */
    @ApiOperation(value = "/getDictLabels",tags = "Get dict labels by values and type")
    @ApiImplicitParams({@ApiImplicitParam(name = "values", value = "values",required = true,dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String"),
            @ApiImplicitParam(name = "defaultValue", value = "defaultValue",required = true,dataType = "String"),
            @ApiImplicitParam(name = "lang", value = "lang",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getDictLabels")
    public ResultJson getDictLabels(@RequestParam("values") String values,
                                    @RequestParam("type") String type,
                                    @RequestParam("defaultValue") String defaultValue,
                                    @RequestParam("lang") String lang){
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            resultJson.put("data", datasourceFeign.getDictLabels(values, type, defaultValue, lang));
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while getting dict labels:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get dict values by labels and type
     */
    @ApiOperation(value = "/getDictValues",tags = "Get dict values by labels and type")
    @ApiImplicitParams({@ApiImplicitParam(name = "values", value = "values",required = true,dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String"),
            @ApiImplicitParam(name = "defaultValue", value = "defaultValue",required = true,dataType = "String"),
            @ApiImplicitParam(name = "lang", value = "lang",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getDictValues")
    public ResultJson getDictValues(@RequestParam("labels") String labels,
                                    @RequestParam("type") String type,
                                    @RequestParam("defaultValue") String defaultValue,
                                    @RequestParam("lang") String lang){
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            resultJson.put("data", datasourceFeign.getDictValues(labels, type, defaultValue, lang));
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while getting dict values:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get dict list for view
     */
    @ApiOperation(value = "getDictListView",tags = "Get dict list for view")
    @ApiImplicitParams({@ApiImplicitParam(name = "types", value = "types",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getDictListView")
    public String getDictListView(@RequestParam("types") String types){
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("获取字典成功");
            resultJson.setMsg_en("Get dict success");
            resultJson.setToken(token);
            resultJson.put("data", datasourceFeign.getDictList(types, false));
            return JsonConvertUtil.objectToJson(resultJson).replace("memberNameEn", "memberName_EN");
        } catch (Exception e) {
            logger.error("Error while getting dict list for view:" + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Get dict list for edit
     */
    @ApiOperation(value = "getDictListEdit",tags = "Get dict list for edit")
    @ApiImplicitParams({@ApiImplicitParam(name = "types", value = "types",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getDictListEdit")
    public String getDictListEdit(@RequestParam("types") String types){
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("获取字典成功");
            resultJson.setMsg_en("Get dict success");
            resultJson.setToken(token);
            resultJson.put("data", datasourceFeign.getDictList(types, true));
            return JsonConvertUtil.objectToJson(resultJson);
        } catch (Exception e) {
            logger.error("Error while getting dict list for edit:" + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Get dict tree for view
     */
    @ApiOperation(value = "getDictTreeView",tags = "Get dict tree for view")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getDictTreeView")
    public String getDictTreeView(@RequestParam("type") String type){
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("获取字典成功");
            resultJson.setMsg_en("Get dict success");
            resultJson.setToken(token);
            List<DictResult> list = datasourceFeign.getDictList(type, false);
            List<TreeView> treeList = Lists.newArrayList();
            for(DictResult dictResult : list) {
                TreeView treeView = new TreeView();
                treeView.setId(dictResult.getMember());
                treeView.setName(dictResult.getMemberName());
                treeView.setSort(dictResult.getSort());
                treeView.setHasChildren(dictResult.isHasChildren());
                treeView.setParentId(StringUtil.isEmpty(dictResult.getParentType()) ? "0" : dictResult.getParentType());
                treeList.add(treeView);
            }
            resultJson.put("data", treeList);
            return JsonConvertUtil.objectToJson(resultJson);
        } catch (Exception e) {
            logger.error("Error while getting dict tree for view:" + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Refresh dict cache
     */
    @ApiOperation(value = "/refreshDictCache",tags = "Refresh dict cache")
    @RequiresPermissions("user")
    @GetMapping("/refreshDictCache")
    public ResultJson refreshDictCache(){
        try {
            ResultJson resultJson = datasourceFeign.refreshDictCache();
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while refreshing dict cache:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    @RequiresPermissions("user")
    @GetMapping("/saveProcessCategory")
    public ResultJson saveProcessCategory(@RequestParam("name") String name) {
        try {
            ResultJson resultJson = new ResultJson();

            Zform parent = new Zform();
            parent.setId("261f40db1acd423f89e3f86a736ef3fa");
            parent.setName("流程分类");
            Zform dict = new Zform();
            dict.setFormNo("sys_dictionary");
            dict.setId("");
            dict.setParent(parent);
            dict.setRemarks("");
            dict.setS01(name);
            dict.setS02(name);
            dict.setS03(name);
            dict.setS04(Global.YES);
            dict.setS05("act_category");
            dict.setS06(Global.YES);
            dict.setS07(Global.YES);
            datasourceFeign.saveZform(dict, currentUserName, "/dynamic/zform");

            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("保存成功");
            resultJson.setMsg_en("Save success");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while saving process category:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
