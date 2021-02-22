package com.jeestudio.datasource.controller.oa;

import com.jeestudio.common.view.oa.CalendarMeetingView;
import com.jeestudio.common.view.oa.MeetingView;
import com.jeestudio.common.view.oa.WorkCalendarView;
import com.jeestudio.datasource.service.oa.MeetingCalendarService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Description: Meeting Calendar Controller
 * @author: gaoqk
 * @Date: 2020-08-19
 */
@Api(value = "MeetingCalendarController",tags = "meetingCalendar Controller")
@RestController
@RequestMapping("${datasourcePath}/oa/meetingCalendar")
public class MeetingCalendarController {

    @Autowired
    private MeetingCalendarService meetingCalendarService;

    @PostMapping("/getDateByMonth")
    public List<String> getDateByMonth(@RequestParam("month") String month){
        List<String> list = meetingCalendarService.getDateByMonth(month);
        return list;
    }

    @PostMapping("/getCalendarMeeting")
    public List<CalendarMeetingView> getCalendarMeeting(@RequestParam("day") String day){
        List<WorkCalendarView> calendarList = meetingCalendarService.getCalendarByDay(day);
        List<MeetingView> meetingList = meetingCalendarService.getMeetingByDay(day);
        List<CalendarMeetingView> calendarMeetingList = new ArrayList<CalendarMeetingView>();
        for(int i = 0; i < calendarList.size(); i++){
            CalendarMeetingView calendarMeetingView = new CalendarMeetingView();
            WorkCalendarView calendarView = calendarList.get(i);
            calendarMeetingView.setId(calendarView.getId());
            calendarMeetingView.setOwnerCode(calendarView.getOwnerCode());
            calendarMeetingView.setTitle(calendarView.getTitle());
            calendarMeetingView.setStartTime(calendarView.getStartTime());
            calendarMeetingView.setEndTime(calendarView.getEndTime());
            calendarMeetingView.setType("日程");
            calendarMeetingList.add(calendarMeetingView);
        }
        for(int i = 0; i < meetingList.size(); i++){
            CalendarMeetingView calendarMeetingView = new CalendarMeetingView();
            MeetingView meetingView = meetingList.get(i);
            calendarMeetingView.setId(meetingView.getId());
            calendarMeetingView.setOwnerCode(meetingView.getOwnerCode());
            calendarMeetingView.setTitle(meetingView.getName());
            calendarMeetingView.setStartTime(meetingView.getMeetingStart());
            calendarMeetingView.setEndTime(meetingView.getMeetingStop());
            calendarMeetingView.setType("会议");
            calendarMeetingList.add(calendarMeetingView);
        }
        listSort(calendarMeetingList);
        return calendarMeetingList;
    }

    @PostMapping("/getCalendarMeetingByYear")
    public List<CalendarMeetingView> getCalendarMeetingByYear(@RequestParam("year") String year){
        List<WorkCalendarView> calendarList = meetingCalendarService.getCalendarByYear(year);
        List<MeetingView> meetingList = meetingCalendarService.getMeetingByYear(year);
        List<CalendarMeetingView> calendarMeetingList = new ArrayList<CalendarMeetingView>();
        for(int i = 0; i < calendarList.size(); i++){
            CalendarMeetingView calendarMeetingView = new CalendarMeetingView();
            WorkCalendarView calendarView = calendarList.get(i);
            calendarMeetingView.setId(calendarView.getId());
            calendarMeetingView.setDate(calendarView.getStartTime());
            calendarMeetingView.setValue(calendarView.getTitle());
            calendarMeetingList.add(calendarMeetingView);
        }
        for(int i = 0; i < meetingList.size(); i++){
            CalendarMeetingView calendarMeetingView = new CalendarMeetingView();
            MeetingView meetingView = meetingList.get(i);
            calendarMeetingView.setId(meetingView.getId());
            calendarMeetingView.setDate(meetingView.getMeetingStart());
            calendarMeetingView.setValue(meetingView.getName());
            calendarMeetingList.add(calendarMeetingView);
        }
        return calendarMeetingList;
    }

    public void listSort(List<CalendarMeetingView> list){
        Collections.sort(list, new Comparator<CalendarMeetingView>() {
            @Override
            public int compare(CalendarMeetingView cm1, CalendarMeetingView cm2) {
                if (cm1.getStartTime().getTime() > cm2.getStartTime().getTime()) {
                    return 1;
                } else if (cm1.getStartTime().getTime() < cm2.getStartTime().getTime()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
}
