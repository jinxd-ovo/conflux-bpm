package com.jeestudio.masterdata.mapper.base.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface MainOfficeDao extends CrudDao<Zform> {

}
