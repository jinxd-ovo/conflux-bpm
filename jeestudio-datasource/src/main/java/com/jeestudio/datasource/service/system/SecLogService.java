package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.datasource.mapper.base.system.SecLogDao;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.utils.IdGen;
import com.jeestudio.utils.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @Description: SecLog Service
 * @author: David
 * @Date: 2020-06-15
 */
@Service
public class SecLogService {

    @Autowired
    private SecLogDao secLogDao;

    @Autowired
    private GenTableService genTableService;

    @Value("${sysSecLogSpace}")
    private int sysSecLogSpace;

    @Value("${sysSecLogSpaceWarningPercent}")
    private int sysSecLogSpaceWarningPercent;

    public void saveSecLog(String account, String ip, String content, String type, String result) {
        String id = IdGen.uuid();
        Date time = new Date();
        secLogDao.saveSecLog(id, account, content, time, ip, type, result);
    }

    public void saveSecLogZform(String account, String ip, String result, String formNo, String action) {
        String id = IdGen.uuid();
        Date time = new Date();
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        String content = action + genTable.getComments() + result;
        String type = action + genTable.getComments();
        secLogDao.saveSecLog(id, account, content, time, ip, type, result);
    }

    public ResultJson getSecLogSpace() {
        ResultJson resultJson = new ResultJson();
        Double logSize = Double.parseDouble(secLogDao.getSecLogSpace());
        Double logSpacePercent = logSize / sysSecLogSpace * 100;
        if (logSpacePercent > sysSecLogSpaceWarningPercent) {
            resultJson.put("color", "red");
        } else {
            resultJson.put("color", "green");
        }
        resultJson.put("result",
                Double.valueOf(String.format("%.2f", logSize)) + "M, "
                        + Double.valueOf(String.format("%.2f", logSpacePercent)) + "%");
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        return resultJson;
    }
}
