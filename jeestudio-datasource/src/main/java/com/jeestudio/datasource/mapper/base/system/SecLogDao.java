package com.jeestudio.datasource.mapper.base.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: SecLog Dao
 * @author: David
 * @Date: 2020-06-15
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface SecLogDao {

    void saveSecLog(@Param("id") String id,
                    @Param("account") String account,
                    @Param("content") String content,
                    @Param("time") Date time,
                    @Param("ip") String ip,
                    @Param("type") String type,
                    @Param("result") String result);

    String getSecLogSpace();
}
