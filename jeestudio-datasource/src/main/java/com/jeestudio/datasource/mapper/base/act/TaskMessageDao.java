package com.jeestudio.datasource.mapper.base.act;

import com.jeestudio.common.entity.act.TaskMessage;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: TaskMessage Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface TaskMessageDao extends CrudDao<TaskMessage> {
}
