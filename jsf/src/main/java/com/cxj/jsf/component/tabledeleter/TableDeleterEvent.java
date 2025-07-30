/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.tabledeleter;

import com.cxj.jsf.component.tablemenu.TableMenu;
import java.util.List;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author Administrator
 */
public class TableDeleterEvent extends AjaxBehaviorEvent {

    private static final long serialVersionUID = 2820332439917671180L;
    private final List<Object> toDeletes;

    public TableDeleterEvent(TableMenu tm, Behavior behavior, List<Object> toDeletes) {
        super(tm, behavior);
        this.toDeletes = toDeletes;
    }

    public List<Object> getToDeletes() {
        return toDeletes;
    }

}
