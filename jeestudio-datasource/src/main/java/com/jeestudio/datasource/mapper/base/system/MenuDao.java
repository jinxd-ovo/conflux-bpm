package com.jeestudio.datasource.mapper.base.system;

import com.jeestudio.common.entity.common.mapper.TreeDao;
import com.jeestudio.common.entity.system.Menu;
import com.jeestudio.common.entity.system.MenuResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: Menu Dao
 * @author: whl
 * @Date: 2019-11-29
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface MenuDao extends TreeDao<Menu> {

    int updateSort(Menu menu);

    List<String> findAllPermissionList(Menu m);

    List<String> findPermissionByUserId(Menu m);

    List<MenuResult> findMenuAllList(Menu m);

    List<MenuResult> findMenuByUserId(Menu m);

    List<Menu> getMenuTagTree();

    int hasPermission(@Param("userId") String userId, @Param("permission") String permission);
}
