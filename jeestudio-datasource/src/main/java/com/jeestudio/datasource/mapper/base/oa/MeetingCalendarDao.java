package com.jeestudio.datasource.mapper.base.oa;

import com.jeestudio.common.view.oa.MeetingView;
import com.jeestudio.common.view.oa.WorkCalendarView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description: Meeting Calendar Dao
 * @author: gaoqk
 * @Date: 2020-08-19
 */
@Qualifier("sqlSessionFactoryBase")
@Component
@Mapper
public interface MeetingCalendarDao {

    List<Date> getCalendarDate(@Param("month") String month);

    List<Date> getMeetingDate(@Param("month") String month);

    List<WorkCalendarView> getCalendarByDay(@Param("day") String day);

    List<MeetingView> getMeetingByDay(@Param("day") String day);

    List<WorkCalendarView> getCalendarByYear(@Param("year") String year);

    List<MeetingView> getMeetingByYear(@Param("year") String year);
}
