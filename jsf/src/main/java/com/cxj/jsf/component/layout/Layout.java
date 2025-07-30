/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.layout;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;

/**
 *
 * @author Administrator
 */
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "components.css"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "cxj", name = "js/json/json2.js"),
    @ResourceDependency(library = "cxj", name = "layout/layout.css"),
    @ResourceDependency(library = "cxj", name = "layout/library/jquery.layout_and_plugins.js"),
    @ResourceDependency(library = "cxj", name = "layout/layout.js")
})
@FacesComponent(Layout.COMPONENT_TYPE)
public class Layout extends UINamingContainer implements Widget, ClientBehaviorHolder {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.layout.Layout";
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_FAMILY = "com.cxj.jsf.component";
    public static final String DEFAULT_RENTER = "com.cxj.jsf.component.layout.LayoutRender";

    public static final String POSITION_SEPARATOR = "_";
    public static final String STYLE_CLASS_PANE = "ui-widget-content ui-corner-all";
    public static final String STYLE_CLASS_PANE_WITH_SUBPANES = "ui-corner-all ui-layout-pane-withsubpanes";
    public static final String STYLE_CLASS_PANE_HEADER = "ui-widget-header ui-corner-top ui-layout-pane-header";
    public static final String STYLE_CLASS_PANE_CONTENT = "ui-layout-pane-content";

    protected enum PropertyKeys {

        style, styleClass, widgetVar, fullPage, options,
        eastPane, westPane, northPane, southPane, centerPane;

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

    public enum Position {

        west, east, north, south, center;
    }

    public Layout() {
        setRendererType(DEFAULT_RENTER);
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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isFullPage() {
        return (boolean) getStateHelper().eval(PropertyKeys.fullPage, true);
    }

    public void setFullPage(boolean fullPage) {
        getStateHelper().put(PropertyKeys.fullPage, fullPage);
    }

    public Object getOptions() {
        return getStateHelper().eval(PropertyKeys.options, null);
    }

    public void setOptions(Object options) {
        getStateHelper().put(PropertyKeys.options, options);
    }

    public String getEastPane() {
        return (String) getStateHelper().eval(PropertyKeys.eastPane, "#");
    }

    public void setEastPane(String pane) {
        getStateHelper().put(PropertyKeys.eastPane, pane);
    }

    public String getWestPane() {
        return (String) getStateHelper().eval(PropertyKeys.westPane, "#");
    }

    public void setWestPane(String pane) {
        getStateHelper().put(PropertyKeys.westPane, pane);
    }

    public String getNorthPane() {
        return (String) getStateHelper().eval(PropertyKeys.northPane, "#");
    }

    public void setNorthPane(String pane) {
        getStateHelper().put(PropertyKeys.northPane, pane);
    }

    public String getSouthPane() {
        return (String) getStateHelper().eval(PropertyKeys.southPane, "#");
    }

    public void setSouthPane(String pane) {
        getStateHelper().put(PropertyKeys.southPane, pane);
    }

    public String getCenterPane() {
        return (String) getStateHelper().eval(PropertyKeys.centerPane, "#");
    }

    public void setCenterPane(String pane) {
        getStateHelper().put(PropertyKeys.centerPane, pane);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public String resolveWidgetVar() {
        FacesContext context = FacesContext.getCurrentInstance();
        String userWidgetVar = (String) getAttributes().get(PropertyKeys.widgetVar.toString());
        if (userWidgetVar != null) {
            return userWidgetVar;
        }
        return "widget_" + getClientId(context).replaceAll("-|"
                + UINamingContainer.getSeparatorChar(context), "_");
    }

    public LayoutPane findPane(Position position) {
        String paneId = "#";
        switch (position) {
            case west:
                paneId = getWestPane();
                break;
            case east:
                paneId = getEastPane();
                break;
            case north:
                paneId = getNorthPane();
                break;
            case south:
                paneId = getSouthPane();
                break;
            case center:
                paneId = getCenterPane();
                break;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
        if (!"#".equals(paneId)) {
            UIComponent pane = findComponent(paneId);
            if (pane instanceof LayoutPane) {
                return (LayoutPane) pane;
            } else {
                throw new IllegalArgumentException("Invalid pane id: " + paneId + "! It refer to a invalid layout pane component!");
            }
        }
        for (UIComponent child : getChildren()) {
            if (child instanceof LayoutPane) {
                LayoutPane pane = (LayoutPane) child;
                if (position.toString().equals(pane.getPosition())) {
                    return pane;
                }
            }
            if (child instanceof UIForm) {
                UIForm form = (UIForm) child;
                LayoutPane pane = form.getChildren().stream().filter(comp -> comp instanceof LayoutPane)
                        .map(comp -> (LayoutPane) comp)
                        .filter(pn -> position.toString().equals(pn.getPosition()))
                        .findFirst().orElse(null);
                if (pane != null) {
                    return pane;
                }
            }
        }
        return null;
    }

    public boolean hasPane(Position position) {
        return findPane(position) != null;
    }

    public boolean hasRenderedPane(Position position) {
        LayoutPane pane = findPane(position);
        if (pane == null) {
            return false;
        }
        return pane.isRendered();
    }

    /**
     * 和组件的options属性无关，返回组件的自治配置
     *
     * @return
     */
    public LayoutOptions resolveOptions() {
        LayoutOptions options = new LayoutOptions();
        LayoutPane east = findPane(Layout.Position.east);
        if (east != null && east.isRendered()) {
            options.createEast().setOption("paneSelector", ComponentUtils.escapeJQueryId(east.getClientId()));
        }
        LayoutPane south = findPane(Layout.Position.south);
        if (south != null && south.isRendered()) {
            options.createSouth().setOption("paneSelector", ComponentUtils.escapeJQueryId(south.getClientId()));
        }
        LayoutPane west = findPane(Layout.Position.west);
        if (west != null && west.isRendered()) {
            options.createWest().setOption("paneSelector", ComponentUtils.escapeJQueryId(west.getClientId()));
        }
        LayoutPane north = findPane(Layout.Position.north);
        if (north != null && north.isRendered()) {
            options.createNorth().setOption("paneSelector", ComponentUtils.escapeJQueryId(north.getClientId()));
        }
        LayoutPane center = findPane(Layout.Position.center);
        if (center != null && center.isRendered()) {
            options.createCenter().setOption("paneSelector", ComponentUtils.escapeJQueryId(center.getClientId()));
        }
        return options;
    }
}
