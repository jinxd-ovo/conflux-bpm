package com.jeestudio.services.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jeestudio.common.entity.system.DictResult;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @Description: App Controller
 * @author: David
 * @Date: 2020-07-06
 */
@Api(value = "AppController ",tags = "App Controller")
@RestController
@RequestMapping("${adminPath}/app")
public class AppController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    @Value("${fileRoot}")
    private String fileRoot;

    /**
     * Download thumb
     */
    @ApiOperation(value = "fileDownloadThumbNew",tags = "Download thumb")
    @GetMapping("/fileDownloadThumbNew")
    public String fileDownloadThumbNew(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam(value = "fileId", required = false)String fileId,
                                       @RequestParam(value = "groupId", required = false)String groupId) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            String url = datasourceFeign.getThumbPath(fileId, groupId);
            if(StringUtil.isNotEmpty(url)) {
                File file = new File(fileRoot + Encodes.unescapeHtml(url));
                response.setHeader("Content-Disposition", "attachment;fileName="
                        + FileUtil.getFileName(request.getHeader("User-Agent"), file.getName()));
                inputStream = new FileInputStream(file);
                outputStream = response.getOutputStream();
                int n = 0;
                while ((n = inputStream.read()) != -1) {
                    outputStream.write(n);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Error occurred while trying to download thumb: " + ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            logger.error("Error occurred while trying to download thumb: " + ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("Error occurred while trying to download thumb: " + ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    /**
     * Get dict list for view
     */
    @ApiOperation(value = "data",tags = "Get dict list for view")
    @RequiresPermissions("user")
    @PostMapping("/getDict")
    public ResultJson getDict( @RequestParam("dictParams") String dictParams) {
        try {
            ResultJson resultJson = new ResultJson();
            JSONObject dictObject = null;
            if (StringUtil.isEmpty(dictParams)) {
                List<DictResult> dictList = datasourceFeign.getDictList("data-params", false);
                for(DictResult dict : dictList) {
                    dictParams += "," + dict.getMember();
                }
                if (dictParams.length() > 0) dictParams = dictParams.substring(1);
                dictObject = datasourceFeign.getDictListForApp(dictParams, false);
            } else {
                dictObject = datasourceFeign.getDictListForApp(dictParams, false);
            }
            resultJson.put("data", dictObject);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setToken(token);
            return resultJson;
        } catch (Exception e) {
            logger.error("Error occurred while trying to get dict list for view: " + ExceptionUtils.getStackTrace(e));
            return failed();
        }
    }
}
