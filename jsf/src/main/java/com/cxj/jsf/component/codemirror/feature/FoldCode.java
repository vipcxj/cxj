/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.codemirror.feature;

import com.cxj.jsf.component.codemirror.CodeMirror;
import com.cxj.utility.JsonUtils;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.FacesComponent;
import org.kopitubruk.util.json.JSONException;

/**
 *
 * @author Administrator
 */
@FacesComponent(FoldCode.COMPONENT_TYPE)
public class FoldCode extends Feature {

    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.codemirror.feature.FoldCode";

    @Override
    public Map<String, Object> resolveOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("foldGutter", true);
        options.put("gutters", new OptionReplacer() {

            @Override
            public Object replace(Object option) {
                if (option == null) {
                    List<String> ret = new ArrayList();
                    ret.add("CodeMirror-linenumbers");
                    ret.add("CodeMirror-foldgutter");
                    return ret;
                } else if (option instanceof String) {
                    String sOption = (String) option;
                    if (JsonUtils.isJsonArray(sOption)) {
                        try {
                            List<Object> jOption = JsonUtils.toJsonArray(sOption);
                            if (!jOption.contains("CodeMirror-linenumbers")) {
                                jOption.add("CodeMirror-linenumbers");
                            }
                            jOption.add("CodeMirror-foldgutter");
                            return jOption;
                        } catch (ParseException|JSONException ex) {
                            throw new IllegalArgumentException("Invalid gutters: " + option, ex);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid gutters: " + option);
                    }
                } else if (option instanceof String[]) {
                    String[] sarOption = (String[]) option;
                    List<String> ret = new ArrayList<>();
                    ret.addAll(Arrays.asList(sarOption));
                    if (!ret.contains("CodeMirror-linenumbers")) {
                        ret.add("CodeMirror-linenumbers");
                    }
                    ret.add("CodeMirror-foldgutter");
                    return ret;
                } else if (option instanceof List) {
                    List<String> ret = new ArrayList<>((List<String>) option);
                    if (!ret.contains("CodeMirror-linenumbers")) {
                        ret.add("CodeMirror-linenumbers");
                    }
                    ret.add("CodeMirror-foldgutter");
                    return ret;
                } else {
                    throw new IllegalArgumentException("Invalid gutters: " + option);
                }
            }
        });
        return options;
    }

    @Override
    public List<Dependency> resolveDependencies() {
        List<Dependency> dependencies = new ArrayList<>();
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/foldgutter.css", Dependency.Type.STYLESHEET));
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/foldcode.js", Dependency.Type.SCRIPT));
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/foldgutter.js", Dependency.Type.SCRIPT));
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/brace-fold.js", Dependency.Type.SCRIPT));
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/xml-fold.js", Dependency.Type.SCRIPT));
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/markdown-fold.js", Dependency.Type.SCRIPT));
        dependencies.add(new Dependency("cxj", "codemirror/library/addon/fold/comment-fold.js", Dependency.Type.SCRIPT));
        return dependencies;
    }

    @Override
    public void requirement(CodeMirror cm) {
    }

}
