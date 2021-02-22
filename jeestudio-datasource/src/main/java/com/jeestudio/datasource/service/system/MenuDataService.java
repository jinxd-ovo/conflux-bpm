package com.jeestudio.datasource.service.system;

import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.system.Menu;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.datasource.feign.CacheFeign;
import com.jeestudio.datasource.mapper.base.system.MenuDao;
import com.jeestudio.datasource.service.ai.TransService;
import com.jeestudio.datasource.service.async.AsyncService;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @Description: Menu Service
 * @author: whl
 * @Date: 2019-12-25
 */
@Service
public class MenuDataService {

    protected static final Logger logger = LoggerFactory.getLogger(TransService.class);

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CacheFeign cacheFeign;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private GenTableService genTableService;

    @Transactional(readOnly = false)
    public ResultJson createMenuGroup(String formNo, String parentId, String icon) {
        ResultJson resultJson = new ResultJson();
        try {
            User user = new User("1");
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            //url type_=1  add type_=3 view/edit/del/lowerlevel type_=4
            Menu parentMenu = menuDao.get(parentId);
            Menu menu = new Menu();
            menu.preInsert();
            menu.setParent(parentMenu);
            menu.setParentIds(parentMenu.getParentIds() + parentId + ",");
            menu.setIsShow(Global.YES);
            menu.setType("1");
            menu.setHref("/" +  genTable.getModule() + "/" + genTable.getName() + "/list");
            if (StringUtil.isNotEmpty(icon)) menu.setIcon(icon);
            menu.setName(genTable.getComments());
            menu.setNameEn(genTable.getCommentsEn());
            menu.setSort(100);
            menu.setCreateBy(user);
            menu.setUpdateBy(user);
            menuDao.insert(menu);

            if (StringUtil.isEmpty(genTable.getProcessDefinitionCategory())) {
                Menu menuAdd = new Menu();
                menuAdd.preInsert();
                menuAdd.setParent(menu);
                menuAdd.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuAdd.setIsShow(Global.YES);
                menuAdd.setType("3");
                menuAdd.setIcon("fa-plus");
                menuAdd.setName("添加");
                menuAdd.setNameEn("Add");
                menuAdd.setSort(10);
                menuAdd.setPermission("app:" + genTable.getName() + ":add");
                menuAdd.setSign("addBtn");
                menuAdd.setCreateBy(user);
                menuAdd.setUpdateBy(user);
                menuDao.insert(menuAdd);

                Menu menuView = new Menu();
                menuView.preInsert();
                menuView.setParent(menu);
                menuView.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuView.setIsShow(Global.YES);
                menuView.setType("4");
                menuView.setName("查看");
                menuView.setNameEn("View");
                menuView.setSort(10);
                menuView.setPermission("app:" + genTable.getName() + ":view");
                menuView.setSign("view");
                menuView.setCreateBy(user);
                menuView.setUpdateBy(user);
                menuDao.insert(menuView);

                Menu menuEdit = new Menu();
                menuEdit.preInsert();
                menuEdit.setParent(menu);
                menuEdit.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuEdit.setIsShow(Global.YES);
                menuEdit.setType("4");
                menuEdit.setName("编辑");
                menuEdit.setNameEn("Edit");
                menuEdit.setSort(20);
                menuEdit.setPermission("app:" + genTable.getName() + ":edit");
                menuEdit.setSign("edit");
                menuEdit.setCreateBy(user);
                menuEdit.setUpdateBy(user);
                menuDao.insert(menuEdit);
            }

            if (false == genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
                Menu menuDeleteButton = new Menu();
                menuDeleteButton.preInsert();
                menuDeleteButton.setParent(menu);
                menuDeleteButton.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuDeleteButton.setIsShow(Global.YES);
                menuDeleteButton.setType("3");
                menuDeleteButton.setName("批量删除");
                menuDeleteButton.setNameEn("Batch Delete");
                menuDeleteButton.setSort(30);
                menuDeleteButton.setPermission("app:" + genTable.getName() + ":remove");
                menuDeleteButton.setSign("removeBtn");
                menuDeleteButton.setCreateBy(user);
                menuDeleteButton.setUpdateBy(user);
                menuDao.insert(menuDeleteButton);
            }

            Menu menuDelete = new Menu();
            menuDelete.preInsert();
            menuDelete.setParent(menu);
            menuDelete.setParentIds(menu.getParentIds() + menu.getId() + ",");
            menuDelete.setIsShow(Global.YES);
            menuDelete.setType("4");
            menuDelete.setName("删除");
            menuDelete.setNameEn("Delete");
            menuDelete.setSort(30);
            menuDelete.setPermission("app:" + genTable.getName() + ":del");
            menuDelete.setSign("del");
            menuDelete.setCreateBy(user);
            menuDelete.setUpdateBy(user);
            menuDao.insert(menuDelete);

            if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
                Menu menuLowerLevel = new Menu();
                menuLowerLevel.preInsert();
                menuLowerLevel.setParent(menu);
                menuLowerLevel.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuLowerLevel.setIsShow(Global.YES);
                menuLowerLevel.setType("4");
                menuLowerLevel.setName("添加下级");
                menuLowerLevel.setNameEn("Add Sub...");
                menuLowerLevel.setSort(40);
                menuLowerLevel.setPermission("app:" + genTable.getName() + ":lowerlevel");
                menuLowerLevel.setSign("lowerlevel");
                menuLowerLevel.setCreateBy(user);
                menuLowerLevel.setUpdateBy(user);
                menuDao.insert(menuLowerLevel);
            }
            //Import and Export
            if (StringUtil.isNotEmpty(genTable.getIsBuildImport()) && Global.YES.equals(genTable.getIsBuildImport())) {
                Menu menuImport = new Menu();
                menuImport.preInsert();
                menuImport.setParent(menu);
                menuImport.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuImport.setIsShow(Global.YES);
                menuImport.setType("3");
                menuImport.setName("导入");
                menuImport.setNameEn("Import");
                menuImport.setSort(13);
                menuImport.setPermission("app:" + genTable.getName() + ":import");
                menuImport.setSign("importBtn");
                menuImport.setCreateBy(user);
                menuImport.setUpdateBy(user);
                menuDao.insert(menuImport);
            }
            if (StringUtil.isNotEmpty(genTable.getIsBuildExport()) && Global.YES.equals(genTable.getIsBuildExport())) {
                Menu menuExport = new Menu();
                menuExport.preInsert();
                menuExport.setParent(menu);
                menuExport.setParentIds(menu.getParentIds() + menu.getId() + ",");
                menuExport.setIsShow(Global.YES);
                menuExport.setType("3");
                menuExport.setName("导出");
                menuExport.setNameEn("Export");
                menuExport.setSort(16);
                menuExport.setPermission("app:" + genTable.getName() + ":export");
                menuExport.setSign("exportBtn");
                menuExport.setCreateBy(user);
                menuExport.setUpdateBy(user);
                menuDao.insert(menuExport);
            }

            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("添加菜单成功");
            resultJson.setMsg_en("Add menu group success");
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("添加菜单失败");
            resultJson.setMsg_en("Add menu group failed");
        }
        return resultJson;
    }

    @Transactional(readOnly = false)
    public ResultJson saveMenu(Menu menu){
        ResultJson resultJson = new ResultJson();
        menu.setParentIds(menu.getParent().getParentIds()
                + menu.getParent().getId() + ",");
        if (StringUtil.isBlank(menu.getId())) {
            if (!"1".equals(menu.getParentId())
                    && !"0".equals(menu.getParentId())) {
                Menu parent = menu.getParent();
                menu.setSubSystemCodeList(parent.getSubSystemCodeList());
            }
            menu.setType("1");
            menu.preInsert();
            menuDao.insert(menu);
            resultJson.setCode(0);
            resultJson.setMsg("保存菜单成功");
            resultJson.setMsg_en("Save menu success");
        } else {
            menu.preUpdate();
            menuDao.update(menu);

            resultJson.setCode(0);
            resultJson.setMsg("更新菜单成功");
            resultJson.setMsg_en("Update menu success");
            for (Menu childMenu : menu.getChildren()) {
                if (childMenu.getId() == null) {
                    continue;
                }
                if (Menu.DEL_FLAG_NORMAL.equals(childMenu.getDelFlag())) {
                    if (StringUtil.isBlank(childMenu.getId())) {
                        childMenu.setParent(menu);
                        childMenu.preInsert();
                        childMenu.setParentIds(menu.getParentIds()
                                + menu.getId() + ",");
                        childMenu.setIsShow(Global.NO);
                        menuDao.insert(childMenu);
                    } else {
                        childMenu.setParent(menu);
                        childMenu.setParentIds(menu.getParentIds()
                                + menu.getId() + ",");
                        childMenu.preUpdate();
                        menuDao.update(childMenu);
                    }
                } else {
                    menuDao.delete(childMenu);
                }
            }
        }
        asyncService.deleteListHash(Global.MENU_CACHE, "_hashMenu_");
        asyncService.deleteListHash(Global.MENU_CACHE, "_permission_");
        return resultJson;
    }

    @Transactional(readOnly = true)
    public ResultJson getMenuTagTree() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.put("data",menuDao.getMenuTagTree());
        resultJson.setMsg("获取菜单成功");
        resultJson.setMsg_en("get menu success");
        return resultJson;
    }

    @Async
    public void refreshMenuCache() {
        Set<String> hashKeySet = cacheFeign.getHashKeys(Global.MENU_CACHE);
        for (String hashKey : hashKeySet) {
            if (hashKey.indexOf("_hashMenu_") != -1) {
                cacheFeign.deleteLikeHash(Global.MENU_CACHE, hashKey);
                UserUtil.getMenuList(new User(hashKey.substring(hashKey.indexOf("_hashMenu_") + 10)));
            } else if (hashKey.indexOf("_permission_") != -1) {
                cacheFeign.deleteLikeHash(Global.MENU_CACHE, hashKey);
                UserUtil.getMenuPermissionList(new User(hashKey.substring(hashKey.indexOf("_permission_") + 12)));
            }
        }
    }

    @Transactional(readOnly = true)
    public Boolean hasPermission(String userId, String permission) {
        return menuDao.hasPermission(userId, permission) > 0;
    }
}
