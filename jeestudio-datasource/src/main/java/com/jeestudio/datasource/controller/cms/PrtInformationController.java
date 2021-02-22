package com.jeestudio.datasource.controller.cms;

import com.google.common.collect.Lists;
import com.jeestudio.common.entity.cms.PrtChannel;
import com.jeestudio.common.entity.cms.PrtConstants;
import com.jeestudio.common.entity.cms.PrtInformation;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.datasource.service.cms.PrtInformationService;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: PrtInformation Controller
 * @author: David
 * @Date: 2020-07-02
 */
@Api(value = "PrtInformationController ",tags = "PrtInformation Controller")
@RestController
@RequestMapping("${datasourcePath}/cms")
public class PrtInformationController {

    @Autowired
    PrtInformationService prtInformationService;

    /**
     * Get news index
     */
    @ApiOperation(value = "/getIndex", tags = "Get news index")
    @ApiImplicitParams({@ApiImplicitParam(name = "activeChannel", value = "activeChannel", required = true, dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "loginName", required = true, dataType = "String")})
    @GetMapping("/getIndex")
    public ResultJson getIndex(@RequestParam("activeChannel") String activeChannel, @RequestParam("loginName") String loginName) {
        ResultJson resultJson = new ResultJson();
        PrtChannel queryChannel = new PrtChannel();
        queryChannel.setParent(new PrtChannel(PrtConstants.NUMBER_0)); //Level 1 channel
        queryChannel.setUseable(PrtConstants.ENABLED);
        List<PrtChannel> tabs = prtInformationService.findChannelList(queryChannel);
        resultJson.put("tabs", tabs);

        if (StringUtil.isBlank(activeChannel)) {
            //2. Rotation
            PrtInformation queryPlay = new PrtInformation();
            queryPlay.setIfPlay(PrtConstants.YES);
            queryPlay.setSelf(true);
            queryPlay.getSqlMap().put("dsf", this.getCmsDataScope()
                    + " AND a.typesimg is not null " + " AND b.useable = '" + PrtConstants.ENABLED + "' ");
            Page<PrtInformation> pagePlay = new Page<>();
            pagePlay.setPageSize(5);
            Page<PrtInformation> slider1 = prtInformationService.findPage(pagePlay, queryPlay, loginName);
            resultJson.put("slider1", slider1.getList());
            //3. Advertising
            List<PrtInformation> slider2 = Lists.newArrayList();
            resultJson.put("slider2", slider1.getList());
        } else {
            resultJson.put("slider1", Lists.newArrayList());
            resultJson.put("slider2", Lists.newArrayList());
        }

        //4. Latest article
        PrtInformation queryNew = new PrtInformation();
        Page<PrtInformation> pageNew = new Page<>();
        queryNew.setSelf(true);
        queryNew.getSqlMap().put("dsf", this.getCmsDataScope() + " AND b.useable = '" + PrtConstants.ENABLED + "' ");

        if (StringUtil.isNotBlank(activeChannel)) {
            queryNew.setChannel(new PrtChannel(activeChannel));
            pageNew.setPageSize(20);
        } else {
            pageNew.setPageSize(5);
        }
        Page<PrtInformation> rows = prtInformationService.findPage(pageNew, queryNew, loginName);
        resultJson.put("rows", rows.getList());
        if (rows.getCount() > rows.getList().size()) {
            resultJson.put("nomMore", false);
        } else {
            resultJson.put("nomMore", true);
        }
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        return resultJson;
    }

    /**
     * Get news list
     */
    @ApiOperation(value = "/getInfoList", tags = "Get news list")
    @ApiImplicitParams({@ApiImplicitParam(name = "activeChannel", value = "activeChannel", required = true, dataType = "String"),
            @ApiImplicitParam(name = "length", value = "length", required = true, dataType = "String")})
    @GetMapping("/getInfoList")
    public ResultJson getInfoList(@RequestParam("activeChannel") String activeChannel, @RequestParam("length") String length) {
        ResultJson resultJson = new ResultJson();
        PrtInformation queryNew = new PrtInformation();
        queryNew.setSelf(true);
        queryNew.getSqlMap().put("dsf", this.getCmsDataScope() + " AND b.useable = '" + PrtConstants.ENABLED + "' ");
        if (StringUtil.isNotBlank(activeChannel)) {
            queryNew.setChannel(new PrtChannel(activeChannel));//栏目
        }
        List<PrtInformation> rows = prtInformationService.findList(queryNew);
        int len = Integer.parseInt(length);
        if ((len + 10) < rows.size()) {
            resultJson.put("rows", rows.subList(len, len + 10));
            resultJson.put("nomMore", false);
        } else if ((len + 10) == rows.size()) {
            resultJson.put("rows", rows.subList(len, len + 10));
            resultJson.put("nomMore", true);
        } else {
            resultJson.put("rows", rows.subList(len, rows.size()));
            resultJson.put("nomMore", true);
        }
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        return resultJson;
    }

    /**
     * Get news
     */
    @ApiOperation(value = "/getInfo", tags = "Get news detail")
    @ApiImplicitParams({@ApiImplicitParam(name = "infoId", value = "infoId", required = true, dataType = "String")})
    @GetMapping("/getInfo")
    public ResultJson getInfo(@RequestParam("infoId") String infoId) {
        ResultJson resultJson = new ResultJson();
        PrtInformation info = prtInformationService.get(infoId);
        resultJson.put("info", info);
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        return resultJson;
    }

    private String getCmsDataScope() {
        StringBuilder sqlString = new StringBuilder();

        return sqlString.toString();
    }
}
