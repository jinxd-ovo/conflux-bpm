package com.jeestudio.datasource.service.system;

import com.google.common.collect.Lists;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.param.PageParam;
import com.jeestudio.datasource.feign.MasterDataFeign;
import com.jeestudio.datasource.service.common.ZformService;
import com.jeestudio.datasource.service.gen.GenTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: Master Data Service
 * @author: David
 * @Date: 2020-09-16
 */
@Service
public class MasterDataService {

    private final String INSERT = "1";
    private final String UPDATE = "2";
    private final String IGNORE = "3";

    @Autowired
    protected GenTableService genTableService;

    @Autowired
    private ZformService zformService;

    @Autowired
    MasterDataFeign masterDataFeign;

    @Autowired
    OfficeService officeService;

    @Autowired
    UserService userService;

    @Transactional(readOnly = false)
    public void syncMasterData() throws Exception {
        this.traceOffice();
        this.traceUser();
    }

    @Transactional(readOnly = false)
    protected void traceOffice() throws Exception {
        //s01 name
        //s04 short_name
        //s03 short_code
        //List master data
        String formNo = "main_office";
        List<Zform> officeList = masterDataFeign.findMainOfficeList();

        //List local data
        Zform zform = new Zform();
        zform.setFormNo(formNo);
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(Integer.MAX_VALUE);
        zform.setPageParam(pageParam);
        String path = "path";
        String loginName = "system";
        String traceFlag = "";
        String parentId = "";
        Page<Zform> page = zformService.findPage(new Page<Zform>(zform.getPageParam().getPageNo(), zform.getPageParam().getPageSize(), zform.getPageParam().getOrderBy()),
                zform,
                path,
                loginName,
                genTable,
                traceFlag,
                parentId);
        List<Zform> localOfficeList = page.getList();

        //Mark insert/update/ignore
        for(Zform office : officeList) {
            office.setStatus(INSERT);
            for(Zform localOffice : localOfficeList) {
                if (office.getS03().equals(localOffice.getS03())) {
                    if (office.getUpdateDate().after(localOffice.getUpdateDate())) {
                        office.setStatus(UPDATE);
                    } else {
                        office.setStatus(IGNORE);
                    }
                    break;
                }
            }
        }

        List<Office> updateList = Lists.newArrayList();
        //Do insert or update
        for(Zform office : officeList) {
            if (office.getStatus().equals(INSERT)) {
                Zform newOffice = new Zform();
                newOffice.setFormNo(formNo);
                newOffice.setParent(office.getParent());
                newOffice.setS01(office.getS01());
                newOffice.setS03(office.getS03());
                newOffice.setS04(office.getS04());
                zformService.save(newOffice, genTable);
            } else if (office.getStatus().equals(UPDATE)) {
                office.setStatus("");
                zformService.save(office, genTable);
                Office newOffice = new Office();
                newOffice.setName(office.getS01());
                newOffice.setShortCode(office.getS03());
                newOffice.setShortName(office.getS04());
                updateList.add(newOffice);
            }
        }

        //Update
        for(Office office : updateList) {
            officeService.updateByMasterData(office);
        }
    }

    @Transactional(readOnly = false)
    protected void traceUser() throws Exception {
        //s01 login_name
        //s02 no
        //s04 name
        //s03 gender
        //s06 user_type, sec level
        //s05 mobile
        //List master data
        String formNo = "main_user";
        List<Zform> userList = masterDataFeign.findMainUserList();

        //List local data
        Zform zform = new Zform();
        zform.setFormNo(formNo);
        GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(Integer.MAX_VALUE);
        zform.setPageParam(pageParam);
        String path = "path";
        String loginName = "system";
        String traceFlag = "";
        String parentId = "";
        Page<Zform> page = zformService.findPage(new Page<Zform>(zform.getPageParam().getPageNo(), zform.getPageParam().getPageSize(), zform.getPageParam().getOrderBy()),
                zform,
                path,
                loginName,
                genTable,
                traceFlag,
                parentId);
        List<Zform> localUserList = page.getList();

        //Mark insert/update/ignore
        for(Zform user : userList) {
            user.setStatus(INSERT);
            for(Zform localUser : localUserList) {
                if (user.getS02().equals(localUser.getS02())) {
                    if (user.getUpdateDate().after(localUser.getUpdateDate())) {
                        user.setStatus(UPDATE);
                    } else {
                        user.setStatus(IGNORE);
                    }
                    break;
                }
            }
        }

        List<User> updateList = Lists.newArrayList();
        //Do insert or update
        for(Zform user : userList) {
            if (user.getStatus().equals(INSERT)) {
                Zform newUser = new Zform();
                newUser.setFormNo(formNo);
                newUser.setParent(user.getParent());
                newUser.setS01(user.getS01());
                newUser.setS02(user.getS02());
                newUser.setS03(user.getS03());
                newUser.setS04(user.getS04());
                newUser.setS05(user.getS05());
                newUser.setS06(user.getS06());
                zformService.save(newUser, genTable);
            } else if (user.getStatus().equals(UPDATE)) {
                user.setStatus("");
                zformService.save(user, genTable);
                User newUser = new User();
                //s01 login_name
                //s02 no
                //s04 name
                //s03 gender
                //s06 user_type, sec level
                //s05 mobile
                newUser.setLoginName(user.getS01());
                newUser.setNo(user.getS02());
                newUser.setName(user.getS04());
                newUser.setUserType(user.getS06());
                newUser.setMobile(user.getS05());
                updateList.add(newUser);
            }
        }

        //Update
        for(User user : updateList) {
            userService.updateByMasterData(user);
        }
    }
}
