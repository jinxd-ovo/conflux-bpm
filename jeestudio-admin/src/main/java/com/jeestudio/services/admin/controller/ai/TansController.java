package com.jeestudio.services.admin.controller.ai;

import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Trans Controller
 * @author: David
 * @Date: 2020-02-26
 */
@Api(value = "TransController ",tags = "Trans Controller")
@RestController
@RequestMapping("${adminPath}/ai/trans")
public class TansController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(TansController.class);

    @Autowired
    private DatasourceFeign datasourceFeign;

    @ApiOperation(value = "getTransResult",tags = "Get trans result")
    @ApiImplicitParams({@ApiImplicitParam(name = "query", value = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "from", value = "from",required = true,dataType = "String"),
            @ApiImplicitParam(name = "to", value = "to",required = true,dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getTransResult")
    public ResultJson getTransResult(@RequestParam("query") String query, @RequestParam("from") String from, @RequestParam("to") String to) {
        try {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.put("data", datasourceFeign.getTransResult(query, from, to));
            resultJson.setToken(token);
            return resultJson;
        }catch (Exception e){
            logger.error("Error occurred while trying to get trans result: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
