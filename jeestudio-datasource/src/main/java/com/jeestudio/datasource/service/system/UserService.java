package com.jeestudio.datasource.service.system;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.entity.tagtree.TagTree;
import com.jeestudio.common.view.system.UserView;
import com.jeestudio.datasource.feign.CacheFeign;
import com.jeestudio.datasource.mapper.base.system.OfficeDao;
import com.jeestudio.datasource.mapper.base.system.UserDao;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.DateUtil;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: User Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private OfficeDao officeDao;

    @Autowired
    private CacheFeign cacheFeign;

    public List<User> findUserListByOfficeIdList(List<String> officeIdList) {
        return userDao.findUserListByOfficeIdList(officeIdList);
    }

    public List<User> findUserListByLevelIdList(List<String> levelIdList) {
        return userDao.findUserListByLevelIdList(levelIdList);
    }

    public List<User> findUserListByPostIdList(List<String> postIdList) {
        return userDao.findUserListByPostIdList(postIdList);
    }

    public List<User> findUserListByRoleIdList(List<String> roleIdList) {
        return userDao.findUserListByRoleIdList(roleIdList);
    }

    public List<User> findUserListByUserIdList(List<String> userIdList) {
        return userDao.findUserListByUserIdList(userIdList);
    }

    public List<User> findUserListByLoginNameList(List<String> assgineeList) {
        return userDao.findUserListByLoginNameList(assgineeList);
    }

    public List<User> findUserListByOrgIdList(List<String> orgIdList) {
        return userDao.findUserListByOrgIdList(orgIdList);
    }

    @Transactional(readOnly = false)
    public void deleteUser(User user) {
        userDao.delete(user);
        userDao.deleteUserRole(user);
        cacheFeign.deleteHash(Global.USER_CACHE, "_" + user.getLoginName());
    }

    public ResultJson getUserTagTreeAsync(String id) {
        ResultJson resultJson = new ResultJson();
        Office office = new Office();
        User user = new User();
        user.setUseable(Global.YES);
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取用户成功");
        resultJson.setMsg_en("Get user success");
        List<TagTree> userList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(id)) {
            office.setId(id);
            user.setOffice(office);
            office.setParent(new Office(id));
            userList = userDao.findUserTagTree(user);
            for (TagTree tagTree : userList) {
                tagTree.setHasChildren(false);
            }
        } else {
            office.setParent(new Office(Global.DEFAULT_ROOT_CODE));
        }
        List<TagTree> officeList = officeDao.findOfficeTagTree(office);
        for (TagTree tagTree : officeList) {
            tagTree.setHasChildren(true);
            tagTree.setDisabled(true);
        }
        userList.addAll(officeList);
        resultJson.put("data", userList);
        return resultJson;
    }

    public List<User> findUserForFlow(String condition) {
        List<User> list = userDao.findUserForFlow(condition);
        return list;
    }

    public ResultJson getUserTagTree(String loginName) {
        ResultJson resultJson = new ResultJson();
        User user = new User();
        user.setUseable(Global.YES);
        user.setLoginName(loginName);
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setMsg("获取用户成功");
        resultJson.setMsg_en("Get user success");
        List<TagTree> userList = new ArrayList<TagTree>();
        userList = userDao.findUserTagTreeAll(user);
        List<TagTree> officeList = officeDao.findOfficeTagTreeAll();
        for(TagTree office : officeList) {
            office.setDisabled(true);
        }
        userList.addAll(officeList);
        Map<String, TagTree> map = Maps.newHashMap();
        for (TagTree tagTree : userList) {
            map.put(tagTree.getId(), tagTree);
        }
        for (TagTree tagTree : userList) {
            if (StringUtil.isNotBlank(tagTree.getParentId())) {
                TagTree tt = map.get(tagTree.getParentId());
                if (tt != null) {
                    tt.setHasChildren(true);
                }
            }
        }
        resultJson.put("data", userList);
        return resultJson;
    }

    public UserView getUserView(String userId) {
        return userDao.getUserView(userId);
    }

    public User getByLoginName(String loginName) {
        User user = new User();
        user.setLoginName(loginName);
        return userDao.getByLoginName(user);
    }

    public List<UserView> findUserViewByName(String name) {
        return userDao.findUserViewByName(name);
    }

    public Integer getLoginExceptionCount(String loginName) {
        return userDao.getLoginExceptionCount(loginName);
    }

    public void clearLoginExceptionCount(String loginName) {
        userDao.clearLoginExceptionCount(loginName);
    }

    public Date isPasswordExpired(String loginName) {
        return userDao.isPasswordExpired(loginName);
    }

    public void unlockUser(String loginName) {
        userDao.unlockUser(loginName);
        cacheFeign.deleteHash(Global.USER_CACHE, "_" + loginName);
    }

    public void lockUser(String loginName) {
        userDao.lockUser(loginName);
        cacheFeign.deleteHash(Global.USER_CACHE, "_" + loginName);
    }

    public void addLoginExceptionCount(String loginName) {
        userDao.addLoginExceptionCount(loginName);
    }

    public void changePassword(String password, String loginName) {
        password = UserUtil.encryptPassword(password);
        Date passwordExpiredDate = DateUtil.addDays(new Date(), 7);
        userDao.changePassword(password, passwordExpiredDate, loginName);
        cacheFeign.deleteHash(Global.USER_CACHE, "_" + loginName);
    }

    @Transactional(readOnly = true)
    public void updateByMasterData(User user) {
        userDao.updateByMasterData(user);
    }
}
