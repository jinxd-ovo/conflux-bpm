package com.jeestudio.datasource.service.act.ext;

import com.google.common.collect.Lists;
import com.jeestudio.common.entity.system.Role;
import com.jeestudio.datasource.service.system.RoleService;
import com.jeestudio.datasource.service.system.UserService;
import com.jeestudio.datasource.utils.ActUtil;
import com.jeestudio.datasource.utils.UserUtil;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Description: ActUserEntity Service
 * @author: David
 * @Date: 2020-01-13
 */
public class ActUserEntityService extends UserEntityManager {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    public User createNewUser(String userId) {
        return new UserEntity(userId);
    }

    public void insertUser(User user) {
        throw new RuntimeException("not implement method.");
    }

    public void updateUser(UserEntity updatedUser) {
        throw new RuntimeException("not implement method.");
    }

    public UserEntity findUserById(String userId) {
        return ActUtil.toActivitiUser(UserUtil.getByLoginName(userId));
    }

    public void deleteUser(String userId) {
        User user = findUserById(userId);
        if (user != null) {
            userService.deleteUser(new com.jeestudio.common.entity.system.User(user.getId()));
        }
    }

    public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        throw new RuntimeException("not implement method.");
    }

    public long findUserCountByQueryCriteria(UserQueryImpl query) {
        throw new RuntimeException("not implement method.");
    }

    public List<Group> findGroupsByUser(String userId) {
        List<Group> list = Lists.newArrayList();
        for (Role role : roleService.findList(new Role(new com.jeestudio.common.entity.system.User(null, userId)))){
            list.add(ActUtil.toActivitiGroup(role));
        }
        return list;
    }

    public UserQuery createNewUserQuery() {
        throw new RuntimeException("not implement method.");
    }

    public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
        throw new RuntimeException("not implement method.");
    }

    public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
        throw new RuntimeException("not implement method.");
    }

    public Boolean checkPassword(String userId, String password) {
        throw new RuntimeException("not implement method.");
    }

    public List<User> findPotentialStarterUsers(String proceDefId) {
        throw new RuntimeException("not implement method.");

    }

    public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        return getDbSqlSession().selectListWithRawParameter("selectUserByNativeQuery", parameterMap, firstResult, maxResults);
    }

    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new RuntimeException("not implement method.");
    }
}
