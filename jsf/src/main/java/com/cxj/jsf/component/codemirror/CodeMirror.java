/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.codemirror;

import com.cxj.jsf.component.codemirror.feature.Feature;
import com.cxj.utility.JsonUtils;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UniqueIdVendor;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.ListenersFor;
import javax.faces.event.PostAddToViewEvent;
import org.apache.commons.beanutils.PropertyUtils;
import org.kopitubruk.util.json.JSONException;
import static org.omnifaces.util.Renderers.RENDERER_TYPE_CSS;
import static org.omnifaces.util.Renderers.RENDERER_TYPE_JS;
import org.primefaces.component.api.Widget;

/**
 *
 * @author cxj
 */
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "components.css"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "cxj", name = "js/json/json2.js"),
    @ResourceDependency(library = "cxj", name = "codemirror/library/lib/codemirror.css"),
    @ResourceDependency(library = "cxj", name = "codemirror/library/lib/codemirror.js"),
    @ResourceDependency(library = "cxj", name = "codemirror/codemirror.css"),
    @ResourceDependency(library = "cxj", name = "codemirror/codemirror.js"),
    @ResourceDependency(library = "cxj", name = "js/utils/resource.js")
})
@FacesComponent(CodeMirror.COMPONENT_TYPE)
@ListenersFor({
    @ListenerFor(systemEventClass = PostAddToViewEvent.class),})
public class CodeMirror extends UIInput implements Widget, NamingContainer, UniqueIdVendor, ClientBehaviorHolder {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.codemirror.CodeMirror";
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_FAMILY = "com.cxj.jsf.component";
    public static final String DEFAULT_RENTER = "com.cxj.jsf.component.codemirror.CodeMirrorRender";

    protected static final String CODE_MIRROR_CLASS = "";
    private static final Collection<String> EVENT_NAMES = ImmutableList.of();
    private static final List<String> SUPPORT_MODES = new ArrayList<>();

    protected enum PropertyKeys {

        lastId, style, styleClass, widgetVar, escape,
        mode, lineSeparator, theme, indentUnit, smartIndent,
        tabSize, indentWithTabs, electricChars, specialChars, specialCharPlaceholder,
        rtlMoveVisually, keyMap, extraKeys, lineWrapping, lineNumbers,
        firstLineNumber, lineNumberFormatter, gutters, fixedGutter, scrollbarStyle,
        coverGutterNextToScrollbar, inputStyle, readOnly, noCursor, showCursorWhenSelecting, lineWiseCopyCut,
        undoDepth, historyEventDelay, tabindex, autofocus;

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

