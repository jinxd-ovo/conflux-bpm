package com.jeestudio.datasource.controller.system;

import com.jeestudio.datasource.service.system.EtlService;
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
 * @Description: ETL Controller
 * @author: houxl
 * @Date: 2020-11-17
 */
@Api(value = "EtlController ",tags = "ETL Controller")
@RestController
@EnableScheduling
@RequestMapping("${datasourcePath}/etl")
public class EtlController {

    protected static final Logger logger = LoggerFactory.getLogger(EtlController.class);

    @Autowired
    EtlService etlService;

    @ApiOperation(value = "/runJobHour", tags = "Run Job Hour")
    @Scheduled(cron = "0 0 * * * ?")
    @PostMapping("/runJobHour")
    public void runJobHour() {
        try {
            long internal = 1;
            etlService.runJob(internal);
        } catch (Exception e) {
            logger.error("Error while running job hour:" + ExceptionUtils.getStackTrace(e));
        }
    }

    @ApiOperation(value = "/runJobDay", tags = "Run Job Day")
    @Scheduled(cron = "0 0 1 * * ?")
    @PostMapping("/runJobDay")
    public void runJobDay() {
        try {
            long internal = 24;
            etlService.runJob(internal);
        } catch (Exception e) {
            logger.error("Error while running job day:" + ExceptionUtils.getStackTrace(e));
        }
    }
}
