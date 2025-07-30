/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.div;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

/**
 *
 * @author Administrator
 */
@FacesComponent(Div.DIV_COMPONENT_TYPE)
public class Div extends UINamingContainer {

    public static final String DIV_COMPONENT_TYPE = "com.cxj.jsf.component.div.Div";
    public static final String DIV_COMPONENT_FAMILY = "com.cxj.jsf.component";
    public static final String DIV_DEFAULT_RENTER = "com.cxj.jsf.component.div.DivRender";

    public Div() {
        setRendererType(DIV_DEFAULT_RENTER);
    }

    @Override
    public String getFamily() {
        return DIV_COMPONENT_FAMILY;
    }

    protected enum PropertyKeys {

        style, styleClass
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }
}
