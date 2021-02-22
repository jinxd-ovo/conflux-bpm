package com.jeestudio.datasource.mapper.base.cms;

import com.jeestudio.common.entity.cms.PrtChannel;
import com.jeestudio.common.entity.common.mapper.CrudDao;
import com.jeestudio.common.entity.cms.PrtInformation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: PrtInformation Dao
 * @author: David
 * @Date: 2020-07-02
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface PrtInformationDao extends CrudDao<PrtInformation> {

    List<PrtInformation> findDfByRelease(PrtInformation prtInformation);

    List<PrtInformation> findYfByPige(PrtInformation prtInformation);

    List<PrtInformation> findTops(PrtInformation prtInformation);

    int restoreByLogic(PrtInformation prtInformation);

    List<PrtChannel> findChannelList(PrtChannel prtChannel);
}
