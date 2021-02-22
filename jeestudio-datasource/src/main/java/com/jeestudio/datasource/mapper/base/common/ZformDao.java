package com.jeestudio.datasource.mapper.base.common;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Zform Dao
 * @author: David
 * @Date: 2020-01-18
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface ZformDao extends CrudDao<Zform> {

    LinkedHashMap getMap(Zform zform);

    void deleteChildren(Zform zform);

    void deleteChildrenForTree(Zform zform);

    List<Zform> findChildrenForDelete(Zform zform);

    List<Zform> findListByZform(Zform zform);

    List<Zform> findListByProc(Zform zform);

    List<LinkedHashMap> findListByProcMap(Zform zform);

    List<Zform> findListCount(Zform zform);

    List<Zform> findListByProcCount(Zform zform);

    List<LinkedHashMap> findListMap(Zform zform);

    String getNameById(@Param("formNo") String formNo, @Param("columnName") String columnName, @Param("id") String id);

    void updateSysMenuIsShowCascade(@Param("parentIds") String parentIds, @Param("isShow") String isShow);

    List<String> findValueList(@Param("formNo") String formNo, @Param("valueColumn") String valueColumn, @Param("keyColumn") String keyColumn, @Param("key") String key);
}
