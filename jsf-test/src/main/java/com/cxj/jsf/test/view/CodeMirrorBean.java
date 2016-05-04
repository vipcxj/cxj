/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.test.view;

import com.cxj.jsf.test.view.config.Pages;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author cxj
 */
@Named
@ViewScoped
public class CodeMirrorBean implements Serializable {

    private static final long serialVersionUID = 7514275619229624834L;

    private String script;
    private String mode;
    private final List<String> modes = Arrays.asList("xml", "groovy", "lua", "javascript");

    @PostConstruct
    public void init() {
        this.script = "<root/>";
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getMode() {
        if (mode == null) {
            mode = modes.get(0);
        }
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<String> getModes() {
        return modes;
    }

    public Class<? extends ViewConfig> toWelecome() {
        return Pages.Welcome.class;
    }

}
