/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.layout;

import com.cxj.jsf.renderkit.BaseRender;
import com.cxj.utility.StringHelper;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.kopitubruk.util.json.JSONUtil;
import org.omnifaces.util.Components;
import org.primefaces.component.api.Widget;
import org.primefaces.util.WidgetBuilder;

/**
 *
 * @author Administrator
 */
@FacesRenderer(rendererType = LayoutPane.DEFAULT_RENTER, componentFamily = LayoutPane.COMPONENT_FAMILY)
public class LayoutPaneRender extends BaseRender {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        LayoutPane pane = (LayoutPane) component;
        String id = pane.getClientId(context);
        writer.startElement("div", null);
        writer.writeAttribute("id", id, null);
        String sytleClass = "ui-layout-" + pane.getPosition();
        if (!StringHelper.isEmpty(pane.getStyleClass())) {
            sytleClass += " " + pane.getStyleClass();
        }
        writer.writeAttribute("class", sytleClass, null);
        if (!StringHelper.isEmpty(pane.getStyle())) {
            writer.writeAttribute("style", pane.getStyle(), null);
        }
        encodeScript(context, pane);
        UIComponent header = component.getFacet("header");
        if (header != null && header.isRendered()) {
            writer.startElement("div", null);
            writer.writeAttribute("id", pane.getHeaderId(), null);
            String headerSytleClass = "ui-layout-header";
            if (!StringHelper.isEmpty(pane.getHeaderClass())) {
                headerSytleClass += " " + pane.getHeaderClass();
            }
            writer.writeAttribute("class", headerSytleClass, null);
            if (!StringHelper.isEmpty(pane.getHeaderStyle())) {
                writer.writeAttribute("style", pane.getHeaderStyle(), null);
            }
            header.encodeAll(context);
            writer.endElement("div");
        }
        writer.startElement("div", null);
        writer.writeAttribute("id", pane.getContentId(), null);
        String contentStyleClass = "ui-layout-content";
        if (!StringHelper.isEmpty(pane.getContentClass())) {
            contentStyleClass += " " + pane.getContentClass();
        }
        writer.writeAttribute("class", contentStyleClass, null);
        if (!StringHelper.isEmpty(pane.getContentStyle())) {
            writer.writeAttribute("style", pane.getContentStyle(), null);
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        LayoutPane pane = (LayoutPane) component;
        if (pane.hasRenderedSubPane()) {
            encodeSubPane(context, pane, Layout.Position.east);
            encodeSubPane(context, pane, Layout.Position.south);
            encodeSubPane(context, pane, Layout.Position.north);
            encodeSubPane(context, pane, Layout.Position.west);
            encodeSubPane(context, pane, Layout.Position.center);
        } else {
            super.encodeChildren(context, component);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        LayoutPane pane = (LayoutPane) component;
        String id = pane.getClientId(context);
        writer.endElement("div");
        UIComponent footer = pane.getFacet("footer");
        if (footer != null && footer.isRendered()) {
            writer.startElement("div", null);
            writer.writeAttribute("id", id + "_footer", null);
            String footerStyleClass = "ui-layout-footer";
            if (!StringHelper.isEmpty(pane.getFooterClass())) {
                footerStyleClass += " " + pane.getFooterClass();
            }
            writer.writeAttribute("class", footerStyleClass, null);
            if (!StringHelper.isEmpty(pane.getFooterStyle())) {
                writer.writeAttribute("style", pane.getFooterStyle(), null);
            }
            footer.encodeAll(context);
            writer.endElement("div");
        }
        writer.endElement("div");
    }

    protected void encodeSubPane(FacesContext context, LayoutPane pane, Layout.Position position) throws IOException {
        if (!pane.hasRenderedSubPane(position)) {
            return;
        }
        LayoutPane subPane = pane.findPane(position);
        UIForm form = Components.getClosestParent(subPane, UIForm.class);
        if (form != null && Components.getClosestParent(form, LayoutPane.class) == pane) {
            form.encodeAll(context);
        } else {
            subPane.encodeAll(context);
        }
    }

    protected void encodeScript(FacesContext context, LayoutPane pane) throws IOException {
        String clientId = pane.getClientId();
        String widgetClass = "LayoutPaneEx";
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady(widgetClass, pane.resolveWidgetVar(), clientId);
        wb.attr("position", pane.getPosition());
        wb.attr("parentWidget", ((Widget) pane.getContainer()).resolveWidgetVar());
        wb.attr("hasSubPane", pane.hasRenderedSubPane());
        wb.nativeAttr("userOptions", parseAttribute(pane.getOptions()));
        wb.nativeAttr("builtinOptions", JSONUtil.toJSON(pane.resolveOptions()));
        encodeClientBehaviors(context, pane);
        wb.finish();
    }
}
