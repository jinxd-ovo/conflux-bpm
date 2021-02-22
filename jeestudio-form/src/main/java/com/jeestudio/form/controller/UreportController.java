package com.jeestudio.form.controller;

import com.bstek.ureport.provider.report.ReportFile;
import com.jeestudio.form.service.UreportService;
import com.jeestudio.utils.ResultJson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "${formPath}/ureport")
public class UreportController {

    protected static final Logger logger = LoggerFactory.getLogger(UreportController.class);

    @Autowired
    private UreportService ureportService;

    @RequestMapping(value = "/data")
    public ResultJson data() {
        try {
            List<ReportFile> list = ureportService.getReportFiles();
            ResultJson resultJson = new ResultJson();
            resultJson.put("rows", list);
            resultJson.put("total", list.size());
            resultJson.setRows(list);
            resultJson.setTotal(list.size());
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while getting zform with act:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    @RequestMapping(value = "/delete")
    public ResultJson delete(@RequestParam("id") String id) {
        try {
            ureportService.deleteReport(id);
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while getting zform with act:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    public ResultJson failed() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_FAILED);
        resultJson.setMsg("操作失败");
        resultJson.setMsg_en("Operation failed");
        return resultJson;
    }
}
