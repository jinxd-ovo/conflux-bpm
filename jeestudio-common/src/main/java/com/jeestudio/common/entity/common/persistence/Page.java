package com.jeestudio.common.entity.common.persistence;

import com.google.common.collect.Lists;
import com.jeestudio.utils.CookieUtil;
import com.jeestudio.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Description: Page
 * @author: whl
 * @Date: 2019-11-28
 */
public class Page<T> {

    protected int pageNo = 1;
    protected int pageSize = 10;
    //Total number of records, set to "- 1" to indicate the total number of no queries
    protected long count;
    protected int first;
    protected int last;
    protected int prev;
    protected int next;
    private boolean firstPage;
    private boolean lastPage;
    protected int length = 8;
    protected int slider = 1;
    private List<T> list = new ArrayList<T>();
    private List<LinkedHashMap> map = Lists.newArrayList();
    private String orderBy = "";
    //Set the JS function name called by clicking the page number. It is page by default. It is used when there are multiple page objects on a page
    protected String funcName = "page";
    //Additional argument to function, third argument value
    protected String funcParam = "";
    //Set the prompt message, which is displayed after "n in total"
    private String message = "";

    public Page() {
        this.pageSize = -1;
    }

    /**
     * Construction method
     */
    public Page(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, -2);
    }

    /**
     * Construction method
     */
    public Page(int pageNo, int pageSize, String orderBy) {
        this.setPageNo(pageNo);
        this.setPageSize(pageSize);
        this.setOrderBy(orderBy);
    }

    /**
     * Construction method
     */
    public Page(HttpServletRequest request, HttpServletResponse response, int defaultPageSize) {
        String no = request.getParameter("pageNo");
        if (StringUtil.isNumeric(no)) {
            CookieUtil.setCookie(response, "pageNo", no);
            this.setPageNo(Integer.parseInt(no));
        } else if (request.getParameter("repage") != null) {
            no = CookieUtil.getCookie(request, "pageNo");
            if (StringUtil.isNumeric(no)) {
                this.setPageNo(Integer.parseInt(no));
            }
        }
        String size = request.getParameter("pageSize");
        if (StringUtil.isNumeric(size)) {
            CookieUtil.setCookie(response, "pageSize", size);
            this.setPageSize(Integer.parseInt(size));
        } else if (request.getParameter("repage") != null) {
            no = CookieUtil.getCookie(request, "pageSize");
            if (StringUtil.isNumeric(size)) {
                this.setPageSize(Integer.parseInt(size));
            }
        } else if (defaultPageSize != -2) {
            this.pageSize = defaultPageSize;
        }
        String orderBy = request.getParameter("orderBy");
        if (StringUtil.isNotBlank(orderBy)) {
            this.setOrderBy(orderBy);
        }
        Object mobileFlag = request.getAttribute("mobileFlag");
        if (mobileFlag != null && Boolean.parseBoolean(mobileFlag.toString())) {
            this.setPageNo(Integer.parseInt(request.getAttribute("pageNo").toString()));
        }
    }

    /**
     * Construction method
     */
    public Page(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0);
    }

    /**
     * Construction method
     */
    public Page(int pageNo, int pageSize, long count) {
        this(pageNo, pageSize, count, new ArrayList<T>());
    }

    /**
     * Construction method
     */
    public Page(int pageNo, int pageSize, long count, List<T> list) {
        this.setCount(count);
        this.setPageNo(pageNo);
        this.pageSize = pageSize;
        this.list = list;
    }

    /**
     * Initialization parameters
     */
    public void initialize() {
        this.first = 1;
        this.last = (int) (count / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);
        if (this.count % this.pageSize != 0 || this.last == 0) {
            this.last++;
        }
        if (this.last < this.first) {
            this.last = this.first;
        }
        if (this.pageNo <= 1) {
            this.pageNo = this.first;
            this.firstPage = true;
        }
        if (this.pageNo >= this.last) {
            this.pageNo = this.last;
            this.lastPage = true;
        }
        if (this.pageNo < this.last - 1) {
            this.next = this.pageNo + 1;
        } else {
            this.next = this.last;
        }
        if (this.pageNo > 1) {
            this.prev = this.pageNo - 1;
        } else {
            this.prev = this.first;
        }
        if (this.pageNo < this.first) {// If the current page is smaller than the first page
            this.pageNo = this.first;
        }
        if (this.pageNo > this.last) {// If the current page is larger than the last page
            this.pageNo = this.last;
        }
    }

    /**
     * Default output current page label
     */
    @Override
    public String toString() {
        return "";
    }

    protected String getSelected(int pageNo, int selectedPageNo) {
        if (pageNo == selectedPageNo) {
            return "active";
        } else {
            return "";
        }
    }

    /**
     * Get paged HTML code
     */
    public String getHtml() {
        return toString();
    }

    /**
     * Total number of get settings
     */
    public long getCount() {
        return count;
    }

    /**
     * Set total data
     */
    public void setCount(long count) {
        this.count = count;
        if (pageSize >= count) {
            pageNo = 1;
        }
    }

    /**
     * Get current page number
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * Set current page number
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * Get page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Set page size (up to 500)
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize; // > 500 ? 500 : pageSize;
    }

    /**
     * Home page index
     */
    public int getFirst() {
        return first;
    }

    /**
     * Index of tail page
     */
    public int getLast() {
        return last;
    }

    /**
     * Total number of pages obtained
     */
    public int getTotalPage() {
        return getLast();
    }

    /**
     * Is it the first page
     */
    public boolean isFirstPage() {
        return firstPage;
    }

    /**
     * Last page or not
     */
    public boolean isLastPage() {
        return lastPage;
    }

    /**
     * Index value of previous page
     */
    public int getPrev() {
        if (isFirstPage()) {
            return pageNo;
        } else {
            return pageNo - 1;
        }
    }

    /**
     * Next page index value
     */
    public int getNext() {
        if (isLastPage()) {
            return pageNo;
        } else {
            return pageNo + 1;
        }
    }

    /**
     * Get the list of data objects on this page
     */
    public List<T> getList() {
        return list;
    }

    /**
     * Set the list of data objects on this page
     */
    public Page<T> setList(List<T> list) {
        this.list = list;
        initialize();
        return this;
    }

    /**
     * Get the map of data objects on this page
     */
    public List<LinkedHashMap> getMap() {
        return map;
    }

    /**
     * Set the map of data objects on this page
     */
    public void setMap(List<LinkedHashMap> map) {
        this.map = map;
        initialize();
    }

    /**
     * Get query sort string
     */
    public String getOrderBy() {
        // SQL filtering, prevent injection
        String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
                + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
        Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        if (sqlPattern.matcher(orderBy).find()) {
            return "";
        }
        return orderBy;
    }

    /**
     * Set query sorting, standard query is valid, instance： updatedate desc, name asc
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Get the JS function name called by clicking the page number
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * Set the JS function name called by clicking the page number.
     * It is page by default. It is used when there are multiple page objects on a page.
     *
     * @param funcName Default is page
     */
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    /**
     * Get additional parameters of paging function
     */
    public String getFuncParam() {
        return funcParam;
    }

    /**
     * Set additional parameters for paging functions
     */
    public void setFuncParam(String funcParam) {
        this.funcParam = funcParam;
    }

    /**
     * Set the prompt message, which is displayed after "n in total"
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Is paging valid
     *
     * @return this.pageSize==-1
     */
    public boolean isDisabled() {
        return this.pageSize == -1;
    }

    /**
     * Do you want to perform total statistics
     *
     * @return this.count==-1
     */
    public boolean isNotCount() {
        return this.count == -1;
    }

    /**
     * get Hibernate FirstResult
     */
    public int getFirstResult() {
        int firstResult = (getPageNo() - 1) * getPageSize();
        if (firstResult >= getCount() || firstResult < 0) {
            firstResult = 0;
        }
        return firstResult;
    }

    /**
     * get Hibernate MaxResults
     */
    public int getMaxResults() {
        return getPageSize();
    }
}
