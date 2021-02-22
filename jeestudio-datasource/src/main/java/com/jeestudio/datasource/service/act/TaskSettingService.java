package com.jeestudio.datasource.service.act;

import java.util.List;

import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.act.TaskSetting;
import com.jeestudio.datasource.mapper.base.act.TaskPermissionDao;
import com.jeestudio.datasource.mapper.base.act.TaskSettingDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: TaskSetting Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class TaskSettingService extends CrudService<TaskSettingDao, TaskSetting> {

    @Autowired
    private TaskPermissionDao taskPermissionDao;

    public TaskSetting get(String id) {
        TaskSetting taskSetting = super.get(id);
        taskSetting.setTaskPermission(taskPermissionDao.get(taskSetting.getTaskPermission().getId()));
        return taskSetting;
    }

    @Transactional(readOnly = false)
    public void save(TaskSetting taskSetting) {
        TaskPermission taskPermission = taskSetting.getTaskPermission();
        if (taskPermission != null) {
            if (taskPermission.getIsNewRecord()) {
                taskPermission.preInsert();
                taskPermissionDao.insert(taskPermission);
            } else {
                taskPermission.preUpdate();
                taskPermissionDao.update(taskPermission);
            }
        }
        super.save(taskSetting);
    }

    /**
     * Get task setting by process and task
     *
     * @param taskSetting
     * @return TaskSetting
     */
    public TaskSetting getByProcAndTask(TaskSetting taskSetting) {
        TaskSetting theTaskSetting = dao.getByProcAndTask(taskSetting);
        if (theTaskSetting != null && theTaskSetting.getTaskPermission() != null) {
            theTaskSetting.setTaskPermission(taskPermissionDao.get(theTaskSetting.getTaskPermission().getId()));
        }
        return theTaskSetting;
    }

    /**
     * Get task setting list by process definition key
     *
     * @param procDefKey
     * @return TaskSetting list
     */
    public List<TaskSetting> findListByProcDefKey(String procDefKey) {
        return dao.findListByProcDefKey(procDefKey);
    }

    /**
     * Update user task id
     *
     * @param oldId
     * @param newId
     */
    @Transactional(readOnly = false)
    public void updateUserTaskId(String oldId, String newId) {
        dao.updateUserTaskId(oldId, newId);
    }
}
