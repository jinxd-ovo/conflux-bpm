package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: Role Dao
 * @author: whl
 * @Date: 2019-12-04
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface RoleDao extends CrudDao<Role> {

    List<String> getAuth(@Param("id") String id);

    void saveAuth(@Param("id") String id, @Param("asList") List<String> asList);

    List<String> getAssign(@Param("id") String id);

    List<String> getDataAssign(@Param("id") String id);

    void saveAssign(@Param("id") String id, @Param("asList") List<String> asList);

    void saveDataAssign(@Param("id") String id, @Param("asList") List<String> asList);

    void deleteAuthById(@Param("id") String id);

    void deleteAssignById(@Param("id") String id);

    void deleteDataAssignById(@Param("id") String id);
}
