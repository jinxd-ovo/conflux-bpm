package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.Level;
import com.jeestudio.datasource.mapper.base.system.LevelDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;

/**
 * @Description: Level Service
 * @author: David
 * @Date: 2020-01-14
 */
@Service
public class LevelService extends CrudService<LevelDao, Level> {
}
