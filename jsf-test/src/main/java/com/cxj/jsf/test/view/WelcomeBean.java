/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.test.view;

import com.cxj.jsf.test.view.config.Pages;
import java.io.Serializable;
import javax.inject.Named;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Administrator
 */
@Named
@ViewScoped
public class WelcomeBean implements Serializable {

    private static final long serialVersionUID = 3873143227309671457L;

    public Class<? extends ViewConfig> toDatatable() {
        return Pages.Datatable.class;
    }
    
    public Class<? extends ViewConfig> toCodemirror() {
        return Pages.Codemirror.class;
    }
    
    public Class<? extends ViewConfig> toLayout() {
        return Pages.Layout.class;
    }
    
    public Class<? extends ViewConfig> toImageupload() {
        return Pages.Imageupload.class;
    }
}
