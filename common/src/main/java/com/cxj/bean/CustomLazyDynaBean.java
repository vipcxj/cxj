/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class CustomLazyDynaBean extends LazyDynaBean implements JsonSerializable {

    private static final long serialVersionUID = -4670159235357440811L;

    private static final Logger LOGGER = LogManager.getLogger(CustomLazyDynaBean.class);

    public CustomLazyDynaBean() {
    }

    public CustomLazyDynaBean(DynaClass dynaClass) {
        super(dynaClass);
    }

    public CustomLazyDynaBean(String name) {
        super(name);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        CustomDynaBeanSerializer.serialize(this, gen, serializers);
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        CustomDynaBeanSerializer.serializeWithType(this, gen, serializers, typeSer);
    }

}
