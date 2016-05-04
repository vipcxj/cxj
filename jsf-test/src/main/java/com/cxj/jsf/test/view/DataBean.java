/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.test.view;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class DataBean implements Serializable {

    private static final long serialVersionUID = 7377012319956417969L;
    public static final String PROP_AGE = "PROP_AGE";
    public static final String PROP_NAME = "PROP_NAME";
    public static final String PROP_POSITION = "PROP_POSITION";
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    private final transient VetoableChangeSupport vetoableChangeSupport = new java.beans.VetoableChangeSupport(this);
    private String name;
    private int age;
    private String position;

    public DataBean() {
    }

    public DataBean(String name, int age, String position) {
        this.name = name;
        this.age = age;
        this.position = position;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     * @throws java.beans.PropertyVetoException
     */
    public void setAge(int age) throws PropertyVetoException {
        int oldAge = this.age;
        vetoableChangeSupport.fireVetoableChange(PROP_AGE, oldAge, age);
        this.age = age;
        propertyChangeSupport.firePropertyChange(PROP_AGE, oldAge, age);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     * @throws java.beans.PropertyVetoException
     */
    public void setName(String name) throws PropertyVetoException {
        java.lang.String oldName = this.name;
        vetoableChangeSupport.fireVetoableChange(PROP_NAME, oldName, name);
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     * @throws java.beans.PropertyVetoException
     */
    public void setPosition(String position) throws PropertyVetoException {
        java.lang.String oldPosition = this.position;
        vetoableChangeSupport.fireVetoableChange(PROP_POSITION, oldPosition, position);
        this.position = position;
        propertyChangeSupport.firePropertyChange(PROP_POSITION, oldPosition, position);
    }

}
