package com.jeestudio.datasource.service.act;

import com.jeestudio.common.entity.act.AssigneeSetting;
import com.jeestudio.datasource.mapper.base.act.AssigneeSettingDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: AssigneeSetting Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class AssigneeSettingService extends CrudService<AssigneeSettingDao, AssigneeSetting> {

    /**
     * Get assignee list by user id
     *
     * @param userId
     * @return AssigneeSetting list
     */
    @Transactional(readOnly = false)
    public List<AssigneeSetting> getAssigneeListByUserId(String userId) {
        return dao.getAssigneeListByUserId(userId);
    }
}
