/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.List;

/**
 *
 * @author 陈晓靖
 * @param <E>
 */
public interface SuperList<E> extends List<E>, SuperCollection<E> {

    public List<E> innerList();
    public <T> List<T> listView(Class<T> clazz);
}
