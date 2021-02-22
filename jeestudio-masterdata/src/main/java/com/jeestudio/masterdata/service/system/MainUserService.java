package com.jeestudio.masterdata.service.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.masterdata.mapper.base.system.MainUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainUserService {

    @Autowired
    private MainUserDao mainUserDao;

    public List<Zform> findList(Zform zform) {
        return mainUserDao.findList(zform);
    }
}
