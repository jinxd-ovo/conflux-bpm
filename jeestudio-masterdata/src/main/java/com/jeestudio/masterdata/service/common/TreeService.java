package com.jeestudio.masterdata.service.common;

import com.jeestudio.common.entity.common.TreeEntity;
import com.jeestudio.common.entity.common.mapper.TreeDao;
import com.jeestudio.masterdata.utils.Reflections;
import com.jeestudio.utils.Global;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: Tree Service
 * @author: David
 * @Date: 2020-01-11
 */
public abstract class TreeService<D extends TreeDao<T>, T extends TreeEntity<T>> extends CrudService<D, T> {

    @Transactional(readOnly = false)
    public void save(T entity) {

        @SuppressWarnings("unchecked")
        Class<T> entityClass = Reflections.getClassGenricType(getClass(), 1);

        if (entity.getParent() == null || StringUtils.isBlank(entity.getParentId())
                || Global.DEFAULT_ROOT_CODE.equals(entity.getParentId())) {
            entity.setParent(null);
        } else {
            entity.setParent(super.get(entity.getParentId()));
        }
        if (entity.getParent() == null) {
            T parentEntity = null;
            try {
                parentEntity = entityClass.getConstructor(String.class).newInstance("0");
            } catch (Exception e) {
                throw new ServiceException(e);
            }
            entity.setParent(parentEntity);
            entity.getParent().setParentIds(StringUtils.EMPTY);
        }

        String oldParentIds = entity.getParentIds();
        entity.setParentIds(entity.getParent().getParentIds() + entity.getParent().getId() + ",");
        super.save(entity);
        T o = null;
        try {
            o = entityClass.newInstance();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        o.setParentIds("%," + entity.getId() + ",%");
        List<T> list = dao.findByParentIdsLike(o);
        for (T e : list) {
            if (e.getParentIds() != null && oldParentIds != null) {
                e.setParentIds(e.getParentIds().replace(oldParentIds, entity.getParentIds()));
                preUpdateChild(entity, e);
                dao.updateParentIds(e);
            }
        }
    }

    protected void preUpdateChild(T entity, T childEntity) {
    }

    public List<T> getChildren(String parentId) {
        return dao.getChildren(parentId);
    }
}
