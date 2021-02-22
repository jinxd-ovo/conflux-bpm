package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.Organization;
import com.jeestudio.common.entity.system.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: Organization Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface OrganizationDao extends CrudDao<Organization> {

    List<User> findUserToOrg(@Param("id") String id);

    void insertUserToOrg(@Param("organization") Organization organization, @Param("user") User user);

    void deleteUserToOrg(@Param("userId") String userId, @Param("orgId") String orgId);

    int findOrgNumberBy(@Param("org") String org);

    List<Organization> findListByUser(@Param("org") Organization org);

    int getCountByOrgAndUser(@Param("orgId") String orgId, @Param("userId") String userId);

    List<String> getAssign(@Param("id") String id);

    void saveAssign(@Param("id") String id, @Param("asList") List<String> asList);

    void deleteAssignById(@Param("id") String id);
}
