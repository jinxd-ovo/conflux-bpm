package com.jeestudio.datasource.service.system;

import com.google.common.collect.Maps;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.common.entity.tagtree.TagTree;
import com.jeestudio.common.view.system.OfficeView;
import com.jeestudio.datasource.mapper.base.system.OfficeDao;
import com.jeestudio.datasource.service.common.TreeService;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: Office Service
 * @author: David
 * @Date: 2020-01-11
 */
@Service
public class OfficeService extends TreeService<OfficeDao, Office> {

    @Autowired
    private OfficeDao officeDao;

    @Transactional(readOnly = true)
    public ResultJson getOfficeTagTree() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("获取机构成功");
        resultJson.setMsg_en("Get office success");
        Map<String, TagTree> map = Maps.newHashMap();
        List<TagTree> list = officeDao.findOfficeTagTreeAll();
        for (TagTree tagTree : list) {
            map.put(tagTree.getId(), tagTree);
        }
        for (TagTree tagTree : list) {
            if (StringUtil.isNotBlank(tagTree.getParentId())) {
                TagTree tt = map.get(tagTree.getParentId());
                if (tt != null) {
                    tt.setHasChildren(true);
                }
            }
        }
        resultJson.put("data", list);
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson getOfficeTagTreeAsync(String id) {
        ResultJson resultJson = new ResultJson();
        Office office = new Office();
        if (id != null && !"".equals(id)) {
            office.setId(id);
            office.setParent(new Office(id));
        } else {
            office.setParent(new Office(Global.DEFAULT_ROOT_CODE));
        }
        resultJson.setCode(0);
        resultJson.setMsg("获取机构成功");
        resultJson.setMsg_en("Get office success");
        List<TagTree> list = officeDao.findOfficeTagTree(office);
        for (TagTree tagTree : list) {
            Office o = new Office();
            o.setParent(new Office(tagTree.getId()));
            List<TagTree> childrenList = officeDao.findOfficeTagTree(o);
            if (childrenList != null && childrenList.size() > 0) {
                tagTree.setHasChildren(true);
            }
        }
        resultJson.put("data", list);
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson findOfficeViewData(OfficeView officeView) {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("获取机构成功");
        resultJson.setMsg_en("Get office success");
        resultJson.put("list", officeDao.findOfficeViewData(officeView));
        return resultJson;
    }

    @Transactional(readOnly = true)
    public String getCompanyIdByOfficeId(String officeId) {
        Office theOffice = officeDao.get(officeId);
        if (Office.TYPE_COMPANY.equals(theOffice.getType())) {
            return theOffice.getId();
        } else {
            return getCompanyIdByOfficeId(theOffice.getParent().getId());
        }
    }

    @Transactional(readOnly = true)
    public void updateByMasterData(Office office) {
        officeDao.updateByMasterData(office);
    }
}
