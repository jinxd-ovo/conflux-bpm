package com.jeestudio.datasource.service.act.ext;

import com.google.common.collect.Lists;
import com.jeestudio.common.entity.system.Role;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.datasource.utils.ActUtil;
import com.jeestudio.datasource.utils.UserUtil;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: ActGroupEntity Service
 * @author: David
 * @Date: 2020-01-13
 */
@Service
public class ActGroupEntityService extends GroupEntityManager {

    public Group createNewGroup(String groupId) {
        return new GroupEntity(groupId);
    }

    public void insertGroup(Group group) {
        throw new RuntimeException("not implement method.");
    }

    public void updateGroup(GroupEntity updatedGroup) {
        throw new RuntimeException("not implement method.");
    }

    public void deleteGroup(String groupId) {
        throw new RuntimeException("not implement method.");
    }

    public GroupQuery createNewGroupQuery() {
        throw new RuntimeException("not implement method.");
    }

    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
        throw new RuntimeException("not implement method.");
    }

    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
        throw new RuntimeException("not implement method.");
    }

    public List<Group> findGroupsByUser(String userId) {
        List<Group> list = Lists.newArrayList();
        User user = UserUtil.getByLoginName(userId);
        if (user != null && user.getRoleList() != null){
            for (Role role : user.getRoleList()){
                list.add(ActUtil.toActivitiGroup(role));
            }
        }
        return list;
    }

    public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new RuntimeException("not implement method.");
    }

    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new RuntimeException("not implement method.");
    }
}
