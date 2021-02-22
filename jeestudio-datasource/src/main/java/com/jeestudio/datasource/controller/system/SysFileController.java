package com.jeestudio.datasource.controller.system;

import com.jeestudio.common.entity.system.SysFile;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.service.system.SysFileService;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: SysFile Controller
 * @author: David
 * @Date: 2020-03-17
 */
@Api(value = "SysFileController ",tags = "SysFile Controller")
@RestController
@RequestMapping("${datasourcePath}/sysFile")
public class SysFileController {

    protected static final Logger logger = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    protected GenTableService genTableService;

    /**
     * Upload file
     */
    @ApiOperation(value = "/uploadFileComplete", tags = "File upload complete")
    @PostMapping("/uploadFileComplete")
    public ResultJson uploadFileComplete(@RequestParam("requestURI") String requestURI, @RequestParam("groupId") String groupId,
                                         @RequestParam("fileName") String fileName, @RequestParam("fileSize") String fileSize,
                                         @RequestParam("chunk") String chunk, @RequestParam("cSize") String cSize,
                                         @RequestParam("secret") String secret, @RequestParam("template") String template,
                                         @RequestParam("fileRoot") String fileRoot, @RequestParam("fileUploadFolder") String fileUploadFolder,
                                         @RequestParam("uploadPathDateFormat") String uploadPathDateFormat, @RequestParam("loginName") String loginName) {
        ResultJson resultJson = new ResultJson();
        try {
            Map<String, Object> map = sysFileService.uploadFileComplete(requestURI, groupId, fileName, fileSize, chunk, cSize, secret, template, fileRoot, fileUploadFolder, uploadPathDateFormat, loginName);
            resultJson.put("map", map);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("上传成功");
            resultJson.setMsg_en("Upload file success");
        } catch (Exception e) {
            logger.error("Error while uploading file:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("上传失败");
            resultJson.setMsg_en("Upload file failed");
        }
        return resultJson;
    }

    /**
     * Get file list
     */
    @ApiOperation(value = "/getFileList", tags = "Get file list")
    @PostMapping("/getFileList")
    public ResultJson getFileList(@RequestParam("groupId") String groupId) {
        ResultJson resultJson = new ResultJson();
        try {
            LinkedHashMap<String, Object> map = sysFileService.getFileList(groupId);
            resultJson.put("fileListMap", map);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("获取文件列表成功");
            resultJson.setMsg_en("Get file list success");
        } catch (Exception e) {
            logger.error("Error while getting file list:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("获取文件列表失败");
            resultJson.setMsg_en("Get file list failed");
        }
        return resultJson;
    }

    /**
     * Get file
     */
    @ApiOperation(value = "/getFile", tags = "Get file")
    @PostMapping("/getFile")
    public File getFile(@RequestParam("fileId") String fileId, @RequestParam("fileRoot") String fileRoot) {
        File file = null;
        try {
            file = sysFileService.getFile(fileId, fileRoot);
        } catch (Exception e) {
            logger.error("Error while getting file:" + ExceptionUtils.getStackTrace(e));

        }
        return file;
    }

    /**
     * Get file
     */
    @ApiOperation(value = "/getFileByFormNo", tags = "Get file by formNo")
    @PostMapping("/getFileByFormNo")
    public File getFileByFormNo(@RequestParam("formNo") String formNo, @RequestParam("fileRoot") String fileRoot) {
        File file = null;
        try {
            String groupId = genTableService.getImportTemplateFileGroupIdByFormNo(formNo, fileRoot);
            file = sysFileService.getFirstByGroupId(groupId, fileRoot);
        } catch (Exception e) {
            logger.error("Error while getting file:" + ExceptionUtils.getStackTrace(e));
        }
        return file;
    }

    /**
     * Delete file
     */
    @ApiOperation(value = "/deleteFile", tags = "Delete file")
    @PostMapping("/deleteFile")
    public ResultJson deleteFile(@RequestParam("fileId") String fileId, @RequestParam("fileRoot") String fileRoot) {
        ResultJson resultJson = new ResultJson();
        try {
            sysFileService.deleteFile(fileId, fileRoot);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("删除文件成功");
            resultJson.setMsg_en("Delete file success");
        } catch (Exception e) {
            logger.error("Error while deleting file list:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("删除文件失败");
            resultJson.setMsg_en("Delete file failed");
        }
        return resultJson;
    }

    /**
     * Get thumb path
     */
    @ApiOperation(value = "/getThumbPath", tags = "Get file thumb path")
    @GetMapping("/getThumbPath")
    public String getThumbPath(@RequestParam("fileId") String fileId, @RequestParam("groupId") String groupId) {
        String thumbPath = "";
        if (StringUtil.isEmpty(fileId)) {
            LinkedHashMap<String, Object> map = sysFileService.getFileList(groupId);
            if (map.size() > 0) {
                List<SysFile> sysFileList = (List<SysFile>) map.get("files");
                if (sysFileList.size() > 0) {
                    thumbPath = sysFileList.get(0).getThumbPath();
                }
            }
        } else {
            SysFile sysFile = sysFileService.get(fileId);
            if (sysFile != null) {
                thumbPath = sysFile.getThumbPath();
            }
        }
        return thumbPath;
    }

    /**
     * Save Secret Level
     */
    @ApiOperation(value = "/saveSecretLevel", tags = "Save Secret Level")
    @PostMapping("/saveSecretLevel")
    public ResultJson saveSecretLevel(@RequestBody SysFile sysFile) {
        ResultJson resultJson = new ResultJson();
        try {
            int count = sysFileService.saveSecretLevel(sysFile);
            if (count == 1) {
                resultJson.setCode(ResultJson.CODE_SUCCESS);
                resultJson.setMsg("修改密级成功");
                resultJson.setMsg_en("Update secret level success");
            } else {
                resultJson.setCode(ResultJson.CODE_FAILED);
                resultJson.setMsg("修改密级失败");
                resultJson.setMsg_en("Update secret level failed");
            }
        } catch (Exception e) {
            logger.error("Error while updating secret level:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("修改密级失败");
            resultJson.setMsg_en("Update secret level failed");
        }
        return resultJson;
    }
}
