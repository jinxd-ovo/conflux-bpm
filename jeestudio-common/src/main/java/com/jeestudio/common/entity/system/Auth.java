package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.TreeEntity;

/**
 * @Description: Auth
 * @author: jinxd
 * @Date: 2020-10-23
 */
public class Auth extends TreeEntity<Area> {

    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Override
    public Area getParent() {
        return null;
    }

    @Override
    public void setParent(Area parent) {

    }
}
