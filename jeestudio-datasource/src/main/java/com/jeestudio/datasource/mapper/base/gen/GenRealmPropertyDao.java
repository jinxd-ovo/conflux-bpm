package com.jeestudio.datasource.mapper.base.gen;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.gen.GenRealmPropertyView;
import com.jeestudio.common.entity.gen.GenTableColumnView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: GenRealmProperty Dao
 * @author: whl
 * @Date: 2020-02-10
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface GenRealmPropertyDao extends CrudDao<GenRealmPropertyView> {

    GenTableColumnView getByName(@Param("name") String name);

    List<GenTableColumnView> realmData(@Param("types") String types, @Param("name") String name);

    List<GenTableColumnView> realmDataDict(@Param("types") String types, @Param("name") String name);
}
