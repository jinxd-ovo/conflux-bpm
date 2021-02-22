package com.jeestudio.services.admin.controller.oa;

import com.jeestudio.common.view.oa.CalendarMeetingView;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: Meeting Calendar Controller
 * @author: gaoqk
 * @Date: 2020-08-19
 */
@Api(value = "MeetingCalendarController", tags = "meetingCalendar Controller")
@RestController
@RequestMapping("${adminPath}/oa/meetingCalendar")
public class MeetingCalendarController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(MeetingCalendarController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    /**
     * Get date by month
     */
    @ApiOperation(value = "getDateByMonth",tags = "get date by month")
    @RequiresPermissions("user")
    @PostMapping("/getDateByMonth")
    public ResultJson getDateByMonth(@RequestParam("month") String month) {
        try {
            ResultJson resultJson = new ResultJson();
            List<String> list = datasourceFeign.getDateByMonth(month);
            resultJson.setRows(list);
            resultJson.setTotal(list.size());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation was successful");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get date by month: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get calendar meeting
     */
    @ApiOperation(value = "getCalendarMeeting",tags = "get calendar meeting")
    @RequiresPermissions("user")
    @PostMapping("/getCalendarMeeting")
    public ResultJson getCalendarMeeting(@RequestParam("day") String day) {
        try {
            ResultJson resultJson = new ResultJson();
            List<CalendarMeetingView> calendarMeetingList = datasourceFeign.getCalendarMeeting(day);
            resultJson.setRows(calendarMeetingList);
            resultJson.setTotal(calendarMeetingList.size());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while getting calendarMeetingList:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }

    /**
     * Get calendar meeting by year
     */
    @ApiOperation(value = "getCalendarMeetingByYear",tags = "get calendar meeting by year")
    @RequiresPermissions("user")
    @PostMapping("/getCalendarMeetingByYear")
    public ResultJson getCalendarMeetingByYear(@RequestParam("year") String year) {
        try {
            ResultJson resultJson = new ResultJson();
            List<CalendarMeetingView> calendarMeetingList = datasourceFeign.getCalendarMeetingByYear(year);
            resultJson.setRows(calendarMeetingList);
            resultJson.setTotal(calendarMeetingList.size());
            resultJson.setToken(token);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("Operation success");
            return resultJson;
        } catch (Exception e) {
            logger.error("Error while getting calendarMeetingList:" + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
