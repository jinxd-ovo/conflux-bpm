package com.jeestudio.services.admin.dao.datasourceFeign;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jeestudio.common.component.FeignSupportConfig;
import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.act.TaskSetting;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenScheme;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumnView;
import com.jeestudio.common.entity.gen.GenTableView;
import com.jeestudio.common.entity.system.*;
import com.jeestudio.common.param.AssignParam;
import com.jeestudio.common.param.GridselectParam;
import com.jeestudio.common.view.act.ProcessView;
import com.jeestudio.common.view.oa.CalendarMeetingView;
import com.jeestudio.common.view.system.OfficeView;
import com.jeestudio.utils.ResultJson;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Datasource Feign
 * @author: whl
 * @Date: 2019-12-25
 */
@FeignClient(value = "datasource",configuration = FeignSupportConfig.class)
@Component
public interface DatasourceFeign {

    /**
     * Get user by login name
     *
     * @param loginName
     * @return user
     */
    @GetMapping("/datasource/user/getUserByLoginName")
    User getUserByLoginName(@RequestParam("loginName") String loginName);

    /**
     * Get user name by login name
     *
     * @param loginName
     * @return user name
     */
    @GetMapping("/datasource/user/getUserNameByLoginName")
    String getUserNameByLoginName(@RequestParam("loginName") String loginName);

    /**
     * Get user by user id
     *
     * @param userId
     * @return user
     */
    @GetMapping("/datasource/user/getUserById")
    User getUserById(@RequestParam("userId") String userId);

    /**
     * Get user view
     *
     * @param userId
     * @return user view
     */
    @GetMapping("/datasource/user/getCurrentUserView")
    ResultJson getCurrentUserView(@RequestParam("userId") String userId);

    /**
     * Get menu list by user id
     *
     * @param userId
     * @return MenuResult list
     */
    @GetMapping("/datasource/menu/getMenuListByUser")
    List<MenuResult> getMenuListByUser(@RequestParam("userId") String userId);

    /**
     * Check permission by user id and permission key
     */
    @PostMapping("/datasource/menu/hasPermission")
    Boolean hasPermission(@RequestParam("userId") String userId, @RequestParam("permission") String permission);

    /**
     * Get dict list
     *
     * @param type
     * @return DictResult list
     */
    @GetMapping("/datasource/dict/dictList")
    List<DictResult> dictList(@RequestParam("type") String type);

    /*Act begin*/

    /*TaskSetting begin*/

    /**
     * Get TaskSetting
     *
     * @param procDefKey
     * @param userTaskId
     * @param userTaskName
     * @return TaskSetting map
     */
    @GetMapping("/datasource/act/taskSetting/getTaskSetting")
    LinkedHashMap<String, Object> getTaskSetting(@RequestParam("procDefKey") String procDefKey, @RequestParam("userTaskId") String userTaskId, @RequestParam("userTaskName") String userTaskName);

    /**
     * Get user list by id index
     *
     * @param objId
     * @return user list
     */
    @GetMapping("/datasource/act/taskSetting/getUsersByIdIndex")
    List<User> getUsersByIdIndexForTaskSetting(@RequestParam("objId") String objId);

    /**
     * Get task permission list by category
     *
     * @param category
     * @return TaskPermission list
     */
    @GetMapping("/datasource/act/taskSetting/getTaskPermissionList")
    List<TaskPermission> getTaskPermissionList(@RequestParam("category") String category);

    /**
     * Delete task setting by ids
     *
     * @param ids
     * @return result message
     */
    @PostMapping("/datasource/act/taskSetting/delete")
    ResultJson delete(@RequestParam("ids") String ids);

    /**
     * Modify user task id
     *
     * @param oldId
     * @param newId
     * @return result message
     */
    @PostMapping("/datasource/act/taskSetting/updateUserTaskId")
    ResultJson updateUserTaskId(@RequestParam("oldId") String oldId, @RequestParam("newId") String newId);

