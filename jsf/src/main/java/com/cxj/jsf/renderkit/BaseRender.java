/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.renderkit;

import com.cxj.utility.JsonUtils;
import org.primefaces.renderkit.CoreRenderer;

/**
 *
 * @author Administrator
 */
public class BaseRender extends CoreRenderer {

    protected String parseAttribute(Object obj) {
        if (obj instanceof String) {
            String sObj = (String) obj;
            if ((sObj.startsWith("{") && sObj.endsWith("}")) || (sObj.startsWith("[") && sObj.endsWith("]"))) {
                return sObj;
            } else {
                return "\"" + sObj + "\"";
            }
        } else {
            return JsonUtils.toJson(obj);
        }
    }
}
