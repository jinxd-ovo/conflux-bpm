package com.jeestudio.form.service;

import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.file.FileReportProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class UreportService {

    private FileReportProvider fileReportProvider ;

    @Value("${ureport.fileStoreDir}")
    private String fileStoreDir;

    private FileReportProvider getFileReportProvider() {
        this.fileReportProvider = new FileReportProvider();
        this.fileReportProvider.setFileStoreDir(fileStoreDir);
        return this.fileReportProvider;
    }

    public List<ReportFile> getReportFiles() {
        fileReportProvider = this.getFileReportProvider();
        File file = new File(fileStoreDir);
        if (!file.exists()){
            file.mkdir();
        }
        File[] var3 = file.listFiles();
        int var4 = var3.length;
        return fileReportProvider.getReportFiles();
    }

    public void deleteReport(String file) {
        if (file.startsWith(fileReportProvider.getPrefix())) {
            fileReportProvider.deleteReport(file);
        }
    }
}
