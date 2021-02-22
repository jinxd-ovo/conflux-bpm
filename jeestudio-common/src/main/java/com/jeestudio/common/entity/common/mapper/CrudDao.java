package com.jeestudio.common.entity.common.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: Dao support class implementation
 * @author: whl
 * @Date: 2019-11-29
 */
public interface CrudDao<T> extends BaseDao {

    /**
     * Get single data
     * @param id
     */
    public T get(String id);

    /**
     * Get single data
     * @param entity
     */
    public T get(T entity);

    /**
     * Get unique records based on entity name and field name and field value
     * @param propertyName
     * @param value
     */
    public  T findUniqueByProperty(@Param(value="propertyName")String propertyName, @Param(value="value")Object value);

    /**
     * Query the data list. If paging is required, set the paging object，as：entity.setPage(new Page<T>());
     * @param entity
     */
    public List<T> findList(T entity);

    public List<LinkedHashMap> findListMap(T entity);

    /**
     * Query the data list (used when managing roles). If paging is required, set the paging object，as：entity.setPage(new Page<T>());
     * @param entity
     */
    public List<T> findListForAdmin(T entity);

    /**
     * Query all data lists
     * @param entity
     */
    public List<T> findAllList(T entity);

    /**
     * Query all data lists
     * @see public List<T> findAllList(T entity)
     */
    @Deprecated
    public List<T> findAllList();

    /**
     * Insert data
     * @param entity
     */
    public int insert(T entity);

    /**
     * Archive data
     * @param entity
     */
    public int insertV(T entity);

    /**
     * Update data
     * @param entity
     */
    public int update(T entity);

    /**
     * Delete data (physical delete, completely delete from database)
     * @param id
     * @see public int delete(T entity)
     */
    @Deprecated
    public int delete(String id);

    /**
     * Delete data (logically delete, update the Del flag field to 1, when the table contains the field del flag, you can call this method to hide the data)
     * @param id
     * @see public int delete(T entity)
     */
    @Deprecated
    public int deleteByLogic(String id);

    /**
     * Delete data (physical delete, completely delete from database)
     * @param entity
     */
    public int delete(T entity);

    /**
     * Delete data (logically delete, update the Del flag field to 1, when the table contains the field del flag, you can call this method to hide the data)
     * @param entity
     */
    public int deleteByLogic(T entity);
}
