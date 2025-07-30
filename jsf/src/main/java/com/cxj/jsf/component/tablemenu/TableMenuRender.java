/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tablemenu;

import java.io.IOException;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.omnifaces.util.Components;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.visit.ResetInputVisitCallback;

/**
 *
 * @author cxj
 */
@FacesRenderer(rendererType = TableMenu.DEFAULT_RENTER_TYPE, componentFamily = TableMenu.DEFAULT_FAMILY)
public class TableMenuRender extends CoreRenderer {

    private void validate(UIComponent component) {
        Components.validateHasDirectParent(component, DataTable.class);
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        validate(component);
        ResponseWriter writer = context.getResponseWriter();
        TableMenu tm = (TableMenu) component;
        String style = tm.getStyle();
        String styleClass = tm.getStyleClass();
        styleClass = (styleClass == null) ? TableMenu.TABLE_MENU_CLASS : TableMenu.TABLE_MENU_CLASS + " " + styleClass;
        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TableMenu tm = (TableMenu) component;
        encodeScript(context, tm);
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-template-row", null);
        writer.writeAttribute("style", "display: none", null);
        writer.write("<!--\n");
        DataTable table = findTable(component);
        if (tm.isInsertCancelRequest(context)) {
            VisitContext visitContext = null;

            for (UIColumn column : table.getColumns()) {
                for (UIComponent grandkid : column.getChildren()) {
                    if (grandkid instanceof CellEditor) {
                        UIComponent inputFacet = grandkid.getFacet("input");

                        if (inputFacet instanceof EditableValueHolder) {
                            ((EditableValueHolder) inputFacet).resetValue();
                        } else {
                            if (visitContext == null) {
                                visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                            }
                            inputFacet.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
                        }
                    }
                }
            }
        }
        encodeTemplateRow(context, table, table.getRowCount(), 0, table.getColumnsCount());
        writer.write("\n-->");
        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, TableMenu tm) throws IOException {
        String clientId = tm.getClientId(context);
        String widgetClass = "TableMenu";
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady(widgetClass, tm.resolveWidgetVar(), clientId);
        //Behaviors
        encodeClientBehaviors(context, tm);
        wb.finish();
    }

    private DataTable findTable(UIComponent component) {
        return Components.getClosestParent(component, DataTable.class);
    }

    public boolean encodeTemplateRow(FacesContext context, DataTable table, int rowIndex, int columnStart, int columnEnd) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = table.isSelectionEnabled();
        List<UIColumn> columns = table.getColumns();

        String userRowStyleClass = table.getRowStyleClass();
        String rowStyleClass = rowIndex % 2 == 0 ? DataTable.ROW_CLASS + " " + DataTable.EVEN_ROW_CLASS : DataTable.ROW_CLASS + " " + DataTable.ODD_ROW_CLASS;
        if (selectionEnabled && !table.isDisabledSelection()) {
            rowStyleClass = rowStyleClass + " " + DataTable.SELECTABLE_ROW_CLASS;
        }

        if (table.isEditingRow()) {
            rowStyleClass = rowStyleClass + " " + DataTable.EDITING_ROW_CLASS;
        }

        if (userRowStyleClass != null) {
            rowStyleClass = rowStyleClass + " " + userRowStyleClass;
        }

        if (table.isExpandedRow()) {
            rowStyleClass = rowStyleClass + " " + DataTable.EXPANDED_ROW_CLASS;
        }

        writer.startElement("tr", null);
        writer.writeAttribute("data-ri", rowIndex, null);
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("role", "row", null);
        if (selectionEnabled) {
            writer.writeAttribute("aria-selected", "false", null);
        }

        for (int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);

            if (column instanceof Column) {
                encodeCell(context, table, column, table.getClientId(), false);
            } else if (column instanceof DynamicColumn) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyModel();

                encodeCell(context, table, dynamicColumn, null, false);
            }
        }

        writer.endElement("tr");

        return true;
    }

    protected void encodeCell(FacesContext context, DataTable table, UIColumn column, String clientId, boolean selected) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = column.getSelectionMode() != null;
        int priority = column.getPriority();
        String style = column.getStyle();
        String styleClass = selectionEnabled ? DataTable.SELECTION_COLUMN_CLASS : (column.getCellEditor() != null) ? DataTable.EDITABLE_COLUMN_CLASS : null;
        styleClass = (column.isSelectRow()) ? styleClass : (styleClass == null) ? DataTable.UNSELECTABLE_COLUMN_CLASS : styleClass + " " + DataTable.UNSELECTABLE_COLUMN_CLASS;
        styleClass = (column.isVisible()) ? styleClass : (styleClass == null) ? DataTable.HIDDEN_COLUMN_CLASS : styleClass + " " + DataTable.HIDDEN_COLUMN_CLASS;
        String userStyleClass = column.getStyleClass();
        styleClass = userStyleClass == null ? styleClass : (styleClass == null) ? userStyleClass : styleClass + " " + userStyleClass;

        if (priority > 0) {
            styleClass = (styleClass == null) ? "ui-column-p-" + priority : styleClass + " ui-column-p-" + priority;
        }

        int colspan = column.getColspan();
        int rowspan = column.getRowspan();

        writer.startElement("td", null);
        writer.writeAttribute("role", "gridcell", null);
        if (colspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }

        if (selectionEnabled) {
            encodeColumnSelection(context, table, clientId, column, selected);
        }
        column.renderChildren(context);
        writer.endElement("td");
    }

    protected void encodeColumnSelection(FacesContext context, DataTable table, String clientId, UIColumn column, boolean selected) throws IOException {
        String selectionMode = column.getSelectionMode();
        boolean disabled = table.isDisabledSelection();

        if (selectionMode.equalsIgnoreCase("single")) {
            encodeRadio(context, table, selected, disabled);
        } else if (selectionMode.equalsIgnoreCase("multiple")) {
            encodeCheckbox(context, table, selected, disabled, HTML.CHECKBOX_CLASS);
        } else {
            throw new FacesException("Invalid column selection mode:" + selectionMode);
        }
    }

    protected void encodeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (table.isNativeElements()) {
            encodeNativeRadio(context, table, checked, disabled);
        } else {
            String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
            String iconClass = checked ? HTML.RADIOBUTTON_CHECKED_ICON_CLASS : HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS;
            boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
            boxClass = checked ? boxClass + " ui-state-active" : boxClass;

            writer.startElement("div", null);
            writer.writeAttribute("class", HTML.RADIOBUTTON_CLASS, null);

            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
            encodeNativeRadio(context, table, checked, disabled);
            writer.endElement("div");

            writer.startElement("div", null);
            writer.writeAttribute("class", boxClass, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");

            writer.endElement("div");
            writer.endElement("div");
        }
    }

    protected void encodeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (table.isNativeElements()) {
            encodeNativeCheckbox(context, table, checked, disabled);
        } else {
            String boxClass = HTML.CHECKBOX_BOX_CLASS;
            boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
            boxClass = checked ? boxClass + " ui-state-active" : boxClass;
            String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, "styleClass");

            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
            encodeNativeCheckbox(context, table, checked, disabled);
            writer.endElement("div");

            writer.startElement("div", null);
            writer.writeAttribute("class", boxClass, null);
            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
            writer.endElement("div");

            writer.endElement("div");
        }
    }

    protected void encodeNativeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("name", table.getClientId(context) + "_checkbox", null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");
    }

    protected void encodeNativeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("name", table.getClientId(context) + "_radio", null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");
    }

}
