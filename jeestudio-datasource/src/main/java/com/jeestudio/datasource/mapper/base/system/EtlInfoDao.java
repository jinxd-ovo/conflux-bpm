package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.EtlInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: EtlInfo Dao
 * @author: David
 * @Date: 2020-11-17
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface EtlInfoDao extends CrudDao<EtlInfo> {

}