    public CodeMirror() {
        setRendererType(DEFAULT_RENTER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
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

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(final boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    public Object getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "");
    }

    public void setMode(Object mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public int getIndentUnit() {
        return (int) getStateHelper().eval(PropertyKeys.indentUnit, 2);
    }

    public void setIndentUnit(int iu) {
        getStateHelper().put(PropertyKeys.indentUnit, iu);
    }

    public String getKeyMap() {
        return (String) getStateHelper().eval(PropertyKeys.keyMap, "default");
    }

    public void setKeyMap(String keyMap) {
        getStateHelper().put(PropertyKeys.keyMap, keyMap);
    }

    public Object getExtraKeys() {
        return getStateHelper().eval(PropertyKeys.extraKeys, null);
    }

    public void setExtraKeys(Object ek) {
        getStateHelper().put(PropertyKeys.extraKeys, ek);
    }

    public boolean isLineNumbers() {
        return (boolean) getStateHelper().eval(PropertyKeys.lineNumbers, true);
    }

    public void setLineNumbers(boolean ln) {
        getStateHelper().put(PropertyKeys.lineNumbers, ln);
    }

    public Object getGutters() {
        return getStateHelper().eval(PropertyKeys.gutters);
    }

    public void setGutters(Object gutters) {
        getStateHelper().put(PropertyKeys.gutters, gutters);
    }

    public boolean isReadOnly() {
        return (boolean) getStateHelper().eval(PropertyKeys.readOnly, false);
    }

    public void setReadOnly(boolean ro) {
        getStateHelper().put(PropertyKeys.readOnly, ro);
    }

    public boolean isNoCursor() {
        return (boolean) getStateHelper().eval(PropertyKeys.noCursor, false);
    }

    public void setNoCursor(boolean nc) {
        getStateHelper().put(PropertyKeys.noCursor, nc);
    }

    /**
     * @see UIComponent#visitTree
     */
    @Override
    public boolean visitTree(VisitContext context,
            VisitCallback callback) {

        // NamingContainers can optimize partial tree visits by taking advantage
        // of the fact that it is possible to detect whether any ids to visit
        // exist underneath the NamingContainer.  If no such ids exist, there
        // is no need to visit the subtree under the NamingContainer.
        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);
        assert (idsToVisit != null);

        // If we have ids to visit, let the superclass implementation
        // handle the visit
        if (!idsToVisit.isEmpty()) {
            return super.visitTree(context, callback);
        }

        // If we have no child ids to visit, just visit ourselves, if
        // we are visitable.
        if (isVisitable(context)) {
            FacesContext facesContext = context.getFacesContext();
            pushComponentToEL(facesContext, null);

            try {
                VisitResult result = context.invokeVisitCallback(this, callback);
                return (result == VisitResult.COMPLETE);
            } finally {
                popComponentFromEL(facesContext);
            }
        }

        // Done visiting this subtree.  Return false to allow 
        // visit to continue.
        return false;
    }

    @Override
    public String createUniqueId(FacesContext context, String seed) {
        Integer i = (Integer) getStateHelper().get(PropertyKeys.lastId);
        int lastId = ((i != null) ? i : 0);
        getStateHelper().put(PropertyKeys.lastId, ++lastId);
        return UIViewRoot.UNIQUE_ID_PREFIX + (seed == null ? lastId : seed);
    }

    protected void validateMode() {
        Object mode = getMode();
        if (mode instanceof CharSequence) {
            String sMode = ((CharSequence) mode).toString().trim();
            if (sMode.startsWith("{") && sMode.endsWith("}")) {
                try {
                    Map<String, Object> mMode = JsonUtils.toJsonObject(sMode);
                    Object oMode = mMode.get("mode");
                    if (!(oMode instanceof CharSequence)) {
                        throw new IllegalArgumentException("Invalid mode: " + mode);
                    }
                    sMode = ((CharSequence) oMode).toString();
                    if (!validMode(sMode)) {
                        throw new IllegalArgumentException("Not support mode: " + mode);
                    }
                } catch (ParseException | JSONException ex) {
                    throw new IllegalArgumentException("Invalid mode: " + mode, ex);
                }
            } else {
                if (!validMode(sMode)) {
                    throw new IllegalArgumentException("Not support mode: " + mode);
                }
            }
        } else {
            try {
                Object oMode = PropertyUtils.getProperty(mode, "mode");
                if (!(oMode instanceof CharSequence)) {
                    throw new IllegalArgumentException("Invalid mode: " + mode);
                }
                String sMode = ((CharSequence) oMode).toString();
                if (!validMode(sMode)) {
                    throw new IllegalArgumentException("Not support mode: " + mode);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new IllegalArgumentException("Invalid mode: " + mode, ex);
            }
        }
    }

    protected void validateExtraKeys() {
        Object extraKeys = getExtraKeys();
        if (extraKeys instanceof String) {
            String sek = (String) extraKeys;
            sek = sek.trim();
            if (!sek.startsWith("{") || !sek.endsWith("}")) {
                throw new IllegalArgumentException("Invalid extraKeys: " + sek);
            }
        }
        if (extraKeys != null) {
            if (!(extraKeys instanceof Map)) {
                throw new IllegalArgumentException("Invalid extraKeys: " + extraKeys);
            }
        }
    }

    protected void validateGutters() {
        Object gutters = getGutters();
        if (gutters instanceof String) {
            String sGutters = (String) gutters;
            sGutters = sGutters.trim();
            if (!sGutters.startsWith("[") || !sGutters.endsWith("]")) {
                throw new IllegalArgumentException("Invalid gutters: " + gutters);
            }
        } else {
            if (gutters != null) {
                if (!gutters.getClass().isArray() && !(gutters instanceof List)) {
                    throw new IllegalArgumentException("Invalid gutters: " + gutters);
                }
            }
        }
    }

//    @Override
//    public boolean isListenerForSource(Object o) {
//        return o instanceof UIViewRoot;
//    }
    protected UIComponent addResource(UIViewRoot root, String libraryName, String resourceName, String target, Feature.Dependency.Type type) {
        UIOutput output = new UIOutput();
        if (type == Feature.Dependency.Type.SCRIPT) {
            output.setRendererType(RENDERER_TYPE_JS);
        } else if (type == Feature.Dependency.Type.STYLESHEET) {
            output.setRendererType(RENDERER_TYPE_CSS);
        } else {
            throw new IllegalArgumentException();
        }
        if (libraryName != null) {
            output.getAttributes().put("library", libraryName);
        }
        output.getAttributes().put("name", resourceName);
        if (root == null) {
            root = this.getFacesContext().getViewRoot();
        }
        root.addComponentResource(this.getFacesContext(), output, target);
        return output;
    }

    protected UIComponent addScriptResource(UIViewRoot root, String libraryName, String resourceName, String target) {
        return addResource(root, libraryName, resourceName, target, Feature.Dependency.Type.SCRIPT);
    }

    protected UIComponent addStyleSheetResource(UIViewRoot root, String libraryName, String resourceName, String target) {
        return addResource(root, libraryName, resourceName, target, Feature.Dependency.Type.STYLESHEET);
    }

    public String resolveMode() {
        String sMode;
        Object mode = getMode();
        if (mode instanceof String) {
            sMode = (String) mode;
            if (JsonUtils.isJsonObject(sMode)) {
                try {
                    sMode = (String) JsonUtils.toJsonObject(sMode).get("mode");
                } catch (ParseException | JSONException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        } else {
            try {
                mode = PropertyUtils.getProperty(mode, "mode");
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new IllegalArgumentException(ex);
            }
            if (mode instanceof String) {
                sMode = (String) mode;
            } else {
                throw new IllegalArgumentException();
            }
        }
        return sMode;
    }

    @Override
    public void processEvent(ComponentSystemEvent evt) throws AbortProcessingException {
        if (evt instanceof PostAddToViewEvent) {
            validateMode();
            validateGutters();
            validateExtraKeys();
//            UIViewRoot root = getFacesContext().getViewRoot();
//            String mode = resolveMode();
//            addScriptResource(root, "cxj", "codemirror/library/mode/" + mode + "/" + mode + ".js", "head");
//            for (UIComponent child : getChildren()) {
//                if (child instanceof Feature) {
//                    Feature feature = (Feature) child;
//                    for (Feature.Dependency dependency : feature.resolveDependencies()) {
//                        addResource(root, dependency.getLibrary(), dependency.getName(), "head", dependency.getType());
//                    }
//                }
//            }
        }
    }

    public boolean validMode(String mode) {
        synchronized (SUPPORT_MODES) {
            if (SUPPORT_MODES.contains(mode)) {
                return true;
            }
            String js = "META-INF/resources/cxj/codemirror/library/mode/" + mode + "/" + mode + ".js";
            URL test = Thread.currentThread().getContextClassLoader().getResource(js);
            if (test != null) {
                SUPPORT_MODES.add(mode);
                return true;
            } else {
                return false;
            }
        }
    }
}
