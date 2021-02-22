package com.jeestudio.services.admin.controller.system;

import com.jeestudio.common.entity.system.SysFile;
import com.jeestudio.services.admin.controller.base.BaseController;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: SysFile Controller
 * @author: David
 * @Date: 2020-03-17
 */
@Api(value = "SysFileController ",tags = "SysFile Controller")
@RestController
@RequestMapping("${adminPath}/system/sysFile")
public class SysFileController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    DatasourceFeign datasourceFeign;

    @Value("${fileRoot}")
    private String fileRoot;

    @Value("${fileUploadFolder}")
    private String fileUploadFolder;

    @Value("${uploadPathDateFormat}")
    private String uploadPathDateFormat;

    @Value("${allowedExtensions}")
    private String allowedExtensions;

    /**
     * Batch upload file
     */
    @ApiOperation(value = "fileUploadBatchProgress",tags = "Batch upload file")
    @RequiresPermissions("user")
    @PostMapping("/fileUploadBatchProgress")
    public ResultJson fileUploadBatchProgress(HttpServletRequest request,
                                              @RequestParam(value = "file", required = false) MultipartFile[] file,
                                              @RequestParam(value = "groupId", required = false) String groupId,
                                              @RequestParam(value = "fileName", required = false) String fileName,
                                              @RequestParam(value = "fileSize", required = false) String fileSize,
                                              @RequestParam(value = "chunk", required = false) String chunk,
                                              @RequestParam(value = "isChunk", required = false) String isChunk,
                                              @RequestParam(value = "cSize", required = false) String cSize,
                                              @RequestParam(value = "secret", required = false, defaultValue = "") String secret,
                                              @RequestParam(value = "template", required = false, defaultValue = "") String template) {

        ResultJson resultJson = null;
        if("false".equals(isChunk) == false && file != null) {
            this.fileUploadBatchProgress(request.getRequestURI(), file, groupId, chunk);
        }else {
            resultJson = datasourceFeign.uploadFileComplete(request.getRequestURI(), groupId, fileName, fileSize, chunk, cSize, secret, template, fileRoot, fileUploadFolder, uploadPathDateFormat, currentUserName);
        }
        if (resultJson == null) {
            resultJson = new ResultJson();
        }
        resultJson.setToken(token);
        return resultJson;
    }

    /**
     * Get file list
     */
    @ApiOperation(value = "getFileList",tags = "Get file list")
    @RequiresPermissions("user")
    @PostMapping("/getFileList")
    public ResultJson getFileList(@RequestParam(value = "groupId", required = false) String groupId){
        ResultJson resultJson = datasourceFeign.getFileList(groupId);
        resultJson.setToken(token);
        return resultJson;
    }

    /**
     * Download file
     */
    @ApiOperation(value = "fileDownload",tags = "File download")
    @GetMapping("/fileDownload")
    public String fileDownload(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "fileId", required = false, defaultValue = "") String fileId, @RequestParam(value = "secret", required = false, defaultValue = "") String secret, @RequestParam(value = "formNo", required = false, defaultValue = "") String formNo) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File file = null;
            if (StringUtil.isEmpty(formNo)) {
                file = datasourceFeign.getFile(fileId, fileRoot);
            } else {
                file = datasourceFeign.getFileByFormNo(formNo, fileRoot);
            }
            response.setHeader("Content-Disposition", "attachment;fileName="
                    + this.getFileName(request.getHeader("User-Agent"), file.getName()));
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            int n = 0;
            byte[] fileBuffer = new byte[1024];
            byte[] fileBuffer2 = new byte[1024];
            if (StringUtil.isNotEmpty(secret)) {
                while ((n = inputStream.read(fileBuffer)) != -1) {
                    for (int j = 0; j<n; j++) {
                        fileBuffer2[j] = (byte) (fileBuffer[j]^0x99);
                    }
                    outputStream.write(fileBuffer2, 0, n);
                }
            } else {
                while ((n = inputStream.read(fileBuffer)) != -1) {
                    outputStream.write(fileBuffer, 0, n);
                }
            }
        } catch (FileNotFoundException e) {
            logger.warn("Error occurred while trying to download file: " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Error occurred while trying to download file: " + e.getMessage());
        } catch (Exception e) {
            logger.warn("Error occurred while trying to download file: " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {

                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    /**
     * Delete file
     */
    @ApiOperation(value = "deleteFile",tags = "Delete file")
    @RequiresPermissions("user")
    @GetMapping("/deleteFile")
    public ResultJson deleteFile(@RequestParam(value = "fileId", required = false)String fileId){
        ResultJson resultJson = datasourceFeign.deleteFile(fileId, fileRoot);
        resultJson.setToken(token);
        return resultJson;
    }

    /**
     * Get file name
     */
    public static String getFileName(String userAgent, String fileName) {
        userAgent = (userAgent == null ? "" : userAgent.toLowerCase());

        String rtn = new String();
        try {
            String new_filename = URLEncoder.encode(fileName, "UTF8");
            new_filename = new_filename.replaceAll("\\+", " ");
            if (userAgent != null) {
                userAgent = userAgent.toLowerCase();
                //IE
                if (userAgent.indexOf("msie") != -1) {
                    rtn = new_filename;
                }
                //Opera filename*
                else if (userAgent.indexOf("opera") != -1) {
                    rtn = "filename*=UTF-8''" + new_filename;
                }
                //Safari
                else if (userAgent.indexOf("safari") != -1) {
                    rtn = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                }
                //Chrome
                else if (userAgent.indexOf("applewebkit") != -1) {
                    new_filename = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = new_filename;
                }
                //FireFox
                else if (userAgent.indexOf("firefox") != -1) {
                    rtn = "\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
                } else {
                    rtn = new_filename;
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error occurred while trying to get file name: " + ExceptionUtils.getStackTrace(e));
        }
        return rtn;
    }

    private Map<String, List<SysFile>> fileUploadBatchProgress(String requestURI, MultipartFile[] file, String groupId, String chunk) {
        for (MultipartFile mf : file) {
            InputStream input = null;
            FileOutputStream out = null;
            try {
                if (mf != null) {
                    if(this.checkExtension(mf)){
                        Date theDate = new Date();
                        String uploadPath = this.getUploadPath(requestURI, groupId, theDate);
                        new File(uploadPath).mkdirs();
                        input = mf.getInputStream();
                        out = new FileOutputStream(uploadPath + chunk+"_chunk_"+mf.getOriginalFilename().split("\\.")[0]);
                        IOUtils.copy(input, out);
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred while trying to upload file: " + ExceptionUtils.getStackTrace(e));
            } finally {
                try {
                    if(input != null) input.close();
                } catch (Exception e) { }
                try {
                    if(out != null) out.close();
                } catch (Exception e) { }
            }
        }
        return null;
    }

    private String getUploadPath(String requestURI, String randomUUID, Date theDate){
        String realPath = fileRoot + fileUploadFolder;
        return new StringBuffer().append(realPath).append(requestURI)
                .append(new SimpleDateFormat(uploadPathDateFormat).format(theDate))
                .append("/").append(randomUUID).append("/").toString();
    }

    private boolean checkExtension(MultipartFile file){
        String[] fileSplit = file.getOriginalFilename().split("\\.");
        String extension = fileSplit[fileSplit.length-1];
        return allowedExtensions.contains(extension.toLowerCase());
    }

    /**
     * Save Secret Level
     */
    @ApiOperation(value = "/saveSecretLevel", tags = "Save Secret Level")
    @PostMapping("/saveSecretLevel")
    public ResultJson saveSecretLevel(@RequestBody SysFile sysFile) {
        ResultJson resultJson = datasourceFeign.saveSecretLevel(sysFile);
        resultJson.setToken(token);
        return resultJson;
    }
}
