/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import javax.persistence.metamodel.EmbeddableType;

/**
 *
 * @author Administrator
 */
public class EmbeddableMeta<X> extends ManagedMeta<X> {

    public EmbeddableMeta(EmbeddableType<X> mt) {
        super(mt);
    }

}
