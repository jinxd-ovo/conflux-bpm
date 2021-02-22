package com.jeestudio.common.entity.common.mapper;

import com.jeestudio.common.entity.common.TreeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: Dao support class implementation
 * @author: whl
 * @Date: 2019-11-29
 */
public interface TreeDao<T extends TreeEntity<T>> extends CrudDao<T> {

    /**
     * Find all child nodes
     */
    public List<T> findByParentIdsLike(T entity);

    /**
     * Update all parent node fields
     */
    public int updateParentIds(T entity);

    public int updateSort(T entity);

    public List<T> getChildren(@Param("parentId") String parentId);
}
