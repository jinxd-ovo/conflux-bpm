package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.TreeDao;
import com.jeestudio.common.entity.system.Area;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.common.entity.tagtree.TagTree;
import com.jeestudio.common.view.system.OfficeView;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @Description: Office Dao
 * @author: David
 * @Date: 2020-01-11
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface OfficeDao extends TreeDao<Office> {

    List<TagTree> findOfficeTagTree(Office office);

    List<TagTree> findOfficeTagTreeAll();

    List<OfficeView> findOfficeViewData(OfficeView officeView);

    void updateByMasterData(Office office);
}