    /**
     * Query node permission data list
     *
     * @param taskPermission
     * @param pageNo
     * @param pageSize
     * @return permission data map
     */
    @PostMapping("/datasource/act/taskSetting/permissionData")
    LinkedHashMap<String, Object> permissionData(@RequestBody TaskPermission taskPermission, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize);

    /**
     * Save task setting
     *
     * @param taskSetting
     * @return result message
     */
    @PostMapping("/datasource/act/taskSetting/save")
    ResultJson saveTaskSetting(@RequestBody TaskSetting taskSetting);

    /**
     * Get gentable list
     *
     * @return gentable list
     */
    @PostMapping("/datasource/act/taskSetting/genTableList")
    ResultJson genTableList();

    /**
     * Get gentable column list
     */
    @PostMapping("/datasource/act/taskSetting/genTableColumnList")
    ResultJson genTableColumnList(@RequestParam("name") String name);
    /*TaskSetting end*/

    /**
     * Save model
     *
     * @param modelId
     * @param json_xml
     * @param svg_xml
     * @param name
     * @param description
     * @return result message
     */
    @PostMapping("/act/model/save")
    String saveModel(@RequestParam("modelId") String modelId, @RequestParam("json_xml") String json_xml, @RequestParam("svg_xml") String svg_xml, @RequestParam("name") String name, @RequestParam("description") String description);

    /**
     * Get editor JSON
     *
     * @param modelId
     * @return the ObjectNode
     */
    @GetMapping("/act/model/json")
    ObjectNode getEditorJson(@RequestParam("modelId") String modelId);

    /**
     * Get model list
     *
     * @param category
     * @param treeId
     * @param pageNo
     * @param pageSize
     * @return model page
     */
    @GetMapping("/act/model/list")
    String findModelList(@RequestParam("category") String category, @RequestParam("treeId") String treeId, @RequestParam("pageNo") String pageNo, @RequestParam("pageSize") String pageSize);

    /**
     * Get model data
     *
     * @param modelId
     * @return model
     */
    @GetMapping("/act/model/data")
    ResultJson getModelData(@RequestParam("modelId") String modelId);

    /**
     * Create model
     *
     * @param name
     * @param key
     * @param description
     * @param category
     * @param scope
     * @param officeId
     * @return result message
     */
    @GetMapping("/act/model/create")
    ResultJson createModel(@RequestParam("name") String name, @RequestParam("key") String key,
                           @RequestParam("description") String description, @RequestParam("category") String category,
                           @RequestParam("scope") String scope, @RequestParam("officeId") String officeId);

    /**
     * Update model
     *
     * @param modelId
     * @param scope
     * @param officeId
     * @return result message
     */
    @GetMapping("/act/model/update")
    ResultJson updateModel(@RequestParam("modelId") String modelId, @RequestParam("scope") String scope, @RequestParam("officeId") String officeId);

    /**
     * Deploy model
     *
     * @param id
     * @return ResultJson
     */
    @GetMapping("/act/model/deploy")
    ResultJson deployModel(@RequestParam("id") String id);

    /**
     * Delete model
     *
     * @param id
     * @return result message
     */
    @GetMapping("/act/model/delete")
    ResultJson deleteModel(@RequestParam("id") String id);
    /*Act end*/

    /**
     * Save dict
     *
     * @param dict
     * @return result message
     */
    @GetMapping("/datasource/dict/saveDict")
    ResultJson saveDict(@RequestBody Dict dict);

    /**
     * Get gentable list
     */
    @PostMapping("/datasource/gen/findPage")
    Page<GenTable> findPage(@RequestBody GenTable genTable);

    /**
     * Get user tree async
     */
    @GetMapping("/datasource/tag/userTreeAsync")
    ResultJson getUserTagTreeAsync(@RequestParam("id") String id);

    /* Zform begin */

