package com.jeestudio.datasource.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.jeestudio.common.entity.system.Dict;
import com.jeestudio.common.entity.system.DictResult;
import com.jeestudio.datasource.service.system.DictDataService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: Dict Controller
 * @author: whl
 * @Date: 2020-01-14
 */
@Api(value = "DictController ",tags = "Dict Controller")
@RestController
@RequestMapping("${datasourcePath}/dict")
public class DictController {

    @Autowired
    private DictDataService dictDataService;

    /**
     * Get dict list
     */
    @ApiOperation(value = "/dictList",tags = "Get dict list")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String")})
    @GetMapping("/dictList")
    public List<DictResult> dictList(@RequestParam("type") String type){
        return dictDataService.dictTypes(type);
    }

    /**
     * Save dict
     */
    @ApiOperation(value = "/saveDict",tags = "Save dict")
    @ApiImplicitParams({@ApiImplicitParam(name = "dict", value = "dict",required = true,dataType = "Dict")})
    @GetMapping("/saveDict")
    public ResultJson saveDict(@RequestBody Dict dict){
        return dictDataService.save(dict);
    }

    /**
     * Get dict labels by values and type
     */
    @ApiOperation(value = "/getDictLabels",tags = "Get dict labels by values and type")
    @ApiImplicitParams({@ApiImplicitParam(name = "values", value = "values",required = true,dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String"),
            @ApiImplicitParam(name = "defaultValue", value = "defaultValue",required = true,dataType = "String"),
            @ApiImplicitParam(name = "lang", value = "lang",required = true,dataType = "String")})
    @GetMapping("/getDictLabels")
    public String getDictLabels(@RequestParam("values") String values,
                                @RequestParam("type") String type,
                                @RequestParam("defaultValue") String defaultValue,
                                @RequestParam("lang") String lang){
        return dictDataService.getDictLabels(values, type, defaultValue, lang);
    }

    /**
     * Get dict value(s) by type and labels
     */
    @ApiOperation(value = "/getDictValues",tags = "Get dict value(s) by type and labels")
    @ApiImplicitParams({@ApiImplicitParam(name = "labels", value = "labels",required = true,dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type",required = true,dataType = "String"),
            @ApiImplicitParam(name = "defaultValue", value = "defaultValue",required = true,dataType = "String"),
            @ApiImplicitParam(name = "lang", value = "lang",required = true,dataType = "String")})
    @GetMapping("/getDictValues")
    public String getDictValues(@RequestParam("labels") String labels,
                                @RequestParam("type") String type,
                                @RequestParam("defaultValue") String defaultValue,
                                @RequestParam("lang") String lang){
        return dictDataService.getDictValues(labels, type, defaultValue, lang);
    }

    /**
     * Get dict list by types
     */
    @ApiOperation(value = "/getDictList",tags = "Get dict list by types")
    @ApiImplicitParams({@ApiImplicitParam(name = "types", value = "types",required = true,dataType = "String"),
            @ApiImplicitParam(name = "isEdit", value = "isEdit",required = true,dataType = "Boolean")})
    @GetMapping("/getDictList")
    public List<DictResult> getDictList(@RequestParam("types") String types, @RequestParam("isEdit") Boolean isEdit){
        return dictDataService.getDictList(types, isEdit);
    }

    /**
     * Get dict list by types
     */
    @ApiOperation(value = "/getDictListForApp",tags = "Get dict list by types")
    @ApiImplicitParams({@ApiImplicitParam(name = "types", value = "types",required = true,dataType = "String"),
            @ApiImplicitParam(name = "isEdit", value = "isEdit",required = true,dataType = "Boolean")})
    @GetMapping("/getDictListForApp")
    public JSONObject getDictListForApp(@RequestParam("types") String types, @RequestParam("isEdit") Boolean isEdit){
        return dictDataService.getDictListForApp(types, isEdit);
    }

    /**
     * Refresh dict cache
     */
    @ApiOperation(value = "/refreshDictCache",tags = "Refresh dict cache")
    @GetMapping("/refreshDictCache")
    public ResultJson refreshDictCache(){
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        dictDataService.refreshDictCache();
        resultJson.setMsg("刷新字典缓存成功");
        resultJson.setMsg_en("Refresh dictionary cache success");
        return resultJson;
    }
}
