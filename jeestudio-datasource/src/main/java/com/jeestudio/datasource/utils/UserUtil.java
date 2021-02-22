package com.jeestudio.datasource.utils;


import com.google.gson.reflect.TypeToken;
import com.jeestudio.common.security.Digests;
import com.jeestudio.datasource.contextHolder.ApplicationContextHolder;
import com.jeestudio.common.entity.system.Menu;
import com.jeestudio.common.entity.system.MenuResult;
import com.jeestudio.common.entity.system.Role;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.datasource.feign.CacheFeign;
import com.jeestudio.datasource.mapper.base.system.DatapermissionDao;
import com.jeestudio.datasource.mapper.base.system.MenuDao;
import com.jeestudio.datasource.mapper.base.system.RoleDao;
import com.jeestudio.datasource.mapper.base.system.UserDao;
import com.jeestudio.datasource.service.async.AsyncService;
import com.jeestudio.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: User Util
 * @author: whl
 * @Date: 2019-11-29
 */
public class UserUtil {

    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    private static MenuDao menuDao = ApplicationContextHolder.getBean(MenuDao.class);
    private static UserDao userDao = ApplicationContextHolder.getBean(UserDao.class);
    private static RoleDao roleDao = ApplicationContextHolder.getBean(RoleDao.class);
    private static DatapermissionDao datapermissionDao = ApplicationContextHolder.getBean(DatapermissionDao.class);
    private static CacheFeign cacheFeign = ApplicationContextHolder.getBean(CacheFeign.class);
    private static AsyncService asyncService = ApplicationContextHolder.getBean(AsyncService.class);

    /**
     * Get current user's menu
     */
    public static List<MenuResult> getMenuList(User user) {
        if(cacheFeign != null && userDao != null){
            Object menuObject = cacheFeign.getHash(Global.MENU_CACHE, "_hashMenu_" + user.getId());
            List<MenuResult> menuList = new ArrayList<>();
            String menuString = "";
            if (menuObject == null){
                Menu m = new Menu();
                if (user.isAdmin()){
                    menuList = menuDao.findMenuAllList(m);
                }else{
                    m.setUserId(user.getId());
                    menuList = menuDao.findMenuByUserId(m);
                }
                menuString = JsonConvertUtil.gsonBuilder().toJson(menuList);
                asyncService.asyncSaveHashCache(Global.MENU_CACHE, "_hashMenu_" + user.getId(), menuString);
            }else{
                menuList = JsonConvertUtil.gsonBuilder().fromJson(menuObject.toString(), new TypeToken<List<MenuResult>>() {}.getType());
            }
            return menuList;
        }else{
            return null;
        }
    }

    /**
     * Get user by id
     * @param id
     * @return user
     */
    public static User get(String id){
        if(cacheFeign != null && userDao != null){
            Object userObject = cacheFeign.getHash(Global.USER_CACHE,"_" + id);
            User user = null;
            if (userObject == null){
                user = userDao.get(id);
                if (user == null){
                    return null;
                }
                user.setRoleList(roleDao.findList(new Role(user)));
                String userString = JsonConvertUtil.objectToJson(user);
                asyncService.asyncSaveHashCache(Global.USER_CACHE,"_" + user.getLoginName(),userString);
                asyncService.asyncSaveHashCache(Global.USER_CACHE,"_" + user.getId(),userString);
            }else{
                user = JsonConvertUtil.gsonBuilder().fromJson(userObject.toString() , User.class);
            }
            return user;
        }else{
            return null;
        }
    }

    /**
     * Get users by login
     * @param loginName
     * @return user
     */
    public static User getByLoginName(String loginName) {
        if(cacheFeign != null && userDao != null){
            Object userObject = cacheFeign.getHash(Global.USER_CACHE,"_" + loginName);
            User user = null;
            if (userObject == null){
                user = userDao.getByLoginName(new User(null, loginName));
                if (user == null){
                    return null;
                }
                user.setRoleList(roleDao.findList(new Role(user)));
                user.setDatapermissionList(datapermissionDao.findListByUserId(user.getId()));
                String userString = JsonConvertUtil.gsonBuilder().toJson(user);
                asyncService.asyncSaveHashCache(Global.USER_CACHE,"_" + user.getLoginName(),userString);
                asyncService.asyncSaveHashCache(Global.USER_CACHE,"_" + user.getId(),userString);
            }else{
                user = JsonConvertUtil.gsonBuilder().fromJson(userObject.toString(), User.class);
            }
            return user;
        }else{
            return null;
        }
    }

    /**
     * Get menu list of the user
     * @param user
     * @return menu list
     */
    public static List<String> getMenuPermissionList(User user) {
        if(cacheFeign != null && userDao != null){
            Object menuObject = cacheFeign.getHash(Global.MENU_CACHE, "_permission_" + user.getId());
            List<String> menuList = new ArrayList<>();
            if (menuObject == null || "".equals(menuObject)){
                Menu m = new Menu();
                if (user.isAdmin() || "1".equals(user.getId())){
                    menuList = menuDao.findAllPermissionList(m);
                }else{
                    m.setUserId(user.getId());
                    menuList = menuDao.findPermissionByUserId(m);
                }
                String menuString = JsonConvertUtil.gsonBuilder().toJson(menuList);
                asyncService.asyncSaveHashCache(Global.MENU_CACHE, "_permission_" + user.getId(), menuString);
            }else{
                menuList = JsonConvertUtil.gsonBuilder().fromJson(menuObject.toString(), new TypeToken<List<String>>() {}.getType());
            }
            return menuList;
        }else{
            return null;
        }
    }

    /**
     * Encrypt password
     * @param plainPassword
     * @return password encrypted
     */
    public static String encryptPassword(String plainPassword) {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt,
                HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }
}
