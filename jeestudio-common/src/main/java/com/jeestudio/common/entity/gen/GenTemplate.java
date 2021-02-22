package com.jeestudio.common.entity.gen;

import com.google.common.collect.Lists;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.jeestudio.common.entity.common.DataEntity;
import com.jeestudio.utils.StringUtil;
import org.hibernate.validator.constraints.Length;

/**
 * @Description: Gen template
 * @author: houxl
 * @Date: 2020-01-18
 */
@XmlRootElement(name = "template")
public class GenTemplate extends DataEntity<GenTemplate> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private String filePath;
    private String fileName;
    private String content;

    public GenTemplate() {
    }

    public GenTemplate(String id) {
        super(id);
    }

    @Length(min = 1, max = 200)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlTransient
    public List<String> getCategoryList() {
        if (this.category == null) {
            return (List) Lists.newArrayList();
        }
        return (List) Lists.newArrayList(StringUtil.split(this.category, ","));
    }

    public void setCategoryList(List<String> categoryList) {
        if (categoryList == null)
            this.category = "";
        else
            this.category = ("," + StringUtil.join(categoryList, ",") + ",");
    }
}