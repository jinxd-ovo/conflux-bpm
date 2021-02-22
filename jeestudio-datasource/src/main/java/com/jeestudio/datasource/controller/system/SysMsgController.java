package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.system.SysMsg;
import com.jeestudio.datasource.service.system.SysMsgService;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Description: SysMsg Controller
 * @author: gaoqk
 * @Date: 2020-08-20
 */
@Api(value = "SysMsgController", tags = "SysMsg Controller")
@RestController
@RequestMapping("${datasourcePath}/system/sysMsg")
public class SysMsgController {

    @Autowired
    private SysMsgService sysMsgService;

    /**
     * get data list
     */
    @PostMapping("/data")
    public ResultJson data(@RequestBody SysMsg sysMsg, @RequestParam("currentUserName") String currentUserName){
        ResultJson resultJson = new ResultJson();
        sysMsg.setRecipient(UserUtil.getByLoginName(currentUserName));
        Page<SysMsg> page = sysMsgService.findPage(new Page<SysMsg>(sysMsg.getPageParam().getPageNo(), sysMsg.getPageParam().getPageSize()), sysMsg);
        resultJson.setRows(page.getList());
        resultJson.setTotal(page.getCount());
        return resultJson;
    }
    /**
     * Set Read
     */
    @PostMapping("/setRead")
    public String setRead(@RequestParam("id") String id){
        SysMsg sysMsg = sysMsgService.get(id);
        if(Global.YES.equals(sysMsg.getStatus())){
            sysMsg.setReadTime(new Date());
            sysMsg.setStatus(Global.NO);
            sysMsgService.save(sysMsg);
            //sysMsgService.sendSysMsg(UserUtil.getByLoginName(loginName).getId());
        }
        return "消息已读";
    }

    /**
     * Set Read All
     */
    @PostMapping("/setReadAll")
    public String setReadAll(@RequestParam("currentUserName") String currentUserName){
        String message = "";
        SysMsg sysMsg = new SysMsg();
        sysMsg.setRecipient(UserUtil.getByLoginName(currentUserName));
        sysMsg.setStatus(Global.YES);
        List<SysMsg> list = sysMsgService.findList(sysMsg);
        if(list == null || list.size() == 0){
            message = "无未读消息";
        } else {
            for(SysMsg msg : list){
                msg.setReadTime(new Date());
                msg.setStatus(Global.NO);
                sysMsgService.save(msg);
            }
            message = "标记已读成功";
        }
        return message;
    }

    /**
     * Get Unread Count
     */
    @GetMapping("/getUnreadCount")
    public Integer getUnreadCount(@RequestParam("currentUserId") String currentUserId){
        int count = sysMsgService.getUnreadCount(currentUserId, Global.YES);
        return count;
    }
}
