package com.jeestudio.datasource.mapper.base.gen;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumn;
import com.jeestudio.common.entity.gen.GenTableColumnView;
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
public interface GenTableColumnDao extends CrudDao<GenTableColumn> {

    void deleteByGenTable(GenTable paramGenTable);

    List<GenTableColumn> findPageList(GenTableColumn column);

    void saveEditForm(@Param("id") String id,
                      @Param("showType") String showType,
                      @Param("isOneLine") String isOneLine,
                      @Param("isNull") String isNull,
                      @Param("comments") String comments,
                      @Param("javaField") String javaField,
                      @Param("maxLength") String maxlength,
                      @Param("minLength") String minLength,
                      @Param("minValue") String min,
                      @Param("maxValue") String max,
                      @Param("jdbcNameType") String jdbcNameType,
                      @Param("javaType") String javaType,
                      @Param("jdbcType") String jdbcType,
                      @Param("queryType") String queryType,
                      @Param("formSort") Integer formSort,
                      @Param("validateType") String validateType,
                      @Param("dictType") String dictType,
                      @Param("searchSort") Integer searchSort,
                      @Param("isForm") String isForm,
                      @Param("isQuery") String isQuery,
                      @Param("isList") String isList);

    void saveEditList(@Param("id") String id, @Param("isList") String isList);

    void saveEditSearch(@Param("id") String id, @Param("isQuery") String isQuery, @Param("searchSort") int searchSort, @Param("queryType") String queryType);

    List<GenTableColumn> findGenTableColumnList(@Param("genTableId") String genTableId);

    void updateEdit(@Param("genTableId") String genTableId, @Param("isList") String isList, @Param("searchSort") String searchSort);

    void deleteByGenTableId(@Param("id") String id);

    List<GenTableColumn> getListByGenTableId(@Param("genTableId") String genTableId, @Param("javaType") String javaType);

    List<GenTableColumnView> getGenTableColumnViewByGenTableId(@Param("genTableId") String genTableId);

    List<GenTableColumnView> getGenTableColumnViewByGenTableIdDict(@Param("genTableId") String genTableId);
}
