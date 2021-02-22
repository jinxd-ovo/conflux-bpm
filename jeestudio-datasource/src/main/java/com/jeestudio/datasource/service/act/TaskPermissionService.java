package com.jeestudio.datasource.service.act;

import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.act.TaskSettingVersion;
import com.jeestudio.datasource.mapper.base.act.TaskPermissionDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: TaskPermission Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class TaskPermissionService extends CrudService<TaskPermissionDao, TaskPermission> {

    /**
     * Find task permission list by task setting version id list
     *
     * @param taskSettingVersionList
     * @return TaskPermission list
     */
    public List<TaskPermission> findListByIdList(List<TaskSettingVersion> taskSettingVersionList) {
        return dao.findListByIdList(taskSettingVersionList);
    }

    /**
     * Find task permission list by category and types
     *
     * @param category
     * @param types
     * @return TaskPermission list
     */
    public List<TaskPermission> findListByPermission(String category, String types) {
        return dao.findListByPermission(category, types);
    }

    /**
     * Find task permission by permission id and types
     *
     * @param permission id
     * @param types
     * @return TaskPermission
     */
    public TaskPermission findByTaskPermissionId(String permission, String types) {
        TaskPermission taskPermission = dao.findByTaskPermissionId(permission, types);
        return taskPermission;
    }
}
