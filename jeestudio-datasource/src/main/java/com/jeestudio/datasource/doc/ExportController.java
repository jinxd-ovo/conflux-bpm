package com.jeestudio.datasource.doc;

import com.google.gson.reflect.TypeToken;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.test.Test;
import com.jeestudio.datasource.service.act.ActService;
import com.jeestudio.utils.JsonConvertUtil;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: Export Controller
 * @author: David
 * @Date: 2020-11-06
 */
@Api(value = "ExportController ",tags = "Export Controller")
@RestController
@RequestMapping("${datasourcePath}/doc/export")
public class ExportController {

    @Autowired
    private ActService actService;

    @Value("${docTemplateRoot}")
    private String docTemplateRoot;

    @GetMapping("/getWord")
    public void getWord(@RequestParam("zformString") String zformString, HttpServletResponse response) {
        if(StringUtil.isNotEmpty(zformString)) {
            Zform zform = JsonConvertUtil.gsonBuilder().fromJson(zformString, new TypeToken<Zform>(){}.getType());
        }

        //e.g
        /*
        Test test = new Test();
        test.setName("张三");
        test.setAge("10");
        actService.replaceBookMark(test,  docTemplateRoot + "/文档.xml", "文档2.doc", response);
        */
    }
}