    /**
     * Get zform
     *
     * @param id
     * @param formNo
     * @param loginName
     * @return zform
     */
    @GetMapping("/datasource/zform/getZform")
    Zform getZform(@RequestParam("formNo") String formNo,
                   @RequestParam("id") String id,
                   @RequestParam("loginName") String loginName);

    /**
     * Get zform map
     * @param formNo
     * @param id
     * @param loginName
     * @return zform map
     */
    @GetMapping("/datasource/zform/getZformMap")
    LinkedHashMap getZformMap(@RequestParam("formNo") String formNo,
                   @RequestParam("id") String id,
                   @RequestParam("loginName") String loginName);

    /**
     * Get zform with act
     *
     * @param formNo
     * @param id
     * @param procDefKey
     * @param loginName
     * @return zform
     */
    @GetMapping("/datasource/zform/getZformWithAct")
    Zform getZformWithAct(@RequestParam("formNo") String formNo,
                          @RequestParam("id") String id,
                          @RequestParam("procDefKey") String procDefKey,
                          @RequestParam("loginName") String loginName);

    /**
     * Find zform list
     *
     * @param zform
     * @param path
     * @param loginName
     * @param traceFlag
     * @param parentId
     * @return zform page
     */
    @PostMapping("/datasource/zform/data")
    Page<Zform> findZformData(@RequestBody Zform zform,
                              @RequestParam("path") String path,
                              @RequestParam("loginName") String loginName,
                              @RequestParam("traceFlag") String traceFlag,
                              @RequestParam("parentId") String parentId);

    /**
     * Find zform map
     *
     * @param zform
     * @param path
     * @param loginName
     * @param traceFlag
     * @param parentId
     * @return zform page
     */
    @PostMapping("/datasource/zform/datamap")
    Page<Zform> findZformDataMap(@RequestBody Zform zform,
                              @RequestParam("path") String path,
                              @RequestParam("loginName") String loginName,
                              @RequestParam("traceFlag") String traceFlag,
                              @RequestParam("parentId") String parentId);

    /**
     * Export Zform data to Excel
     *
     * @param zform
     * @param path
     * @param loginName
     * @param traceFlag
     * @param parentId
     * @return zform page
     */
    @PostMapping("/datasource/zform/expdata")
    List<List<LinkedHashMap<String, String>>> expdata(@RequestBody Zform zform,
                              @RequestParam("path") String path,
                              @RequestParam("loginName") String loginName,
                              @RequestParam("traceFlag") String traceFlag,
                              @RequestParam("parentId") String parentId);

    /**
     * Import Excel data into Zform
     *
     * @param parentId
     * @param fileId
     * @param fileRoot
     * @param uniqueId
     * @return int 1:success -1:fail
     */
    @PostMapping("/datasource/zform/impdata")
    ResultJson impdata(
              @RequestParam("formNo") String formNo,
              @RequestParam("parentFormNo") String parentFormNo,
              @RequestParam("toCompany") String toCompany,
              @RequestParam("parentId") String parentId,
              @RequestParam("fileId") String fileId,
              @RequestParam("fileRoot") String fileRoot,
              @RequestParam("uniqueId") String uniqueId,
              @RequestParam("loginName") String loginName);

    /**
     * Find gridselect data
     */
    @PostMapping("/datasource/zform/gridselectData")
    ResultJson findGridselectData(@RequestBody GridselectParam gridselectParam);

    /**
     * Get zform data count
     */
    @PostMapping("/datasource/zform/dataCount")
    String findZformDataCount(@RequestBody Zform zform,
                              @RequestParam("path") String path,
                              @RequestParam("loginName") String loginName);

