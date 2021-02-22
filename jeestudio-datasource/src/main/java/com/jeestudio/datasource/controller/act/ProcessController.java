package com.jeestudio.datasource.controller.act;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.view.act.ProcessView;
import com.jeestudio.datasource.service.act.ActProcessService;
import com.jeestudio.utils.ResultJson;
import io.swagger.annotations.Api;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Process Controller
 * @author: David
 * @Date: 2020-08-13
 */
@Api(value = "ProcessController ",tags = "Process Controller")
@RestController
@RequestMapping("${datasourcePath}/act/process")
public class ProcessController {

    protected static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ActProcessService actProcessService;

    @PostMapping("/list")
    public ResultJson getProcessList(@RequestParam("category") String category, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize){
        ResultJson resultJson = new ResultJson();
        Page<ProcessView> page = actProcessService.processList(new Page<ProcessView>(pageNo, pageSize), category);
        resultJson.setRows(page.getList());
        resultJson.setTotal(page.getCount());
        return resultJson;
    }

    @PostMapping("/updateState")
    public String updateState(@RequestParam("state") String state, @RequestParam("procDefId") String procDefId){
        String message = actProcessService.updateState(state, procDefId);
        return message;
    }

    @PostMapping("/delete")
    public void delete(@RequestParam("deploymentId") String deploymentId){
        actProcessService.delete(deploymentId);
    }

    @PostMapping("/toModel")
    public String toModel(@RequestParam("procDefId") String procDefId) throws UnsupportedEncodingException, XMLStreamException {
        Model model = actProcessService.toModel(procDefId);
        String message = "Operation success, Model ID is " + model.getId();
        return message;
    }

    @PostMapping("/deploy")
    public String deploy(@RequestParam("category") String category, @RequestParam("filePath") String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile =new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        String fileName = multipartFile.getOriginalFilename();
        String message = "";
        if(StringUtils.isBlank(fileName)){
            message = "Please select file";
        }else{
            message = actProcessService.deploy(category, multipartFile);
        }
        return message;
    }

    @PostMapping("/runningList")
    public ResultJson runningList(@RequestBody ProcessView processView){
        ResultJson resultJson = new ResultJson();
        Integer pageNo = processView.getPageParam().getPageNo();
        Integer pageSize = processView.getPageParam().getPageSize();
        String procInsId = processView.getProcInsId();
        String procDefKey = processView.getProcDefKey();
        Page<ProcessInstance> page = actProcessService.runningList(new Page<ProcessInstance>(pageNo, pageSize), procInsId, procDefKey);
        List<ProcessView> list = new ArrayList<>();
        for(int i = 0; i <page.getList().size(); i++){
            ProcessInstance instance = page.getList().get(i);
            ProcessView view = new ProcessView();
            view.setProcessDefinitionName(instance.getProcessDefinitionName());
            view.setId(instance.getId());
            view.setProcessInstanceId(instance.getProcessInstanceId());
            view.setProcessDefinitionId(instance.getProcessDefinitionId());
            view.setActivityId(instance.getActivityId());
            view.setSuspended(instance.isSuspended());
            list.add(view);
        }
        resultJson.setRows(list);
        resultJson.setTotal(page.getCount());
        return resultJson;
    }

    @PostMapping("/historyList")
    public ResultJson historyList(@RequestBody ProcessView processView){
        ResultJson resultJson = new ResultJson();
        Integer pageNo = processView.getPageParam().getPageNo();
        Integer pageSize = processView.getPageParam().getPageSize();
        String procInsId = processView.getProcInsId();
        String procDefKey = processView.getProcDefKey();
        Page<HistoricProcessInstance> page = actProcessService.historyList(new Page<HistoricProcessInstance>(pageNo, pageSize), procInsId, procDefKey);
        resultJson.setRows(page.getList());
        resultJson.setTotal(page.getCount());
        return resultJson;
    }

    @PostMapping("/trace/photo")
    public void historyPhoto(@RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("executionId") String executionId, HttpServletResponse response) throws IOException {
        InputStream imageStream = actProcessService.tracePhoto(processDefinitionId, executionId);

        byte[] b = new byte[1024];
        int len;
        while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    @PostMapping("/deleteProcIns")
    public String deleteProcIns(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("reason") String reason) {
        String message = "";
        if(StringUtils.isBlank(reason)){
            message = "Please input reason";
        }else{
            actProcessService.deleteProcIns(processInstanceId, reason);
            message = "Operation success, process instance ID is " + processInstanceId;
        }
        return message;
    }
}
