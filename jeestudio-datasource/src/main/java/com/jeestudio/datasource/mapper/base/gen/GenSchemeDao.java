package com.jeestudio.datasource.mapper.base.gen;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.gen.GenScheme;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: GenScheme Dao
 * @author: whl
 * @Date: 2020-02-12
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface GenSchemeDao  extends CrudDao<GenScheme> {
}
