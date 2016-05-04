/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.test.view.config;

import org.apache.deltaspike.core.api.config.view.DefaultErrorView;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.jsf.api.config.view.View;

/**
 *
 * @author cxj
 */
@View(navigation = View.NavigationMode.REDIRECT)
public interface Pages extends ViewConfig {

    @View(navigation = View.NavigationMode.REDIRECT)
    public class Welcome extends DefaultErrorView {
    }

    public class Datatable implements Pages {
    }

    public class Codemirror implements Pages {
    }

    public class Layout implements Pages {
    }

    public class Imageupload implements Pages {
    }
}
