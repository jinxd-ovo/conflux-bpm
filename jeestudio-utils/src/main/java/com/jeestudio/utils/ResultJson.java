package com.jeestudio.utils;

import java.util.LinkedHashMap;

/**
 * @Description: Result Json
 * @author: whl
 * @Date: 2019-12-03
 */
public class ResultJson {

    public static final int CODE_FAILED = -1;
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_TOKEN_EXPIRED = 1001;

    //Return code 0 success -1 fail 1001 token expired
    private Integer code;

    //Chinese message
    private String msg;

    //English message
    private String msg_en;

    //Data
    private LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();

    private String token;

    //Page data list
    private Object rows;

    //Row count
    private Object total;

    //Page offset
    private Object offset;

    //Return id after inserted
    private String insertedId;

    public ResultJson() {
    }

    public ResultJson(Integer code, String msg, LinkedHashMap<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultJson(Integer code, String msg, String msg_en, LinkedHashMap<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.msg_en = msg_en;
        this.data = data;
    }

    public ResultJson(Integer code, String msg, LinkedHashMap<String, Object> data, String token) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.token = token;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LinkedHashMap<String, Object> getData() {
        return data;
    }

    public void setData(LinkedHashMap<String, Object> data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void put(String key, Object value){
        data.put(key, value);
    }

    public void remove(String key){
        data.remove(key);
    }

    public String getMsg_en() {
        return msg_en;
    }

    public void setMsg_en(String msg_en) {
        this.msg_en = msg_en;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }

    public Object getOffset() {
        return offset;
    }

    public void setOffset(Object offset) {
        this.offset = offset;
    }

    public String getInsertedId() {
        return insertedId;
    }

    public void setInsertedId(String insertedId) {
        this.insertedId = insertedId;
    }
}
