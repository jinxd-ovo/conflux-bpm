package com.jeestudio.services.admin.controller.base;

import com.jeestudio.utils.JwtUtil;
import com.jeestudio.utils.ResultJson;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: Base Controller
 * @author: whl
 * @Date: 2019-12-24
 */
public abstract class BaseController {

    protected String token = "";
    protected String currentUserName = "";
    protected String currentUserId = "";
    protected String ip = "";

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if(httpServletRequest.getHeader("token") != null){
            String msg = JwtUtil.getCurrentUser(httpServletRequest.getHeader("token"));
            token = httpServletResponse.getHeader("token");
            currentUserName = msg.split("_")[0];
            currentUserId = msg.split("_")[1];
            ip = httpServletRequest.getRemoteAddr();
        }
    }

    public ResultJson failed() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(ResultJson.CODE_FAILED);
        resultJson.setMsg("操作失败");
        resultJson.setMsg_en("Operation has failed");
        resultJson.setToken(token);
        return resultJson;
    }
}
