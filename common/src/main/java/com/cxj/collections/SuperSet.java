/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.Set;

/**
 *
 * @author Administrator
 * @param <E>
 */
public interface SuperSet<E> extends Set<E>, SuperCollection<E> {

    public Set<E> innerSet();

    public <T> Set<T> setView(Class<T> clazz);
}
