package com.jeestudio.datasource.utils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.datasource.annotation.FieldName;
import com.jeestudio.common.entity.act.Act;
import com.jeestudio.common.entity.system.Role;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.utils.Encodes;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Act Util
 * @author: David
 * @Date: 2020-01-13
 */
public class ActUtil {

    protected static final Logger logger = LoggerFactory.getLogger(ActUtil.class);

    @SuppressWarnings({"unused"})
    public static Map<String, Object> getMobileEntity(Object entity, String spiltType) {
        if (spiltType == null) {
            spiltType = "@";
        }
        Map<String, Object> map = Maps.newHashMap();
        List<String> field = Lists.newArrayList();
        List<String> value = Lists.newArrayList();
        List<String> chinesName = Lists.newArrayList();
        try {
            for (Method m : entity.getClass().getMethods()) {
                if (m.getAnnotation(JsonIgnore.class) == null && m.getAnnotation(JsonBackReference.class) == null && m.getName().startsWith("get")) {
                    if (m.isAnnotationPresent(FieldName.class)) {
                        Annotation p = m.getAnnotation(FieldName.class);
                        FieldName fieldName = (FieldName) p;
                        chinesName.add(fieldName.value());
                    } else {
                        chinesName.add("");
                    }
                    if (m.getName().equals("getAct")) {
                        Object act = m.invoke(entity, new Object[]{});
                        Method actMet = act.getClass().getMethod("getTaskId");
                        map.put("taskId", ObjectUtils.toString(m.invoke(act, new Object[]{}), ""));
                    } else {
                        field.add(StringUtils.uncapitalize(m.getName().substring(3)));
                        value.add(ObjectUtils.toString(m.invoke(entity, new Object[]{}), ""));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Warn while getting mobile entity:" + ExceptionUtils.getStackTrace(e));
        }
        map.put("beanTitles", StringUtils.join(field, spiltType));
        map.put("beanInfos", StringUtils.join(value, spiltType));
        map.put("chineseNames", StringUtils.join(chinesName, spiltType));
        return map;
    }

    public static String getFormUrl(String formKey, Act act) {
        StringBuilder formUrl = new StringBuilder();
        formUrl.append(formKey).append(formUrl.indexOf("?") == -1 ? "?" : "&");
        formUrl.append("act.taskId=").append(act.getTaskId() != null ? act.getTaskId() : "");
        formUrl.append("&act.taskName=").append(act.getTaskName() != null ? Encodes.urlEncode(act.getTaskName()) : "");
        formUrl.append("&act.taskDefKey=").append(act.getTaskDefKey() != null ? act.getTaskDefKey() : "");
        formUrl.append("&act.procInsId=").append(act.getProcInsId() != null ? act.getProcInsId() : "");
        formUrl.append("&act.procDefId=").append(act.getProcDefId() != null ? act.getProcDefId() : "");
        formUrl.append("&act.status=").append(act.getStatus() != null ? act.getStatus() : "");
        formUrl.append("&id=").append(act.getBusinessId() != null ? act.getBusinessId() : "");
        return formUrl.toString();
    }

    public static String parseToZhType(String type) {
        Map<String, String> types = new HashMap<String, String>();
        types.put("userTask", "用户任务");
        types.put("serviceTask", "系统任务");
        types.put("startEvent", "开始节点");
        types.put("endEvent", "结束节点");
        types.put("exclusiveGateway", "条件判断节点(系统自动根据条件处理)");
        types.put("inclusiveGateway", "并行处理任务");
        types.put("callActivity", "子流程");
        return types.get(type) == null ? type : types.get(type);
    }

    public static UserEntity toActivitiUser(User user) {
        if (user == null) {
            return null;
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(user.getLoginName());
            userEntity.setFirstName(user.getName());
            userEntity.setLastName(StringUtils.EMPTY);
            userEntity.setPassword(user.getPassword());
            userEntity.setEmail(user.getEmail());
            userEntity.setRevision(1);
            return userEntity;
        }
    }

    public static GroupEntity toActivitiGroup(Role role) {
        if (role == null) {
            return null;
        } else {
            GroupEntity groupEntity = new GroupEntity();
            groupEntity.setId(role.getEnname());
            groupEntity.setName(role.getName());
            //groupEntity.setType(role.getRoleType());
            groupEntity.setRevision(1);
            return groupEntity;
        }
    }
}
