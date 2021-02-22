package com.jeestudio.services.admin.service.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.services.admin.controller.system.SecLogController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: SecLog Service
 * @author: David
 * @Date: 2020-06-15
 */
@Service
public class SecLogService {

    protected static final Logger logger = LoggerFactory.getLogger(SecLogController.class);

    public static final String ACTION_QUERY = "查询";
    public static final String ACTION_SAVE = "保存";
    public static final String ACTION_DELETE = "删除";

    @Autowired
    private DatasourceFeign datasourceFeign;

    @Value(value = "${sec.switch}")
    private boolean secSwitch;

    @Async
    public void saveSecLog(String account, String ip, String content, String type, String result) {
        if (secSwitch) {
            datasourceFeign.saveSecLog(account, ip, content, type, Global.YES.equals(result) ? "成功" : "失败");
        }
    }

    @Async
    public void saveSecLogZform(String account, String ip, String result, String formNo, String action) {
        if (secSwitch) {
            datasourceFeign.saveSecLogZform(account, ip, Global.YES.equals(result) ? "成功" : "失败", formNo, action);
        }
    }

    public Boolean getSecSwitch() {
        return this.secSwitch;
    }

    public ResultJson data(Zform zform, String path, String formNo, String traceFlag, String loginName, String ip) {
        ResultJson resultJson = new ResultJson();
        try {
            zform.setFormNo(formNo);
            Page<Zform> page = datasourceFeign.findSecLogData(zform, path, loginName, traceFlag, "", secSwitch);
            resultJson.setRows(page.getList());
            resultJson.setTotal(page.getCount());
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Successful operation");
            this.saveSecLogZform(loginName, ip, Global.YES, formNo, ACTION_QUERY);

        } catch (Exception e) {
            logger.error("Error occurred while trying to get secLog list: " + ExceptionUtils.getStackTrace(e));
            this.saveSecLogZform(loginName, ip, Global.NO, formNo, ACTION_QUERY);
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("操作失败");
            resultJson.setMsg_en("Failed operation");
        }
        return resultJson;
    }

    public ResultJson passwordExpired(String loginName) {
        ResultJson resultJson = new ResultJson();
        try {
            Boolean passwordExpired = false;
            if (this.secSwitch) {
                User currentUser = datasourceFeign.getUserByLoginName(loginName);
                if (currentUser.getPasswordExpiredDate() != null) {
                    passwordExpired = currentUser.getPasswordExpiredDate().before(new Date());
                }
            }
            resultJson.put("passwordExpired", passwordExpired);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
        } catch (Exception e) {
            logger.error("Error occurred while trying to set passwordExpired: " + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
        }
        return resultJson;
    }

    public ResultJson getSecLogSpace() {
        return datasourceFeign.getSecLogSpace();
    }
}
