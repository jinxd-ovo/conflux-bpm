package com.jeestudio.masterdata.service.common;

import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.common.persistence.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @Description: Crud Service
 * @author: David
 * @Date: 2020-01-09
 */
public abstract class CrudService<D extends CrudDao<T>, T extends DataEntity<T>> {

    @Autowired
    protected D dao;

    public T get(String id) {
        return dao.get(id);
    }

    public T get(T entity) {
        return dao.get(entity);
    }

    public List<T> findList(T entity) {
        return dao.findList(entity);
    }

    public Page<T> findPage(Page<T> page, T entity) {
        entity.setPage(page);
        page.setList(dao.findList(entity));
        return page;
    }

    @Transactional(readOnly = false)
    public void save(T entity) {
        if (entity.getIsNewRecord()){
            entity.preInsert();
            dao.insert(entity);
            entity.setIsNewRecord(false);
        }else{
            entity.preUpdate();
            dao.update(entity);
        }
    }

    @Transactional(readOnly = false)
    public void saveV(T entity) {
        if (!entity.getIsNewRecord()){
            entity.preUpdate();
            dao.insertV(entity);
        }
    }

    @Transactional(readOnly = false)
    public void delete(T entity) {
        dao.delete(entity);
    }

    @Transactional(readOnly = false)
    public void deleteAll(Collection<T> entitys) {
        for (T entity : entitys) {
            dao.delete(entity);
        }
    }

    public T findUniqueByProperty(String propertyName, Object value){
        return dao.findUniqueByProperty(propertyName, value);
    }
}
