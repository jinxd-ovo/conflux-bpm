package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.Organization;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.datasource.mapper.base.system.OrganizationDao;
import com.jeestudio.datasource.service.common.CrudService;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: Organization Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class OrganizationService extends CrudService<OrganizationDao, Organization> {

    @Transactional(readOnly = false)
    public List<User> findUserToOrg(String id) {
        return dao.findUserToOrg(id);
    }

    @Transactional(readOnly = false)
    public void insertUserToOrg(Organization organization, User user) {
        List<User> userList = findUserToOrg(organization.getId());
        if (false == userList.contains(user)) dao.insertUserToOrg(organization, user);
    }

    @Transactional(readOnly = false)
    public void deleteUserToOrg(String userId, String orgId) {
        dao.deleteUserToOrg(userId, orgId);
    }

    @Transactional(readOnly = false)
    public int findOrgNumberBy(String org) {
        return dao.findOrgNumberBy(org);
    }

    @Transactional(readOnly = false)
    public List<Organization> findListByUser(Organization organization) {
        return dao.findListByUser(organization);
    }

    public int getCountByOrgAndUser(String orgId, String userId) {
        int count = dao.getCountByOrgAndUser(orgId, userId);
        return count;
    }

    @Transactional(readOnly = true)
    public ResultJson getAssign(String id) {
        ResultJson resultJson = new ResultJson();
        resultJson.put("data", dao.getAssign(id));
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取用户成功");
        resultJson.setMsg_en("Get user success");
        return resultJson;
    }

    @Transactional(readOnly = false)
    public ResultJson saveAssign(String id, String ids) {
        ResultJson resultJson = new ResultJson();
        dao.deleteAssignById(id);
        String[] id_s = {};
        if (StringUtil.isNotBlank(ids)) {
            id_s = ids.split(",");
            dao.saveAssign(id, Arrays.asList(id_s));
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("分配用户成功");
            resultJson.setMsg_en("Assign user to organization success");
        } else {
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("分配用户失败");
            resultJson.setMsg_en("Assign user to organization failed");
        }
        return resultJson;
    }
}
