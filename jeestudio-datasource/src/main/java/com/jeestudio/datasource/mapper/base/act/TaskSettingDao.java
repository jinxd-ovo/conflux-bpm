package com.jeestudio.datasource.mapper.base.act;

import org.apache.ibatis.annotations.Param;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.act.TaskSetting;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: TaskSetting Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface TaskSettingDao extends CrudDao<TaskSetting> {

    TaskSetting getByProcAndTask(@Param("taskSetting")TaskSetting taskSetting);

    List<TaskSetting> findListByProcDefKey(@Param("procDefKey")String procDefKey);

    void updateUserTaskId(@Param("oldId")String oldId, @Param("newId")String newId);
}
