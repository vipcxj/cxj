/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.layout;

import com.cxj.jsf.renderkit.BaseRender;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.kopitubruk.util.json.JSONUtil;
import org.omnifaces.util.Components;
import org.primefaces.util.WidgetBuilder;

/**
 *
 * @author Administrator
 */
@FacesRenderer(rendererType = Layout.DEFAULT_RENTER, componentFamily = Layout.COMPONENT_FAMILY)
public class LayoutRender extends BaseRender {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;
        String id = layout.getClientId(context);
        encodeScript(context, layout);
        if (!layout.isFullPage()) {
            writer.startElement("div", layout);
            writer.writeAttribute("id", id, "id");
            if (layout.getStyle() != null) {
                writer.writeAttribute("style", layout.getStyle(), "style");
            }
            if (layout.getStyleClass() != null) {
                writer.writeAttribute("class", layout.getStyleClass(), "styleClass");
            }
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        Layout layout = (Layout) component;
        encodePane(context, layout, Layout.Position.east);
        encodePane(context, layout, Layout.Position.south);
        encodePane(context, layout, Layout.Position.north);
        encodePane(context, layout, Layout.Position.west);
        encodePane(context, layout, Layout.Position.center);
    }

    protected void encodePane(FacesContext context, Layout layout, Layout.Position position) throws IOException {
        if (!layout.hasRenderedPane(position)) {
            return;
        }
        LayoutPane pane = layout.findPane(position);
        UIForm form = Components.getClosestParent(pane, UIForm.class);
        if (form != null && Components.getClosestParent(form, Layout.class) == layout) {
            form.encodeAll(context);
        } else {
            pane.encodeAll(context);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;
        if (!layout.isFullPage()) {
            writer.endElement("div");
        }
    }

    protected void encodeScript(FacesContext context, Layout layout) throws IOException {
        String clientId = layout.getClientId();
        String widgetClass = "LayoutEx";
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady(widgetClass, layout.resolveWidgetVar(), clientId);
        wb.attr("fullPage", layout.isFullPage());
        wb.nativeAttr("userOptions", parseAttribute(layout.getOptions()));
        wb.nativeAttr("builtinOptions", JSONUtil.toJSON(layout.resolveOptions()));
        encodeClientBehaviors(context, layout);
        wb.finish();
    }
}
