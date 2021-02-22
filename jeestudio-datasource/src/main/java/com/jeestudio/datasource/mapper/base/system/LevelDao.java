package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.Level;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: Level Dao
 * @author: David
 * @Date: 2020-01-14
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface LevelDao extends CrudDao<Level> {
}
