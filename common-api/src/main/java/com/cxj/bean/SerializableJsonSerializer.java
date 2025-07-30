/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.fasterxml.jackson.databind.JsonSerializer;
import java.io.Serializable;

/**
 *
 * @author Administrator
 * @param <T>
 */
public abstract class SerializableJsonSerializer<T> extends JsonSerializer<T> implements Serializable {

    private static final long serialVersionUID = -5342767057886290513L;

}
