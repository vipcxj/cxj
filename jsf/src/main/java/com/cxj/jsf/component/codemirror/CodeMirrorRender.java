/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.codemirror;

import com.cxj.jsf.component.codemirror.feature.Feature;
import com.cxj.jsf.renderkit.BaseRender;
import com.cxj.utility.CollectionHelper;
import com.cxj.utility.StringHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

/**
 *
 * @author cxj
 */
@FacesRenderer(rendererType = CodeMirror.DEFAULT_RENTER, componentFamily = CodeMirror.COMPONENT_FAMILY)
public class CodeMirrorRender extends BaseRender {

    private static final String JSON_SIGN = "{|JSON|}";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        CodeMirror cm = (CodeMirror) component;
        if (cm.isReadOnly()) {
            return;
        }
        decodeBehaviors(context, cm);
        String clientId = cm.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String submittedValue = params.get(clientId);
        cm.setSubmittedValue(submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        CodeMirror cm = (CodeMirror) component;
        encodeMarkup(context, cm);
        encodeScript(context, cm);
    }

    protected void encodeMarkup(FacesContext context, CodeMirror cm) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String id = cm.getClientId(context);
        writer.startElement("textarea", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);

        renderPassThruAttributes(context, cm, HTML.INPUT_TEXTAREA_ATTRS);
        renderDomEvents(context, cm, HTML.INPUT_TEXT_EVENTS);

        final String valueToRender = ComponentUtils.getValueToRender(context, cm);
        if (valueToRender != null) {
            if (cm.isEscape()) {
                writer.writeText(valueToRender, null);
            } else {
                writer.write(valueToRender);
            }
        }

        writer.endElement("textarea");
    }

    protected void encodeScript(FacesContext context, CodeMirror cm) throws IOException {
        String clientId = cm.getClientId(context);
        String widgetClass = "CodeMirror";
        //options
        Map<String, Object> options = new HashMap<>();
        if (!StringHelper.isEmptyString(cm.getMode()) || cm.getMode() != null) {
            options.put("mode", cm.getMode());
        }
        options.put("indentUnit", cm.getIndentUnit());
        options.put("keyMap", cm.getKeyMap());
        if (!StringHelper.isEmptyString(cm.getExtraKeys()) || cm.getExtraKeys() != null) {
            options.put("extraKeys", cm.getExtraKeys());
        }
        options.put("lineNumbers", cm.isLineNumbers());
        if (!StringHelper.isEmptyString(cm.getGutters()) || cm.getGutters() != null) {
            options.put("gutters", cm.getGutters());
        }
        if (cm.isReadOnly()) {
            if (cm.isNoCursor()) {
                options.put("readOnly", "nocursor");
            } else {
                options.put("readOnly", true);
            }
        } else {
            options.put("readOnly", false);
        }
        for (UIComponent child : cm.getChildren()) {
            if (child instanceof Feature) {
                Feature feature = (Feature) child;
                for (Map.Entry<String, Object> option : feature.resolveOptions().entrySet()) {
                    if (option.getValue() instanceof Feature.OptionReplacer) {
                        Feature.OptionReplacer replacer = (Feature.OptionReplacer) option.getValue();
                        options.put(option.getKey(), replacer.replace(options.get(option.getKey())));
                    } else {
                        options.put(option.getKey(), option.getValue());
                    }
                }
            }
        }
        //resources
        List<Feature.Dependency> dependencies = new ArrayList<>();
        String mode = cm.resolveMode();
        dependencies.add(new Feature.Dependency("cxj", "codemirror/library/mode/" + mode + "/" + mode + ".js", Feature.Dependency.Type.SCRIPT));
        for (UIComponent child : cm.getChildren()) {
            if (child instanceof Feature) {
                Feature feature = (Feature) child;
                dependencies.addAll(feature.resolveDependencies());
            }
        }
        CollectionHelper.uniqueList(dependencies);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady(widgetClass, cm.resolveWidgetVar(), clientId);
        for (Map.Entry<String, Object> option : options.entrySet()) {
            wb.nativeAttr(option.getKey(), parseAttribute(option.getValue()));
        }
        int idx = 0;
        for (Feature.Dependency dependency : dependencies) {
            String attr = "{"
                    + "library: \"" + dependency.getLibrary() + "\", "
                    + "name: \"" + dependency.getName() + "\""
                    + "}";
            if (dependency.getType() == Feature.Dependency.Type.SCRIPT) {
                wb.nativeAttr("script_" + idx ++, attr);
            }
            if (dependency.getType() == Feature.Dependency.Type.STYLESHEET) {
                wb.nativeAttr("css_" + idx ++, attr);
            }
        }
        //Behaviors
        encodeClientBehaviors(context, cm);
        wb.finish();
    }
}
