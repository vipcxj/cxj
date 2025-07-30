/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.taghandler;

import java.io.IOException;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import org.omnifaces.el.DelegatingVariableMapper;

/**
 *
 * @author Administrator
 */
public class AutoId extends TagHandler {

    private static final String MARKER = AutoId.class.getName();

    public AutoId(TagConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        checkAndMarkMapper(ctx);
        VariableMapper variableMapper = ctx.getVariableMapper();
        ValueExpression idVe = variableMapper.resolveVariable("id");
        if (idVe != null) {
            if (idVe.isLiteralText()) {
                ELContext elCtx = ctx.getFacesContext().getELContext();
                Object idV = idVe.getValue(elCtx);
                if (idV != null && (!(idV instanceof String) || !((String) idV).isEmpty())) {
                    return;
                }
            } else {
                return;
            }
        }
        String id = ctx.getFacesContext().getViewRoot().createUniqueId();
        variableMapper.setVariable("id", ctx.getExpressionFactory().createValueExpression(id, String.class));
    }

    private static void checkAndMarkMapper(FaceletContext context) {
        Integer marker = (Integer) context.getAttribute(MARKER);

        if (marker != null && marker.equals(context.hashCode())) {
            return; // Already marked.
        }

        VariableMapper variableMapper = context.getVariableMapper();
        ValueExpression valueExpressionParentMarker = variableMapper.resolveVariable(MARKER);

        if (valueExpressionParentMarker == null) { // We're the outer faces tag, or parent didn't mark because it didn't have any attributes set.
            context.setAttribute(MARKER, context.hashCode());
            return;
        }

        variableMapper.setVariable(MARKER, null); // If we have our own mapper, this will not affect our parent mapper.
        ValueExpression valueExpressionParentMarkerCheck = variableMapper.resolveVariable(MARKER);

        if (valueExpressionParentMarkerCheck == null || !valueExpressionParentMarkerCheck.equals(valueExpressionParentMarker)) {
            // We were able to remove our parent's mapper, so we share it.

            variableMapper.setVariable(MARKER, valueExpressionParentMarker); // First put parent marker back ...
            context.setVariableMapper(new DelegatingVariableMapper(variableMapper)); // ... then add our own variable mapper.
        }

        context.setAttribute(MARKER, context.hashCode());
    }

}
