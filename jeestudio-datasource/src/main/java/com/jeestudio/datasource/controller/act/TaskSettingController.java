package com.jeestudio.datasource.controller.act;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumn;
import com.jeestudio.common.entity.system.*;
import com.jeestudio.common.view.system.OfficeView;
import com.jeestudio.common.entity.act.TaskPermission;
import com.jeestudio.common.entity.act.TaskSetting;
import com.jeestudio.datasource.service.act.TaskPermissionService;
import com.jeestudio.datasource.service.act.TaskSettingService;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.service.system.*;
import com.jeestudio.datasource.utils.OfficeUtil;
import com.jeestudio.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: TaskSetting Controller
 * @author: David
 * @Date: 2020-01-14
 */
@Api(value = "TaskSettingController ",tags = "TaskSetting")
@RestController
@RequestMapping("${datasourcePath}/act/taskSetting")
public class TaskSettingController {

    protected static final Logger logger = LoggerFactory.getLogger(TaskSettingController.class);

    @Autowired
    private TaskSettingService taskSettingService;

    @Autowired
    private TaskPermissionService taskPermissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private PostService postService;

    @Autowired
    private LevelService levelService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private GenTableService genTableService;

    @Autowired
    private DictDataService dictDataService;

    /**
     * Get node permission settings
     *
     * @param procDefKey
     * @param userTaskId
     * @param userTaskName
     * @return permission settings map
     */
    @ApiOperation(value = "/getTaskSetting", tags = "Get node permission settings")
    @ApiImplicitParams({@ApiImplicitParam(name = "procDefKey", value = "procDefKey", required = true, dataType = "String")})
    @GetMapping("/getTaskSetting")
    public LinkedHashMap<String, Object> getTaskSetting(@RequestParam("procDefKey") String procDefKey
            , @RequestParam("userTaskId") String userTaskId
            , @RequestParam("userTaskName") String userTaskName) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        TaskSetting taskSettingParam = new TaskSetting();
        taskSettingParam.setUserTaskName(userTaskName);
        taskSettingParam.setProcDefKey(procDefKey);
        taskSettingParam.setUserTaskId(userTaskId);
        TaskSetting taskSetting = taskSettingService.getByProcAndTask(taskSettingParam);
        if (taskSetting != null) {
            taskSettingParam = taskSetting;
        }
        List<Office> officeList = officeService.findList(new Office());
        List<OfficeView> officeViewList = Lists.newArrayList();
        OfficeUtil.OfficeViewCopy(officeList, officeViewList);
        List<Post> postList = postService.findList(new Post());
        List<Level> levelList = levelService.findList(new Level());
        List<Role> roleList = roleService.findList(new Role());
        List<Organization> orgList = organizationService.findList(new Organization());
        resultMap.put("taskSetting", taskSettingParam);
        resultMap.put("officeList", officeViewList);
        resultMap.put("postList", postList);
        resultMap.put("levelList", levelList);
        resultMap.put("roleList", roleList);
        resultMap.put("orgList", orgList);
        return resultMap;
    }

    /**
     * Get user list by id index
     *
     * @param objId
     * @return user list
     */
    @ApiOperation(value = "/getUsersByIdIndex", tags = "Get user list by id index")
    @ApiImplicitParams({@ApiImplicitParam(name = "objId", value = "objId", required = true, dataType = "String")})
    @GetMapping("/getUsersByIdIndex")
    public List<User> getUsersByIdIndex(@RequestParam("objId") String objId) {
        List<User> list = Lists.newArrayList();
        if (objId.split(":")[0].equalsIgnoreCase("role")) {
            String condition = " AND b." + objId.split(":")[0] + "_id='" + objId.split(":")[1] + "' ";
            list = userService.findUserForFlow(condition);
        } else if (objId.split(":")[0].equalsIgnoreCase("post")
                || objId.split(":")[0].equalsIgnoreCase("level")
                || objId.split(":")[0].equalsIgnoreCase("office")) {
            String condition = " AND a." + objId.split(":")[0] + "_id='" + objId.split(":")[1] + "' ";
            list = userService.findUserForFlow(condition);
        } else if (objId.split(":")[0].equalsIgnoreCase("org")) {
            list = organizationService.findUserToOrg(objId.split(":")[1]);
        }
        Map<String, User> map = Maps.newHashMap();
        for (User user : list) {
            map.put(user.getId(), user);
        }
        list.clear();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            if (entry.getValue() != null && StringUtil.isNotBlank(entry.getValue().getIsSys()) && Global.YES.equals(entry.getValue().getIsSys())) {
                //Filter isSys
            } else if (entry.getValue() != null && entry.getValue().isSystem()) {
                //Filter three admin
            } else {
                //Others
                list.add(entry.getValue());
            }
        }
        return list;
    }

    /**
     * Get task permission list by category
     *
     * @param category
     * @return task permission list
     */
    @ApiOperation(value = "/getTaskPermissionList", tags = "Get task permission list by category")
    @ApiImplicitParams({@ApiImplicitParam(name = "category", value = "category", required = true, dataType = "String")})
    @GetMapping("/getTaskPermissionList")
    public List<TaskPermission> getTaskPermissionList(@RequestParam("category") String category) {
        TaskPermission taskPermission = new TaskPermission();
        taskPermission.getSqlMap().put("dsf", " AND a.category = '" + category + "'");
        return taskPermissionService.findList(taskPermission);
    }

    /**
     * Delete task setting by ids
     *
     * @param ids
     * @return result message
     */
    @ApiOperation(value = "/delete", tags = "Delete task setting by ids")
    @ApiImplicitParams({@ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "String")})
    @PostMapping("/delete")
    public ResultJson delete(@RequestParam("ids") String ids) {
        ResultJson resultJson = new ResultJson();
        try {
            List<TaskSetting> list = Lists.newArrayList();
            for (String id : ids.split(",")) {
                TaskSetting taskSetting = new TaskSetting(id);
                list.add(taskSetting);
            }
            taskSettingService.deleteAll(list);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("删除成功");
            resultJson.setMsg_en("Delete success");
        } catch (Exception e) {
            logger.error("Error while deleting task setting:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("删除失败");
            resultJson.setMsg_en("Delete failed");
        }
        return resultJson;
    }

    /**
     * Modify user task id
     *
     * @param oldId
     * @param newId
     * @return result message
     */
    @ApiOperation(value = "/updateUserTaskId", tags = "Modify user task id")
    @ApiImplicitParams({@ApiImplicitParam(name = "oldId", value = "oldId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newId", value = "newId", required = true, dataType = "String")})
    @PostMapping("/updateUserTaskId")
    public ResultJson updateUserTaskId(@RequestParam("oldId") String oldId, @RequestParam("newId") String newId) {
        ResultJson resultJson = new ResultJson();
        try {
            taskSettingService.updateUserTaskId(oldId, newId);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("修改用户任务ID成功");
            resultJson.setMsg_en("Modify user task id success");
        } catch (Exception e) {
            logger.error("Error while modifying user task id:" + ExceptionUtils.getStackTrace(e));
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("修改用户任务ID失败");
            resultJson.setMsg_en("Modify user task id failed");
        }
        return resultJson;
    }

    /**
     * Query node permission data list
     *
     * @param taskPermission
     * @return permission data map
     */
    @ApiOperation(value = "/permissionData", tags = "Query node permission data list")
    @PostMapping("/permissionData")
    public LinkedHashMap<String, Object> permissionData(@RequestBody TaskPermission taskPermission, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        taskPermission.getSqlMap().put("dsf", "AND a.types = '1'");
        String cate = taskPermission.getCategory();
        if ("".equals(cate) || cate == null) {
            taskPermission.setCategory("请选择权限类型");
        }
        if (pageNo != null && pageSize != null) {
            taskPermission.getPageParam().setPageNo(pageNo);
            taskPermission.getPageParam().setPageSize(pageSize);
        }
        Page<TaskPermission> page = taskPermissionService.findPage(
                new Page<TaskPermission>(taskPermission.getPageParam().getPageNo(),
                        taskPermission.getPageParam().getPageSize(),
                        taskPermission.getPageParam().getOrderBy()),
                taskPermission);
        List<TaskPermission> list = page.getList();
        for (int i = 0; i < list.size(); i++) {
            TaskPermission tp = list.get(i);
            String category = dictDataService.getDictLabels(tp.getCategory(), "oa_task_permission_category", "", "");
            tp.setCategoryLabel(category);
        }
        map.put("rows", page.getList());
        map.put("total", page.getCount());
        return map;
    }

    /**
     * Save task setting
     *
     * @param taskSetting
     * @return result message
     */
    @ApiOperation(value = "/save", tags = "Save task setting")
    @PostMapping("/save")
    public ResultJson save(@RequestBody TaskSetting taskSetting) throws Exception {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.setCode(0);
            resultJson.setMsg("保存节点权限成功");
            resultJson.setMsg_en("Save task setting success");
            if (false == taskSetting.getIsNewRecord()) {
                TaskSetting t = taskSettingService.get(taskSetting.getId());
                BeanUtil.copyBeanNotNull2Bean(taskSetting, t);
                taskSettingService.save(t);
                resultJson.put("taskSetting", t);
            } else {
                taskSettingService.save(taskSetting);
                resultJson.setInsertedId(taskSetting.getId());
                resultJson.put("taskSetting", taskSetting);
            }
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("保存节点权限失败");
            resultJson.setMsg_en("Save task setting failed");
            logger.error("Error while saving task setting:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }

    /**
     * Get gentable list
     *
     * @return gentable list
     */
    @ApiOperation(value = "/genTableList", tags = "Get gentable list")
    @PostMapping("/genTableList")
    public ResultJson genTableList() {
        ResultJson resultJson = new ResultJson();
        try {
            resultJson.put("genTableList", genTableService.findAll());
            resultJson.setCode(0);
            resultJson.setMsg("获取表单列表成功");
            resultJson.setMsg_en("Get gentable list success");
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("获取表单列表失败");
            resultJson.setMsg_en("Get gentable list failed");
            logger.error("Error while getting gentable list:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }

    /**
     * Get gentable column list
     *
     * @return gentable column list
     */
    @ApiOperation(value = "/genTableColumnList", tags = "Get gentable column list")
    @PostMapping("/genTableColumnList")
    public ResultJson genTableColumnList(@RequestParam("name") String name) throws Exception {
        ResultJson resultJson = new ResultJson();
        try {
            GenTable genTableName = genTableService.getByName(name);

            GenTableColumn column = new GenTableColumn();
            column.setGenTable(genTableName);
            column.setIsForm(Global.YES);
            List<GenTableColumn> list = genTableService.findByColum(column);
            resultJson.put("rows", list);
            resultJson.setRows(list);
            resultJson.setCode(0);
            resultJson.setMsg("获取表单字段列表成功");
            resultJson.setMsg_en("Get gentable column list success");
        } catch (Exception e) {
            resultJson.setCode(-1);
            resultJson.setMsg("获取表单字段列表失败");
            resultJson.setMsg_en("Get gentable column list failed");
            logger.error("Error getting gentable column list:" + ExceptionUtils.getStackTrace(e));
        }
        return resultJson;
    }
}
