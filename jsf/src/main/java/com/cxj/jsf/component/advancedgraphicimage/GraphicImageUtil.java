/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cxj.jsf.component.advancedgraphicimage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import org.omnifaces.util.Components;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.context.RequestContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;
import org.primefaces.util.DynamicResourceBuilder;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.StringEncrypter;

/**
 */
public class GraphicImageUtil {

    private static final String SB_BUILD = DynamicResourceBuilder.class.getName() + "#build";

    public static GraphicImageManager retrieveManager(FacesContext context) {
        ELContext eLContext = context.getELContext();
        ValueExpression ve = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), buildElForGraphicImageManager(), GraphicImageManager.class);
        return (GraphicImageManager) ve.getValue(eLContext);

    }

    private static String buildElForGraphicImageManager() {
        StringBuilder result = new StringBuilder();
        result.append("#{").append(GraphicImageManager.class.getSimpleName()).append("}");
        return result.toString();

    }
    
    public static String registerAdvancedImage(StreamedContent image) throws UnsupportedEncodingException {
        return registerAdvancedImage(image, null);
    }

    public static String registerAdvancedImage(StreamedContent image, String uid) throws UnsupportedEncodingException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (uid == null) {
            uid = UUID.randomUUID().toString();
        }
        StringEncrypter encrypter = RequestContext.getCurrentInstance().getEncrypter();
        uid = encrypter.encrypt(uid);
        GraphicImageManager graphicImageManager = GraphicImageUtil.retrieveManager(context);
        graphicImageManager.registerImage(image, uid);
        return uid;
    }

    public static String getAdvancedImageSrc(String uid) throws UnsupportedEncodingException {
        return getAdvancedImageSrc(uid, Collections.emptyMap(), true);
    }

    public static String getAdvancedImageSrc(String uid, Map<String, Object> params, boolean cache) throws UnsupportedEncodingException {
        StringEncrypter encrypter = RequestContext.getCurrentInstance().getEncrypter();
        uid = encrypter.encrypt(uid);
        FacesContext context = FacesContext.getCurrentInstance();
        GraphicImageManager graphicImageManager = GraphicImageUtil.retrieveManager(context);
        GraphicImageManager.Meta meta = graphicImageManager.retrieveMeta(uid);
        if (meta == null) {
            return null;
        }
        String contentType = meta.getContentType();
        Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent", "advancedPrimefaces", contentType);
        String resourcePath = resource.getRequestPath();

        String src;
        StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);
        builder.append(resourcePath).append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(uid, "UTF-8"));
        String format = contentType.substring(contentType.indexOf('/') + 1, contentType.length());
        builder.append("&").append("format").append("=").append(format);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            builder.append("&").append(param.getKey()).append("=");
            if (param.getValue() != null) {
                builder.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
            }
        }

        src = builder.toString();

        src += src.contains("?") ? "&" : "?";
        src += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + cache;

        if (!cache) {
            src += "&uid=" + UUID.randomUUID().toString();
        }

        return context.getExternalContext().encodeResourceURL(src);
    }

    public static String getAdvancedImageSrcFromComponent(String id) throws UnsupportedEncodingException {
        String src;
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent component = Components.getCurrentComponent().findComponent(id);
        if (!(component instanceof GraphicImage)) {
            throw new IllegalArgumentException("Invalid component id: " + id);
        }
        GraphicImage image = (GraphicImage) component;
        StreamedContent streamedContent = (StreamedContent) image.getValue();
        if (streamedContent == null) {
            return "";
        }
        Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent", "advancedPrimefaces", streamedContent.getContentType());
        String resourcePath = resource.getRequestPath();
        StringEncrypter encrypter = RequestContext.getCurrentInstance().getEncrypter();

        ValueExpression expression = ValueExpressionAnalyzer.getExpression(context.getELContext(), image.getValueExpression("value"));
        String rid = encrypter.encrypt(expression.getExpressionString() + image.getClientId(context));

        GraphicImageManager graphicImageManager = GraphicImageUtil.retrieveManager(context);
        graphicImageManager.registerImage(streamedContent, rid);

        StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);

        builder.append(resourcePath).append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(rid, "UTF-8"));

        for (UIComponent kid : image.getChildren()) {
            if (kid instanceof UIParameter) {
                UIParameter param = (UIParameter) kid;
                Object paramValue = param.getValue();

                builder.append("&").append(param.getName()).append("=");

                if (paramValue != null) {
                    builder.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
                }
            }
        }

        src = builder.toString();

        boolean cache = image.isCache();

        src += src.contains("?") ? "&" : "?";
        src += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + cache;

        if (!cache) {
            src += "&uid=" + UUID.randomUUID().toString();
        }

        return context.getExternalContext().encodeResourceURL(src);
    }
}
