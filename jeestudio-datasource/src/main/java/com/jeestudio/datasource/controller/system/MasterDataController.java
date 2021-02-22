package com.jeestudio.datasource.controller.system;

import com.jeestudio.datasource.service.system.MasterDataService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Master Data Controller
 * @author: David
 * @Date: 2020-09-16
 */
@Api(value = "MasterDataController ",tags = "Master Data Controller")
@RestController
@EnableScheduling
@RequestMapping("${datasourcePath}/masterData")
public class MasterDataController {

    protected static final Logger logger = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private MasterDataService masterDataService;

    /**
     * Sync master data
     */
    @ApiOperation(value = "/syncMasterData", tags = "Sync master data")
    @Scheduled(cron = "0 0 7 * * ?")
    @PostMapping("/syncMasterData")
    public ResultJson syncMasterData() {
        ResultJson resultJson = new ResultJson();
        try {
            masterDataService.syncMasterData();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("同步主数据成功");
            resultJson.setMsg_en("Sync master data success");
        } catch (Exception e) {
            logger.error("Error while doing sync master data:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("同步主数据失败");
            resultJson.setMsg_en("Sync master data failed");
        }
        return resultJson;
    }
}
