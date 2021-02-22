package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.SysMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description: SysMsg Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface SysMsgDao extends CrudDao<SysMsg> {

    int getUnreadCount(@Param("currentUserId") String currentUserId, @Param("status") String status);

}
