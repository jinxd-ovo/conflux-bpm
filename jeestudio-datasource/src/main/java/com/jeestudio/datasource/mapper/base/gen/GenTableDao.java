package com.jeestudio.datasource.mapper.base.gen;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableChildren;
import com.jeestudio.common.entity.gen.GenTableView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: GenTable Dao
 * @author: David
 * @Date: 2020-01-18
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface GenTableDao extends CrudDao<GenTable> {

    int buildTable(@Param("sql") String paramString);

    GenTable getByName(@Param("name") String name);

    void saveSql(@Param("id") String id,
                 @Param("sqlColumns") String sqlColumns,
                 @Param("sqlColumnsFriendly") String sqlColumnsFriendly,
                 @Param("sqlJoins") String sqlJoins,
                 @Param("sqlInsert") String sqlInsert,
                 @Param("sqlUpdate") String sqlUpdate,
                 @Param("sqlSort") String sqlSort);

    int findCount(@Param("name") String tableName);

    List<GenTable> getChildren(@Param("name") String name);

    GenTableView getGengTableViewById(@Param("id") String id);

    List<GenTableChildren> getGenTableViewByParentTable(@Param("name") String name);

    void saveImportAndExport(GenTableView genTable);
    void saveImport(GenTableView genTable);
    void saveExport(GenTableView genTable);
}