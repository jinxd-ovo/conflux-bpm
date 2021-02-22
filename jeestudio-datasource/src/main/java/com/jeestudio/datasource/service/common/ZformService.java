package com.jeestudio.datasource.service.common;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeestudio.common.entity.act.Act;
import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenTable;
import com.jeestudio.common.entity.gen.GenTableColumn;
import com.jeestudio.common.entity.system.*;
import com.jeestudio.common.param.GridselectParam;
import com.jeestudio.datasource.utils.UserUtil;
import com.jeestudio.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description: Service of dynamic form
 * @author: David
 * @Date: 2020-01-20
 */
@Service
public class ZformService extends ZformBaseService {

    protected static final Logger logger = LoggerFactory.getLogger(ZformService.class);

    @Override
    public Zform get(String id, GenTable genTable) throws Exception {
        Zform zform = super.get(id, genTable);
        this.processBlockChainGet(zform, genTable);
        this.afterGet(zform, genTable);
        return zform;
    }

    @Override
    public LinkedHashMap getMap(String id, GenTable genTable) throws Exception {
        return super.getMap(id, genTable);
    }

    @Override
    public List<Zform> findList(Zform zform, GenTable genTable) {
        return super.findList(zform, genTable);
    }

    @Override
    public Page<Zform> findPage(Page<Zform> page, Zform zform, GenTable genTable) {
        return super.findPage(page, zform, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Zform zform, GenTable genTable) throws Exception {
        this.extendBeforeSave(zform, genTable);
        this.processBlockChainSet(zform, genTable);
        super.save(zform, genTable);
        this.extendAfterSave(zform, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTree(Zform zform, GenTable genTable) {
        super.saveTree(zform, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void beforeSave(Zform zform, GenTable genTable) {
        super.beforeSave(zform, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Zform zform, GenTable genTable) throws Exception {
        this.processBlockChainDel(zform, genTable);
        super.delete(zform, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCascade(Zform zform, GenTable genTable) {
        super.deleteCascade(zform, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAll(String ids, String formNo, GenTable genTable) throws Exception {
        super.deleteAll(ids, formNo, genTable);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAct(String businessKey, Zform zform, String loginName, GenTable genTable) throws Exception {
        this.processBlockChainSet(zform, genTable);
        super.saveAct(businessKey, zform, loginName, genTable);
        this.extendAfterSaveAct(zform, genTable);
    }

    @Override
    public LinkedHashMap<String, Object> getStartingUserList(Zform zform, String loginName) {
        return super.getStartingUserList(zform, loginName);
    }

    @Override
    public LinkedHashMap<String, Object> getTargetUserList(Zform zform, String loginName) {
        return super.getTargetUserList(zform, loginName);
    }

    @Override
    public Page<Zform> findPage(Page<Zform> page, Zform zform, String path, String loginName, GenTable genTable, String traceFlag, String parentId) throws Exception {
        User currentUser = UserUtil.getByLoginName(loginName);
        this.setSqlMap(zform, genTable, currentUser);
        Page<Zform> thePage = super.findPage(page, zform, path, loginName, genTable, traceFlag, parentId);
        this.processBlockChainGet(thePage, genTable);
        return thePage;
    }

    @Override
    public Page<Zform> findPageMap(Page<Zform> page, Zform zform, String path, String loginName, GenTable genTable, String traceFlag, String parentId) throws Exception {
        User currentUser = UserUtil.getByLoginName(loginName);
        this.setSqlMap(zform, genTable, currentUser);
        return super.findPageMap(page, zform, path, loginName, genTable, traceFlag, parentId);
    }

    @Override
    public Page<Zform> findPageMap(Page<Zform> page, Zform zform, GenTable genTable) {
        return super.findPageMap(page, zform, genTable);
    }

    @Override
    public List<Zform> findDataList(Zform zform, String path, String processDefinitionCategory, String loginName, GenTable genTable) {
        return super.findDataList(zform, path, processDefinitionCategory, loginName, genTable);
    }

    @Override
    public String findCount(Page<Zform> page, Zform zform, GenTable genTable) {
        return super.findCount(page, zform, genTable);
    }

    @Override
    public String findCount(Page<Zform> page, Zform zform, String path, String loginName, GenTable genTable) {
        return super.findCount(page, zform, path, loginName, genTable);
    }

    @Override
    public void setAct(Zform zform, String loginName) {
        super.setAct(zform, loginName);
    }

    @Override
    public void setSqlMap(Zform zform, GenTable genTable, User currentUser) {
        super.setSqlMap(zform, genTable, currentUser);
    }

    @Override
    @Transactional(readOnly = false)
    public void buildParentIdsForChildren(String rootParentIds, Zform zform, GenTable gentable) {
        super.buildParentIdsForChildren(rootParentIds, zform, gentable);
    }

    @Override
    @Transactional(readOnly = false)
    public void superSave(Zform zform, GenTable genTable) {
        super.superSave(zform, genTable);
    }

    @Override
    public ResultJson gridselectData(GridselectParam gridselectParam) {
        return super.gridselectData(gridselectParam);
    }

    @Override
    public Map<String, Object> getTaskList(List<String> categoryList, String path, String loginName, int pageNo, int pageSize, Map<String, String> paramMap) {
        return super.getTaskList(categoryList, path, loginName, pageNo, pageSize, paramMap);
    }

    @Override
    @Transactional(readOnly = false)
    public void importData(String ownerCode, String columns, String formNo, String parentFormNo, String parentId, String uniqueId, List<LinkedHashMap<String, String>> dataList, User currentUser) throws Exception {
        if ("con_hash".equals(formNo)) {
            this.importConData(ownerCode, columns, formNo, parentFormNo, parentId, uniqueId, dataList, currentUser);
        } else {
            super.importData(ownerCode, columns, formNo, parentFormNo, parentId, uniqueId, dataList, currentUser);
        }
    }

    @Override
    public List<List<LinkedHashMap<String, String>>> exportData(Page<Zform> page, Zform zform, GenTable genTable, String loginName) throws Exception {
        if ("con_hash".equals(genTable.getName())) {
            return this.exportConData(page, zform, genTable, loginName);
        } else {
            return super.exportData(page, zform, genTable, loginName);
        }
    }

    private LinkedHashMap<String, String> getMapValue(Zform obj, GenTable genTable) throws Exception {
        String idValue = this.getIdValue(obj, genTable);
        String hashKey = this.getHashKey(idValue);
        String getFunctionName = "get";
        String conAddress = this.getConAddress(genTable.getBlockChainParam1());
        String hashValue = BlockChainUtil.getValue(conAddress, getFunctionName, hashKey);
        LinkedHashMap<String, String> mapValue = Maps.newLinkedHashMap();
        if (StringUtil.isNotEmpty(hashValue)) {
            mapValue = JsonConvertUtil.gsonBuilder().fromJson(hashValue, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
        }
        return mapValue;
    }

    private LinkedHashMap<String, String> getMapValue(Zform obj, GenTable genTable, String idValue) throws Exception {
        String hashKey = this.getHashKey(idValue);
        String getFunctionName = "get";
        String conAddress = this.getConAddress(genTable.getBlockChainParam1());
        String hashValue = BlockChainUtil.getValue(conAddress, getFunctionName, hashKey);
        LinkedHashMap<String, String> mapValue = Maps.newLinkedHashMap();
        if (StringUtil.isNotEmpty(hashValue)) {
            mapValue = JsonConvertUtil.gsonBuilder().fromJson(hashValue, new TypeToken<LinkedHashMap<String, String>>(){}.getType());
        }
        return mapValue;
    }

    private String getIdValue(Zform obj, GenTable genTable) throws IllegalAccessException {
        Field fieldPk = Reflections.getThisField(obj.getClass(), "id");
        String idValue = null;
        if (fieldPk != null) {
            fieldPk.setAccessible(true);
            idValue = (String) fieldPk.get(obj);
        }
        return idValue;
    }

    private void processBlockChainGet(Page<Zform> thePage, GenTable genTable) throws Exception {
        this.initCfx();
        if (StringUtil.isNotEmpty(genTable.getBlockChainParam1())) {
            String blockChainColumns = this.getGenTableBlockChainColumns(genTable);
            if (StringUtil.isNotEmpty(blockChainColumns)) {
                for(Zform obj : thePage.getList()) {
                    String idValue = this.getIdValue(obj, genTable);
                    String hashKey = this.getHashKey(idValue);
                    obj.setHashKey(hashKey);
                    LinkedHashMap<String, String> mapValue = this.getMapValue(obj, genTable, idValue);
                    for (GenTableColumn genTableColumn : genTable.getColumnList()) {
                        String javaField = genTableColumn.getJavaField();
                        if (blockChainColumns.indexOf("," + javaField + ",") == -1) continue;
                        Field field = Reflections.getThisField(obj.getClass(), javaField);
                        if (field != null) {
                            field.setAccessible(true);
                            String value = mapValue.get(javaField);
                            if(Global.YES.equals(genTableColumn.getBlockChainParam2())) {
                                value = Aes.aesDecrypt(value);
                            }
                            if (StringUtil.isNotEmpty(value)) {
                                field.set(obj, value);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processBlockChainGet(Zform obj, GenTable genTable) throws Exception {
        if (obj.getId() != null) {
            this.initCfx();
            if (StringUtil.isNotEmpty(genTable.getBlockChainParam1())) {
                String idValue = this.getIdValue(obj, genTable);
                String hashKey = this.getHashKey(idValue);
                obj.setHashKey(hashKey);
                String blockChainColumns = this.getGenTableBlockChainColumns(genTable);
                if (StringUtil.isNotEmpty(blockChainColumns)) {
                    LinkedHashMap<String, String> mapValue = this.getMapValue(obj, genTable, idValue);
                    for (GenTableColumn genTableColumn : genTable.getColumnList()) {
                        String javaField = genTableColumn.getJavaField();
                        if (blockChainColumns.indexOf("," + javaField + ",") == -1) continue;
                        Field field = Reflections.getThisField(obj.getClass(), javaField);
                        if (field != null) {
                            field.setAccessible(true);
                            String value = mapValue.get(javaField);
                            if(Global.YES.equals(genTableColumn.getBlockChainParam2())) {
                                value = Aes.aesDecrypt(value);
                            }
                            field.set(obj, value);
                        }
                    }
                }
            }
        }
    }

    private void processBlockChainSet(Zform obj, GenTable genTable) throws Exception {
        this.initCfx();
        if (StringUtil.isNotEmpty(genTable.getBlockChainParam1())) {
            String blockChainColumns = this.getGenTableBlockChainColumns(genTable);
            String setFunctionName = "insert";
            String idValue = obj.getId();
            if (obj.getIsNewRecord()) {
                obj.setPreId(IdGen.uuid());
                idValue = obj.getPreId();
            }
            if (StringUtil.isNotEmpty(blockChainColumns)) {
                String conAddress = this.getConAddress(genTable.getBlockChainParam1());
                String privateKey = this.getPrivateKey();
                String rowKey = idValue;
                LinkedHashMap<String, String> rowValueMap = Maps.newLinkedHashMap();
                for (GenTableColumn genTableColumn : genTable.getColumnList()) {
                    String javaField = genTableColumn.getJavaField();
                    if (blockChainColumns.indexOf("," + javaField + ",") == -1) continue;
                    Field field = Reflections.getThisField(obj.getClass(), javaField);
                    if (field != null) {
                        field.setAccessible(true);
                        Object object = field.get(obj);
                        String value = "";
                        if (object != null) {
                            value = (String) object;
                            rowKey += "," + javaField;
                            if(Global.YES.equals(genTableColumn.getBlockChainParam2())) {
                                value = Aes.aesEncrypt(value);
                            }
                        }
                        rowValueMap.put(javaField, value);
                    }
                }

                if (rowKey.indexOf(",") != -1) {
                    String timeStamp = Long.toString(System.currentTimeMillis());
                    rowKey += "," + timeStamp;
                    String rowValue = new Gson().toJson(rowValueMap);
                    String existsRowValue = BlockChainUtil.getValue(conAddress, "get", rowKey);
                    if (StringUtil.isEmpty(existsRowValue) || false == existsRowValue.equals(rowValue)) {
                        String hash = BlockChainUtil.setValue(conAddress, setFunctionName, privateKey, rowKey, rowValue);
                        this.setHashValue(genTable.getBlockChainParam1(), rowKey, hash, true, Global.NO);
                    }
                }
            }
        }
    }

    private void processBlockChainDel(Zform obj, GenTable genTable) throws Exception {
        this.initCfx();
        if (StringUtil.isNotEmpty(genTable.getBlockChainParam1())) {
            String blockChainColumns = this.getGenTableBlockChainColumns(genTable);
            String setFunctionName = "insert";
            String idValue = obj.getId();
            if (StringUtil.isNotEmpty(blockChainColumns)) {
                String conAddress = this.getConAddress(genTable.getBlockChainParam1());
                String privateKey = this.getPrivateKey();
                String rowKey = idValue;
                for (GenTableColumn genTableColumn : genTable.getColumnList()) {
                    String javaField = genTableColumn.getJavaField();
                    if (blockChainColumns.indexOf("," + javaField + ",") == -1) continue;
                    Field field = Reflections.getThisField(obj.getClass(), javaField);
                    if (field != null) {
                        field.setAccessible(true);
                        Object object = field.get(obj);
                        if (object != null) {
                            rowKey += "," + javaField;
                        }
                    }
                }
                if (rowKey.indexOf(",") != -1) {
                    String timeStamp = Long.toString(System.currentTimeMillis());
                    rowKey += "," + timeStamp;
                    String hash = BlockChainUtil.setValue(conAddress, setFunctionName, privateKey, rowKey, "deleted");
                    this.setHashValue(genTable.getBlockChainParam1(), rowKey, hash, true, Global.NO);
                }
            }
        }
    }

    private void setHashValue(String conAddressId, String key, String hash, Boolean isNewRecord, String delFlag) throws Exception {
        String formNo = "con_hash";
        GenTable genTable = genTableService.getGenTableWithDefination(formNo);
        Zform zform = new Zform();
        zform.setFormNo(formNo);
        if (isNewRecord) {
            zform.setPreId(key);
        } else {
            zform.setId(key);
        }
        zform.setS01(key);
        zform.setS02(hash);
        Zform g01 = new Zform(conAddressId, "con_address");
        zform.setG01(g01);
        zform.setDelFlag(delFlag);
        super.save(zform, genTable);
    }

    private String getHashKey(String idValue) {
        String hashKey = null;
        if (StringUtil.isNotEmpty(idValue)) {
            String formNo = "con_hash";
            GenTable genTable = genTableService.getGenTableWithDefination(formNo);
            Zform zform = new Zform();
            zform.setFormNo(formNo);
            Page<Zform> page = new Page<Zform>();
            page.setPageSize(1);
            page.setOrderBy("a.key_ desc");
            zform.setPage(page);
            zform.getSqlMap().put("dsf", " AND a.key_ LIKE '" + idValue + "%'");
            List<Zform> list = this.findList(zform, genTable);
            if (list.size() > 0) {
                hashKey = list.get(0).getS01();
            }
        }
        return hashKey;
    }

    private String getGenTableBlockChainColumns(GenTable genTable) {
        String blockChainColumns = ",";
        for(GenTableColumn column : genTable.getColumnList()) {
            if (Global.YES.equals(column.getBlockChainParam1())) {
                blockChainColumns += column.getJavaField() + ",";
            }
        }
        if (blockChainColumns.length() == 1) {
            blockChainColumns = "";
        }
        return blockChainColumns;
    }

    public void initCfx() throws Exception {
        if (BlockChainUtil.getCfx() == null) {
            Zform zform = new Zform();
            zform.setFormNo("con_network");
            GenTable genTable = genTableService.getGenTableWithDefination(zform.getFormNo());
            zform = super.get("1", genTable);
            BlockChainUtil.initCfx(zform.getS02(), Long.parseLong(zform.getS04()));
        }
    }

    private String getPrivateKey() {
        return super.getFirstValueByKey("con_manager", "private_key", "id", "1");
    }

    private String getConAddress(GenTable genTable) {
        return super.getFirstValueByKey("con_address", "contract_address", "id", genTable.getBlockChainParam1());
    }

    private String getConAddress(String addressId) {
        return super.getFirstValueByKey("con_address", "contract_address", "id", addressId);
    }

    //Extend business logic
    private void afterGet(Zform zform, GenTable genTable) {
        if ("con_manager".equalsIgnoreCase(genTable.getName())) {
            if (StringUtil.isNotEmpty(zform.getS03())) {
                zform.setS03("");
            }
        }
    }

    public String getHashByKey(String key) {
        if (key.startsWith("log")) {
            return this.getHashKey(key);
        } else {
            return super.getFirstValueByKey("con_hash", "hash_value", "key_", key);
        }
    }

    @Transactional(readOnly = false)
    public void extendBeforeSave(Zform zform, GenTable genTable) throws Exception {
        this.initCfx();
        if ("con_manager".equalsIgnoreCase(genTable.getName())) {
            if (StringUtil.isNotEmpty(zform.getS03())) {
                //import private key
                zform.setS02(BlockChainUtil.getAddress(zform.getS03()));
            } else if (StringUtil.isNotEmpty(zform.getS02())) {
                zform.setS03(this.getPrivateKey());
            }
        } else if ("con_template".equalsIgnoreCase(genTable.getName())) {
            if (StringUtil.isNotEmpty(zform.getS04())) {
                String hashValue = BlockChainUtil.deploy(this.getPrivateKey(), zform.getS04());
                zform.setS04("");
                zform.setS05(hashValue);
            }
        }
    }

    @Transactional(readOnly = false)
    public void extendAfterSave(Zform zform, GenTable genTable) {
        if (genTable.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
            if ("sys_menu".equalsIgnoreCase(genTable.getName())) {
                zformDao.updateSysMenuIsShowCascade(zform.getParentIds() + zform.getId() + ",", zform.getS07());
            }
            if ("sys_dictionary".equalsIgnoreCase(genTable.getName()) && zform.getParentIds().indexOf("data-params") != -1) {
                dictDataService.refreshDictCacheByType("data-params");
            }
        }
    }

    public void extendAfterSaveAct(Zform zform, GenTable genTable) throws Exception {
        if (Global.YES.equals(genTable.getBlockChainParam2())) {
            List<Act> list = histoicFlowList(zform.getProcInsId());
            String timeStamp = Long.toString(System.currentTimeMillis());
            String rowKey = "log," + zform.getId() + "," + timeStamp;
            String rowValue = new Gson().toJson(list);
            String setFunctionName = "insert";
            String conAddress = this.getConAddress(genTable.getBlockChainParam1());
            String privateKey = this.getPrivateKey();
            String hash = BlockChainUtil.setValue(conAddress, setFunctionName, privateKey, rowKey, rowValue);
            this.setHashValue(genTable.getBlockChainParam1(), rowKey, hash, true, Global.NO);
        }
    }

    @Transactional(readOnly = false)
    public void importConData(String ownerCode, String columns, String formNo, String parentFormNo, String parentId, String uniqueId, List<LinkedHashMap<String, String>> dataList, User currentUser) throws Exception {
        int i = 0;
        String newConAddress = null;
        for (LinkedHashMap row : dataList) {
            if (i++ == 0) continue;
            if (newConAddress == null) {
                newConAddress = this.getConAddress((String)row.get("g01.id|name"));
            }
            String key = (String)row.get("s01");
            String value = (String)row.get("remarks");
            BlockChainUtil.setValue(newConAddress, "insert", this.getPrivateKey(), key, value);
        }
    }

    public List<List<LinkedHashMap<String, String>>> exportConData(Page<Zform> page, Zform zform, GenTable genTable, String loginName) throws Exception {
        List<List<LinkedHashMap<String, String>>> data = super.exportData(page, zform, genTable, loginName);
        String addressId = null;
        String conAddress = null;
        for (ListIterator<List<LinkedHashMap<String, String>>> it = data.listIterator(); it.hasNext(); ) {
            List<LinkedHashMap<String, String>> row = it.next();
            String newAddressId = row.get(0).get("g01.id|name");
            if (addressId == null || false == newAddressId.equals(addressId)) {
                conAddress = this.getConAddress(newAddressId);
            }
            String key = row.get(1).get("s01");
            String value = BlockChainUtil.getValue(conAddress, "get", key);
            row.get(2).put("remarks", value);
        }
        return data;
    }

    public ResultJson deleteProc(Zform zform) {
        ResultJson resultJson = new ResultJson();
        try {
            super.deleteAct(zform);
            resultJson.setCode(ResultJson.CODE_SUCCESS);
            resultJson.setMsg("操作成功");
            resultJson.setMsg_en("operate success");
        } catch (Exception e) {
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("操作失败");
            resultJson.setMsg_en("operate fail");
            e.printStackTrace();
        }
        return resultJson;
    }
}