    /**
     * Save zform
     */
    @PostMapping("/datasource/zform/save")
    ResultJson saveZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName, @RequestParam("businessKey") String businessKey);

    /**
     * Save main table and return id
     */
    @PostMapping("/datasource/zform/beforeSave")
    ResultJson beforeSaveZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Delete zform
     */
    @PostMapping("/datasource/zform/delete")
    ResultJson deleteZform(@RequestParam("formNo") String formNo, @RequestParam("id") String id);

    /**
     * Delete zform by ids
     */
    @PostMapping("/datasource/zform/deleteAll")
    ResultJson deleteAllZform(@RequestParam("formNo") String formNo, @RequestParam("ids") String ids);

    /**
     * Get user list map
     */
    @PostMapping("/datasource/zform/getUserList")
    LinkedHashMap<String, Object> getUserListZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Get node list map
     */
    @PostMapping("/datasource/zform/getNodeList")
    LinkedHashMap<String, Object> getNodeListZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Get current user name
     */
    @GetMapping("/datasource/zform/getCurrentUser")
    String getCurrentUser(@RequestParam("loginName") String loginName);

    /**
     * Get rule variables and return zform
     */
    @PostMapping("/datasource/zform/getRuleArgs")
    Zform getRuleArgsZform(@RequestBody Zform zform, @RequestParam("procDefKey") String procDefKey, @RequestParam("loginName") String loginName);

    /**
     * Create a node
     */
    @PostMapping("/datasource/zform/createNode")
    LinkedHashMap<String, Object> createNodeZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Delete a node
     */
    @PostMapping("/datasource/zform/deleteNode")
    LinkedHashMap<String, Object> deleteNodeZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Get back tasks by ids
     */
    @PostMapping("/datasource/zform/backward")
    LinkedHashMap<String, Object> backwardZform(@RequestParam("formNo") String formNo,
                                                @RequestParam("ids") String ids,
                                                @RequestParam("loginName") String loginName);

    /**
     * Revoke tasks by ids
     */
    @PostMapping("/datasource/zform/revoke")
    LinkedHashMap<String, Object> revokeZform(@RequestParam("formNo") String formNo, @RequestParam("ids") String ids);

    /**
     * Create a notify node
     */
    @PostMapping("/datasource/zform/notifyNode")
    LinkedHashMap<String, Object> notifyNodeZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Create a distribute node
     */
    @PostMapping("/datasource/zform/distributeNode")
    LinkedHashMap<String, Object> distributeNodeZform(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Check whether a node could be fallback
     */
    @PostMapping("/datasource/zform/rollBackCheck")
    LinkedHashMap<String, Object> rollBackCheckZform(@RequestParam("procInsId") String procInsId, @RequestParam("loginName") String loginName);

    /**
     * Get process definition list
     */
    @GetMapping("/datasource/zform/getProcDefList")
    ResultJson getProcDefList(@RequestParam("formNo") String formNo, @RequestParam("currentUserName") String currentUserName);

    /**
     * Is cache view
     */
    @GetMapping("/datasource/zform/isCacheView")
    ResultJson isCacheView(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("loginName") String loginName);

    /**
     * Is cache data
     */
    @GetMapping("/datasource/zform/isCacheData")
    ResultJson isCacheData(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("loginName") String loginName);

    /**
     * Trace photo
     */
    @GetMapping("/datasource/zform/tracePhoto")
    ResultJson tracePhoto(@RequestParam("procDefId") String procDefId, @RequestParam("procInsId") String procInsId);

    /**
     * History flow
     */
    @GetMapping("/datasource/zform/histoicFlow")
    ResultJson histoicFlow(@RequestParam("procInsId") String procInsId);

    /**
     * Get task list
     */
    @PostMapping("/datasource/zform/getTaskList")
    Map<String, Object> getTaskList(@RequestBody Zform zform,
                                    @RequestParam("path") String path,
                                    @RequestParam("loginName") String loginName);

    /**
     * Get hash value by key
     */
    @GetMapping("/datasource/zform/getHashByKey")
    String getHashByKey(@RequestParam("key") String key);

    /* Zform end */

    /* SysFile begin */

    /**
     * Upload file
     */
    @PostMapping("/datasource/sysFile/uploadFileComplete")
    ResultJson uploadFileComplete(@RequestParam("requestURI") String requestURI,
                                  @RequestParam("groupId") String groupId,
                                  @RequestParam("fileName") String fileName,
                                  @RequestParam("fileSize") String fileSize,
                                  @RequestParam("chunk") String chunk,
                                  @RequestParam("cSize") String cSize,
                                  @RequestParam("secret") String secret,
                                  @RequestParam("template") String template,
                                  @RequestParam("fileRoot") String fileRoot,
                                  @RequestParam("fileUploadFolder") String fileUploadFolder,
                                  @RequestParam("uploadPathDateFormat") String uploadPathDateFormat,
                                  @RequestParam("loginName") String loginName);

    /**
     * Get file list
     */
    @PostMapping("/datasource/sysFile/getFileList")
    ResultJson getFileList(@RequestParam("groupId") String groupId);

    /**
     * Get file
     */
    @PostMapping("/datasource/sysFile/getFile")
    File getFile(@RequestParam("fileId") String fileId, @RequestParam("fileRoot") String fileRoot);

    /**
     * Get file by formNo
     */
    @PostMapping("/datasource/sysFile/getFileByFormNo")
    File getFileByFormNo(@RequestParam("formNo") String formNo, @RequestParam("fileRoot") String fileRoot);

    /**
     * Delete file
     */
    @PostMapping("/datasource/sysFile/deleteFile")
    ResultJson deleteFile(@RequestParam("fileId") String fileId, @RequestParam("fileRoot") String fileRoot);

    /**
     * Get thumb path
     */
    @GetMapping("/datasource/sysFile/getThumbPath")
    String getThumbPath(@RequestParam("fileId") String fileId, @RequestParam("groupId") String groupId);

    /**
     * Save Secret Level
     */
    @PostMapping("/datasource/sysFile/saveSecretLevel")
    ResultJson saveSecretLevel(@RequestBody SysFile sysFile);

    /* SysFile end */

    /* Org begin */

    /**
     * Get level list
     */
    @PostMapping("/datasource/level/data")
    Page<Level> findLevelData(@RequestBody Level level);

    /**
     * Get user tree
     */
    @GetMapping("/datasource/tag/userTree")
    ResultJson getUserTagTree(@RequestParam("loginName") String loginName);

    /**
     * Get office tree
     */
    @GetMapping("/datasource/tag/officeTagTree")
    ResultJson getOfficeTagTree();

    /**
     * Get office tree async
     */
    @GetMapping("/datasource/tag/officeTagTreeAsync")
    ResultJson getOfficeTagTreeAsync(@RequestParam("id") String id);

    /**
     * Get area tree async
     */
    @GetMapping("/datasource/tag/areaTagTreeAsync")
    ResultJson getAreaTagTreeAsync(@RequestParam("id") String id);

    /**
     * Get area tree
     */
    @GetMapping("/datasource/tag/areaTagTree")
    ResultJson getAreaTagTree();

    /**
     * Get office view
     */
    @PostMapping("/datasource/office/viewData")
    ResultJson findOfficeViewData(@RequestBody OfficeView officeView);

    /**
     * Get permissions
     */
    @GetMapping("/datasource/role/getAuth")
    ResultJson getAuth(@RequestParam("id") String id);

    /**
     * Save permissions
     */
    @PostMapping("/datasource/role/saveAuth")
    ResultJson saveAuth(@RequestParam("id") String id, @RequestParam("ids") String ids);

    /**
     * Get assigned roles
     */
    @GetMapping("/datasource/role/getAssign")
    ResultJson getAssign(@RequestParam("id") String id);

    /**
     * Get assigned data roles
     */
    @GetMapping("/datasource/role/getDataAssign")
    ResultJson getDataAssign(@RequestParam("id") String id);

    /**
     * Save assign roles
     */
    @PostMapping("/datasource/role/saveAssign")
    ResultJson saveAssign(@RequestParam("id") String id, @RequestParam("ids") String ids);

    /**
     * Save assign roles by param
     */
    @PostMapping("/datasource/role/saveAssignByParam")
    ResultJson saveAssignByParam(@RequestBody AssignParam assignParam);

    /**
     * Save assign data roles by param
     */
    @PostMapping("/datasource/role/saveDataAssignByParam")
    ResultJson saveDataAssignByParam(@RequestBody AssignParam assignParam);

    /**
     * Get menu tree
     */
    @GetMapping("/datasource/menu/menuTree")
    ResultJson getMenuTagTree();

    /**
     * Create menu group
     */
    @PostMapping("/datasource/menu/createMenuGroup")
    ResultJson createMenuGroup(@RequestParam("formNo") String formNo, @RequestParam("parentId") String parentId, @RequestParam("icon") String icon);

    /**
     * Refresh menu cache
     */
    @GetMapping("/datasource/menu/refreshMenuCache")
    ResultJson refreshMenuCache();

    /**
     * Save secLog
     */
    @PostMapping("/datasource/secLog/saveSecLog")
    void saveSecLog(@RequestParam("account") String account, @RequestParam("ip") String ip, @RequestParam("content") String content, @RequestParam("type") String type, @RequestParam("result") String result);

    /**
     * Save secLog by zform
     */
    @PostMapping("/datasource/secLog/saveSecLogZform")
    void saveSecLogZform(@RequestParam("account") String account, @RequestParam("ip") String ip, @RequestParam("result") String result, @RequestParam("formNo") String formNo, @RequestParam("action") String action);

    /**
     * Get secLog list
     */
    @PostMapping("/datasource/secLog/data")
    Page<Zform> findSecLogData(@RequestBody Zform zform,
                               @RequestParam("path") String path,
                               @RequestParam("loginName") String loginName,
                               @RequestParam("traceFlag") String traceFlag,
                               @RequestParam("parentId") String parentId,
                               @RequestParam("secSwitch") Boolean secSwitch);

    /**
     * Get SecLogSpace
     */
    @GetMapping("/datasource/secLog/getSecLogSpace")
    ResultJson getSecLogSpace();

    /**
     * Save user
     */
    @PostMapping("/datasource/user/saveUser")
    ResultJson saveUser(@RequestBody Zform zform, @RequestParam("loginName") String loginName);

    /**
     * Get login exception count
     */
    @GetMapping("/datasource/user/getLoginExceptionCount")
    Integer getLoginExceptionCount(@RequestParam("loginName") String loginName);

    /**
     * Clear login exception count
     */
    @PostMapping("/datasource/user/clearLoginExceptionCount")
    void clearLoginExceptionCount(@RequestParam("loginName") String loginName);

    /**
     * Is password expired
     */
    @GetMapping("/datasource/user/isPasswordExpired")
    Boolean isPasswordExpired(@RequestParam("loginName") String loginName);

    /**
     * Unlock user
     */
    @PostMapping("/datasource/user/unlockUser")
    void unlockUser(@RequestParam("loginName") String loginName);

    /**
     * Lock user
     */
    @PostMapping("/datasource/user/lockUser")
    void lockUser(@RequestParam("loginName") String loginName);

    /**
     * Add login exception count
     */
    @PostMapping("/datasource/user/addLoginExceptionCount")
    void addLoginExceptionCount(@RequestParam("loginName") String loginName);

    /**
     * Change password
     */
    @PostMapping("/datasource/user/changePassword")
    void changePassword(@RequestParam("password") String password, @RequestParam("loginName") String loginName);

    /* Org end */

    /* Gentable begin */

    /**
     * Get gentable
     */
    @PostMapping("/datasource/gen/editForm")
    ResultJson editForm(@RequestBody GenTable genTable);

    /**
     * Get realm data
     */
    @PostMapping("/datasource/gen/realmData")
    ResultJson realmData(@RequestParam("genRealm") String[] genRealm);

    /**
     * Save gentable
     */
    @PostMapping("/datasource/gen/saveGenTable")
    ResultJson saveGenTable(@RequestBody GenTable genTable);

    /**
     * Synchronize database table
     */
    @PostMapping("/datasource/gen/syncDynamic")
    ResultJson syncDynamic(@RequestBody GenTable genTable);

    /**
     * Remove gentable
     */
    @PostMapping("/datasource/gen/removeDynamic")
    ResultJson removeDynamic(@RequestBody GenTable genTable);

    /**
     * Delete gentable
     */
    @PostMapping("/datasource/gen/deleteDynamic")
    ResultJson deleteDynamic(@RequestBody GenTable genTable);

    /**
     * Copy gentable
     */
    @PostMapping("/datasource/gen/copyDynamic")
    ResultJson copyDynamic(GenTable genTable);

    /**
     * Get database table list for importing
     */
    @PostMapping("/datasource/gen/importListDynamic")
    ResultJson importListDynamic();

    /**
     * Get dict for gentable design
     */
    @GetMapping("/datasource/gen/dictDynamic")
    ResultJson dictDynamic(@RequestParam("key") String key);

    /**
     * Import gentable from database table
     */
    @PostMapping("/datasource/gen/importDynamic")
    ResultJson importDynamic(@RequestBody GenTable genTable);

    /**
     * Pre-release gentable
     */
    @PostMapping("/datasource/gen/buildViewDynamic")
    ResultJson buildViewDynamic(@RequestBody GenScheme genScheme);

    /**
     * Release gentable
     */
    @PostMapping("/datasource/gen/buildDynamic")
    ResultJson buildDynamic(@RequestBody GenScheme genScheme, @RequestParam("currentUserName") String currentUserName);

    /**
     * Get dict for view
     */
    @GetMapping("/datasource/gen/dictViewDynamic")
    ResultJson dictViewDynamic(@RequestParam("key") String key);

    /**
     * Refresh gentable cache
     */
    @GetMapping("/datasource/gen/refreshGenTableCache")
    ResultJson refreshGenTableCache();

    @PostMapping("/datasource/gen/getRealmColumnByName")
    GenTableColumnView getRealmColumnByName(@RequestParam("name") String name);

    @PostMapping("/datasource/gen/getExportFilePathByFormNo")
    String getExportFilePathByFormNo(@RequestParam("formNo") String formNo, @RequestParam("fileRoot") String fileRoot);

    @PostMapping("/datasource/gen/saveImportAndExport")
    ResultJson saveImportAndExport(@RequestBody GenTableView genTable);

    /* Gentable end */

    /* Dict begin */

    /**
     * Get dict labels by values and type
     */
    @GetMapping("/datasource/dict/getDictLabels")
    String getDictLabels(@RequestParam("values") String values, @RequestParam("type") String type, @RequestParam("defaultValue") String defaultValue, @RequestParam("lang") String lang);

    /**
     * Get dict values by labels and type
     */
    @GetMapping("/datasource/dict/getDictValues")
    String getDictValues(@RequestParam("labels") String labels, @RequestParam("type") String type, @RequestParam("defaultValue") String defaultValue, @RequestParam("lang") String lang);

    /**
     * Get dict list
     */
    @GetMapping("/datasource/dict/getDictList")
    List<DictResult> getDictList(@RequestParam("types") String types, @RequestParam("isEdit") Boolean isEdit);

    /**
     * Get dict list for app
     */
    @GetMapping("/datasource/dict/getDictListForApp")
    JSONObject getDictListForApp(@RequestParam("types") String types, @RequestParam("isEdit") Boolean isEdit);

    /**
     * Refresh dict cache
     */
    @GetMapping("/datasource/dict/refreshDictCache")
    ResultJson refreshDictCache();

    /* Dict end */

    /* Cms begin */

    /**
     * Get news index
     */
    @GetMapping("/datasource/cms/getIndex")
    ResultJson getIndex(@RequestParam("activeChannel") String activeChannel, @RequestParam("loginName") String loginName);

    /**
     * Get news list
     */
    @GetMapping("/datasource/cms/getInfoList")
    ResultJson getInfoList(@RequestParam("activeChannel") String activeChannel, @RequestParam("length") String length);

    /**
     * Get news
     */
    @GetMapping("/datasource/cms/getInfo")
    ResultJson getInfo(@RequestParam("infoId") String infoId);

    /* Cms end */

    @GetMapping("/datasource/ai/trans/getTransResult")
    String getTransResult(@RequestParam("query") String query, @RequestParam("from") String from, @RequestParam("to") String to);

    @PostMapping("/datasource/act/process/list")
    ResultJson getProcessList(@RequestParam("category") String category, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize);

    @PostMapping("/datasource/act/process/updateState")
    String updateState(@RequestParam("state") String state, @RequestParam("procDefId") String procDefId);

    @PostMapping("/datasource/act/process/delete")
    void deleteProcess(@RequestParam("deploymentId") String deploymentId);

    @PostMapping("/datasource/act/process/toModel")
    String convertToModel(@RequestParam("procDefId") String procDefId);

    @PostMapping("/datasource/act/process/deploy")
    String processDeploy(@RequestParam("category") String category, @RequestParam("filePath") String filePath);

    @PostMapping("/datasource/act/process/runningList")
    ResultJson processRunningList(@RequestBody ProcessView processView);

    @PostMapping("/datasource/act/process/historyList")
    ResultJson processHistoryList(@RequestBody ProcessView processView);

    @PostMapping("/datasource/act/process/trace/photo")
    void processHistoryPhoto(@RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("executionId") String executionId);

    @PostMapping("/datasource/act/process/deleteProcIns")
    String deleteProcIns(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("reason") String reason);

    @PostMapping("/datasource/oa/meetingCalendar/getDateByMonth")
    List<String> getDateByMonth(@RequestParam("month") String month);

    @PostMapping("/datasource/oa/meetingCalendar/getCalendarMeeting")
    List<CalendarMeetingView> getCalendarMeeting(@RequestParam("day") String day);

    @PostMapping("/datasource/oa/meetingCalendar/getCalendarMeetingByYear")
    List<CalendarMeetingView> getCalendarMeetingByYear(@RequestParam("year") String year);

    @PostMapping("/datasource/system/sysMsg/setRead")
    String setRead(@RequestParam("id") String id);

    @PostMapping("/datasource/system/sysMsg/setReadAll")
    String setReadAll(@RequestParam("currentUserName") String currentUserName);

    @GetMapping("/datasource/system/sysMsg/getUnreadCount")
    Integer getUnreadCount(@RequestParam("currentUserId") String currentUserId);

    @PostMapping("/datasource/system/sysMsg/data")
    ResultJson getSysMsgData(@RequestBody SysMsg sysMsg, @RequestParam("currentUserName") String currentUserName);

    @GetMapping("/datasource/datapermission/datapermissionTree")
    ResultJson getDataPermissionTree();

    @GetMapping("/datasource/datapermission/getPermission")
    ResultJson getDataPermission(@RequestParam("id") String id);

    @PostMapping("/datasource/datapermission/savePermission")
    ResultJson saveDataPermission(@RequestParam("id") String id, @RequestParam("ids") String ids);

    /**
     * Get gentable
     */
    @GetMapping("/datasource/gen/getGentableByFormNo")
    GenTable getGenTableByFormNo(@RequestParam("formNo") String formNo);

    /* Org begin */
    /**
     * Get assigned users of organization
     */
    @GetMapping("/datasource/org/getOrgAssign")
    ResultJson getOrgAssign(@RequestParam("id") String id);

    /**
     * Save assign users of organization by param
     */
    @PostMapping("/datasource/org/saveOrgAssignByParam")
    ResultJson saveOrgAssignByParam(@RequestBody AssignParam assignParam);
    /* Org end */

    @PostMapping("/datasource/zform/deleteProc")
    ResultJson deleteProc(Zform zform);
}
