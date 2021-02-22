package com.jeestudio.datasource.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeestudio.common.entity.gen.*;
import com.jeestudio.common.entity.system.Area;
import com.jeestudio.common.entity.system.Office;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.common.mapper.JaxbMapper;
import com.jeestudio.datasource.contextHolder.ApplicationContextHolder;
import com.jeestudio.utils.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Gen Util
 * @author: David
 * @Date: 2020-01-19
 */
public class GenUtil {

    private static Logger logger = LoggerFactory.getLogger(GenUtil.class);
    private static RestTemplate restTemplate;
    public static final String GENTABLE_CACHE = "genTableCache";

    /**
     * Initialize column property fields
     */
    public static void initColumnField(GenTable genTable) {
        for (GenTableColumn column : genTable.getColumnList()) {
            if (StringUtil.isNotBlank(column.getId())) {
                continue;
            }
            if (StringUtil.isBlank(column.getComments())) {
                column.setComments(column.getName());
            }
            if (StringUtil.startsWithIgnoreCase(column.getJdbcType(), "CHAR")
                    || StringUtil.startsWithIgnoreCase(column.getJdbcType(), "VARCHAR")
                    || StringUtil.startsWithIgnoreCase(column.getJdbcType(), "NARCHAR")) {
                column.setJavaType("String");
            } else if (StringUtil.startsWithIgnoreCase(column.getJdbcType(), "DATETIME")
                    || StringUtil.startsWithIgnoreCase(column.getJdbcType(), "DATE")
                    || StringUtil.startsWithIgnoreCase(column.getJdbcType(), "TIMESTAMP")) {
                column.setJavaType("java.util.Date");
                column.setShowType("dateselect");
            } else if (StringUtil.startsWithIgnoreCase(column.getJdbcType(), "BIGINT")
                    || StringUtil.startsWithIgnoreCase(column.getJdbcType(), "NUMBER")
                    || StringUtil.startsWithIgnoreCase(column.getJdbcType(), "DECIMAL")) {
                String[] ss = StringUtil.split(StringUtil.substringBetween(column.getJdbcType(), "(", ")"), ",");
                if (ss != null && ss.length == 2 && Integer.parseInt(ss[1]) > 0) {
                    column.setJavaType("Double");
                } else if (ss != null && ss.length == 1 && Integer.parseInt(ss[0]) <= 10) {
                    column.setJavaType("Integer");
                } else {
                    column.setJavaType("Long");
                }
            }
            column.setJavaField(StringUtil.toCamelCase(column.getName()));
            column.setIsPk(genTable.getPkList().contains(column.getName()) ? Global.YES : Global.NO);
            column.setIsInsert(Global.YES);
            column.setIsEdit(Global.YES);
            column.setIsList(Global.NO);
            column.setIsQuery(Global.NO);
            column.setIsForm(Global.NO);
            column.setShowType("input");
            column.setIsOneLine(Global.NO);

            //Init isForm
            if (!StringUtil.equalsIgnoreCase(column.getName(), "id")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "create_by")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "create_date")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "update_by")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "update_date")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "remarks")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "owner_code")
                    && !StringUtil.equalsIgnoreCase(column.getName(), "del_flag")) {
                column.setIsForm(Global.YES);
            }

            //Init list and query
            if (StringUtil.equalsIgnoreCase(column.getName(), "name")
                    || StringUtil.endsWithIgnoreCase(column.getName(), "name")
                    || StringUtil.equalsIgnoreCase(column.getName(), "title")) {
                column.setIsList(Global.YES);
            }
            if (StringUtil.equalsIgnoreCase(column.getName(), "name")
                    || StringUtil.endsWithIgnoreCase(column.getName(), "name")
                    || StringUtil.equalsIgnoreCase(column.getName(), "title")) {
                column.setIsQuery(Global.YES);
                column.setQueryType("like");
            }

            //Init javaType, showType and javaField for user, area, office and parent
            if (StringUtil.endsWithIgnoreCase(column.getName(), "user_id")) {
                column.setJavaType(User.class.getName());
                column.setJavaField(column.getJavaField().replaceAll("Id", ".id|name"));
                column.setShowType("treeselectRedio");
            } else if (StringUtil.endsWithIgnoreCase(column.getName(), "office_id")) {
                column.setJavaType(Office.class.getName());
                column.setJavaField(column.getJavaField().replaceAll("Id", ".id|name"));
                column.setShowType("officeselectTree");
            } else if (StringUtil.endsWithIgnoreCase(column.getName(), "area_id")) {
                column.setJavaType(Area.class.getName());
                column.setJavaField(column.getJavaField().replaceAll("Id", ".id|name"));
                column.setShowType("areaselect");
            } else if (StringUtil.startsWithIgnoreCase(column.getName(), "create_by")
                    || StringUtil.startsWithIgnoreCase(column.getName(), "update_by")) {
                column.setJavaType(User.class.getName());
                column.setJavaField(column.getJavaField() + ".id");
                column.setShowType("treeselectRedio");
            } else if (StringUtil.startsWithIgnoreCase(column.getName(), "create_date")
                    || StringUtil.startsWithIgnoreCase(column.getName(), "update_date")) {
                column.setShowType("dateselect");
                column.setDateType("yyyy-MM-dd HH:mm:ss");
            } else if (StringUtil.equalsIgnoreCase(column.getName(), "remarks")
                    || StringUtil.equalsIgnoreCase(column.getName(), "content")) {
                column.setShowType("textarea");
                column.setIsOneLine(Global.YES);
            } else if (StringUtil.equalsIgnoreCase(column.getName(), "parent_id")) {
                column.setJavaType("This");
                column.setJavaField("parent.id|name");
                column.setShowType("parentId");
            } else if (StringUtil.equalsIgnoreCase(column.getName(), "parent_ids")) {
                column.setQueryType("like");
            } else if (StringUtil.equalsIgnoreCase(column.getName(), "del_flag")) {
                column.setShowType("radiobox");
                column.setDictType("del_flag");
            } else if (StringUtil.equalsIgnoreCase(column.getName(), "sort") || StringUtil.startsWithIgnoreCase(column.getName(), "sort")) {
                column.setJavaType("Integer");
                column.setJdbcType("integer");
            }
        }
    }

    /**
     * Get template path
     */
    public static String getTemplatePath() {
        try {
            File file = new DefaultResourceLoader().getResource("").getFile();
            if (file != null) {
                return file.getAbsolutePath() + File.separator + StringUtil.replaceEach(GenUtil.class.getName(),
                        new String[]{"util." + GenUtil.class.getSimpleName(), "."}, new String[]{"template", File.separator});
            }
        } catch (Exception e) {
            logger.error("Error while getting template path:" + ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    /**
     * XML files converting to object
     */
    public static <T> T fileToObject(String fileName, Class<?> clazz) {
        try {
            String pathName = "/templates/modules/" + fileName;
            Resource resource = new ClassPathResource(pathName);
            InputStream is = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append("\r\n");
            }
            if (is != null) {
                is.close();
            }
            if (br != null) {
                br.close();
            }
            return (T) JaxbMapper.fromXml(sb.toString(), clazz);
        } catch (IOException e) {
            logger.warn("Error while converting to object:" + ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * Get code generation configuration object
     */
    public static GenConfig getConfig() {
        return fileToObject("config.xml", GenConfig.class);
    }

    /**
     * Get template list by category
     *
     * @param config
     * @param category
     * @param isChildTable
     * @return template list
     */
    public static List<GenTemplate> getTemplateList(GenConfig config, String category, boolean isChildTable, boolean isMobile) {
        List<GenTemplate> templateList = Lists.newArrayList();
        if (config != null && config.getCategoryList() != null && category != null) {
            for (GenCategory e : config.getCategoryList()) {
                List<String> list = null;
                if (!isChildTable) {
                    list = e.getTemplate();
                } else {
                    list = e.getChildTableTemplate();
                }
                if (list != null) {
                    for (String s : list) {
                        if (false == isMobile && (s.indexOf("mobile") != -1 || s.indexOf("app") == 0)) continue;
                        if (StringUtil.startsWith(s, GenCategory.CATEGORY_REF)) {
                            templateList.addAll(getTemplateList(config, StringUtil.replace(s, GenCategory.CATEGORY_REF, ""), false, isMobile));
                        } else {
                            GenTemplate template = fileToObject(s, GenTemplate.class);
                            if (template != null) {
                                template.setId(s); //by David
                                templateList.add(template);
                            }
                        }
                    }
                }
                break;
            }
        }
        return templateList;
    }

    /**
     * Get template list by category for form service
     *
     * @param config
     * @param category
     * @param isChildTable
     * @return template list
     */
    public static List<GenTemplate> getTemplateList(GenConfig config, String category, boolean isChildTable) {
        List<GenTemplate> templateList = Lists.newArrayList();
        if (config != null && config.getCategoryList() != null && category != null) {
            for (GenCategory e : config.getCategoryList()) {
                if (category.equals(e.getName())) {
                    List<String> list = null;
                    if (!isChildTable) {
                        list = e.getTemplate();
                    } else {
                        list = e.getChildTableTemplate();
                    }
                    if (list != null) {
                        for (String s : list) {
                            if (StringUtil.startsWith(s, GenCategory.CATEGORY_REF)) {
                                templateList.addAll(getTemplateList(config, StringUtil.replace(s, GenCategory.CATEGORY_REF, ""), false));
                            } else {
                                GenTemplate template = fileToObject(s, GenTemplate.class);
                                if (template != null) {
                                    template.setId(s);
                                    templateList.add(template);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return templateList;
    }

    /**
     * Get data model
     *
     * @param genScheme
     * @return data model map
     */
    public static Map<String, Object> getDataModel(GenScheme genScheme, String dbType) {
        Map<String, Object> model = Maps.newHashMap();

        model.put("packageName", StringUtil.lowerCase(genScheme.getPackageName()));
        model.put("lastPackageName", StringUtil.substringAfterLast((String) model.get("packageName"), "."));
        model.put("moduleName", StringUtil.lowerCase(genScheme.getModuleName()));
        model.put("subModuleName", StringUtil.lowerCase(genScheme.getSubModuleName()));
        model.put("className", StringUtil.uncapitalize(genScheme.getGenTable().getClassName()));
        model.put("ClassName", StringUtil.capitalize(genScheme.getGenTable().getClassName()));

        model.put("functionName", genScheme.getFunctionName());
        model.put("functionNameSimple", genScheme.getFunctionNameSimple());
        model.put("functionAuthor", StringUtil.isNotBlank(genScheme.getFunctionAuthor()) ? genScheme.getFunctionAuthor() : "David");
        model.put("functionVersion", DateUtil.getDate());

        model.put("urlPrefix", model.get("moduleName") + (StringUtil.isNotBlank(genScheme.getSubModuleName())
                ? "/" + StringUtil.lowerCase(genScheme.getSubModuleName()) : "") + "/" + model.get("className"));
        model.put("viewPrefix",
                model.get("urlPrefix"));
        model.put("permissionPrefix", model.get("moduleName") + (StringUtil.isNotBlank(genScheme.getSubModuleName())
                ? ":" + StringUtil.lowerCase(genScheme.getSubModuleName()) : "") + ":" + model.get("className"));

        model.put("dbType", dbType);
        model.put("table", genScheme.getGenTable());

        return model;
    }

    /**
     * Generating files
     *
     * @param isChild
     * @param category
     * @param tpl
     * @param genScheme
     * @param isReplaceFile
     * @param timePath
     * @return result message
     */
    public static String xgenerateToFile(Boolean isChild,
                                         String category,
                                         GenTemplate tpl,
                                         GenScheme genScheme,
                                         boolean isReplaceFile,
                                         String timePath,
                                         String projectPath,
                                         String genKey,
                                         String genUrl,
                                         String dbType) {
        Map<String, Object> model = GenUtil.getDataModel(genScheme, dbType);
        String fileName = projectPath + "/" + FreeMarkerUtil.renderString(tpl.getFilePath() + "/", model) + tpl.getFileName();

        //local render
        //String content = FreeMarkerUtil.renderString(StringUtil.trimToEmpty(tpl.getContent()), model);
        //LOGGER.info("Content === \r\n" + content);
        //server render

        String content = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String genSchemeString = mapper.writeValueAsString(genScheme);
            genSchemeString = Encodes.encodeBase64(genSchemeString);
            Map<String, String> params = new HashMap<String, String>();
            params.put("genscheme", genSchemeString);
            params.put("isChild", isChild ? Global.YES : Global.NO);
            params.put("code", genKey + ":" + getLocalMac(InetAddress.getLocalHost()));
            params.put("category", category);
            params.put("tplId", tpl.getId());

            GenParam genParam = new GenParam();
            genParam.setCategory(category);
            genParam.setCode(genKey + ":" + getLocalMac(InetAddress.getLocalHost()));
            genParam.setGenscheme(genSchemeString);
            genParam.setIsChild(isChild ? Global.YES : Global.NO);
            genParam.setTplId(tpl.getId());

            if (restTemplate == null) restTemplate = new RestTemplate();
            content = restTemplate.postForObject(genUrl, genParam, String.class);
        } catch (Exception e) {
            content = "error:" + e.getMessage();
        }
        logger.info("Content === \r\n" + content);

        if (content.indexOf("error") == 0) {
            logger.info("File error === " + fileName);
            content = content.replaceAll("error:", "");
            logger.info(content);
            if (content.length() > 9) content = content.substring(0, 9);
            return "Released failed:" + fileName + " " + content + "<br/>";
        } else {
            if (isReplaceFile) {
                FileUtil.deleteFile(fileName);
            }
            if (FileUtil.createFile(fileName)) {
                try {
                    FileUtil.writeToFile(fileName, content, true);
                    logger.info("File create === " + fileName);
                    xgenerateCustom(fileName, genScheme.getGenTable(), isReplaceFile);
                    return "Released success:" + fileName + "<br/>";
                } catch (Exception e) {
                    logger.error(fileName + " released failed, " + ExceptionUtils.getStackTrace(e));
                    return "Released failed:" + fileName + "<br/>";
                }
            } else {
                logger.info("File extents === " + fileName);
                return "File already exist:" + fileName + "<br/>";
            }
        }
    }

    private static void xgenerateCustom(String fileName, GenTable genTable, boolean isReplaceFile) {
        if (Global.YES.equals(genTable.getIsCustom()) && fileName.indexOf("/form.html") != -1) {
            fileName = fileName.replaceFirst("form.html", "customform.html");
            if (isReplaceFile) {
                FileUtil.deleteFile(fileName);
                if (FileUtil.createFile(fileName)) {
                    try {
                        String head = "<div class=\"container\" style=\"width: 100%;\">\n" +
                                "\t<form id=\"inputForm\" class=\"form-horizontal\" style=\"overflow: auto;\">\n" +
                                "\t\t<input name=\"id\" type=\"hidden\" id=\"id\"/>\n" +
                                "\t\t<input name=\"formNo\" type=\"hidden\" id=\"formNo\"/>\n";
                        if (StringUtil.isNotEmpty(genTable.getProcessDefinitionCategory())) {
                            head += "\t\t<input component-type=\"input\" type=\"hidden\" id=\"act.taskId\" name=\"act.taskId\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"act.taskName\" name=\"act.taskName\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"act.taskDefKey\" name=\"act.taskDefKey\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"procInsId\" name=\"procInsId\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"act.procDefId\" name=\"act.procDefId\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"flag\" name=\"act.flag\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"tempNodeKey\" name=\"tempNodeKey\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"tempLoginName\" name=\"tempLoginName\" />\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"procDefKey\" name=\"procDefKey\">\n" +
                                    "\t\t<input component-type=\"input\" type=\"hidden\" id=\"ruleArgsJson\" name='ruleArgsJson' />";
                        }
                        String bottom = "\n\t</form>\n" +
                                "</div>";
                        FileUtil.writeToFile(fileName, head + genTable.getExtJsp() + bottom, true);
                        logger.info("File create === " + fileName);
                    } catch (Exception e) {
                        logger.error(fileName + " released failed, " + ExceptionUtils.getStackTrace(e));
                    }
                } else {
                    logger.warn("File extents === " + fileName);
                }
            }
        }
    }

    /**
     * Construct SQL segment of dynamic form
     *
     * @param genTable
     */
    public static void buildSqlMapForDynamicTable(GenTable genTable, String dbType) {
        buildSqlColumn(genTable, dbType);
        buildSqlJoins(genTable);
        buildSqlInsert(genTable);
        buildSqlUpdate(genTable);
    }

    /**
     * Construct sqlcolumn
     *
     * @param table
     */
    private static void buildSqlColumn(GenTable table, String dbType) {
        StringBuilder s = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        for (GenTableColumn c : table.getColumnList()) {
            if ("procTaskPermission".equalsIgnoreCase(c.getJavaField())) {
                s.append("a." + c.getName() + " AS \"procTaskPermission.id\",");
                s.append("\n");
                s2.append("a." + c.getName() + " AS \"procTaskPermission.id\",");
                s2.append("\n");
            } else {
                if ("parentId".equalsIgnoreCase(c.getShowType())) {
                    if (table.getTableType().equals(GenTable.TABLE_TYPE_RIGHTTABLE)) {
                        s.append("a.parent_id AS \"parent.id\",");
                        s.append("\n");
                        s2.append("a.parent_id AS \"parent.id\",");
                        s2.append("\n");
                    } else if (table.getTableType().equals(GenTable.TABLE_TYPE_TREE)) {
                        //(case when exists(select 1 from [tableName] b where b.parent_id = a.id) then 1 else 0 end) as hasChildren
                        s.append("a.parent_id AS \"parent.id\",");
                        s.append("\n");
                        s2.append("a.parent_id AS \"parent.id\",");
                        s2.append("\n");
                        s.append("(CASE WHEN EXISTS(SELECT 1 FROM " + table.getName() + " child WHERE child.parent_id = a.id) THEN 1 ELSE 0 END) AS hasChildren,");
                        s.append("\n");
                        s2.append("(CASE WHEN EXISTS(SELECT 1 FROM " + table.getName() + " child WHERE child.parent_id = a.id) THEN 1 ELSE 0 END) AS hasChildren,");
                        s2.append("\n");
                    }
                } else {
                    s.append("a." + c.getName() + " AS \"" + c.getJavaFieldId() + "\",");
                    s.append("\n");
                    if("java.util.Date".equals(c.getJavaType())) {
                        s2.append(getDateFormatSql(c.getName(), c.getDateType(), dbType));
                        s2.append("\n");
                    } else {
                        s2.append("a." + c.getName() + " AS \"" + c.getName() + "\",");
                        s2.append("\n");
                    }
                    if("createBy.id".equals(c.getJavaFieldId())) {
                        s.append("createBy.name AS \"createBy.name\",");
                        s.append("\n");
                        s2.append("createBy.name AS \"createBy.name\",");
                        s2.append("\n");
                    } else if ("updateBy.id".equals(c.getJavaFieldId())) {
                        s.append("updateBy.name AS \"updateBy.name\",");
                        s.append("\n");
                        s2.append("updateBy.name AS \"updateBy.name\",");
                        s2.append("\n");
                    }
                }
            }
        }
        for (GenTableColumn c : table.getColumnList()) {
            if (false == StringUtil.isEmpty(c.getShowType())) {
                if ("userselect".equalsIgnoreCase(c.getShowType())
                        || "treeselectRedio".equalsIgnoreCase(c.getShowType())
                        || "officeselect".equalsIgnoreCase(c.getShowType())
                        || "officeselectTree".equalsIgnoreCase(c.getShowType())
                        || "treeselect".equalsIgnoreCase(c.getShowType())
                        || "areaselect".equalsIgnoreCase(c.getShowType())) {
                    for (String[] a : c.getJavaFieldAttrs()) {
                        //${c.simpleJavaField}.${a[1]} AS "${c.simpleJavaField}.${a[0]}",
                        s.append(c.getSimpleJavaField() + "." + a[1] + " AS \"" + c.getSimpleJavaField() + "." + a[0] + "\",");
                        s.append("\n");
                        s2.append(c.getSimpleJavaField() + "." + a[1] + " AS \"" + c.getSimpleJavaField() + "." + a[0] + "\",");
                        s2.append("\n");
                    }
                } else if ("gridselect".equalsIgnoreCase(c.getShowType())) {
                    //${c.simpleJavaField}.${c.searchKey} AS "${c.simpleJavaField}.${c.searchKey}",
                    if (c.getSearchKey() != null) {
                        String[] search = c.getSearchKey().split("\\|");
                        s.append(c.getSimpleJavaField() + "." + search[0] + " AS \"" + c.getJavaFieldName() + "\",");
                        s.append("\n");
                        s2.append(c.getSimpleJavaField() + "." + search[0] + " AS \"" + c.getSimpleJavaField() + "." + search[0] + "\",");
                        s2.append("\n");
                    }
                } else if ("parentId".equalsIgnoreCase(c.getShowType())) {
                    for (String[] a : c.getJavaFieldAttrs()) {
                        //b.${a[1]} AS "${c.simpleJavaField}.${a[0]}",
                        s.append("b." + a[1] + " AS \"" + c.getSimpleJavaField() + "." + a[0] + "\",");
                        s.append("\n");
                        s2.append("b." + a[1] + " AS \"" + c.getSimpleJavaField() + "." + a[0] + "\",");
                        s2.append("\n");
                    }
                }
            }
        }
        //Delete last comma
        if (s.length() > 0) s.deleteCharAt(s.lastIndexOf(","));
        table.setSqlColumns(s.toString());
        if (s2.length() > 0) s2.deleteCharAt(s2.lastIndexOf(","));
        table.setSqlColumnsFriendly(s2.toString());
    }

    private static String getDateFormatSql(String name, String dataType, String dbType) {
        String sql = null;
        if ("mysql".equalsIgnoreCase(dbType)) {
            //%Y-%m-%d %H:%i:%s
            if ("yyyy-MM-dd".equals(dataType)) {
                sql = "date_format(a." + name + ",'%Y-%m-%d') AS \"" + name + "\",";
            } else if ("yyyy-MM-dd HH:mm".equals(dataType)) {
                sql = "date_format(a." + name + ",'%Y-%m-%d %H:%i') AS \"" + name + "\",";
            } else if ("yyyy-MM-dd HH".equals(dataType)) {
                sql = "date_format(a." + name + ",'%Y-%m-%d %H') AS \"" + name + "\",";
            } else {
                sql = "date_format(a." + name + ",'%Y-%m-%d %H:%i:%s') AS \"" + name + "\",";
            }
        } else if ("mssql".equalsIgnoreCase(dbType)) {
            if ("yyyy-MM-dd".equals(dataType)) {
                sql = "CONVERT(varchar(100), a." + name + ", 23) AS \"" + name + "\",";
            } else {
                sql = "CONVERT(varchar(100), a." + name + ", 20) AS \"" + name + "\",";
            }
        } else if ("oracle".equalsIgnoreCase(dbType)) {
            //YYYY-MM-DD HH24:mi:ss
            if ("yyyy-MM-dd".equals(dataType)) {
                sql = "TO_CHAR(a." + name + ",'YYYY-MM-DD') AS \"" + name + "\",";
            } else if ("yyyy-MM-dd HH:mm".equals(dataType)) {
                sql = "TO_CHAR(a." + name + ",'YYYY-MM-DD HH24:mi') AS \"" + name + "\",";
            } else if ("yyyy-MM-dd HH".equals(dataType)) {
                sql = "TO_CHAR(a." + name + ",'YYYY-MM-DD HH24') AS \"" + name + "\",";
            } else {
                sql = "TO_CHAR(a." + name + ",'YYYY-MM-DD HH24:mi:ss') AS \"" + name + "\",";
            }
        } else {
            sql= "a." + name + " AS \"" + name + "\",";
        }
        return sql;
    }

    /**
     * Construct sqljoins
     *
     * @param table
     */
    private static void buildSqlJoins(GenTable table) {
        StringBuilder s = new StringBuilder();
        //Associated master table
        if (table.getParentExists() && false == GenTable.TABLE_TYPE_RIGHTTABLE.equals(table.getTableType())) {
            //LEFT JOIN ${table.parentTable} b ON b.id = a.${table.parentTableFk}
            s.append("LEFT JOIN " + table.getParentTable() + " b ON b.id = a." + table.getParentTableFk());
            s.append("\n");
        }
        //Associated system table
        for (GenTableColumn c : table.getColumnList()) {
            if (false == StringUtil.isEmpty(c.getShowType())) {
                if ("userselect".equalsIgnoreCase(c.getShowType()) || "treeselectRedio".equalsIgnoreCase(c.getShowType())) {
                    //LEFT JOIN sys_user ${c.simpleJavaField} ON ${c.simpleJavaField}.id = a.${c.name}
                    s.append("LEFT JOIN sys_user " + c.getSimpleJavaField() + " ON " + c.getSimpleJavaField() + ".id = a." + c.getName());
                    s.append("\n");
                } else if ("officeselect".equalsIgnoreCase(c.getShowType()) || "officeselectTree".equalsIgnoreCase(c.getShowType())) {
                    //LEFT JOIN sys_office ${c.simpleJavaField} ON ${c.simpleJavaField}.id = a.${c.name}
                    s.append("LEFT JOIN sys_office " + c.getSimpleJavaField() + " ON " + c.getSimpleJavaField() + ".id = a." + c.getName());
                    s.append("\n");
                } else if ("areaselect".equalsIgnoreCase(c.getShowType())) {
                    //LEFT JOIN sys_area ${c.simpleJavaField} ON ${c.simpleJavaField}.id = a.${c.name}
                    s.append("LEFT JOIN sys_area " + c.getSimpleJavaField() + " ON " + c.getSimpleJavaField() + ".id = a." + c.getName());
                    s.append("\n");
                } else if ("treeselect".equalsIgnoreCase(c.getShowType())) {
                    s.append("LEFT JOIN " + c.getTableName() + " " + c.getSimpleJavaField() + " ON " + c.getSimpleJavaField() + ".id = a." + c.getName());
                    s.append("\n");
                } else if ("gridselect".equalsIgnoreCase(c.getShowType())) {
                    //LEFT JOIN ${c.tableName} ${c.simpleJavaField} ON ${c.simpleJavaField}.id = a.${c.name}
                    s.append("LEFT JOIN " + c.getTableName() + " " + c.getSimpleJavaField() + " ON " + c.getSimpleJavaField() + ".id = a." + c.getName());
                    s.append("\n");
                } else if ("parentId".equalsIgnoreCase(c.getShowType())) {
                    if (GenTable.TABLE_TYPE_TREE.equals(table.getTableType())) {
                        s.append("LEFT JOIN " + table.getName() + " b ON b.id = a.parent_id ");
                        s.append("\n");
                    } else if (GenTable.TABLE_TYPE_RIGHTTABLE.equals(table.getTableType())) {
                        s.append("LEFT JOIN " + table.getParentTable() + " b ON b.id = a.parent_id ");
                        s.append("\n");
                    }
                }
            }
        }
        if (false == StringUtil.isEmpty(table.getIsProcessDefinition())
                && Global.YES.equals(table.getIsProcessDefinition())) {
            //LEFT JOIN sys_user u  ON a.create_by = u.id
            s.append("LEFT JOIN sys_user u  ON a.create_by = u.id");
            s.append("\n");
        }
        table.setSqlJoins(s.toString());
    }

    /**
     * Construct sqlinsert
     *
     * @param table
     */
    private static void buildSqlInsert(GenTable table) {
        StringBuilder s = new StringBuilder();
        s.append("(");
        s.append("\n");
        for (GenTableColumn c : table.getColumnList()) {
            if (false == StringUtil.isEmpty(c.getIsInsert()) && Global.YES.equals(c.getIsInsert())) {
                //${c.name},
                s.append(c.getName() + ",");
                s.append("\n");
            }
        }
        //Delete last comma
        if (s.length() > 0) s.deleteCharAt(s.lastIndexOf(","));
        s.append(") VALUES (");
        s.append("\n");
        for (GenTableColumn c : table.getColumnList()) {
            if (false == StringUtil.isEmpty(c.getIsInsert()) && Global.YES.equals(c.getIsInsert())) {
                //${"#"}{${c.javaFieldId}<#if c.javaFieldId == "procTaskPermission">.id</#if>},
                s.append("#{" + c.getJavaFieldId());
                if ("procTaskPermission".equalsIgnoreCase(c.getJavaFieldId())) {
                    s.append(".id");
                }
                s.append("},");
                s.append("\n");
            }
        }
        //Delete last comma
        if (s.length() > 0) s.deleteCharAt(s.lastIndexOf(","));
        s.append(")");
        s.append("\n");
        table.setSqlInsert(s.toString());
    }

    /**
     * Construct sqlupdate
     *
     * @param table
     */
    private static void buildSqlUpdate(GenTable table) {
        StringBuilder s = new StringBuilder();
        for (GenTableColumn c : table.getColumnList()) {
            if (false == StringUtil.isEmpty(c.getIsEdit()) && Global.YES.equals(c.getIsEdit())) {
                //${c.name} = ${"#"}{${c.javaFieldId}<#if c.javaFieldId == "procTaskPermission">.id</#if>},
                s.append(c.getName() + " = #{" + c.getJavaFieldId());
                if ("procTaskPermission".equalsIgnoreCase(c.getJavaFieldId())) {
                    s.append(".id");
                }
                s.append("},");
                s.append("\n");
            }
        }
        //Delete last comma
        if (s.length() > 0) s.deleteCharAt(s.lastIndexOf(","));
        table.setSqlUpdate(s.toString());
    }

    /**
     * Get local mac
     *
     * @param ia
     * @throws SocketException
     */
    public static String getLocalMac(InetAddress ia) {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    sb.append("0" + str);
                } else {
                    sb.append(str);
                }
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            logger.error("Error while getting local mac:" + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
