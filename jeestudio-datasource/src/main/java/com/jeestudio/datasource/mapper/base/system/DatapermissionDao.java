package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.Datapermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: Datapermission Dao
 * @author: David
 * @Date: 2020-08-27
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface DatapermissionDao extends CrudDao<Datapermission> {

    List<Datapermission> findListByUserId(@Param("userId") String userId);

    List<String> getPermission(@Param("id") String id);

    void deletePermissionById(@Param("id") String id);

    void savePermission(@Param("id") String id, @Param("asList") List<String> asList);
}
