package com.jeestudio.masterdata.controller.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.masterdata.service.system.MainUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "MainUserController ",tags = "Main User Controller")
@RestController
@RequestMapping("${datasourcePath}/mainUser")
public class MainUserController {

    @Autowired
    private MainUserService mainUserService;

    @ApiOperation(value = "/findMainUserList", tags = "Find main user list")
    @GetMapping("/findMainUserList")
    public List<Zform> findAllList() {
        return mainUserService.findList(new Zform());
    }
}
