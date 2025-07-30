/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

/**
 *
 * @author Administrator
 */
public abstract class ManagedMeta<X> {

        private final ManagedType<X> mt;
        private final Map<String, AttributeMeta> attributes;

        public ManagedMeta(ManagedType<X> mt) {
            this.mt = mt;
            this.attributes = new HashMap<>();
            for (Object o : mt.getAttributes()) {
                Attribute a = (Attribute) o;
                this.attributes.put(a.getName(), new AttributeMeta(a));
            }
        }

        public ManagedType<X> unwrap() {
            return mt;
        }

        public Map<String, AttributeMeta> getAttributes() {
            return attributes;
        }
    }
