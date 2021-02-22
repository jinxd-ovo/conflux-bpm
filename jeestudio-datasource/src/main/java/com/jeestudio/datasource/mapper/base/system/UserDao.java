package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.entity.tagtree.TagTree;
import com.jeestudio.common.view.system.UserView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description: User Dao
 * @author: whl
 * @Date: 2019-12-04
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface UserDao extends CrudDao<User> {

    User getByLoginName(User user);

    List<User> findUserListByOfficeIdList(@Param("officeIdList") List<String> officeIdList);

    List<User> findUserListByLevelIdList(@Param("levelIdList") List<String> levelIdList);

    List<User> findUserListByPostIdList(@Param("postIdList") List<String> postIdList);

    List<User> findUserListByRoleIdList(@Param("roleIdList") List<String> roleIdList);

    List<User> findUserListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<User> findUserListByLoginNameList(@Param("assgineeList") List<String> assgineeList);

    List<User> findUserListByOrgIdList(@Param("orgIdList") List<String> orgIdList);

    int deleteUserRole(User user);

    List<TagTree> findUserTagTree(User user);

    List<User> findUserForFlow(@Param("condition") String condition);

    List<TagTree> findUserTagTreeAll(User user);

    UserView getUserView(@Param("userId") String userId);

    List<UserView> findUserViewByName(@Param("name") String name);

    Integer getLoginExceptionCount(@Param("loginName") String loginName);

    void clearLoginExceptionCount(@Param("loginName") String loginName);

    Date isPasswordExpired(@Param("loginName") String loginName);

    void unlockUser(@Param("loginName") String loginName);

    void lockUser(@Param("loginName") String loginName);

    void addLoginExceptionCount(@Param("loginName") String loginName);

    void changePassword(@Param("password") String password, @Param("passwordExpiredDate") Date passwordExpiredDate, @Param("loginName") String loginName);

    void updateByMasterData(User user);
}
