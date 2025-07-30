/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tableinserter;

import com.cxj.jsf.component.tablemenu.TableMenu;
import com.cxj.utility.StringHelper;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.omnifaces.util.Components;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;

/**
 *
 * @author cxj
 */
@FacesRenderer(rendererType = TableInserter.DEFAULT_RENTER_TYPE, componentFamily = TableInserter.DEFAULT_FAMILY)
public class TableInserterRender extends CoreRenderer {

    private void validate(UIComponent component) {
        Components.validateHasNoChildren(component);
        Components.validateHasDirectParent(component, TableMenu.class);
        Components.validateHasParent(component, DataTable.class);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        validate(component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TableInserter ti = (TableInserter) component;
        String sytleClass = StringHelper.isEmpty(ti.getStyleClass()) ? TableInserter.TABLE_INSERTER_CLASS : TableInserter.TABLE_INSERTER_CLASS + " " + ti.getStyleClass();
        encodeIcon(writer, sytleClass, ti.getStyle(), ti.getTitle());
    }

    protected void encodeIcon(ResponseWriter writer, String styleClass, String style, String title) throws IOException {
        writer.startElement("span", null);
        if (!StringHelper.isEmpty(title)) {
            writer.writeAttribute("title", title, null);
        }
        if (!StringHelper.isEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass, null);
        }
        if (!StringHelper.isEmpty(style)) {
            writer.writeAttribute("style", style, null);
        }
        writer.endElement("span");
    }
}
