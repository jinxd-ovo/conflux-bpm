package com.jeestudio.datasource.mapper.base.act;

import com.jeestudio.common.entity.act.Act;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: Act Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface ActDao extends CrudDao<Act> {

    int updateProcInsIdByBusinessId(Act act);
}
