package com.jeestudio.datasource.service.act;

import com.jeestudio.common.entity.act.Act;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import com.jeestudio.common.entity.act.TaskSettingVersion;
import com.jeestudio.datasource.mapper.base.act.TaskSettingVersionDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: TaskSettingVersion Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class TaskSettingVersionService extends CrudService<TaskSettingVersionDao, TaskSettingVersion> {

    /**
     * Batch save task setting version list
     *
     * @param taskSettingVersionList
     */
    @Transactional(readOnly = false)
    public void batchSave(List<TaskSettingVersion> taskSettingVersionList) {
        dao.batchSave(taskSettingVersionList);
    }

    /**
     * Get task setting version by act
     *
     * @param act
     * @return TaskSettingVersion
     */
    public TaskSettingVersion getTaskSettingVersionByAct(Act act) {
        return dao.getTaskSettingVersionByAct(act);
    }

    /**
     * Get task setting version list by permission and process definition id
     *
     * @param permission
     * @param procDefId
     * @return TaskSettingVersion list
     */
    public List<TaskSettingVersion> getByPermission(String permission, String procDefId) {
        return dao.getByPermission(permission, procDefId);
    }

    /**
     * Update act
     *
     * @param bytes
     * @param deploymentId
     */
    @Transactional(readOnly = false)
    public void updateActByte(byte[] bytes, String deploymentId) {
        dao.updateActByte(bytes, deploymentId);
    }

    /**
     * Update history task
     *
     * @param newTaskEntity
     */
    @Transactional(readOnly = false)
    public void updateHistoricTask(TaskEntity newTaskEntity) {
        dao.updateHistoricTask(newTaskEntity);
    }
}
