package com.jeestudio.datasource.service.system;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.common.entity.system.SysFile;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.datasource.mapper.base.system.OfficeDao;
import com.jeestudio.datasource.mapper.base.system.SysFileDao;
import com.jeestudio.datasource.service.common.CrudService;
import com.jeestudio.datasource.utils.PicUtil;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: SysFile Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class SysFileService extends CrudService<SysFileDao, SysFile> {

    @Autowired
    private OfficeDao officeDao;

    public static final String STATUS_NO = "0";

    public List<SysFile> findListAndContent(SysFile sysFile) {
        return dao.findListAndContent(sysFile);
    }

    @Transactional(readOnly = false)
    public void delete(SysFile sysFile, String fileRoot) {
        String url = sysFile.getPath();
        File file = new File(fileRoot + Encodes.unescapeHtml(url));
        if (file.exists()) {
            file.delete();
        }
        String pdfUrl = sysFile.getPdfPath();
        File pdfFile = new File(fileRoot + Encodes.unescapeHtml(pdfUrl));
        if (pdfFile.exists()) {
            pdfFile.delete();
        }
        String thumbUrl = sysFile.getThumbPath();
        File thumbFile = new File(fileRoot + Encodes.unescapeHtml(thumbUrl));
        if (thumbFile.exists()) {
            thumbFile.delete();
        }
        String fileType = sysFile.getType();
        if (FileUtil.TYPE_PIC.equals(fileType)) {
            String webappUrl = SpringContextHolder.getBean(ServletContext.class).getRealPath(sysFile.getThumbPath());
            File webappFile = new File(webappUrl);
            if (webappFile.exists()) {
                webappFile.delete();
            }
        }
        if (FileUtil.TYPE_VIDEO.equals(fileType) || FileUtil.TYPE_AUDIO.equals(fileType)) {
            String webappUrl = SpringContextHolder.getBean(ServletContext.class).getRealPath(sysFile.getPath());
            File webappFile = new File(webappUrl);
            if (webappFile.exists()) {
                webappFile.delete();
            }
        }
        super.delete(sysFile);
    }

    @Transactional(readOnly = false)
    public void saveSysFileList(String groupId, Map<String, Object> resultMap, boolean secFlag, String loginName) {
        User uploadUser = UserUtil.getByLoginName(loginName);
        List<SysFile> successList = (List<SysFile>) resultMap.get("successList");
        String ownerCode = this.findRootOffice().getCode();
        int sort = 0;
        if (StringUtils.isNotBlank(groupId)) {
            SysFile f = new SysFile();
            f.setGroupId(groupId);
            List<SysFile> files = this.findList(f);
            if (files != null && files.size() > 0) {
                sort = files.get(files.size() - 1).getSort();
            }
        }
        boolean toPdf = false;
        for (SysFile sysFile : successList) {
            if (FileUtil.TYPE_FILE.equals(sysFile.getType())) {
                toPdf = true;
                sysFile.setToPdf(this.STATUS_NO);
            }
            sysFile.setIsNewRecord(true);
            sysFile.setOwnerCode(ownerCode);
            sysFile.setUploadTime(new Date());
            sysFile.setUploadUser(uploadUser);
            sysFile.setSort(++sort);
            sysFile.setDesc("");
            if (true == secFlag) {
                if (FileUtil.TYPE_FILE.equals(sysFile.getType())) {
                    sysFile.setSecFlag(Global.YES);
                } else {
                    sysFile.setSecFlag(Global.NO);
                }
            } else {
                sysFile.setSecFlag(Global.NO);
            }
            sysFile.setVisitCount(0);
            this.save(sysFile);
        }
    }

    @Transactional(readOnly = false)
    public void saveSysFileList(String groupId, Map<String, Object> resultMap, String loginName) {
        saveSysFileList(groupId, resultMap, true, loginName);
    }

    public Office findRootOffice() {
        Office office = new Office();
        office.getSqlMap().put("dsf", " AND a.parent_id = '0' ");
        List<Office> list = officeDao.findList(office);
        if (list != null && list.size() > 0) {
            for (Office f : list) {
                if ((Global.DEFAULT_ROOT_CODE).equals(f.getParentId())) {
                    office = f;
                    break;
                }
            }
        }
        return office;
    }

    @Transactional(readOnly = false)
    public Map<String, Object> uploadFileComplete(String requestURI,
                                                  String groupId,
                                                  String fileName,
                                                  String fileSize,
                                                  String chunk,
                                                  String cSize,
                                                  String secret,
                                                  String template,
                                                  String fileRoot,
                                                  String fileUploadFolder,
                                                  String uploadPathDateFormat,
                                                  String loginName) {
        SysFile sysFileSuccess = new SysFile();
        Date theDate = new Date();
        String fileType = FileUtil.getFileType(fileName);
        groupId = StringUtils.isNotBlank(groupId) ? groupId : UUID.randomUUID().toString();
        sysFileSuccess.setGroupId(groupId);
        sysFileSuccess.setName(fileName);
        sysFileSuccess.setExt(FilenameUtils.getExtension(fileName));
        sysFileSuccess.setType(fileType);
        sysFileSuccess.setSize(this.getFileSizeMerge(Long.valueOf(fileSize)));
        Map<String, Object> map = Maps.newHashMap();
        map.put("groupId", groupId);
        this.uploadFileComplete(requestURI, groupId, fileType, fileName, theDate, sysFileSuccess, chunk, map, cSize, secret, template, fileRoot, fileUploadFolder, uploadPathDateFormat, loginName);
        return map;
    }

    public void uploadFileComplete(String requestURI,
                                   String groupId,
                                   String fileType,
                                   String fileNameMerge,
                                   Date theDate,
                                   SysFile sysFileSuccess,
                                   String chunk,
                                   Map<String, Object> map,
                                   String cSize,
                                   String secret,
                                   String template,
                                   String fileRoot,
                                   String fileUploadFolder,
                                   String uploadPathDateFormat,
                                   String loginName) {
        int cs = Integer.valueOf(cSize);
        List<SysFile> successList = Lists.newArrayList();
        List<SysFile> failList = Lists.newArrayList();

        sysFileSuccess.setGroupId(groupId);
        String downloadPath = this.getDownloadPath(requestURI, groupId, theDate, fileUploadFolder, uploadPathDateFormat, template);
        String uploadPath = this.getUploadPath(requestURI, groupId, theDate, fileRoot, fileUploadFolder, uploadPathDateFormat, template);
        sysFileSuccess.setPath(downloadPath + fileNameMerge);
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fileou = null;
        FileOutputStream fileouEncode = null;
        FileInputStream filein = null;
        try {
            fileou = new FileOutputStream(uploadPath + fileNameMerge, true);
            int num_name = Integer.valueOf(chunk);
            for (int i = 0; i < num_name; i++) {
                filein = new FileInputStream(uploadPath + i + "_chunk_" + fileNameMerge.split("\\.")[0]);
                byte[] fileBuffer = new byte[cs];
//                byte[] fileBuffer2 = new byte[cs];
                int n = 0;
                    while ((n = filein.read(fileBuffer)) != -1) {
                        fileou.write(fileBuffer, 0, n);
                    }
                if (filein != null) {
                    filein.close();
                }

                if (StringUtil.isNotEmpty(secret)) {
                    FileInputStream fis = new FileInputStream(uploadPath + fileNameMerge);
                    byte[] b = new byte[fis.available()];
                    int read = fis.read(b);
                    for (int j = 0; j < read; j++) {
                        b[j] = (byte) (b[j] ^ 0x99);
                    }
                    FileOutputStream fos = new FileOutputStream(uploadPath + fileNameMerge);
                    fos.write(b);
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                }

                File mergeFile = new File(uploadPath + i + "_chunk_" + fileNameMerge.split("\\.")[0]);
                if (mergeFile.exists()) {
                    mergeFile.delete();
                }
            }

            if (FileUtil.TYPE_PIC.equals(fileType)) {
                String inputFile = uploadPath + fileNameMerge;
                String fileName = fileNameMerge;
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                String outputFile = uploadPath + fileName + "_thumb.jpg";
                PicUtil.commpressPicForScale(inputFile, outputFile, 100, 0.9);
                sysFileSuccess.setThumbPath(downloadPath + fileName + "_thumb.jpg");
                String webappFile = this.getUploadPath(requestURI, groupId, theDate, fileRoot, fileUploadFolder, uploadPathDateFormat, template) + fileName + "_thumb.jpg";
                FileUtil.copyFile(outputFile, webappFile);
            }
            successList.add(sysFileSuccess);
            map.put("successList", successList);
            map.put("failList", failList);
            saveSysFileList(groupId, map, loginName);
        } catch (Exception err) {
            sysFileSuccess.setName(fileNameMerge);
            sysFileSuccess.setFailType("IllegalExtension");
            failList.add(sysFileSuccess);
            map.put("successList", successList);
            map.put("failList", failList);
            err.printStackTrace();
        } finally {
            if (fileou != null)
                try {
                    fileou.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (filein != null)
                try {
                    filein.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fileouEncode != null)
                try {
                    fileouEncode.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    @Transactional(readOnly = false)
    public LinkedHashMap<String, Object> getFileList(String groupId) {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        SysFile sysFile = new SysFile();
        sysFile.setGroupId(groupId);
        if (StringUtils.isBlank(groupId)) {
            return map;
        }
        List<SysFile> sysFileListInDb = this.findList(sysFile);
        map.put("files", sysFileListInDb);
        return map;
    }

    @Transactional(readOnly = true)
    public List<SysFile> getFileListByGroupId(String groupId) {
        SysFile sysFile = new SysFile();
        sysFile.setGroupId(groupId);
        List<SysFile> sysFileListInDb = this.findList(sysFile);
        return sysFileListInDb;
    }

    @Transactional(readOnly = false)
    public File getFile(String fileId, String fileRoot) {
        String url = this.get(fileId).getPath();
        File file = new File(fileRoot + Encodes.unescapeHtml(url));
        return file;
    }

    @Transactional(readOnly = true)
    public File getFirstByGroupId(String groupId, String fileRoot) {
        SysFile sysFile = new SysFile();
        sysFile.setGroupId(groupId);
        List<SysFile> sysFileListInDb = this.findList(sysFile);
        String fileId = "";
        for(SysFile theSysFile : sysFileListInDb) {
            fileId = theSysFile.getId();
            break;
        }
        if (StringUtil.isNotEmpty(fileId)) {
            String url = this.get(fileId).getPath();
            File file = new File(fileRoot + Encodes.unescapeHtml(url));
            return file;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public String getFirstFilePathByGroupId(String groupId, String fileRoot) {
        SysFile sysFile = new SysFile();
        sysFile.setGroupId(groupId);
        List<SysFile> sysFileListInDb = this.findList(sysFile);
        String filePath = "";
        for(SysFile theSysFile : sysFileListInDb) {
            filePath = fileRoot + theSysFile.getPath();
            break;
        }
        return filePath;
    }

    @Transactional(readOnly = false)
    public void deleteFile(String fileId, String fileRoot) {
        this.delete(this.get(fileId), fileRoot);
    }

    private String getFileSizeMerge(long size) {
        if (size / 1024 < 1024) {
            return "(" + new DecimalFormat("0.0").format(size / 1024D) + "K)";
        } else {
            return "(" + new DecimalFormat("0.0").format(size / 1024D / 1024D) + "M)";
        }
    }

    private String getDownloadPath(String requestURI, String randomUUID, Date theDate, String fileUploadFolder, String uploadPathDateFormat, String template) {
        if (StringUtil.isNotEmpty(template)) {
            return new StringBuffer().append(fileUploadFolder).append(requestURI)
                    .append(template)
                    .append("/").append(randomUUID).append("/").toString();
        } else {
            return new StringBuffer().append(fileUploadFolder).append(requestURI)
                    .append(new SimpleDateFormat(uploadPathDateFormat).format(theDate))
                    .append("/").append(randomUUID).append("/").toString();
        }
    }

    private String getUploadPath(String requestURI, String randomUUID, Date theDate, String fileRoot, String fileUploadFolder, String uploadPathDateFormat, String template) {
        String realPath = fileRoot + fileUploadFolder;
        if (StringUtil.isNotEmpty(template)) {
            return new StringBuffer().append(realPath).append(requestURI)
                    .append(template)
                    .append("/").append(randomUUID).append("/").toString();
        } else {
            return new StringBuffer().append(realPath).append(requestURI)
                    .append(new SimpleDateFormat(uploadPathDateFormat).format(theDate))
                    .append("/").append(randomUUID).append("/").toString();
        }
    }

    @Transactional(readOnly = false)
    public int saveSecretLevel(SysFile sysFile) {
        return dao.saveSecretLevel(sysFile);
    }
}
