/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tabledeleter;

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
@FacesRenderer(rendererType = TableDeleter.DEFAULT_RENTER_TYPE, componentFamily = TableDeleter.DEFAULT_FAMILY)
public class TableDeleterRender extends CoreRenderer {
    
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
        TableDeleter td = (TableDeleter) component;
        String styleClass = StringHelper.isEmpty(td.getStyleClass()) ? TableDeleter.TABLE_DELETER_CLASS : TableDeleter.TABLE_DELETER_CLASS + " " + td.getStyleClass();
        encodeIcon(writer, styleClass, td.getStyle(), td.getTitle());
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
