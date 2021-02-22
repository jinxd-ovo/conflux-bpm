package com.jeestudio.datasource.service.system;

import com.google.common.collect.Maps;
import com.jeestudio.common.entity.system.Area;
import com.jeestudio.common.entity.tagtree.TagTree;
import com.jeestudio.datasource.mapper.base.system.AreaDao;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: Area Service
 * @author: whl
 * @Date: 2020-02-05
 */
@Service
public class AreaService {

    @Autowired
    private AreaDao areaDao;

    @Transactional(readOnly = true)
    public ResultJson getAreaTagTreeAsync(String id) {
        ResultJson resultJson = new ResultJson();
        Area area = new Area();
        if (StringUtil.isNotEmpty(id)) {
            area.setId(id);
            area.setParent(new Area(id));
        } else {
            area.setParent(new Area(Global.DEFAULT_ROOT_CODE));
        }
        resultJson.setCode(0);
        resultJson.setMsg("获取区域成功");
        resultJson.setMsg_en("Get area success");
        List<TagTree> list = areaDao.findAreaTagTree(area);
        for (TagTree tagTree : list) {
            Area a = new Area();
            a.setParent(new Area(tagTree.getId()));
            List<TagTree> childrenList = areaDao.findAreaTagTree(a);
            if (childrenList != null && childrenList.size() > 0) {
                tagTree.setHasChildren(true);
            }
        }
        resultJson.put("data", list);
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson getAreaTagTree() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("获取区域成功");
        resultJson.setMsg_en("Get area success");
        Map<String, TagTree> map = Maps.newHashMap();
        List<TagTree> list = areaDao.findAreaTagTreeAll();
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
}
