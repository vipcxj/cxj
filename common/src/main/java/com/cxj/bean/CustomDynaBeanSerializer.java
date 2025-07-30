/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class CustomDynaBeanSerializer {
    
    private static final Logger LOGGER = LogManager.getLogger(CustomDynaBeanSerializer.class);

    public static void serialize(DynaBean bean, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        LOGGER.info("serializing the byna bean!");
        gen.writeStartObject();
        DynaProperty[] dynaProperties = bean.getDynaClass().getDynaProperties();
        for (DynaProperty dynaProperty : dynaProperties) {
            try {
                gen.writeObjectField(dynaProperty.getName(), PropertyUtils.getProperty(bean, dynaProperty.getName()));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                LOGGER.error(ex);
            }
        }
        gen.writeEndObject();
    }

    public static void serializeWithType(DynaBean bean, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        LOGGER.info("serializing the byna bean!");
        gen.writeStartObject();
        typeSer.writeTypePrefixForObject(bean, gen);
        DynaProperty[] dynaProperties = bean.getDynaClass().getDynaProperties();
        for (DynaProperty dynaProperty : dynaProperties) {
            try {
                gen.writeObjectField(dynaProperty.getName(), PropertyUtils.getProperty(bean, dynaProperty.getName()));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                LOGGER.error(ex);
            }
        }
        typeSer.writeTypeSuffixForObject(bean, gen);
        gen.writeEndObject();
    }
}
