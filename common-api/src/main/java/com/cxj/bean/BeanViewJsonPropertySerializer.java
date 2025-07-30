/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 *
 * @author Administrator
 */
public interface BeanViewJsonPropertySerializer {

    public void serialize(JsonGenerator gen, SerializerProvider serializers, String nativePropertyName, String mappedPropertyName);
}
