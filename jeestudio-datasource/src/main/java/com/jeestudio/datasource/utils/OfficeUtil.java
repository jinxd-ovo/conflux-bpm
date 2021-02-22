package com.jeestudio.datasource.utils;

import com.jeestudio.common.view.system.OfficeView;
import com.jeestudio.common.entity.system.Office;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description: Office Util
 * @author: David
 * @Date: 2020-01-14
 */
public class OfficeUtil {
    public static void OfficeViewCopy(List<Office> sourceList, List<OfficeView> targetList) {
        for (Office object: sourceList) {
            OfficeView targetObject = new OfficeView();
            BeanUtils.copyProperties(object, targetObject);
            targetList.add(targetObject);
        }
    }
}
