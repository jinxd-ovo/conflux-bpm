package com.jeestudio.datasource.service.act;

import com.jeestudio.common.entity.act.TaskMessage;
import com.jeestudio.datasource.mapper.base.act.TaskMessageDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;

/**
 * @Description: TaskMessage Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
class TaskMessageService extends CrudService<TaskMessageDao, TaskMessage> {
}
