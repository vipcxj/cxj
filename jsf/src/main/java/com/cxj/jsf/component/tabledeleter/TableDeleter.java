/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tabledeleter;

import javax.faces.application.ResourceDependencies;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;

/**
 *
 * @author cxj
 */
@ResourceDependencies({})
@FacesComponent(TableDeleter.COMPONENT_TYPE)
public class TableDeleter extends UIComponentBase {

    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.tabledeleter.TableDeleter";
    public static final String DEFAULT_FAMILY = "com.cxj.jsf.component";
    public static final String DEFAULT_RENTER_TYPE = "com.cxj.jsf.component.tabledeleter.TableDeleterRender";
    protected static final String TABLE_DELETER_CLASS = "ui-icon ui-icon-minus ui-table-deleter";

    protected enum PropertyKeys {

        style, styleClass, title;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public TableDeleter() {
        setRendererType(DEFAULT_RENTER_TYPE);
    }

    @Override
    public String getFamily() {
        return DEFAULT_FAMILY;
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

    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title);
    }

    public void setTitle(String _title) {
        getStateHelper().put(PropertyKeys.title, _title);
    }

}
