/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import javax.persistence.metamodel.EntityType;

/**
 *
 * @author Administrator
 */
public class EntityMeta<X> extends ManagedMeta<X> {

    public EntityMeta(EntityType et) {
        super(et);
    }

    @Override
    public EntityType<X> unwrap() {
        return (EntityType<X>) super.unwrap();
    }

    public String getName() {
        return unwrap().getName();
    }
}
