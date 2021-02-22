package com.jeestudio.datasource.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumn;
import com.jeestudio.common.entity.system.*;
import com.jeestudio.common.param.GridselectParam;
import com.jeestudio.common.view.system.UserView;
import com.jeestudio.datasource.mapper.base.common.ZformDao;
import com.jeestudio.datasource.service.act.ActService;
import com.jeestudio.datasource.service.act.TaskPermissionService;
import com.jeestudio.datasource.service.act.TaskSettingVersionService;
import com.jeestudio.datasource.service.gen.GenTableService;
import com.jeestudio.datasource.service.system.DictDataService;
import com.jeestudio.datasource.service.system.OfficeService;
import com.jeestudio.datasource.service.system.SysFileService;
import com.jeestudio.datasource.service.system.UserService;
import com.jeestudio.datasource.utils.DictUtil;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: Base service of dynamic form
 * @author: David
 * @Date: 2020-11-26
 */
public class ZformBaseService extends ActService<ZformDao, Zform> {

    protected static final Logger logger = LoggerFactory.getLogger(ZformBaseService.class);

    @Value("${spring.datasource.dbType}")
    protected String dbType;

    @Autowired
    protected ZformDao zformDao;

    @Autowired
    protected GenTableService genTableService;

    @Autowired
    protected OfficeService officeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected TaskSettingVersionService taskSettingVersionService;

    @Autowired
    protected TaskPermissionService taskPermissionService;

    @Autowired
    protected SysFileService sysFileService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DictDataService dictDataService;

    protected static final String NODE_MARK_CREATE = "create";
    protected static final String NODE_MARK_NOTIFY = "notify";
    protected static final String UNDERLINE = "_";
    protected static final String NODE_MARK_DISTRIBUTE = "distribute";

    protected static final String COUNT_RESULT_MORETHAN9 = "9+";

    protected static final String DBNAME_ORACLE = "oracle";
    protected static final String DBNAME_MYSQL = "mysql";
    protected static final String DBNAME_MSSQL = "mssql";
    protected static final String DBNAME_DM = "dm";

    protected static final String QUERYTYPE_LIKE = "like";
    protected static final String QUERYTYPE_LEFT_LIKE = "left_like";
    protected static final String QUERYTYPE_RIGHT_LIKE = "right_like";

    protected static final String JAVATYPE_SGTRING = "String";
    protected static final String JAVATYPE_LONG = "Long";
    protected static final String JAVATYPE_INTEGER = "Integer";
    protected static final String JAVATYPE_DOUBLE = "Double";
    protected static final String JAVATYPE_DATE = "java.util.Date";
    protected static final String JAVATYPE_USER = "com.jeestudio.common.entity.system.User";
    protected static final String JAVATYPE_OFFICE = "com.jeestudio.common.entity.system.Office";
    protected static final String JAVATYPE_AREA = "com.jeestudio.common.entity.system.Area";
    protected static final String JAVATYPE_ZFORM = "com.jeestudio.common.entity.common.Zform";
    protected static final String JAVATYPE_THIS = "This";

    protected static final String PATH_QUERY = "path";

    /**
     * Rule variables
     */
    protected enum RuleArgs {
        //format
        key, value,
        //scope
        form, content, hand, automatic, extend, operation, formExtend,
        //disabled
        flag, reject;
    }

    /**
     * Get zform and children
     */
    public Zform get(String id, GenTable genTable) throws Exception {
        Zform zform = new Zform();
        zform.setId(id);
        zform.setFormNo(genTable.getName());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        String procInsId = zform.getProcInsId();
        zform = zformDao.get(zform);
        if (zform == null) {
            //Business data has been deleted, clear process data
            zform = new Zform();
            if (false == StringUtil.isEmpty(procInsId)) {
                zform.setProcInsId(procInsId);
                this.deleteAct(zform);
                zform.setProcInsId(null);
            }
        }
        zform.setFormNo(genTable.getName());
        return this.buildGridselectValue(zform, genTable);
    }

