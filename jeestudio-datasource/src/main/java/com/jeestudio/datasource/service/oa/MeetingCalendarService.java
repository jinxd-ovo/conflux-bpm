package com.jeestudio.datasource.service.oa;

import com.google.common.collect.Lists;
import com.jeestudio.common.view.oa.MeetingView;
import com.jeestudio.common.view.oa.WorkCalendarView;
import com.jeestudio.datasource.mapper.base.oa.MeetingCalendarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: Meeting Calendar Service
 * @author: gaoqk
 * @Date: 2020-08-19
 */
@Service
public class MeetingCalendarService {
    @Autowired
    private MeetingCalendarDao meetingCalendarDao;

    public List<String> getDateByMonth(String month){
        List<String> calendarStrList = Lists.newArrayList();
        List<String> meetingStrList = Lists.newArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Date> calendarList = meetingCalendarDao.getCalendarDate("'" + month + "%'");
        List<Date> meetingList = meetingCalendarDao.getMeetingDate("'" + month + "%'");
        for (Date d : calendarList) {
            calendarStrList.add(simpleDateFormat.format(d));
        }
        for (Date d : meetingList) {
            meetingStrList.add(simpleDateFormat.format(d));
        }

        calendarStrList.removeAll(meetingStrList);
        calendarStrList.addAll(meetingStrList);
        return calendarStrList;
    }

    public List<WorkCalendarView> getCalendarByDay(String day) {
        List<WorkCalendarView> list = meetingCalendarDao.getCalendarByDay("'" + day + "%'");
        return list;
    }

    public List<MeetingView> getMeetingByDay(String day) {
        List<MeetingView> list = meetingCalendarDao.getMeetingByDay("'" + day + "%'");
        return list;
    }

    public List<WorkCalendarView> getCalendarByYear(String year) {
        List<WorkCalendarView> list = meetingCalendarDao.getCalendarByYear("'" + year + "%'");
        return list;
    }

    public List<MeetingView> getMeetingByYear(String year) {
        List<MeetingView> list = meetingCalendarDao.getMeetingByYear("'" + year + "%'");
        return list;
    }
}
