/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.codemirror.feature;

import com.cxj.jsf.component.codemirror.CodeMirror;
import java.util.List;
import java.util.Map;
import javax.faces.component.FacesComponent;

/**
 *
 * @author Administrator
 */
@FacesComponent(AutoComplete.COMPONENT_TYPE)
public class AutoComplete extends Feature {

    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.codemirror.feature.AutoComplete";

    @Override
    public Map<String, Object> resolveOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Dependency> resolveDependencies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void requirement(CodeMirror cm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
