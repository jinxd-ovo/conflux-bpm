package com.jeestudio.services.admin.aspect;

import com.jeestudio.common.param.GridselectParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Description: Grid Select Data Aspect
 * @author: jinxd
 * @Date: 2020-10-29
 */
@Aspect
@Component
public class GridSelectDataAspect {

    @Pointcut("execution(public * com.jeestudio.services.admin.controller.dynamic.ZformController.gridselectData(com.jeestudio.common.param.GridselectParam))")
    public void gridSelectData() {

    }

    @Around("gridSelectData()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object[] args = pjp.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof GridselectParam) {
                GridselectParam gridselectParam = (GridselectParam) args[0];
                String tableName = gridselectParam.getTableName();
                if ("sys_level".equals(tableName)) {
                    gridselectParam.setDsfPlus(" AND a.useable = '1' ");
                } else if ("sys_post".equals(tableName)) {
                    gridselectParam.setDsfPlus(" AND a.useable = '1' ");
                }
            }
        } catch (Exception e) {

        } finally {
            Object proceed = pjp.proceed();
            return proceed;
        }
    }
}
