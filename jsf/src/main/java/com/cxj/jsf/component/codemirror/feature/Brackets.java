/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.codemirror.feature;

import com.cxj.jsf.component.codemirror.CodeMirror;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.FacesComponent;

/**
 *
 * @author Administrator
 */
@FacesComponent(Brackets.COMPONENT_TYPE)
public class Brackets extends Feature {

    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.codemirror.feature.Brackets";

    protected enum PropertyKeys {

        match, autoClose;

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

    public boolean isMatch() {
        return (boolean) getStateHelper().eval(PropertyKeys.match, true);
    }

    public void setMatch(boolean match) {
        getStateHelper().put(PropertyKeys.match, match);
    }

    public boolean isAutoClose() {
        return (boolean) getStateHelper().eval(PropertyKeys.autoClose, true);
    }

    public void setAutoClose(boolean ac) {
        getStateHelper().put(PropertyKeys.autoClose, ac);
    }

    @Override
    public Map<String, Object> resolveOptions() {
        Map<String, Object> options = new HashMap<>();
        if (isMatch()) {
            options.put("matchBrackets", true);
        }
        if (isAutoClose()) {
            options.put("autoCloseBrackets", true);
        }
        return options;
    }

    @Override
    public List<Dependency> resolveDependencies() {
        List<Dependency> dependencies = new ArrayList<>();
        String library = "cxj";
        if (isMatch()) {
            dependencies.add(new Dependency(library, "codemirror/library/addon/edit/matchbrackets.js", Dependency.Type.SCRIPT));
        }
        if (isAutoClose()) {
            dependencies.add(new Dependency(library, "codemirror/library/addon/edit/closebrackets.js", Dependency.Type.SCRIPT));
        }
        return dependencies;
    }

    @Override
    public void requirement(CodeMirror cm) {
    }

}
