package com.jeestudio.datasource.controller.gen;

import com.jeestudio.datasource.service.gen.GenDevelopUserService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Form Controller
 * @author: David
 * @Date: 2020-02-11
 */
@Api(value = "FormController ",tags = "Form Controller")
@RestController
@RequestMapping("${datasourcePath}/gen/form")
public class FormController {

    protected static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Autowired
    private GenDevelopUserService genDevelopUserService;

    /**
     * Get content of generated code file
     */
    @ResponseBody
    @RequestMapping(value = "content")
    public String getContent(String genscheme, String code, String isChild, String category, String tplId) {
        String content = "";

        return content;
    }
}
