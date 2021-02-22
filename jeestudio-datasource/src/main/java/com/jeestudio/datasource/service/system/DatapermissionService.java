package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.Datapermission;
import com.jeestudio.datasource.mapper.base.system.DatapermissionDao;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * @Description: Datapermission Service
 * @author: gaoqk
 * @Date: 2020-08-27
 */
@Service
public class DatapermissionService {

    @Autowired
    private DatapermissionDao datapermissionDao;

    public ResultJson getDatapermissionTree() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        Datapermission datapermission = new Datapermission();
        resultJson.put("data",datapermissionDao.findList(datapermission));
        resultJson.setMsg("获取数据权限成功");
        resultJson.setMsg_en("get datapermission success");
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson getPermission(String id) {
        ResultJson resultJson = new ResultJson();
        resultJson.put("data", datapermissionDao.getPermission(id));
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取权限成功");
        resultJson.setMsg_en("Get permission success");
        return resultJson;
    }

    public ResultJson savePermission(String id, String ids) {
        ResultJson resultJson = new ResultJson();
        datapermissionDao.deletePermissionById(id);
        String[] id_s = {};
        if (StringUtil.isNotBlank(ids)) {
            id_s = ids.split(",");
            datapermissionDao.savePermission(id, Arrays.asList(id_s));
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("保存数据权限成功");
            resultJson.setMsg_en("Save data permission success");
        } else {
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("请选择数据权限");
            resultJson.setMsg_en("Save data permission failed");
        }
        return resultJson;
    }
}
