/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.layout;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.kopitubruk.util.json.JSONAble;
import org.kopitubruk.util.json.JSONConfig;
import org.kopitubruk.util.json.JSONUtil;

/**
 *
 * @author Administrator
 */
public class LayoutOptions implements JSONAble {

    private Map<String, Object> options;
    private ChildLayoutOptions east;
    private ChildLayoutOptions west;
    private ChildLayoutOptions north;
    private ChildLayoutOptions south;
    private ChildLayoutOptions center;

    public Map<String, Object> getOptions() {
        if (options == null) {
            options = new HashMap<>();
        }
        return options;
    }

    /**
     * 创建东子面板的配置并返回。若配置已存在，直接直接返回存在的配置
     *
     * @return 东子面板的配置
     */
    public LayoutOptions createEast() {
        if (east == null) {
            east = new ChildLayoutOptions();
        }
        return east;
    }

    public LayoutOptions getEast() {
        return east;
    }

    /**
     * @see #createWest()
     * @return
     */
    public LayoutOptions createWest() {
        if (west == null) {
            west = new ChildLayoutOptions();
        }
        return west;
    }

    public LayoutOptions getWest() {
        return west;
    }

    /**
     * @see #createNorth()
     * @return
     */
    public LayoutOptions createNorth() {
        if (north == null) {
            north = new ChildLayoutOptions();
        }
        return north;
    }

    public LayoutOptions getNorth() {
        return north;
    }

    /**
     * @see #createSouth()
     * @return
     */
    public LayoutOptions createSouth() {
        if (south == null) {
            south = new ChildLayoutOptions();
        }
        return south;
    }

    public LayoutOptions getSouth() {
        return south;
    }

    /**
     * @see #createCenter()
     * @return
     */
    public LayoutOptions createCenter() {
        if (center == null) {
            center = new ChildLayoutOptions();
        }
        return center;
    }

    public LayoutOptions getCenter() {
        return center;
    }

    public Object getOption(String name) {
        return getOptions().get(name);
    }

    public void setOption(String name, Object value) {
        getOptions().put(name, value);
    }

    public Object removeOption(String name) {
        return getOptions().remove(name);
    }

    protected void subToJson(JSONConfig jsonConfig, Writer json, boolean empty) throws IOException {
        if (getEast() != null) {
            if (!empty) {
                json.append(",");
            } else {
                empty = false;
            }
            json.append("\"east\":");
            json.append(getEast().toJSON(jsonConfig));
        }
        if (getSouth() != null) {
            if (!empty) {
                json.append(",");
            } else {
                empty = false;
            }
            json.append("\"south\":");
            json.append(getSouth().toJSON(jsonConfig));
        }
        if (getWest() != null) {
            if (!empty) {
                json.append(",");
            } else {
                empty = false;
            }
            json.append("\"west\":");
            json.append(getWest().toJSON(jsonConfig));
        }
        if (getNorth() != null) {
            if (!empty) {
                json.append(",");
            } else {
                empty = false;
            }
            json.append("\"north\":");
            json.append(getNorth().toJSON(jsonConfig));
        }
        if (getCenter() != null) {
            if (!empty) {
                json.append(",");
            }
            json.append("\"center\":");
            json.append(getCenter().toJSON(jsonConfig));
        }
    }

    @Override
    public void toJSON(JSONConfig jsonConfig, Writer json) throws IOException {
        if (getEast() == null && getSouth() == null && getWest() == null && getNorth() == null && getCenter() == null) {
            json.append(JSONUtil.toJSON(getOptions(), jsonConfig));
        } else {
            json.append("{");
            boolean empty = true;
            if (!getOptions().isEmpty()) {
                empty = false;
                json.append("\"panes\":");
                json.append(JSONUtil.toJSON(getOptions(), jsonConfig));
            }
            subToJson(jsonConfig, json, empty);
            json.append("}");
        }
    }
}
