package com.jeestudio.datasource.mapper.base.act;

import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.act.TaskSettingVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: TaskPermission Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface TaskPermissionDao extends CrudDao<TaskPermission> {

    List<TaskPermission> findListByIdList(@Param("taskSettingVersionList") List<TaskSettingVersion> taskSettingVersionList);

    List<TaskPermission> findListByPermission(@Param("category") String category, @Param("types") String types);

    TaskPermission findByTaskPermissionId(@Param("permission") String permission, @Param("types") String types);
}