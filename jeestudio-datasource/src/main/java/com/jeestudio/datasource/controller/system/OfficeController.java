package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.view.system.OfficeView;
import com.jeestudio.datasource.service.system.OfficeService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Office Controller
 * @author: David
 * @Date: 2020-02-06
 */
@Api(value = "OfficeController ",tags = "Office Controller")
@RestController
@RequestMapping("${datasourcePath}/office")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    /**
     * Get office view list
     */
    @ApiOperation(value = "/viewData",tags = "Get office view list")
    @PostMapping("/viewData")
    public ResultJson viewData(@RequestBody OfficeView officeView) {
        return officeService.findOfficeViewData(officeView);
    }
}
