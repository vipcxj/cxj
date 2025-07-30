/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.div;

import com.cxj.utility.StringHelper;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

/**
 *
 * @author Administrator
 */
@FacesRenderer(rendererType = Div.DIV_DEFAULT_RENTER, componentFamily = Div.DIV_COMPONENT_FAMILY)
public class DivRender extends Renderer {

    private boolean superJSF22() {
        Boolean result = superJSF22ByVersion();
        if (result == null) {
            return superJSF22ByClass();
        } else {
            return result;
        }
    }

    private boolean superJSF22ByClass() {
        try {
            Class.forName("javax.faces.flow.Flow");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private Boolean superJSF22ByVersion() {
        String version = FacesContext.class.getPackage().getImplementationVersion();

        if (version != null) {
            Pattern p = Pattern.compile("(\\d+)\\.(\\d+)");
            Matcher matcher = p.matcher(version);
            if (matcher.lookingAt()) {
                try {
                    int majarVersion = Integer.parseInt(matcher.group(1));
                    int minorVersion = Integer.parseInt(matcher.group(2));
                    if (majarVersion > 2) {
                        return true;
                    } else if (majarVersion == 2) {
                        return minorVersion >= 2;
                    } else {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void renderPassThroughAttributes(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String, Object> passthroughAttributes = component.getPassThroughAttributes(false);

        if (passthroughAttributes != null && !passthroughAttributes.isEmpty()) {
            for (Map.Entry<String, Object> attribute : passthroughAttributes.entrySet()) {

                Object attributeValue = attribute.getValue();
                if (attributeValue != null) {
                    String value = null;

                    if (attributeValue instanceof ValueExpression) {
                        Object expressionValue = ((ValueExpression) attributeValue).getValue(context.getELContext());
                        if (expressionValue != null) {
                            value = expressionValue.toString();
                        }
                    } else {
                        value = attributeValue.toString();
                    }

                    if (value != null) {
                        writer.writeAttribute(attribute.getKey(), value, null);
                    }
                }
            }
        }
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Div div = (Div) component;
        writer.startElement("div", div);
        String style = div.getStyle();
        if (!StringHelper.isEmpty(style)) {
            writer.writeAttribute("style", style, "sytle");
        }
        String styleClass = div.getStyleClass();
        if (!StringHelper.isEmpty(styleClass)) {
            writer.writeAttribute("styleClass", styleClass, "styleClass");
        }
        if (superJSF22()) {
            renderPassThroughAttributes(context, component);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("div");
    }

}
