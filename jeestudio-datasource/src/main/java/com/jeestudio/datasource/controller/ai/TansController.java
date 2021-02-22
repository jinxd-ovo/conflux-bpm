package com.jeestudio.datasource.controller.ai;

import com.jeestudio.datasource.service.ai.TransService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @Description: Trans Controller
 * @author: David
 * @Date: 2020-03-05
 */
@Api(value = "TransController ",tags = "Trans Controller")
@RestController
@RequestMapping("${datasourcePath}/ai/trans")
public class TansController {

    @Autowired
    private TransService transService;

    /**
     * Get translated result
     *
     * @param query
     * @param from
     * @param to
     * @return translated result
     * @throws UnsupportedEncodingException
     */
    @ApiOperation(value = "getTransResult", tags = "Get trans result")
    @ApiImplicitParams({@ApiImplicitParam(name = "query", value = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "from", value = "from", required = true, dataType = "String"),
            @ApiImplicitParam(name = "to", value = "to", required = true, dataType = "String")})
    @RequiresPermissions("user")
    @GetMapping("/getTransResult")
    public String getTransResult(@RequestParam("query") String query, @RequestParam("from") String from, @RequestParam("to") String to) throws UnsupportedEncodingException {
        return transService.getTransResult(query, from, to);
    }
}
