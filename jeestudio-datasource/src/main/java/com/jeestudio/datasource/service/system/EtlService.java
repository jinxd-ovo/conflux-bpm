package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.system.EtlInfo;
import com.jeestudio.common.entity.system.SysFile;
import com.jeestudio.datasource.mapper.base.system.EtlInfoDao;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.utils.StringUtil;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: ETL Service
 * @author: houxl
 * @Date: 2020-11-17
 */
@Service
public class EtlService {

    @Autowired
    protected GenTableService genTableService;

    @Autowired
    private EtlInfoDao etlInfoDao;

    @Autowired
    private SysFileService sysFileService;

    @Value("${docTemplateRoot}")
    private String docTemplateRoot;

    @Transactional(readOnly = false)
    public void runJob(long internal) throws KettleException {
        EtlInfo etlInfo = new EtlInfo();
        etlInfo.setInternal(String.valueOf(internal));
        List<EtlInfo> list = etlInfoDao.findList(etlInfo);
        for(EtlInfo theEtlInfo : list) {
            if (StringUtil.isNotEmpty(theEtlInfo.getScriptfile())) {
                List<SysFile> fileList = sysFileService.getFileListByGroupId(theEtlInfo.getScriptfile());
                if (this.isScriptFileValid(fileList)) {
                    String jobPath = null;
                    String ktrPath = null;
                    for(SysFile sysFile : fileList) {
                        if (sysFile.getName().endsWith(".ktr")) {
                            ktrPath = docTemplateRoot + "/" + sysFile.getName();
                        } else {
                            jobPath = docTemplateRoot + "/" + sysFile.getName();
                        }
                    }
                    KettleEnvironment.init();
                    JobMeta jobMeta = new JobMeta(jobPath, null);
                    Job job = new Job(null,jobMeta);
                    job.setVariable("ktrPath", ktrPath);
                    job.setVariable("host", theEtlInfo.getHost());
                    job.setVariable("database", theEtlInfo.getDatabase());
                    job.setVariable("pattern", theEtlInfo.getPattern());
                    job.setVariable("port", theEtlInfo.getPort());
                    job.setVariable("userName", theEtlInfo.getUserName());
                    job.setVariable("password", theEtlInfo.getPassword());
                    job.start();
                    job.waitUntilFinished();
                }
            }
        }
    }

    private boolean isScriptFileValid(List<SysFile> fileList) {
        boolean result = true;
        if (fileList.size() != 2) {
            result = false;
        } else {
            String name1 = fileList.get(0).getName();
            String name2 = fileList.get(1).getName();
            if (name1.endsWith(".ktr")) {
                if (false == name2.endsWith(".kjb")) {
                    result = false;
                }
            } else if (name1.endsWith(".kjb")) {
                if (false == name2.endsWith(".ktr")) {
                    result = false;
                }
            } else {
                result = false;
            }
        }
        return result;
    }
}
