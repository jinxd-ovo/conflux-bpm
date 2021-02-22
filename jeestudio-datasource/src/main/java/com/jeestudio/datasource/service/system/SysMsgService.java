package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.SysMsg;
import com.jeestudio.datasource.mapper.base.system.SysMsgDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: SysMsg Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class SysMsgService extends CrudService<SysMsgDao, SysMsg> {

    @Autowired
    private SysMsgDao sysMsgDao;

    public void sendSysMsg(String userId) {
        //TODO
    }

    public int getUnreadCount(String currentUserId, String status){
        int count = sysMsgDao.getUnreadCount(currentUserId, status);
        return count;
    }
}
