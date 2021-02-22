package com.jeestudio.datasource.mapper.base.gen;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: GenTableColumn Dao
 * @author: David
 * @Date: 2020-01-18
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface GenDataBaseDictDao extends CrudDao<GenTableColumn> {

    List<GenTable> findTableList(GenTable genTable);

    List<GenTableColumn> findTableColumnList(GenTable genTable);

    List<String> findTablePK(GenTable genTable);
}
