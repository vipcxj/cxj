/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tablemenu;

import com.cxj.jsf.component.tabledeleter.TableDeleterEvent;
import com.cxj.jsf.component.tableinserter.TableInserterEvent;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.faces.application.Application;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreValidateEvent;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Components;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.Widget;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

/**
 *
 * @author cxj
 */
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "primefaces.css"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "primefaces.js"),
    @ResourceDependency(library = "cxj", name = "tablemenu/tablemenu.css"),
    @ResourceDependency(library = "cxj", name = "tablemenu/tablemenu.js")
})
@FacesComponent(TableMenu.COMPONENT_TYPE)
public class TableMenu extends UIComponentBase implements Widget, ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.tablemenu.TableMenu";
    public static final String DEFAULT_FAMILY = "com.cxj.jsf.component";
    public static final String DEFAULT_RENTER_TYPE = "com.cxj.jsf.component.tablemenu.TableMenuRender";

    protected static final String TABLE_MENU_CLASS = "ui-table-menu ui-helper-clearfix";
    private static final Collection<String> EVENT_NAMES = ImmutableList.of("insertInit", "insertInvoke", "insertCancel", "delete");

    protected enum PropertyKeys {

        style, styleClass, widgetVar, position, itemType, item;

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

    public TableMenu() {
        setRendererType(DEFAULT_RENTER_TYPE);
    }

    @Override
    public String getFamily() {
        return DEFAULT_FAMILY;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    protected boolean isInsertRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId() + "_rowInsertAction");
    }

    protected boolean isInsertInitRequest(FacesContext context) {
        String action = context.getExternalContext().getRequestParameterMap().get(getClientId() + "_rowInsertAction");
        return Objects.equals(action, "init");
    }

    protected boolean isInsertSaveRequest(FacesContext context) {
        String action = context.getExternalContext().getRequestParameterMap().get(getClientId() + "_rowInsertAction");
        return Objects.equals(action, "save");
    }

    protected boolean isInsertCancelRequest(FacesContext context) {
        String action = context.getExternalContext().getRequestParameterMap().get(getClientId() + "_rowInsertAction");
        return Objects.equals(action, "cancel");
    }

    protected boolean isDeleteRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId() + "_rowDeleteAction");
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, null);

        try {
            try {
                if (isInsertInitRequest(context)) {
                    createItem();
                } else if (isInsertSaveRequest(context)) {
                    DataTable dt = getDataTable();
                    dt.setRowIndex(-1);
                    Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                    requestMap.put(dt.getVar(), getItem());
                    for (UIColumn column : dt.getColumns()) {
                        CellEditor cellEditor = column.getCellEditor();
                        if (cellEditor != null) {
                            UIComponent facet = cellEditor.getFacet("input");
                            facet.processDecodes(context);
                        }
                    }
                    requestMap.remove(dt.getVar());
                }
            } catch (RuntimeException e) {
                context.renderResponse();
                throw e;
            }
            // Process all facets and children of this component
            Iterator kids = getFacetsAndChildren();
            while (kids.hasNext()) {
                UIComponent kid = (UIComponent) kids.next();
                kid.processDecodes(context);
            }

            // Process this component itself
            try {
                decode(context);
            } catch (RuntimeException e) {
                context.renderResponse();
                throw e;
            }
        } finally {
            popComponentFromEL(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, null);

        try {
            DataTable dt = getDataTable();
            if (isInsertSaveRequest(context)) {
                dt.setRowIndex(-1);
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                requestMap.put(dt.getVar(), getItem());
                for (UIColumn column : dt.getColumns()) {
                    CellEditor cellEditor = column.getCellEditor();
                    if (cellEditor != null) {
                        UIComponent facet = cellEditor.getFacet("input");
                        facet.processValidators(context);
                    }
                }
                requestMap.remove(dt.getVar());
            }
            Application app = context.getApplication();
            app.publishEvent(context, PreValidateEvent.class, this);
            // Process all the facets and children of this component
            Iterator kids = getFacetsAndChildren();
            while (kids.hasNext()) {
                UIComponent kid = (UIComponent) kids.next();
                kid.processValidators(context);
            }
            app.publishEvent(context, PostValidateEvent.class, this);
        } finally {
            popComponentFromEL(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }
        pushComponentToEL(context, null);
        try {
            // Process all facets and children of this component
            Iterator kids = getFacetsAndChildren();
            while (kids.hasNext()) {
                UIComponent kid = (UIComponent) kids.next();
                kid.processUpdates(context);
            }
            DataTable dt = getDataTable();
            if (isInsertSaveRequest(context)) {
                dt.setRowIndex(-1);
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                requestMap.put(dt.getVar(), getItem());
                for (UIColumn column : dt.getColumns()) {
                    CellEditor cellEditor = column.getCellEditor();
                    if (cellEditor != null) {
                        UIComponent facet = cellEditor.getFacet("input");
                        facet.processUpdates(context);
                    }
                }
                requestMap.remove(dt.getVar());
                Object dtValue = dt.getValue();
                if (dtValue == null) {
                    throw new NullPointerException("The value of datatable is null!");
                }
                if (dtValue instanceof Collection) {
                    ((Collection) dtValue).add(getItem());
                } else {
                    throw new IllegalArgumentException("To support insert operation, the value type of datatable must be collection!");
                }
            } else if (isDeleteRequest(context)) {
                Object dtValue = dt.getValue();
                if (dtValue instanceof Collection) {
                    for (Object selection : getSelectionItems(context)) {
                        ((Collection) dtValue).remove(selection);
                    }
                } else {
                    throw new IllegalArgumentException("To support delete operation, the value type of datatable must be collection!");
                }
            }
        } finally {
            popComponentFromEL(context);
        }
    }

    @Override
    public void queueEvent(@Nonnull FacesEvent event) {
        FacesContext context = getFacesContext();
        if (isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            if (eventName.equals("insertInit") || eventName.equals("insertInvoke") || eventName.equals("insertCancel")) {
                wrapperEvent = new TableInserterEvent(this, behaviorEvent.getBehavior(), getItem());
            } else if (eventName.equals("delete")) {
                wrapperEvent = new TableDeleterEvent(this, behaviorEvent.getBehavior(), getSelectionItems(context));
            } else {
                String msg_support_events = "TableMenu支持的ajax事件名如下：" + String.join(",", getEventNames()) + ".";
                throw new IllegalArgumentException("不支持的ajax事件名称：" + eventName + ". " + msg_support_events);
            }
            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            super.queueEvent(wrapperEvent);
        } else {
            super.queueEvent(event);
        }
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

    public DataTable getDataTable() {
        return Components.getClosestParent(this, DataTable.class);
    }

    public List<Object> getSelectionItems(FacesContext context) {
        String preSelection = context.getExternalContext().getRequestParameterMap().get(getClientId() + "_rowDeleteAction");
        if (StringUtils.isEmpty(preSelection)) {
            return Collections.emptyList();
        }
        String[] selectedRowKeys = preSelection.split(",");
        DataTable dt = getDataTable();
        List<Object> selectItems = new ArrayList<>();
        for (Object selectedRowKey : selectedRowKeys) {
            selectItems.add(dt.getRowData(String.valueOf(selectedRowKey)));
        }
        return selectItems;
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

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, "left");
    }

    public void setPosition(String _position) {
        getStateHelper().put(PropertyKeys.position, _position);
    }

    public String getItemType() {
        return (String) getStateHelper().eval(PropertyKeys.itemType);
    }

    public void setItemType(String itemType) {
        getStateHelper().put(PropertyKeys.itemType, itemType);
    }

    public Object getItem() {
        return getStateHelper().eval(PropertyKeys.item);
    }

    public void setItem(Object item) {
        getStateHelper().put(PropertyKeys.item, item);
    }

    public boolean isRequestSource(FacesContext context) {
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        return partialSource != null && this.getClientId(context).equals(partialSource);
    }

    public void createItem() {
        if (getItemType() == null) {
            return;
        }
        try {
            Class itemType = Class.forName(getItemType());
            setItem(itemType.newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
