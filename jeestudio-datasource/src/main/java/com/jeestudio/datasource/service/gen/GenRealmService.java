package com.jeestudio.datasource.service.gen;

import com.jeestudio.common.entity.gen.GenTableColumnView;
import com.jeestudio.common.entity.system.DictGenView;
import com.jeestudio.datasource.mapper.base.gen.GenRealmPropertyDao;
import com.jeestudio.datasource.mapper.base.system.DictDao;
import com.jeestudio.datasource.service.system.DictDataService;
import com.jeestudio.utils.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: GenRealm Service
 * @author: whl
 * @Date: 2020-02-10
 */
@Service
public class GenRealmService {

    @Autowired
    private GenRealmPropertyDao genRealmPropertyDao;

    @Autowired
    private DictDao dictDao;

    public ResultJson realmData(String[] genRealm) {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("获取领域成功");
        resultJson.setMsg_en("Get realm form success");
        List<GenTableColumnView> list = new ArrayList<GenTableColumnView>();
        for (int i = 0; i < genRealm.length; i++) {
            List<GenTableColumnView> g = genRealmPropertyDao.realmData("普通字段", genRealm[i]);
            List<GenTableColumnView> gDict = genRealmPropertyDao.realmDataDict("普通字段", genRealm[i]);
            if (g.size() > 0) {
                GenTableColumnView columnView = g.get(0);
                if (columnView != null) {
                    list.add(columnView);
                }
            } else if (gDict.size() > 0) {
                GenTableColumnView columnView = gDict.get(0);
                if (columnView != null) {
                    List<DictGenView> dictGenViewList = dictDao.getDictGenView(columnView.getDictType());
                    columnView.setDictList(dictGenViewList);
                    list.add(columnView);
                }
            } else {
                GenTableColumnView columnView = new GenTableColumnView();
                columnView = valiateRealmProperty(columnView, genRealm[i], i);
                list.add(columnView);
            }
        }
        resultJson.put("realm", list);
        return resultJson;
    }

    private GenTableColumnView valiateRealmProperty(GenTableColumnView columnView, String comment, int i) {
        columnView.setIsForm("1");
        if (comment.indexOf("时间") != -1 || comment.indexOf("日期") != -1) {
            columnView.setName("d_date" + i);
            columnView.setComments(comment);
            columnView.setShowType("dateselect");
            columnView.setJdbcType("datetime");
            columnView.setJavaType("java.util.Date");
            columnView.setDictType("");
            columnView.setJavaField("d");
            columnView.setIsForm("1");
        } else if (comment.indexOf("复选") != -1 || comment.indexOf("多项") != -1 || comment.indexOf("多选") != -1) {
            columnView.setName("c_box" + i);
            columnView.setComments(comment);
            columnView.setShowType("checkbox");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("String");
            columnView.setDictType("like_type");
            columnView.setJavaField("c");
            columnView.setIsForm("1");
        } else if (comment.indexOf("单选") != -1 || comment.indexOf("单项") != -1) {
            columnView.setName("s_box" + i);
            columnView.setComments(comment);
            columnView.setShowType("radiobox");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("String");
            columnView.setDictType("study_type");
            columnView.setJavaField("s");
            columnView.setIsForm("1");
        } else if (comment.indexOf("下拉") != -1 || comment.indexOf("选择") != -1 || comment.indexOf("选项") != -1) {
            columnView.setName("s_select" + i);
            columnView.setComments(comment);
            columnView.setShowType("select");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("String");
            columnView.setDictType("yes_no");
            columnView.setJavaField("s");
            columnView.setIsForm("1");
        } else if (comment.indexOf("人") != -1 || comment.indexOf("人员") != -1) {
            columnView.setName("u_user" + i);
            columnView.setComments(comment);
            columnView.setShowType("treeselectRedio");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("com.gt_plus.modules.sys.entity.User");
            columnView.setDictType("");
            columnView.setJavaField("user");
            columnView.setIsForm("1");
        } else if (comment.indexOf("部门") != -1 || comment.indexOf("机构") != -1 || comment.indexOf("单位") != -1) {
            columnView.setName("o_office" + i);
            columnView.setComments(comment);
            columnView.setShowType("officeselectTree");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("ccom.gt_plus.modules.sys.entity.Office");
            columnView.setDictType("");
            columnView.setJavaField("office");
            columnView.setIsForm("1");
        } else if (comment.indexOf("区域") != -1 || comment.indexOf("地区") != -1 || comment.indexOf("位置") != -1 || comment.indexOf("地址") != -1) {
            columnView.setName("a_area" + i);
            columnView.setComments(comment);
            columnView.setShowType("areaselect");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("com.gt_plus.modules.sys.entity.Area");
            columnView.setDictType("");
            columnView.setJavaField("area");
            columnView.setIsForm("1");
        } else if (comment.indexOf("附件") != -1 || comment.indexOf("文件") != -1 || comment.indexOf("上传") != -1) {
            columnView.setName("f_file" + i);
            columnView.setComments(comment);
            columnView.setShowType("fileupload");
            columnView.setJdbcType("longtext");
            columnView.setJavaType("String");
            columnView.setDictType("");
            columnView.setJavaField("s");
            columnView.setIsForm("1");
        } else {
            columnView.setName("s_input" + i);
            columnView.setComments(comment);
            columnView.setShowType("input");
            columnView.setJdbcType("varchar(64)");
            columnView.setJavaType("String");
            columnView.setDictType("");
            columnView.setJavaField("s");
            columnView.setIsForm("1");
        }
        return columnView;
    }

    public GenTableColumnView getByName(String name) {
        return genRealmPropertyDao.getByName(name);
    }
}
