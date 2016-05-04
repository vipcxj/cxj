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
public class LayoutBean implements Serializable {

    private static final long serialVersionUID = -7145835148775990168L;

    public Class<? extends ViewConfig> toWelecome() {
        return Pages.Welcome.class;
    }

}
