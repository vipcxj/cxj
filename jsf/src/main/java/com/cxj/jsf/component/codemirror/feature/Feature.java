/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.codemirror.feature;

import com.cxj.jsf.component.codemirror.CodeMirror;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.omnifaces.util.Components;
import org.omnifaces.util.Events;
import org.omnifaces.util.Faces;

/**
 *
 * @author Administrator
 */
public abstract class Feature extends UIComponentBase implements SystemEventListener {

    public static final String COMPONENT_FAMILY = "com.cxj.jsf.component.codemirror";

    public Feature() {
        super();
        if (!Faces.isPostback()) {
            Events.subscribeToViewEvent(PreRenderViewEvent.class, this);
        }
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean isListenerForSource(Object o) {
        return o instanceof UIViewRoot;
    }

    @Override
    public void processEvent(SystemEvent evt) throws AbortProcessingException {
        if (evt instanceof PreRenderViewEvent) {
            Components.validateHasDirectParent(this, CodeMirror.class);
            requirement((CodeMirror) getParent());
        }
    }

    public abstract Map<String, Object> resolveOptions();

    public abstract List<Dependency> resolveDependencies();

    public abstract void requirement(CodeMirror cm);

    public static class Dependency {

        private String library;
        private String name;
        private Type type;

        public Dependency(String library, String name, Type type) {
            this.library = library;
            this.name = name;
            this.type = type;
        }

        /**
         * @return the library
         */
        public String getLibrary() {
            return library;
        }

        /**
         * @param library the library to set
         */
        public void setLibrary(String library) {
            this.library = library;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 31 * hash + Objects.hashCode(this.library);
            hash = 31 * hash + Objects.hashCode(this.name);
            hash = 31 * hash + Objects.hashCode(this.type);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Dependency other = (Dependency) obj;
            if (!Objects.equals(this.library, other.library)) {
                return false;
            }
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return this.type == other.type;
        }

        @Override
        public String toString() {
            return "Dependency{" + "library=" + library + ", name=" + name + ", type=" + type + '}';
        }

        public enum Type {

            SCRIPT, STYLESHEET;
        }
    }

    public interface OptionReplacer {

        public Object replace(Object option);
    }
}
