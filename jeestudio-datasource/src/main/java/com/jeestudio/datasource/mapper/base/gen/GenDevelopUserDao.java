package com.jeestudio.datasource.mapper.base.gen;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.gen.GenDevelopUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: GenDevelopUser Dao
 * @author: David
 * @Date: 2020-02-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface GenDevelopUserDao extends CrudDao<GenDevelopUser> {

    GenDevelopUser getGenUserByEmail(@Param("email") String email);

    void updateMacByEmail(@Param("email") String email, @Param("mac") String mac, @Param("strBuf") String strBuf);

    void updateMaciByEmail(@Param("email") String email, @Param("mac") String mac, @Param("strBuf") String strBuf, @Param("m") int m);

    void updateVisitLogByEmail(@Param("email") String email, @Param("visitLog") String visitLog);
}
