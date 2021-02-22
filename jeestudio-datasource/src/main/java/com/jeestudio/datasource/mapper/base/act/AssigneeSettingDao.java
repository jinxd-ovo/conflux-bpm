package com.jeestudio.datasource.mapper.base.act;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.act.AssigneeSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: Act Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface AssigneeSettingDao extends CrudDao<AssigneeSetting> {

    List<AssigneeSetting> getAssigneeListByUserId(@Param("userId")String userId);
}
