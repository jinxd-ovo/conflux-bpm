package com.jeestudio.masterdata.service.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.masterdata.mapper.base.system.MainOfficeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainOfficeService {

    @Autowired
    private MainOfficeDao mainOfficeDao;

    public List<Zform> findList(Zform zform) {
        return mainOfficeDao.findList(zform);
    }
}
