package com.jeestudio.datasource.feign;

import com.jeestudio.common.component.FeignSupportConfig;
import com.jeestudio.common.entity.common.Zform;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description: Master Data Feign
 * @author: houxl
 * @Date: 2019-09-18
 */
@FeignClient(value = "masterdata",configuration = FeignSupportConfig.class)
@Component
public interface MasterDataFeign {

    @GetMapping("/mainOffice/findMainOfficeList")
    List<Zform> findMainOfficeList();

    @GetMapping("/mainOffice/findMainUserList")
    List<Zform> findMainUserList();
}
