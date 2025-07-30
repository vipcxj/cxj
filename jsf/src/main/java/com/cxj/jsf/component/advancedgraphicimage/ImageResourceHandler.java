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

import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;

import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 */
public class ImageResourceHandler extends ResourceHandlerWrapper {

    private ResourceHandler wrapped;

    public ImageResourceHandler(ResourceHandler original) {
        this.wrapped = original;
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String library = params.get("ln");
        String dynamicContentId = params.get(Constants.DYNAMIC_CONTENT_PARAM);

        if (dynamicContentId != null && library != null && library.equals("advancedPrimefaces")) {
            GraphicImageManager graphicImageManager = GraphicImageUtil.retrieveManager(context);
            StreamedContent streamedContent = graphicImageManager.retrieveImage(dynamicContentId);

            ExternalContext externalContext = context.getExternalContext();
            externalContext.setResponseStatus(200);
            externalContext.setResponseContentType(streamedContent.getContentType());

            byte[] buffer = new byte[2048];

            int length;
            try (InputStream inputStream = streamedContent.getStream();) {
                while ((length = (inputStream.read(buffer))) >= 0) {
                    externalContext.getResponseOutputStream().write(buffer, 0, length);
                }
            }
            externalContext.responseFlushBuffer();
            context.responseComplete();

        } else {
            getWrapped().handleResourceRequest(context);
        }

    }
}
