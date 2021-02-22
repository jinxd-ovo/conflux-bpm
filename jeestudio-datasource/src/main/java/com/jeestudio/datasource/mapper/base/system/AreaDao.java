package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.system.Area;
import com.jeestudio.common.entity.tagtree.TagTree;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: Area Dao
 * @author: whl
 * @Date: 2020-02-05
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface AreaDao extends CrudDao<Area> {

    List<TagTree> findAreaTagTree(Area area);

    List<TagTree> findAreaTagTreeAll();
}
