/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.test.view;

import com.cxj.jsf.component.tabledeleter.TableDeleterEvent;
import com.cxj.jsf.component.tableinserter.TableInserterEvent;
import com.cxj.jsf.component.tablemenu.TableMenu;
import com.cxj.jsf.test.view.config.Pages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author cxj
 */
@Named
@ViewScoped
public class DataTableBean implements Serializable {

    private static final long serialVersionUID = 1171356765554242860L;
    private static final Logger LOGGER = LogManager.getLogger(DataTableBean.class);

    private List<DataBean> dataList;
    private DataBean selection;

    @PostConstruct
    public void init() {
        LOGGER.info("init");
        dataList = new ArrayList<>();
        dataList.add(new DataBean("cxj", 22, "student"));
        dataList.add(new DataBean("John", 35, "engineer"));
        dataList.add(new DataBean("Jim", 57, "teacher"));
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("destroy");
        dataList = null;
    }

    public List<DataBean> getDataList() {
        return dataList;
    }

    public DataBean getSelection() {
        return selection;
    }

    public void setSelection(DataBean selection) {
        this.selection = selection;
    }

    public Class<? extends ViewConfig> toWelecome() {
        return Pages.Welcome.class;
    }

    public void onInserterInit(TableInserterEvent evt) {
        LOGGER.info("I want to insert an item to the datatable!");
        ((TableMenu) evt.getComponent()).setItem(new DataBean());
    }

    public void onInserterInvoke(TableInserterEvent evt) {
        LOGGER.info("Before I insert " + evt.getToAdd() + " to the datatable!");
    }

    public void onInserterCancel(TableInserterEvent evt) {
        LOGGER.info("I cancel the insert operation!");
    }
    
    public void onDeleterInvoke(TableDeleterEvent evt) {
        LOGGER.info("Before I delete " + evt.getToDeletes() + " from the datatable!");
    }
}
