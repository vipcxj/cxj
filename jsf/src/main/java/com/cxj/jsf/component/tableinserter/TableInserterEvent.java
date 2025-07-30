/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tableinserter;

import com.cxj.jsf.component.tablemenu.TableMenu;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesListener;

/**
 *
 * @author Administrator
 */
public class TableInserterEvent extends AjaxBehaviorEvent {

    private static final long serialVersionUID = -6281281314879574646L;
    private final Object toAdd;

    public TableInserterEvent(TableMenu tm, Behavior behavior, Object toAdd) {
        super(tm, behavior);
        this.toAdd = toAdd;
    }

    public Object getToAdd() {
        return toAdd;
    }

    @Override
    public void processListener(FacesListener listener) {
        super.processListener(listener); //To change body of generated methods, choose Tools | Templates.
    }

}
