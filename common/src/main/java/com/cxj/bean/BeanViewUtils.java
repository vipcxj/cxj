/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.cxj.utility.ReflectHelper;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Administrator
 */
public class BeanViewUtils {

    /**
     *
     * @param bean
     * @param name 不支持下标和嵌套
     * @param force
     * @return
     */
    public static Object getProperty(BeanView bean, String name, boolean force) {
        if (!force) {
            return bean.getProperty(name);
        }
        BeanViewInfo info = bean.getBeanInfo();
        String nName = info.restoreFrom(name);
        if (info.isValidNativeProperty(nName)) {
            if (bean.getExtendProperties().containsKey(nName)) {
                return bean.getExtendProperties().get(nName);
            }
            if (bean.isIndexed() || bean.isMapBased()) {
                return bean.getProperty(nName);
            }
            try {
                return BeanUtils.getProperty(bean.unWrapped(), nName);
            } catch (InvocationTargetException ex) {
                throw new IllegalArgumentException(ex);
            } catch (IllegalAccessException | NoSuchMethodException ex) {
                try {
                    return ReflectHelper.getField(bean.unWrapped(), nName);
                } catch (NoSuchFieldException ex1) {
                    throw new IllegalArgumentException(ex1);
                }
            }
        } else {
            throw new IllegalArgumentException(new NoSuchFieldException(name));
        }
    }
}
