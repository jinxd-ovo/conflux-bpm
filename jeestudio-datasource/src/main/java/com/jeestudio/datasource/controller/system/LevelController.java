package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.Level;
import com.jeestudio.datasource.service.system.LevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Level Controller
 * @author: David
 * @Date: 2020-02-01
 */
@Api(value = "LevelController ",tags = "Level Controller")
@RestController
@RequestMapping("${datasourcePath}/level")
public class LevelController {

    @Autowired
    private LevelService levelService;

    /**
     * Get level list
     */
    @ApiOperation(value = "/data", tags = "Get level list")
    @PostMapping("/data")
    public Page<Level> data(@RequestBody Level level) {
        return levelService.findPage(new Page<Level>(level.getPageParam().getPageNo(), level.getPageParam().getPageSize(), level.getPageParam().getOrderBy()), level);
    }
}
