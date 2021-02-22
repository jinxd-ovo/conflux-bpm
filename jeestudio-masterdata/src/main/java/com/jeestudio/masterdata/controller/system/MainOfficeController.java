package com.jeestudio.masterdata.controller.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.masterdata.service.system.MainOfficeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "MainOfficeController ",tags = "Main Office Controller")
@RestController
@RequestMapping("${datasourcePath}/mainOffice")
public class MainOfficeController {

    @Autowired
    private MainOfficeService mainOfficeService;

    @ApiOperation(value = "/findMainOfficeList", tags = "Find main office list")
    @GetMapping("/findMainOfficeList")
    public List<Zform> findList() {
        return mainOfficeService.findList(new Zform());
    }
}