    /**
     * Get zform and children
     */
    public LinkedHashMap getMap(String id, GenTable genTable) throws Exception {
        Zform zform = new Zform();
        zform.setId(id);
        zform.setFormNo(genTable.getName());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS_FRIENDLY, genTable.getSqlColumnsFriendly());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        return zformDao.getMap(zform);
    }

    /**
     * Find zform list
     */
    public List<Zform> findList(Zform zform, GenTable genTable) {
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        return super.findList(zform);
    }

    /**
     * Find zform list by page
     */
    public Page<Zform> findPage(Page<Zform> page, Zform zform, GenTable genTable) {
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        return super.findPage(page, zform);
    }

    /**
     * Find zform map by page
     */
    public Page<Zform> findPageMap(Page<Zform> page, Zform zform, GenTable genTable) {
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS_FRIENDLY, genTable.getSqlColumnsFriendly());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        return super.findPageMap(page, zform);
    }

    /**
     * Save zform
     *
     * @param zform
     * @param genTable
     */
    public void save(Zform zform, GenTable genTable) throws Exception {
        zform.getSqlMap().put(GenTable.SQLMAP_SQLINSERT, genTable.getSqlInsert());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLUPDATE, genTable.getSqlUpdate());
        if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
            //Tree table, rebuild parent_ids
            String rootParentIds = Global.DEFAULT_ROOT_CODE + ",";
            Zform parent = this.get(zform.getParent().getId(), genTable);
            if (parent != null && StringUtil.isNotEmpty(parent.getId())) {
                rootParentIds = parent.getParentIds();
            } else {
                parent = new Zform();
                parent.setId(Global.DEFAULT_ROOT_CODE);
                parent.setParentIds(rootParentIds);
                zform.setParent(parent);
            }
            this.buildParentIdsForChildren(rootParentIds, zform, genTable);
        } else {
            this.superSave(zform, genTable);
        }
    }

    /**
     * Save tree
     *
     * @param zform
     * @param genTable
     */
    @Transactional(readOnly = false)
    public void saveTree(Zform zform, GenTable genTable) {
        this.superSave(zform, genTable);
    }

    /**
     * Save a new zform
     *
     * @param zform
     */
    @Transactional(readOnly = false)
    public void beforeSave(Zform zform, GenTable genTable) {
        zform.getSqlMap().put(GenTable.SQLMAP_SQLINSERT, genTable.getSqlInsert());
        this.superSave(zform, genTable);
    }

    /**
     * Delete a zform and children
     *
     * @param zform
     */
    @Transactional(readOnly = false)
    public void delete(Zform zform, GenTable genTable) throws Exception {
        super.delete(zform);
        if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
            zformDao.deleteChildrenForTree(zform);
        } else {
            List<GenTable> subGenTableList = genTableService.findList(new GenTable(genTable));
            for (int i = 0; i < subGenTableList.size(); i++) {
                GenTable subGenTable = genTableService.getGenTableWithDefination(subGenTableList.get(i).getName());
                Zform subZform = new Zform(zform);
                subZform.setFormNo(subGenTable.getName());
                subZform.setFk(subGenTable.getParentTableFk());
                List<Zform> subList = zformDao.findChildrenForDelete(subZform);
                for (Zform obj : subList) {
                    obj.setFormNo(subGenTable.getName());
                    this.deleteCascade(obj, subGenTable);
                }
            }
        }
        super.deleteAct(zform);
    }

    public void deleteCascade(Zform zform, GenTable genTable) {
        super.delete(zform);
        if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
            zformDao.deleteChildrenForTree(zform);
        } else {
            List<GenTable> subGenTableList = genTableService.findList(new GenTable(genTable));
            for (int i = 0; i < subGenTableList.size(); i++) {
                GenTable subGenTable = genTableService.getGenTableWithDefination(subGenTableList.get(i).getName());
                Zform subZform = new Zform(zform);
                subZform.setFormNo(subGenTable.getName());
                subZform.setFk(subGenTable.getParentTableFk());
                List<Zform> subList = zformDao.findChildrenForDelete(subZform);
                for (Zform obj : subList) {
                    this.deleteCascade(zform, subGenTable);
                }
            }
        }
    }

    /**
     * Delete zform by ids
     *
     * @param ids
     * @param formNo
     * @param genTable
     */
    public void deleteAll(String ids, String formNo, GenTable genTable) throws Exception {
        String idArray[] = ids.split(",");
        for (String id : idArray) {
            Zform zform = this.get(id, genTable);
            this.delete(zform, genTable);
        }
    }

    private boolean isFileSecLevelValid(Zform zform, GenTable genTable) throws NoSuchFieldException, IllegalAccessException {
        boolean result = true;
        if (StringUtil.isNotEmpty(zform.getSecLevel())) {
            int secLevel = Integer.parseInt(zform.getSecLevel());
            List<String> fileColumnList = Lists.newArrayList();
            String fileColumns = ",";
            for(GenTableColumn column : genTable.getColumnList()) {
                if ("fileuploadsec".equals(column.getShowType())) {
                    Field field = zform.getClass().getDeclaredField(column.getJavaField());
                    if (field != null) {
                        field.setAccessible(true);
                        Object object = field.get(zform);
                        String groupId = "";
                        if (object != null) {
                            groupId = (String) object;
                        }
                        if (StringUtil.isNotEmpty(groupId)) {
                            List<SysFile> fileList = sysFileService.getFileListByGroupId(groupId);
                            for(SysFile sysFile : fileList) {
                                if (secLevel < Integer.parseInt(sysFile.getSecretLevel())) {
                                    result = false;
                                    break;
                                }
                            }
                            if (false == result) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Save zform with act
     *
     * @param businessKey
     * @param zform
     * @param loginName
     */
    public void saveAct(String businessKey, Zform zform, String loginName, GenTable genTable) throws Exception {
        zform.setEdocType(genTable.getProcessDefinitionCategory());
        if (StringUtil.isEmpty(genTable.getProcessDefinitionCategory())) {
            //Non workflow form, call save
            this.save(zform, genTable);
        } else {
            zform.getSqlMap().put(GenTable.SQLMAP_SQLINSERT, genTable.getSqlInsert());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLUPDATE, genTable.getSqlUpdate());

            Map<String, Object> vars = Maps.newHashMap();
            String procDefKey = null;
            if (false == StringUtil.isEmpty(zform.getProcInsId())) {
                procDefKey = getProcIns(zform.getProcInsId()).getProcessDefinitionKey();
            } else {
                procDefKey = zform.getAct().getProcDefKey();
            }
            super.saveAct(zform, genTable.getComments(), procDefKey, businessKey, vars, loginName);
            if (StringUtil.isNotBlank(zform.getProcInsId())) {
                Task activeTask = this.getActiveTask(zform.getProcInsId());
                if (activeTask != null) {
                    zform.getAct().setProcDefId(this.getProcIns(zform.getProcInsId()).getProcessDefinitionId());
                    zform.getAct().setTaskDefKey(activeTask.getTaskDefinitionKey());
                    this.setRuleArgs(zform, loginName);
                    if (zform.getRuleArgs().get("form") != null) {
                        String taskStatusValue = zform.getRuleArgs().get("formExtend").get("taskStatus");
                        if (StringUtil.isNotBlank(taskStatusValue)) {
                            //To save the status of process node settings, you need to add "taskstatus" in the "form" rule variable in the process node.
                            // When you reach this node, the status value of business data
                            zform.setStatus(taskStatusValue);
                            this.save(zform);
                        }
                    }
                } else {
                    Map<String, String> paramMap = new Gson().fromJson(Encodes.unescapeHtml(zform.getAct().getParam()), Map.class);
                    String type = paramMap.get("type");
                    if (type.equalsIgnoreCase("SAVEANDTERMINATE")) {
                        //Terminate 00
                        zform.setStatus(genTable.getStatusBreak());
                    } else {
                        //There are no active tasks in the current process, which is equivalent to the end of all tasks.
                        // 9 or 99 should be changed to the corresponding status of "finish"
                        zform.setStatus(genTable.getStatusEnd());
                    }
                    this.save(zform);
                }
            } else {
                //No process has been entered. Save the status corresponding to "staging".
                // Here 0 or 10 should be changed to the status corresponding to "staging"
                zform.setStatus(genTable.getStatusStart());
                this.save(zform);
            }
        }
    }

    /**
     * Get user list when the process starting
     *
     * @param zform
     * @param loginName
     * @return user list when the process starting
     */
    public LinkedHashMap<String, Object> getStartingUserList(Zform zform, String loginName) {
        String procDefKey = null;
        if (false == StringUtil.isEmpty(zform.getProcInsId())) {
            procDefKey = getProcIns(zform.getProcInsId()).getProcessDefinitionKey();
        } else {
            procDefKey = zform.getAct().getProcDefKey();
        }
        zform.getAct().setProcDefKey(procDefKey);
        return super.getStartingUserList(zform, loginName);
    }

    /**
     * Get target user list when submitting a process
     *
     * @param zform
     * @param loginName
     * @return target user list when submitting a process
     */
    public LinkedHashMap<String, Object> getTargetUserList(Zform zform, String loginName) {
        String procDefKey = null;
        if (false == StringUtil.isEmpty(zform.getProcInsId())) {
            procDefKey = getProcIns(zform.getProcInsId()).getProcessDefinitionKey();
        } else {
            procDefKey = zform.getAct().getProcDefKey();
        }
        if (StringUtil.isEmpty(zform.getId()) || StringUtil.isEmpty(zform.getProcInsId())) {
            zform.getAct().setProcDefKey(procDefKey);
            return super.getStartingUserList(zform, loginName);
        } else {
            zform.getAct().setProcDefKey(procDefKey);
            return super.getTargetUserList(zform, loginName);
        }
    }

    /**
     * Read the corresponding list through to do, being done, done, to be sent and sent by page
     *
     * @param page
     * @param zform
     * @param path
     * @param loginName
     * @param genTable
     * @param traceFlag while the table tpye is tree table, not empty for trace all children
     * @return zform list page
     */
    public Page<Zform> findPage(Page<Zform> page, Zform zform, String path, String loginName, GenTable genTable, String traceFlag, String parentId) throws Exception {
        if (StringUtil.isEmpty(path)) path = PATH_QUERY;
        String processDefinitionCategory = genTable.getProcessDefinitionCategory();
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        if (StringUtil.isEmpty(page.getOrderBy()) && StringUtil.isNotEmpty(genTable.getSqlSort())) {
            page.setOrderBy(genTable.getSqlSort());
        } else if (StringUtil.isEmpty(page.getOrderBy()) && genTable.getSqlColumns().indexOf("a.sort AS \"sort\"") > -1) {
            page.setOrderBy(" a.sort asc ");
        }
        User currentUser = UserUtil.getByLoginName(loginName);
        //this.setSqlMap(zform, genTable, currentUser);
        if (StringUtil.isNotEmpty(parentId)) {
            String querySql = " AND a.parent_id = '" + parentId + "' ";
            if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                String dsf = zform.getSqlMap().get("dsf");
                dsf = dsf == null ? "" : dsf;
                dsf += querySql;
                zform.getSqlMap().put("dsf", dsf);
            } else {
                zform.getSqlMap().put("dsf", querySql);
            }
        }

        if (StringUtil.isEmpty(processDefinitionCategory) || PATH_QUERY.equals(path)) {
            //return findPage(page, zform);
            String querySql;
            if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
                page.setPageSize(Integer.MAX_VALUE);
                //" AND a.parent_id = '" + ((zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId())) ? zform.getParent().getId() : "0") + "' ";
                if (StringUtil.isNotEmpty(traceFlag)) {
                    Zform theZform = this.get(zform.getId(), genTable);
                    String parentIds = "0,";
                    if (theZform != null && StringUtil.isNotEmpty(theZform.getId()) && StringUtil.isNotEmpty(theZform.getParentIds())) {
                        parentIds = theZform.getParentIds();
                    }
                    querySql = " AND (a.parent_id = '" + ((zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId())) ? zform.getParent().getId() : "0") + "' OR a.parent_ids LIKE '" + parentIds + "%') ";
                } else {
                    querySql = " AND a.parent_id = '" + ((zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId())) ? zform.getParent().getId() : "0") + "' ";
                }
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += querySql;
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", querySql);
                }
                Page<Zform> pageResult = buildGridselectValues(findPage(page, zform), genTable);
                return pageResult;
            } else if (genTable.getTableType().equals(GenTable.TABLE_TYPE_RIGHTTABLE)) {
                if (zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId()) && false == "0".equals(zform.getParent().getId())) {
                    querySql = " AND (b.id = '" + zform.getParent().getId() + "' OR b.parent_ids LIKE '%," + zform.getParent().getId() + ",%' ) ";
                    if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                        String dsf = zform.getSqlMap().get("dsf");
                        dsf = dsf == null ? "" : dsf;
                        dsf += querySql;
                        zform.getSqlMap().put("dsf", dsf);
                    } else {
                        zform.getSqlMap().put("dsf", querySql);
                    }
                }
                return buildGridselectValues(findPage(page, zform), genTable);
            } else {
                return buildGridselectValues(findPage(page, zform), genTable);
            }
        } else {
            if (zform != null && zform.getCreateTimeEnd() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(zform.getCreateTimeEnd());
                calendar.add(Calendar.HOUR, 24);
                calendar.add(Calendar.MILLISECOND, -1);
                zform.setCreateTimeEnd(calendar.getTime());
            }
            //Unsent
            if (super.isUnsent(path)) {
                zform.setPage(page);
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')";
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')");
                }
                page.setList(dao.findList(zform));
            }
            //To do, doing, done, sent
            else {
                List<String> processInstanceIdList = super.getProcessInstanceIdList(processDefinitionCategory, path, loginName);
                if (processInstanceIdList.size() > 0) {
                    zform.setPage(page);
                    zform.setProcInsIdList(processInstanceIdList);
                    page.setList(dao.findListByProc(zform));
                } else {
                    zform.setPage(page);
                    List<Zform> list = Lists.newArrayList();
                    page.setList(list);
                }
            }
            return buildGridselectValues(page, genTable);
        }
    }

    public Page<Zform> findPageMap(Page<Zform> page, Zform zform, String path, String loginName, GenTable genTable, String traceFlag, String parentId) throws Exception {
        if (StringUtil.isEmpty(path)) path = PATH_QUERY;
        String processDefinitionCategory = genTable.getProcessDefinitionCategory();
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS_FRIENDLY, genTable.getSqlColumnsFriendly());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        if (StringUtil.isEmpty(page.getOrderBy()) && StringUtil.isNotEmpty(genTable.getSqlSort())) {
            page.setOrderBy(genTable.getSqlSort());
        } else if (StringUtil.isEmpty(page.getOrderBy()) && genTable.getSqlColumns().indexOf("a.sort AS \"sort\"") > -1) {
            page.setOrderBy(" a.sort asc ");
        }
        User currentUser = UserUtil.getByLoginName(loginName);
        //this.setSqlMap(zform, genTable, currentUser);
        if (StringUtil.isNotEmpty(parentId)) {
            String querySql = " AND a.parent_id = '" + parentId + "' ";
            if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                String dsf = zform.getSqlMap().get("dsf");
                dsf = dsf == null ? "" : dsf;
                dsf += querySql;
                zform.getSqlMap().put("dsf", dsf);
            } else {
                zform.getSqlMap().put("dsf", querySql);
            }
        }

        if (StringUtil.isEmpty(processDefinitionCategory) || PATH_QUERY.equals(path)) {
            //return findPage(page, zform);
            String querySql;
            if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
                page.setPageSize(Integer.MAX_VALUE);
                //" AND a.parent_id = '" + ((zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId())) ? zform.getParent().getId() : "0") + "' ";
                if (StringUtil.isNotEmpty(traceFlag)) {
                    Zform theZform = this.get(zform.getId(), genTable);
                    String parentIds = "0,";
                    if (theZform != null && StringUtil.isNotEmpty(theZform.getId()) && StringUtil.isNotEmpty(theZform.getParentIds())) {
                        parentIds = theZform.getParentIds();
                    }
                    querySql = " AND (a.parent_id = '" + ((zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId())) ? zform.getParent().getId() : "0") + "' OR a.parent_ids LIKE '" + parentIds + "%') ";
                } else {
                    querySql = " AND a.parent_id = '" + ((zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId())) ? zform.getParent().getId() : "0") + "' ";
                }
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += querySql;
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", querySql);
                }
                Page<Zform> pageResult = findPageMap(page, zform);
                return pageResult;
            } else if (genTable.getTableType().equals(GenTable.TABLE_TYPE_RIGHTTABLE)) {
                if (zform.getParent() != null && StringUtil.isNotEmpty(zform.getParent().getId()) && false == "0".equals(zform.getParent().getId())) {
                    querySql = " AND (b.id = '" + zform.getParent().getId() + "' OR b.parent_ids LIKE '%," + zform.getParent().getId() + ",%' ) ";
                    if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                        String dsf = zform.getSqlMap().get("dsf");
                        dsf = dsf == null ? "" : dsf;
                        dsf += querySql;
                        zform.getSqlMap().put("dsf", dsf);
                    } else {
                        zform.getSqlMap().put("dsf", querySql);
                    }
                }
                return findPageMap(page, zform);
            } else {
                return findPageMap(page, zform);
            }
        } else {
            if (zform != null && zform.getCreateTimeEnd() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(zform.getCreateTimeEnd());
                calendar.add(Calendar.HOUR, 24);
                calendar.add(Calendar.MILLISECOND, -1);
                zform.setCreateTimeEnd(calendar.getTime());
            }
            //Unsent
            if (super.isUnsent(path)) {
                zform.setPage(page);
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')";
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')");
                }
                page.setMap(dao.findListMap(zform));
            }
            //To do, doing, done, sent
            else {
                List<String> processInstanceIdList = super.getProcessInstanceIdList(processDefinitionCategory, path, loginName);
                if (processInstanceIdList.size() > 0) {
                    zform.setPage(page);
                    zform.setProcInsIdList(processInstanceIdList);
                    page.setMap(dao.findListByProcMap(zform));
                } else {
                    zform.setPage(page);
                    List<LinkedHashMap> map = Lists.newArrayList();
                    page.setMap(map);
                }
            }
            return page;
        }
    }

    /**
     * Read the corresponding list through to do, being done, done, to be sent and sent
     *
     * @param zform
     * @param path
     * @param processDefinitionCategory
     * @param loginName
     * @return zform list
     */
    public List<Zform> findDataList(Zform zform, String path, String processDefinitionCategory, String loginName, GenTable genTable) {
        if (StringUtil.isEmpty(path)) path = PATH_QUERY;
        if (StringUtil.isEmpty(processDefinitionCategory) || PATH_QUERY.equals(path)) {
            return findList(zform);
        } else {
            User currentUser = UserUtil.getByLoginName(loginName);
            List<Zform> list = new ArrayList<Zform>();
            zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());

            if (zform != null && zform.getCreateTimeEnd() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(zform.getCreateTimeEnd());
                calendar.add(Calendar.HOUR, 24);
                calendar.add(Calendar.MILLISECOND, -1);
                zform.setCreateTimeEnd(calendar.getTime());
            }
            //Unsent
            if (super.isUnsent(path)) {
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')";
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')");
                }
                list = dao.findList(zform);
            }
            //To do, doing, done, sent
            else {
                List<String> processInstanceIdList = super.getProcessInstanceIdList(processDefinitionCategory, path, loginName);
                if (processInstanceIdList.size() > 0) {
                    zform.setProcInsIdList(processInstanceIdList);
                    list = dao.findListByProc(zform);
                } else {
                    List<Zform> zformList = Lists.newArrayList();
                    list = zformList;
                }
            }
            return list;
        }
    }

    /**
     * Find count
     *
     * @param page
     * @param zform
     * @return count
     */
    public String findCount(Page<Zform> page, Zform zform, GenTable genTable) {
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        page = super.findPage(page, zform);
        if (page.getCount() > 9) {
            return COUNT_RESULT_MORETHAN9; //9+
        } else {
            return String.valueOf(page.getCount());
        }
    }

    /**
     * Find count by params
     *
     * @param page
     * @param zform
     * @param path
     * @param loginName
     * @return count
     */
    public String findCount(Page<Zform> page, Zform zform, String path, String loginName, GenTable genTable) {
        if (StringUtil.isEmpty(path)) path = PATH_QUERY;
        String processDefinitionCategory = genTable.getProcessDefinitionCategory();
        if (StringUtil.isEmpty(processDefinitionCategory) || PATH_QUERY.equals(path)) {
            return findCount(page, zform, genTable);
        } else {
            User currentUser = UserUtil.getByLoginName(loginName);
            zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
            zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());

            if (zform != null && zform.getCreateTimeEnd() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(zform.getCreateTimeEnd());
                calendar.add(Calendar.HOUR, 24);
                calendar.add(Calendar.MILLISECOND, -1);
                zform.setCreateTimeEnd(calendar.getTime());
            }
            //Unsent
            if (super.isUnsent(path)) {
                zform.setPage(page);
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')";
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", " AND a.create_by = '" + currentUser.getId() + "' AND (a.proc_ins_id IS NULL OR a.proc_ins_id = '')");
                }
                page.setList(dao.findListCount(zform));
            }
            //To do, doing, done, sent
            else {
                List<String> processInstanceIdList = super.getProcessInstanceIdList(processDefinitionCategory, path, loginName);
                if (processInstanceIdList.size() > 0) {
                    zform.setPage(page);
                    zform.setProcInsIdList(processInstanceIdList);
                    page.setList(dao.findListByProcCount(zform));
                } else {
                    zform.setPage(page);
                    List<Zform> list = Lists.newArrayList();
                    page.setList(list);
                }
            }
            if (page.getCount() > 9) {
                return COUNT_RESULT_MORETHAN9; //9+
            } else {
                return String.valueOf(page.getCount());
            }
        }
    }

    /**
     * Set workflow information, called when the form form is opened
     *
     * @param zform
     */
    public void setAct(Zform zform, String loginName) {
        super.setAct(zform, loginName);
    }

    /**
     * Set sql map
     *
     * @param zform
     * @param genTable
     */
    public void setSqlMap(Zform zform, GenTable genTable, User currentUser) {
        try {
            String userSecLevel = currentUser.getSecLevel();
            StringBuffer stringBuffer = new StringBuffer();
            List<GenTableColumn> columnList = genTable.getColumnList();
            for (GenTableColumn genTableColumn : columnList) {
                if (StringUtil.isNotBlank(genTableColumn.getIsQuery()) && Global.YES.equals(genTableColumn.getIsQuery())) {
                    String javaField = genTableColumn.getSimpleJavaField();//getJavaField();
                    String javaFieldId = genTableColumn.getJavaFieldId();
                    if (javaFieldId.startsWith("g0") || javaFieldId.startsWith("user0")) {
                        javaFieldId = javaFieldId.substring(0, javaFieldId.lastIndexOf("."));
                    }
                    Field field = Reflections.getThisField(zform.getClass(), javaFieldId);
                    if (field != null) {
                        field.setAccessible(true);
                        Object object = field.get(zform);
                        String queryType = genTableColumn.getQueryType();
                        if (object != null || "between".equalsIgnoreCase(queryType)) {
                            StringBuffer subSql = new StringBuffer();
                            //The first section of SQL, splicing the SQL on the left side of the value
                            String name = genTableColumn.getName();
                            //Control whether to splice single quotation marks. Use this value to judge when the second SQL segment
                            boolean addSingleMark = true;
                            if ("=".equals(queryType) || StringUtil.isEmpty(queryType)) {
                                subSql.append(" AND a." + name + " = ");
                            } else if ("!=".equals(queryType)) {
                                subSql.append(" AND a." + name + " != ");
                            } else if ("&gt;".equals(queryType)) {
                                subSql.append(" AND a." + name + " > ");
                            } else if ("&gt;=".equals(queryType)) {
                                subSql.append(" AND a." + name + " >= ");
                            } else if ("&lt;".equals(queryType)) {
                                subSql.append(" AND a." + name + " < ");
                            } else if ("&lt;=".equals(queryType)) {
                                subSql.append(" AND a." + name + " <= ");
                            } else if ("between".equals(queryType) || "Between".equals(queryType)) {
                                //In the generated list date query criteria, the name format of the date range field is:
                                // Java attribute name + begin or Java attribute name + end
                                javaField = javaField.substring(0, 1).toUpperCase() + javaField.substring(1);
                                //String beginStr = request.getParameter("begin" + javaField);
                                //String endStr = request.getParameter("end" + javaField);


                                //If it's between, you can judge it's date type
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(genTableColumn.getDateType());

                                String beginStr = "";
                                String endStr = "";
                                Field fieldBegin = zform.getClass().getDeclaredField("begin" + javaField);
                                Field fieldEnd = zform.getClass().getDeclaredField("end" + javaField);
                                fieldBegin.setAccessible(true);
                                fieldEnd.setAccessible(true);
                                if (fieldBegin != null && fieldEnd != null && fieldBegin.get(zform) != null && fieldEnd.get(zform) != null) {
                                    beginStr = simpleDateFormat.format((Date) fieldBegin.get(zform));
                                    endStr = simpleDateFormat.format((Date) fieldEnd.get(zform));
                                }

                                if (StringUtil.isNotBlank(beginStr) && StringUtil.isNotBlank(endStr)) {
                                    Date beginDate = simpleDateFormat.parse(beginStr);
                                    Date endDate = simpleDateFormat.parse(endStr);
                                    if (beginDate != null && endDate != null) {
                                        String dataType = genTableColumn.getDateType();
                                        if (dataType.length() == 4) {
                                            //year
                                            dataType += "-MM-dd";
                                            beginStr += "-01-01";
                                            endStr += "-12-31";
                                        } else if (dataType.length() == 7) {
                                            //month
                                            dataType += "-dd";
                                            beginStr += "-01";
                                            endStr += "-01";
                                            endStr = DateUtil.getMonthEnd(endStr, "yyyy-MM-dd");
                                        }
                                        if (DBNAME_ORACLE.equals(dbType)) {
                                            dataType = dataType.replaceAll("mm", "mi");
                                            dataType = dataType.replaceAll("HH", "hh24");
                                            dataType = dataType.replaceAll("MM", "mm");
                                            subSql.append(" AND a." + name + " BETWEEN to_date('" + beginStr + "','" + dataType + "') AND to_date('" + endStr + "','" + dataType + "') ");
                                            stringBuffer.append(subSql.toString());
                                        } else {
                                            subSql.append(" AND a." + name + " BETWEEN '" + beginStr + "' AND '" + endStr + "' ");
                                            stringBuffer.append(subSql.toString());
                                        }
                                    }
                                }
                                continue;
                            } else if (QUERYTYPE_LIKE.equals(queryType)) {
                                subSql.append(" AND a." + name + " like '%");
                                addSingleMark = false;
                            } else if (QUERYTYPE_LEFT_LIKE.equals(queryType)) {
                                subSql.append(" AND a." + name + " like '%");
                                addSingleMark = false;
                            } else if (QUERYTYPE_RIGHT_LIKE.equals(queryType)) {
                                subSql.append(" AND a." + name + " like '");
                                addSingleMark = false;
                            }

                            //SQL second segment, splicing value
                            String javaType = genTableColumn.getJavaType();
                            if (JAVATYPE_SGTRING.equals(javaType)) {
                                String value = (String) object;
                                if (StringUtil.isNotBlank(value)) {
                                    if (addSingleMark) {
                                        subSql.append(" '" + value + "' ");
                                    } else {
                                        subSql.append(value);
                                    }
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_LONG.equals(javaType)) {
                                Long value = (Long) object;
                                if (value != null) {
                                    //Long does not need single quotes
                                    subSql.append(value);
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_INTEGER.equals(javaType)) {
                                Integer value = (Integer) object;
                                if (value != null) {
                                    //Integer does not need single quotes
                                    subSql.append(value);
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_DOUBLE.equals(javaType)) {
                                Double value = (Double) object;
                                if (value != null) {
                                    //Double does not need single quotes
                                    subSql.append(value);
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_DATE.equals(javaType)) {
                                Date value = (Date) object;
                                if (value != null) {
                                    if (addSingleMark) {
                                        if (DBNAME_ORACLE.equals(dbType)) {
                                            String oracleDataType = genTableColumn.getDateType();
                                            oracleDataType = oracleDataType.replaceAll("mm", "mi");
                                            oracleDataType = oracleDataType.replaceAll("HH", "hh24");
                                            oracleDataType = oracleDataType.replaceAll("MM", "mm");
                                            subSql.append("  to_date('" + value + "','" + oracleDataType + "') ");
                                            stringBuffer.append(subSql.toString());
                                        } else {
                                            subSql.append(" '" + value + "' ");
                                        }
                                    } else {
                                        subSql.append(value);
                                    }
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_USER.equals(javaType)) {
                                User value = (User) object;
                                if (value != null && StringUtil.isNotBlank(value.getId())) {
                                    if (addSingleMark) {
                                        subSql.append(" '" + value.getId() + "' ");
                                    } else {
                                        subSql.append(value.getId());
                                    }
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_OFFICE.equals(javaType)) {
                                Office value = (Office) object;
                                if (value != null && StringUtil.isNotBlank(value.getId())) {
                                    if (addSingleMark) {
                                        subSql.append(" '" + value.getId() + "' ");
                                    } else {
                                        subSql.append(value.getId());
                                    }
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_AREA.equals(javaType)) {
                                Area value = (Area) object;
                                if (value != null && StringUtil.isNotBlank(value.getId())) {
                                    if (addSingleMark) {
                                        subSql.append(" '" + value.getId() + "' ");
                                    } else {
                                        subSql.append(value.getId());
                                    }
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_ZFORM.equals(javaType)) {
                                Zform value = (Zform) object;
                                if (value != null && StringUtil.isNotBlank(value.getId())) {
                                    if (addSingleMark) {
                                        subSql.append(" '" + value.getId() + "' ");
                                    } else {
                                        subSql.append(value.getId());
                                    }
                                } else {
                                    continue;
                                }
                            } else if (JAVATYPE_THIS.equals(javaType)) {
                                Zform value = (Zform) object;
                                if (value != null && StringUtil.isNotBlank(value.getId())) {
                                    if (addSingleMark) {
                                        subSql.append(" '" + value.getId() + "' ");
                                    } else {
                                        subSql.append(value.getId());
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                //Object of custom input
                                Class<?> clazz = Class.forName(javaType);
                                if (clazz != null) {
                                    Object obj = clazz.cast(object);
                                    if (obj != null) {
                                        //Field f = obj.getClass().getDeclaredField("id");
                                        Field f = Reflections.getThisField(obj.getClass(), "id");
                                        if (f != null) {
                                            f.setAccessible(true);
                                            String id = (String) f.get(obj);
                                            if (id != null) {
                                                if (addSingleMark) {
                                                    subSql.append(" '" + id + "' ");
                                                } else {
                                                    subSql.append(id);
                                                }
                                            } else {
                                                continue;
                                            }
                                        }
                                    }
                                }
                            }

                            //The third section of SQL, splicing the SQL on the right side of the value
                            if (QUERYTYPE_LIKE.equals(queryType)) {
                                subSql.append("%' ");
                            } else if (QUERYTYPE_LEFT_LIKE.equals(queryType)) {
                                subSql.append("' ");
                            } else if (QUERYTYPE_RIGHT_LIKE.equals(queryType)) {
                                subSql.append("%' ");
                            }
                            stringBuffer.append(subSql.toString());
                        }
                    }
                }
            }

            if (Global.YES.equals(genTable.getIsBuildSecret())) {
                stringBuffer.append(" AND a.sec_level <= '" + userSecLevel + "' ");
            }

            if (false == currentUser.getLoginName().equals("admin") && genTable.getName().equals("sys_role")) {
                stringBuffer.append(" AND a.is_sys != '1' ");
            }

            if (false == genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE) && false == genTable.getTableType().equals(GenTable.TABLE_TYPE_RIGHTTABLE)) {
                if (zform.getParent() != null) {
                    if (StringUtil.isNotEmpty(genTable.getParentTableFk())) {
                        stringBuffer.append(" AND a." + genTable.getParentTableFk() + " = '" + zform.getParent().getId() + "' ");
                    } else {
                        stringBuffer.append(" AND 1 <> 1 ");
                    }
                }
            }

            //Data Permission
            //{company}
            String ownerCodeCompany = currentUser.getCompany().getCode();
            //{office}
            String ownerCodeOffice = currentUser.getOffice().getCode();
            for(Datapermission datapermission : currentUser.getDatapermissionList()) {
                if (datapermission.getMainTable().equalsIgnoreCase(genTable.getName())) {
                    String expression = datapermission.getExpression();
                    expression = expression.replaceAll("\\{company\\}", ownerCodeCompany);
                    expression = expression.replaceAll("\\{office\\}", ownerCodeOffice);
                    stringBuffer.append(" ");
                    stringBuffer.append(expression);
                    stringBuffer.append(" ");
                }
            }

            String querySql = stringBuffer.toString();
            if (StringUtil.isNotBlank(querySql)) {
                if (zform.getSqlMap() != null && zform.getSqlMap().size() > 0) {
                    String dsf = zform.getSqlMap().get("dsf");
                    dsf = dsf == null ? "" : dsf;
                    dsf += querySql;
                    zform.getSqlMap().put("dsf", dsf);
                } else {
                    zform.getSqlMap().put("dsf", querySql);
                }
            }
        } catch (Exception e) {
            logger.info("Set sql map for zform:" + zform.getFormNo());
            logger.error("Error while set sql map for zform:" + ExceptionUtils.getStackTrace(e));
        }
    }

    public void buildParentIdsForChildren(String rootParentIds, Zform zform, GenTable gentable) {
        if (zform.getParent() == null || StringUtil.isEmpty(zform.getParent().getId()) || "0".equals(zform.getParent().getId())) {
            zform.setParentIds(rootParentIds);
        } else {
            zform.setParentIds(rootParentIds + zform.getParent().getId() + ",");
        }
        this.saveTree(zform, gentable);
        if (zform.isHasChildren()) {
            Zform param = new Zform();
            param.setParent(new Zform(zform.getId(), gentable.getName()));
            List<Zform> childrenList = zformDao.findList(param);
            for (Zform theZform : childrenList) {
                this.buildParentIdsForChildren(zform.getParentIds(), theZform, gentable);
            }
        }
    }

    public void superSave(Zform zform, GenTable genTable) {
        if (genTable.getName().equalsIgnoreCase("sys_user")) {
            String officeId = zform.getParent().getId();
            String companyId = officeService.getCompanyIdByOfficeId(officeId);
            zform.setOffice01(new Office(companyId));
            zform.setOffice02(new Office(officeId));
        }
        super.save(zform);
    }

    public ResultJson gridselectData(GridselectParam gridselectParam) {
        ResultJson resultJson = new ResultJson();
        Zform zform = new Zform();
        zform.setFormNo(gridselectParam.getTableName());
        GenTable genTable = genTableService.getGenTableWithDefination(gridselectParam.getTableName());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLCOLUMNS, genTable.getSqlColumns());
        zform.getSqlMap().put(GenTable.SQLMAP_SQLJOINS, genTable.getSqlJoins());
        zform.getSqlMap().put("dsf", " AND a." + gridselectParam.getSearchKey() + " LIKE '%" + gridselectParam.getSearchValue() + "%' ");
        String dsfPlus = gridselectParam.getDsfPlus();
        if (StringUtil.isNotBlank(dsfPlus)) {
            String dsf = zform.getSqlMap().get("dsf");
            if (StringUtil.isNotBlank(dsf)) {
                dsf += dsfPlus;
            } else {
                dsf = dsfPlus;
            }
            zform.getSqlMap().put("dsf", dsf);
        }
        Page<Zform> page = new Page<Zform>(gridselectParam.getPageParam().getPageNo(), gridselectParam.getPageParam().getPageSize(), gridselectParam.getPageParam().getOrderBy());
        zform.setPage(page);
        Page<Zform> result = super.findPage(page, zform);
        resultJson.setCode(ResultJson.CODE_SUCCESS);
        resultJson.setRows(result.getList());
        resultJson.setTotal(result.getCount());
        return resultJson;
    }

    private Page<Zform> buildGridselectValues(Page<Zform> page, GenTable genTable) {
        for (Zform zform : page.getList()) {
            zform = this.buildGridselectValue(zform, genTable);
        }
        return page;
    }

    private Zform buildGridselectValue(Zform zform, GenTable genTable) {
        if (zform.getG01() != null
                && StringUtil.isNotEmpty(zform.getG01().getId())) {
            String name = this.getGridselectNamesByIds(zform.getG01().getId(), "g01", genTable);
            zform.getG01().setName(name);
        }
        if (zform.getG02() != null
                && StringUtil.isNotEmpty(zform.getG02().getId())) {
            String name = this.getGridselectNamesByIds(zform.getG02().getId(), "g02", genTable);
            zform.getG02().setName(name);
        }
        if (zform.getG03() != null
                && StringUtil.isNotEmpty(zform.getG03().getId())) {
            String name = this.getGridselectNamesByIds(zform.getG03().getId(), "g03", genTable);
            zform.getG03().setName(name);
        }
        if (zform.getG04() != null
                && StringUtil.isNotEmpty(zform.getG04().getId())) {
            String name = this.getGridselectNamesByIds(zform.getG04().getId(), "g04", genTable);
            zform.getG04().setName(name);
        }
        if (zform.getG05() != null
                && StringUtil.isNotEmpty(zform.getG05().getId())) {
            String name = this.getGridselectNamesByIds(zform.getG05().getId(), "g05", genTable);
            zform.getG05().setName(name);
        }
        return zform;
    }

    private String getGridselectNamesByIds(String ids, String gridName, GenTable genTable) {
        String names = "";
        for (GenTableColumn column : genTable.getColumnList()) {
            if (column.getShowType().equalsIgnoreCase("gridselect")
                    && column.getJavaField().startsWith(gridName)) {
                String[] idsArray = ids.split(",");
                for (int i = 0; i < idsArray.length; i++) {
                    String name = zformDao.getNameById(column.getTableName(), column.getSearchKey(), idsArray[i]);
                    if (StringUtil.isEmpty(name)) name = "";
                    names += "," + name;
                }
                break;
            }
        }
        return names.substring(1);
    }

    public Map<String, Object> getTaskList(List<String> categoryList, String path, String loginName, int pageNo, int pageSize, Map<String, String> paramMap) {
        List<String> loginNameList = null;
        if (paramMap == null) paramMap = Maps.newHashMap();
        if (paramMap.get(ProcessMap.PROC_CREATE_USER.name()) != null
                && StringUtil.isNotBlank(paramMap.get(ProcessMap.PROC_CREATE_USER.name()))) {
            loginNameList = this.getLoginNameList(paramMap.get(ProcessMap.PROC_CREATE_USER.name()));
        }
        return super.getTaskList(categoryList, path, loginName, loginNameList, pageNo, pageSize, paramMap);
    }

    private List<String> getLoginNameList(String name) {
        List<String> loginNameList = Lists.newArrayList();
        List<UserView> userViewList = userService.findUserViewByName(name);
        for (UserView userView : userViewList) {
            loginNameList.add(userView.getLoginName());
        }
        return loginNameList;
    }

    private String getFirstProcDefKey(String formNo, String currentUserName) {
        String procDefKey = "";
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        List<ProcessDefinition> procDefList = this.getProcessDefinitionList(genTable.getProcessDefinitionCategory(), currentUserName);
        List<Map<String, String>> list = Lists.newArrayList();
        for (ProcessDefinition processDefinition : procDefList) {
            procDefKey = processDefinition.getKey();
            break;
        }
        return procDefKey;
    }

    protected Field getThisField(Class clazz, String javaField) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(javaField);
        } catch (NoSuchFieldException e) {
            clazz = clazz.getSuperclass();
            try {
                field = clazz.getDeclaredField(javaField);
            } catch (NoSuchFieldException ee) {
                clazz = clazz.getSuperclass();
                try {
                    field = clazz.getDeclaredField(javaField);
                } catch (NoSuchFieldException eee) {
                    clazz = clazz.getSuperclass();
                    try {
                        field = clazz.getDeclaredField(javaField);
                    } catch (NoSuchFieldException eeee) {
                    }
                }
            }
        }
        return field;
    }

    /**
     * Import data into table(s)
     * @param formNo
     * @param parentFormNo
     * @param parentId
     * @param uniqueId
     * @param dataList
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ParseException
     */
    public void importData(String ownerCode,
                           String columns,
                           String formNo,
                           String parentFormNo,
                           String parentId,
                           String uniqueId,
                           List<LinkedHashMap<String, String>> dataList,
                           User currentUser) throws Exception {
        columns = "!" + columns + "!";
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        int i = 0;
        for (LinkedHashMap row : dataList) {
            if (i++ == 0) continue;
            Zform zform = new Zform();
            zform.setDelFlag(Global.NO);
            zform.setOwnerCode(ownerCode);
            zform.setFormNo(formNo);
            zform.setCreateBy(currentUser);
            zform.setUpdateBy(currentUser);
            if (Global.YES.equals(genTable.getIsProcessDefinition())) zform.setProcDefKey(this.getFirstProcDefKey(formNo, currentUser.getLoginName()));
            if (StringUtil.isNotEmpty(parentId)) zform.setParent(new Zform(parentId, parentFormNo));
            for (GenTableColumn genTableColumn : genTable.getColumnList()) {
                String javaField = genTableColumn.getJavaField();
                if (columns.indexOf("!" + javaField + "!") == -1) continue;
                if (StringUtil.contains(javaField, "|")){
                    javaField = javaField.substring(0, javaField.indexOf("."));
                }
                Field field = this.getThisField(zform.getClass(), javaField); //zform.getClass().getDeclaredField(javaField);
                //String methname = javaField.substring(0,1).toUpperCase() + javaField.substring(1);
                //Method m = zform.getClass().getMethod("set" + methname, java.lang.String);
                if (field != null) {
                    field.setAccessible(true);
                    if (javaField.startsWith("sort")) {
                        //sort
                        String value = (String) row.get(genTableColumn.getJavaField());
                        if (StringUtil.isNotEmpty(value)) {
                            value = value.replaceFirst(".0", "");
                            field.set(zform, Integer.parseInt(value));
                        }
                    } else if(javaField.startsWith("s") || javaField.startsWith("m") || javaField.startsWith("c") || javaField.equals("remarks")){
                        String value = (String) row.get(genTableColumn.getJavaField());
                        value = dictDataService.getDictValues(value, genTableColumn.getDictType(), value, "");
                        field.set(zform, value);
                    } else if (javaField.startsWith("d")) {
                        //Date
                        Calendar calendar = new GregorianCalendar(1900, 0, -1);
                        Date d = calendar.getTime();
                        Date value = null;
                        Object dateObj = row.get(genTableColumn.getJavaField());
                        if (dateObj != null) {
                            String dateStr = StringUtil.isBlank(dateObj.toString()) ? "" : dateObj.toString().trim();
                            if (StringUtil.isNotEmpty(dateStr)) {
                                value = DateUtil.addDays(d, Integer.valueOf(dateStr));
                            }
                        }
                        field.set(zform, value);
                    } else if (javaField.startsWith("users")) {
                        //users
                        //Zform value = new Zform();
                        //value.setId((String) row.get(genTableColumn.getJavaField()));
                        //field.set(zform, value);
                    } else if (javaField.startsWith("user")) {
                        //user
                        String name = (String) row.get(genTableColumn.getJavaField());
                        String id = this.getFirstValueByKey("sys_user", "id", "name", name);
                        User obj = new User();
                        if (StringUtil.isNotEmpty(id)){
                            obj.setId(id);
                        }
                        field.set(zform, obj);
                    } else if (javaField.startsWith("office")) {
                        //office
                        String name = (String) row.get(genTableColumn.getJavaField());
                        String id = this.getFirstValueByKey("sys_office", "id", "name", name);
                        Office obj = new Office();
                        if (StringUtil.isNotEmpty(id)){
                            obj.setId(id);
                        }
                        field.set(zform, obj);
                    } else if (javaField.startsWith("area")) {
                        //area
                        String name = (String) row.get(genTableColumn.getJavaField());
                        String id = this.getFirstValueByKey("sys_area", "id", "name", name);
                        Area obj = new Area();
                        if (StringUtil.isNotEmpty(id)){
                            obj.setId(id);
                        }
                        field.set(zform, obj);
                    } else if (javaField.startsWith("g")) {
                        //gridselect
                        Zform value = new Zform();
                        String name = (String) row.get(genTableColumn.getJavaField());
                        String id = this.getFirstValueByKey(genTableColumn.getTableName(), "id", genTableColumn.getSearchKey(), name);
                        value.setId(id);
                        field.set(zform, value);
                    } else if (javaField.startsWith("t")) {
                        //tree
                        Zform value = new Zform();
                        value.setId((String) row.get(genTableColumn.getJavaField()));
                        field.set(zform, value);
                    } else {
                        String value = (String) row.get(genTableColumn.getJavaField());
                        field.set(zform, value);
                    }
                }
            }
            this.save(zform, genTable);
        }
    }

    /**
     * Export data
     * @param page
     * @param zform
     * @param loginName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public List<List<LinkedHashMap<String, String>>> exportData(Page<Zform> page,
                                                                Zform zform,
                                                                GenTable genTable,
                                                                String loginName) throws Exception {
        String exportList = "," + genTable.getExportList() + ",";
        String path = "path";
        String traceFlag = "1";
        String parentId = "";
        Page<Zform> data = this.findPage(page, zform, path, loginName, genTable, traceFlag, parentId);
        List<List<LinkedHashMap<String, String>>> list = Lists.newArrayList();
        for(Zform obj : data.getList()) {
            List<LinkedHashMap<String, String>> row = Lists.newArrayList();
            for (GenTableColumn genTableColumn : genTable.getColumnList()) {
                String javaField = genTableColumn.getJavaField();
                if (exportList.indexOf("," + javaField + ",") == -1) continue;
                if (StringUtil.contains(javaField, "|")){
                    javaField = javaField.substring(0, javaField.indexOf("."));
                }
                Field field = this.getThisField(zform.getClass(), javaField);
                if (field != null) {
                    field.setAccessible(true);
                    Object object = field.get(obj);
                    //if (exportList.indexOf("," + javaField + ",") != -1) {
                    LinkedHashMap<String, String> value = Maps.newLinkedHashMap();
                    if(javaField.startsWith("s") || javaField.startsWith("m") || javaField.startsWith("c")){
                        //判断其是否为字典值
                        String sValue = "";
                        if (object != null) {
                            sValue = (String) object;
                            sValue = dictDataService.getDictLabels(sValue, genTableColumn.getDictType(), sValue, "");
                        }
                        value.put(genTableColumn.getJavaField(), sValue);
                    } else if (javaField.startsWith("d")) {
                        //Date
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(genTableColumn.getDateType());
                        value.put(genTableColumn.getJavaField(), object == null ? "" : simpleDateFormat.format((Date) object));
                    } else if (javaField.startsWith("sort")) {
                        //sort
                        value.put(genTableColumn.getJavaField(), (String) object);
                    } else if (javaField.startsWith("users")) {
                        //users
                        value.put(genTableColumn.getJavaField(), "");
                    } else if (javaField.startsWith("user")) {
                        //user
                        ObjectMapper objectMapper = new ObjectMapper();
                        User user = objectMapper.convertValue(object, User.class);
                        String userName = "";
                        if (user != null){
                            userName = user.getName();
                        }
                        value.put(genTableColumn.getJavaField(), userName);
                    } else if (javaField.startsWith("office")) {
                        //office
                        ObjectMapper objectMapper = new ObjectMapper();
                        Office office = objectMapper.convertValue(object, Office.class);
                        String officeName = "";
                        if (office != null){
                            officeName = office.getName();
                        }
                        value.put(genTableColumn.getJavaField(), officeName);
                    } else if (javaField.startsWith("area")) {
                        //area
                        ObjectMapper objectMapper = new ObjectMapper();
                        Area area = objectMapper.convertValue(object, Area.class);
                        String areaName = "";
                        if (area != null){
                            areaName = area.getName();
                        }
                        value.put(genTableColumn.getJavaField(), areaName);
                    } else if (javaField.startsWith("g")) {
                        //gridselect
                        ObjectMapper objectMapper = new ObjectMapper();
                        Zform grid = objectMapper.convertValue(object, Zform.class);
                        String sValue = "";
                        if (StringUtil.isNotEmpty(grid.getId())) {
                            sValue = grid.getName();
                        }
                        value.put(genTableColumn.getJavaField(), sValue);
                    } else if (javaField.startsWith("t")) {
                        //tree
                        value.put(genTableColumn.getJavaField(), (String) object);
                    } else {
                        value.put(genTableColumn.getJavaField(), object == null ? "" : (String) object);
                    }
                    row.add(value);
                    //}
                }
            }
            list.add(this.sortExportRow(row, genTable.getExportList()));
        }
        return list;
    }

    private List<LinkedHashMap<String, String>> sortExportRow(List<LinkedHashMap<String, String>> row, String columnList) {
        List<LinkedHashMap<String, String>> result = Lists.newArrayList();
        String[] columnListArray = columnList.split(",");
        for(int i=0; i<columnListArray.length; i++) {
            for(LinkedHashMap<String, String> linkedHM : row)
            {
                for (Map.Entry<String, String> entry : linkedHM.entrySet()) {
                    if (columnListArray[i].equals(entry.getKey())) {
                        LinkedHashMap<String, String> obj = Maps.newLinkedHashMap();
                        obj.put(entry.getKey(), entry.getValue());
                        result.add(obj);
                        break;
                    }
                }
            }
        }
        return result;
    }

    protected String getFirstValueByKey(String formNo, String valueColumn, String keyColumn, String key) {
        List<String> valueList = zformDao.findValueList(formNo, valueColumn, keyColumn, key);
        String value = "";
        if (valueList.size() > 0) {
            value = valueList.get(0);
        }
        return value;
    }
}
