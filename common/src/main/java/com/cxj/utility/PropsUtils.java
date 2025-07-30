/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PropsUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(PropsUtils.class);

    /**
     * 加载属性文件
     */
    public static Properties loadConfigure(Properties base, String path) {
        Properties configure;
        if (base != null) {
            configure = new Properties(base);
        } else {
            configure = new Properties();
        }
        File configureFile = new File(path);
        if (configureFile.exists()) {
            try (InputStream is = new FileInputStream(configureFile)) {
                try (InputStreamReader reader = new InputStreamReader(is, Charsets.UTF_8)) {
                    configure.load(reader);
                }
            } catch (IOException ex) {
                LOGGER.catching(ex);
            }
        }
        return configure;
    }
    
    public static void saveConfigure(Properties base, String path) {
        File target = new File(path);
        if (target.isDirectory()) {
            throw new IllegalArgumentException();
        }
        if (target.exists()) {
            if (!target.delete()) {
                throw new IllegalStateException("Unable to delete the file: " + path);
            }
        }
        try (OutputStream os = new FileOutputStream(target)){
            try (OutputStreamWriter osw = new OutputStreamWriter(os, Charsets.UTF_8)){
                base.store(osw, null);
            }
        } catch (IOException ex) {
            LOGGER.catching(ex);
        }
    }
    
    public static Map<String, Object> toMap(Properties properties) {
        Set<String> propertyNames = properties.stringPropertyNames();
        Map<String, Object> map = new HashMap<>();
        for (String propertyName : propertyNames) {
            map.put(propertyName, properties.getProperty(propertyName));
        }
        return map;
    }

    /**
     * 获取字符型属性（默认值为空字符串）
     */
    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }

    /**
     * 获取字符型属性（可指定默认值）
     */
    public static String getString(Properties props, String key, String defaultValue) {
        String value = defaultValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取数值型属性（默认值为 0）
     */
    public static int getInt(Properties props, String key) {
        return getInt(props, key, 0);
    }

    // 获取数值型属性（可指定默认值）
    public static int getInt(Properties props, String key, int defaultValue) {
        int value = defaultValue;
        if (props.containsKey(key)) {
            value = CastUtils.castInt(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取布尔型属性（默认值为 false）
     */
    public static boolean getBoolean(Properties props, String key) {
        return getBoolean(props, key, false);
    }

    /**
     * 获取布尔型属性（可指定默认值）
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (props.containsKey(key)) {
            value = CastUtils.castBoolean(props.getProperty(key));
        }
        return value;
    }
}
