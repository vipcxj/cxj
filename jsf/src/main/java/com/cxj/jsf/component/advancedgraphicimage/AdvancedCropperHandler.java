/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.advancedgraphicimage;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 *
 * @author Administrator
 */
public class AdvancedCropperHandler extends TagHandler {
    
    private final TagAttribute advImage;

    public AdvancedCropperHandler(TagConfig config) {
        super(config);
        advImage = config.getTag().getAttributes().get("advImage");
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        parent.getAttributes().put("advImage", advImage.getValue(ctx));
    }
    
}
