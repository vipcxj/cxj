/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.layout;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.kopitubruk.util.json.JSONConfig;
import org.kopitubruk.util.json.JSONUtil;

/**
 *
 * @author Administrator
 */
public class ChildLayoutOptions extends LayoutOptions {

    @Override
    public void toJSON(JSONConfig jsonConfig, Writer json) throws IOException {
        if (getEast() == null && getSouth() == null && getWest() == null && getNorth() == null && getCenter() == null) {
            json.append(JSONUtil.toJSON(getOptions(), jsonConfig));
        } else {
            json.append("{");
            boolean empty = true;
            for (Map.Entry<String, Object> option : getOptions().entrySet()) {
                if (empty) {
                    empty = false;
                } else {
                    json.append(",");
                }
                json.append("\"" + option.getKey() + "\":");
                json.append(JSONUtil.toJSON(option.getValue(), jsonConfig));
            }
            if (!empty) {
                json.append(",");
            }
            json.append("\"childOptions\":{");
            subToJson(jsonConfig, json, true);
            json.append("}");
            json.append("}");
        }
    }

}
