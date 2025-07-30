/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.utils;

import java.util.UUID;
import javax.faces.context.FacesContext;

/**
 *
 * @author Administrator
 */
public class ComponentUtils {

    public static String generateComponentId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //String id = UIComponent.getCurrentComponent(facesContext).getClientId();
        String id = UUID.randomUUID().toString();
        return '_' + id;
    }
}
