package com.jeestudio.utils;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.gson.*;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Description: JSON convert util
 * @author: whl
 * @Date: 2019-12-03
 */
public class JsonConvertUtil {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"
            , "MMM d, yyyy K:m:s a"};

    /**
     * JSON to object
     */
    public static <T> T jsonToObject(String pojo, Class<T> clazz) {

        return JSONObject.parseObject(pojo, clazz);
    }

    /**
     * Object to JSON
     */
    public static <T> String objectToJson(T t) {

        return JSONObject.toJSONString(t);
    }

    /**
     * String to class as: gsonBuilder().fromJson("",Zclass.class)
     */
    public static Gson gsonBuilder() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String dateStr = json == null ? "" : json.getAsString().replaceAll("\"", "");
                        boolean b = dateStr.matches("^[0-9]+$");
                        Date date = null;
                        if (b) {
                            date = new Date(Long.valueOf(dateStr));
                        } else {
                            try {
                                date = DateUtils.parseDate(dateStr, parsePatterns);
                            } catch (ParseException e) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                                try {
                                    date = sdf.parse(dateStr);
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        return date;
                    }
                }).create();
        return gson;
    }
}
