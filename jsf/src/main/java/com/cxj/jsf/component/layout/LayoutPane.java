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
import org.omnifaces.util.Components;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;

/**
 *
 * @author Administrator
 */
@ResourceDependencies({
    @ResourceDependency(library = "cxj", name = "layout/layoutpane.js")
})
@FacesComponent(LayoutPane.COMPONENT_TYPE)
public class LayoutPane extends UINamingContainer implements Widget, ClientBehaviorHolder {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.layout.LayoutPane";
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_FAMILY = "com.cxj.jsf.component";
    public static final String DEFAULT_RENTER = "com.cxj.jsf.component.layout.LayoutPaneRender";

    protected enum PropertyKeys {

        style, styleClass, widgetVar, options, position,
        eastSubPane, westSubPane, northSubPane, southSubPane, centerSubPane,
        contentClass, contentStyle, headerClass, headerStyle, footerClass, footerStyle;

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

    public LayoutPane() {
        setRendererType(DEFAULT_RENTER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
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

    public Object getOptions() {
        return getStateHelper().eval(PropertyKeys.options, null);
    }

    public void setOptions(Object options) {
        getStateHelper().put(PropertyKeys.options, options);
    }

    // position "north" | "south" | "west" | "east" | "center"
    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, "center");
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public String getEastSubPane() {
        return (String) getStateHelper().eval(PropertyKeys.eastSubPane, "#");
    }

    public void setEastSubPane(String pane) {
        getStateHelper().put(PropertyKeys.eastSubPane, pane);
    }

    public String getWestSubPane() {
        return (String) getStateHelper().eval(PropertyKeys.westSubPane, "#");
    }

    public void setWestSubPane(String pane) {
        getStateHelper().put(PropertyKeys.westSubPane, pane);
    }

    public String getNorthSubPane() {
        return (String) getStateHelper().eval(PropertyKeys.northSubPane, "#");
    }

    public void setNorthSubPane(String pane) {
        getStateHelper().put(PropertyKeys.northSubPane, pane);
    }

    public String getSouthSubPane() {
        return (String) getStateHelper().eval(PropertyKeys.southSubPane, "#");
    }

    public void setSouthSubPane(String pane) {
        getStateHelper().put(PropertyKeys.southSubPane, pane);
    }

    public String getCenterSubPane() {
        return (String) getStateHelper().eval(PropertyKeys.centerSubPane, "#");
    }

    public void setCenterSubPane(String pane) {
        getStateHelper().put(PropertyKeys.centerSubPane, pane);
    }

    public String getContentClass() {
        return (String) getStateHelper().eval(PropertyKeys.contentClass, "ui-layout-content");
    }

    public void setContentClass(String contentSelector) {
        getStateHelper().put(PropertyKeys.contentClass, contentSelector);
    }

    public String getContentStyle() {
        return (String) getStateHelper().eval(PropertyKeys.contentStyle);
    }

    public void setContentStyle(String contentStyle) {
        getStateHelper().put(PropertyKeys.contentStyle, contentStyle);
    }

    public String getHeaderClass() {
        return (String) getStateHelper().eval(PropertyKeys.headerClass, "ui-layout-header");
    }

    public void setHeaderClass(String headerClass) {
        getStateHelper().put(PropertyKeys.headerClass, headerClass);
    }

    public String getHeaderStyle() {
        return (String) getStateHelper().eval(PropertyKeys.headerStyle);
    }

    public void setHeaderStyle(String headerStyle) {
        getStateHelper().put(PropertyKeys.headerStyle, headerStyle);
    }

    public String getFooterClass() {
        return (String) getStateHelper().eval(PropertyKeys.footerClass, "ui-layout-footer");
    }

    public void setFooterClass(String footerClass) {
        getStateHelper().put(PropertyKeys.footerClass, footerClass);
    }

    public String getFooterStyle() {
        return (String) getStateHelper().eval(PropertyKeys.footerStyle);
    }

    public void setFooterStyle(String footerStyle) {
        getStateHelper().put(PropertyKeys.footerStyle, footerStyle);
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

    public LayoutPane findPane(Layout.Position position) {
        String paneId = "#";
        switch (position) {
            case west:
                paneId = getWestSubPane();
                break;
            case east:
                paneId = getEastSubPane();
                break;
            case north:
                paneId = getNorthSubPane();
                break;
            case south:
                paneId = getSouthSubPane();
                break;
            case center:
                paneId = getCenterSubPane();
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

    public boolean hasSubPane(Layout.Position position) {
        return findPane(position) != null;
    }

    public boolean hasRenderedSubPane(Layout.Position position) {
        LayoutPane subPane = findPane(position);
        return subPane != null && subPane.isRendered();
    }

    public boolean hasSubPane() {
        return hasSubPane(Layout.Position.east)
                || hasSubPane(Layout.Position.south)
                || hasSubPane(Layout.Position.west)
                || hasSubPane(Layout.Position.north)
                || hasSubPane(Layout.Position.center);
    }

    public boolean hasRenderedSubPane() {
        return hasRenderedSubPane(Layout.Position.east)
                || hasRenderedSubPane(Layout.Position.south)
                || hasRenderedSubPane(Layout.Position.west)
                || hasRenderedSubPane(Layout.Position.north)
                || hasRenderedSubPane(Layout.Position.center);
    }

    protected boolean validateParentPane(LayoutPane pane) {
        return pane.findPane(Layout.Position.valueOf(getPosition())) == this;
    }

    public boolean isSubPane() {
        LayoutPane pane = Components.getClosestParent(this, LayoutPane.class);
        if (pane == null) {
            return false;
        }
        if (validateParentPane(pane)) {
            return true;
        } else {
            throw new IllegalArgumentException("Invalid parent pane: " + pane.getClientId());
        }
    }

    public LayoutPane getParentPane() {
        LayoutPane pane = Components.getClosestParent(this, LayoutPane.class);
        if (pane == null) {
            return null;
        }
        if (validateParentPane(pane)) {
            return pane;
        } else {
            throw new IllegalArgumentException("Invalid parent pane: " + pane.getClientId());
        }
    }

    public Layout getLayout() {
        return Components.getClosestParent(this, Layout.class);
    }

    public UIComponent getContainer() {
        LayoutPane parentPane = getParentPane();
        if (parentPane != null) {
            return parentPane;
        }
        return getLayout();
    }

    public String getContentId() {
        return getClientId() + "_content";
    }

    public String getHeaderId() {
        return getClientId() + "_header";
    }

    public String getFooterId() {
        return getClientId() + "_footer";
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
