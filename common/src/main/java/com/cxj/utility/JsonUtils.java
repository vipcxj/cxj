/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.kopitubruk.util.json.JSONException;
import org.kopitubruk.util.json.JSONParser;
import org.kopitubruk.util.json.JSONUtil;

/**
 *
 * @author Administrator
 */
public class JsonUtils {

    public static boolean isJson(String toTest) {
        try {
            JSONParser.parseJSON(toTest);
            return true;
        } catch (ParseException|JSONException e) {
            return false;
        }
    }

    public static boolean isJsonObject(String toTest) {
        toTest = toTest.trim();
        if (!toTest.startsWith("{")) {
            return false;
        }
        try {
            JSONParser.parseJSON(toTest);
            return true;
        } catch (ParseException|JSONException e) {
            return false;
        }
    }

    public static boolean isJsonString(String toTest) {
        toTest = toTest.trim();
        if (!toTest.startsWith("\"") && !toTest.startsWith("'")) {
            return false;
        }
        try {
            JSONParser.parseJSON(toTest);
            return true;
        } catch (ParseException|JSONException e) {
            return false;
        }
    }

    public static boolean isJsonDate(String toTest) {
        toTest = toTest.trim();
        if (!toTest.startsWith("\"") && !toTest.startsWith("'")) {
            return false;
        }
        try {
            return JSONParser.parseJSON(toTest) instanceof Date;
        } catch (ParseException|JSONException e) {
            return false;
        }
    }

    public static boolean isJsonNull(String toTest) {
        toTest = toTest.trim();
        return "null".equals(toTest);
    }

    public static boolean isJsonTrue(String toTest) {
        return "true".equals(toTest.trim());
    }

    public static boolean isJsonFalse(String toTest) {
        return "false".equals(toTest.trim());
    }

    public static boolean isJsonNumber(String toTest) {
        toTest = toTest.trim();
        if (toTest.startsWith("'")
                || toTest.startsWith("\"")
                || toTest.startsWith("[")
                || toTest.startsWith("{") || isJsonTrue(toTest) || isJsonFalse(toTest) || isJsonNull(toTest)) {
            return false;
        }
        try {
            return JSONParser.parseJSON(toTest) instanceof Number;
        } catch (ParseException|JSONException e) {
            return false;
        }
    }

    public static boolean isJsonArray(String toTest) {
        toTest = toTest.trim();
        if (!toTest.startsWith("[")) {
            return false;
        }
        try {
            JSONParser.parseJSON(toTest);
            return true;
        } catch (ParseException|JSONException e) {
            return false;
        }
    }

    public static String toStandardJson(String json) throws JSONException, ParseException {
        return JSONUtil.toJSON(JSONParser.parseJSON(json));
    }
    
    public static String toJson(Object obj) {
        return JSONUtil.toJSON(obj);
    }

    public static Map<String, Object> toJsonObject(String json) throws JSONException, ParseException {
        return (Map<String, Object>) JSONParser.parseJSON(json);
    }

    public static List<Object> toJsonArray(String json) throws JSONException, ParseException {
        return (List<Object>) JSONParser.parseJSON(json);
    }

    public static String toJsonString(String json) throws JSONException, ParseException {
        return (String) JSONParser.parseJSON(json);
    }

    public static Date toJsonDate(String json) throws JSONException, ParseException {
        return (Date) JSONParser.parseJSON(json);
    }

    public static String toJsonNumber(String json) throws JSONException, ParseException {
        return (String) JSONParser.parseJSON(json);
    }

    public static Long toJsonInteger(String json) throws JSONException, ParseException {
        return (Long) JSONParser.parseJSON(json);
    }

    public static Double toJsonDecimal(String json) throws JSONException, ParseException {
        return (Double) JSONParser.parseJSON(json);
    }
}
