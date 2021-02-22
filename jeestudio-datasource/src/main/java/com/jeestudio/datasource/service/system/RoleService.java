package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.Role;
import com.jeestudio.datasource.mapper.base.system.RoleDao;
import com.jeestudio.datasource.service.common.CrudService;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * @Description: Role Service
 * @author: David
 * @Date: 2020-01-13
 */
@Service
public class RoleService extends CrudService<RoleDao, Role> {

    @Autowired
    private RoleDao roleDao;

    @Transactional(readOnly = true)
    public ResultJson getAuth(String id) {
        ResultJson resultJson = new ResultJson();
        resultJson.put("data", roleDao.getAuth(id));
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取权限成功");
        resultJson.setMsg_en("Get permission success");
        return resultJson;
    }

    @Transactional(readOnly = false)
    public ResultJson saveAuth(String id, String ids) {
        ResultJson resultJson = new ResultJson();
        roleDao.deleteAuthById(id);
        String[] id_s = {};
        if (StringUtil.isNotBlank(ids)) {
            id_s = ids.split(",");
            roleDao.saveAuth(id, Arrays.asList(id_s));
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("设置权限成功");
            resultJson.setMsg_en("Save permission success");
        } else {
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("请选择权限");
            resultJson.setMsg_en("Save permission failed");
        }
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson getAssign(String id) {
        ResultJson resultJson = new ResultJson();
        resultJson.put("data", roleDao.getAssign(id));
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取角色成功");
        resultJson.setMsg_en("Get role success");
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson getDataAssign(String id) {
        ResultJson resultJson = new ResultJson();
        resultJson.put("data", roleDao.getDataAssign(id));
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取数据角色成功");
        resultJson.setMsg_en("Get data role success");
        return resultJson;
    }

    @Transactional(readOnly = false)
    public ResultJson saveAssign(String id, String ids) {
        ResultJson resultJson = new ResultJson();
        roleDao.deleteAssignById(id);
        String[] id_s = {};
        if (StringUtil.isNotBlank(ids)) {
            id_s = ids.split(",");
            roleDao.saveAssign(id, Arrays.asList(id_s));
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("分配用户成功");
            resultJson.setMsg_en("Assign user to role success");
        } else {
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("分配用户失败");
            resultJson.setMsg_en("Assign user to role failed");
        }
        return resultJson;
    }

    @Transactional(readOnly = false)
    public ResultJson saveDataAssign(String id, String ids) {
        ResultJson resultJson = new ResultJson();
        roleDao.deleteDataAssignById(id);
        String[] id_s = {};
        if (StringUtil.isNotBlank(ids)) {
            id_s = ids.split(",");
            roleDao.saveDataAssign(id, Arrays.asList(id_s));
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("分配用户成功");
            resultJson.setMsg_en("Assign user to data role success");
        } else {
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("分配用户失败");
            resultJson.setMsg_en("Assign user to data role failed");
        }
        return resultJson;
    }
}
