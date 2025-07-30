/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.util.regex.Matcher;

/**
 *
 * @author Administrator
 */
public interface ReplaceCallBack {

    /**
     * 将text转化为特定的字串返回
     *
     * @param text 指定的字符串
     * @param index 替换的次序
     * @param matcher Matcher对象
     * @return
     */
    public String replace(String text, int index, Matcher matcher);
}
