package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.SysFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: SysFile Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface SysFileDao extends CrudDao<SysFile> {

    List<SysFile> findListAndContent(SysFile sysFile);
    int saveSecretLevel(SysFile sysFile);
}
