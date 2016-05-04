/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 *
 * @author Administrator
 */
public class StringHelper {

    /**
     * 将普通字符串转换为兼容的正则表达式
     *
     * @param s 普通字符串
     * @param isInArray 是否将要放在正则表达式"[]"内部
     * @return 兼容的正则表达式
     */
    public static String toRegex(String s, boolean isInArray) {
        s = s.replaceAll("\\\\", Matcher.quoteReplacement("\\\\"));
        s = s.replaceAll("\\^", "\\\\^");
        s = s.replaceAll("\\$", Matcher.quoteReplacement("\\$"));
        s = s.replaceAll("\\(", "\\\\(");
        s = s.replaceAll("\\)", "\\\\)");
        s = s.replaceAll("\\*", "\\\\*");
        s = s.replaceAll("\\+", "\\\\+");
        s = s.replaceAll("\\?", "\\\\?");
        s = s.replaceAll("\\.", "\\\\.");
        s = s.replaceAll("\\[", "\\\\[");
        if (isInArray) {
            s = s.replaceAll("]", "\\\\]");
        }
        s = s.replaceAll("\\{", "\\\\{");
        return s.replaceAll("\\|", "\\\\|");
    }

    /**
     * 将普通字符串转换为兼容的正则表达式，同
     * {@link #toRegex(java.lang.String, boolean) toRegex(s, false)}
     *
     * @param s 普通字符串
     * @return 兼容的正则表达式
     */
    public static String toRegex(String s) {
        return toRegex(s, false);
    }

    /**
     * sql语法中的like操作符。"%"，"_"为通配符。"%"匹配任意数量个任意字符，"_"匹配单个任意字符
     *
     * @param str 匹配对象
     * @param patten 待匹配的模式，支持使用"%"，"_"通配符
     * @param escape 指定转义符，为null或空时不支持转义符
     * @return 是否符合like语义
     */
    public static boolean jpqlLike(String str, String patten, String escape) {
        patten = toRegex(patten);
        if (escape == null || escape.isEmpty()) {
            patten = patten.replaceAll("%", "[\\\\s\\\\S]*").replaceAll("_", "[\\\\s\\\\S]");
        } else {
            escape = toRegex(escape);
            patten = patten.replaceAll("([^(?:" + escape + ")])%", "$1[\\\\s\\\\S]*")
                    .replaceAll("([^(?:" + escape + ")])_", "$1[\\\\s\\\\S]")
                    .replaceAll("(" + escape + ")%", "%")
                    .replaceAll("(" + escape + ")_", "_");
        }
        return str.matches(patten);
    }

    public static boolean jpqlLike(String str, String patten) {
        return jpqlLike(str, patten, null);
    }

    public static List<Boolean> jpqlLike(List<String> strs, String patten, String escape) {
        return strs.stream().map(e -> e == null ? null : jpqlLike(e, patten, escape)).collect(Collectors.toList());
    }

    public static List<Boolean> jpqlLike(List<String> strs, String patten) {
        return jpqlLike(strs, patten, null);
    }
    
    public static String parseEscape(String input, boolean isSingle) {
        String quote = isSingle ? "'" : "\"";
        return input.replaceAll("\\\\\\\\", "\\").replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replaceAll("\\\\" + toRegex(quote), quote);
    }
    
    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
    
    public static boolean isEmptyString(Object input) {
        return input != null && input instanceof String && isEmpty((String) input);
    }
    
    public static boolean isNotEmptyString(Object input) {
        return input != null && input instanceof String && !isEmpty((String) input);
    }
}
