/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.advancedgraphicimage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import javax.el.ValueExpression;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.FacesRenderer;
import javax.imageio.ImageIO;
import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.component.imagecropper.ImageCropper;
import org.primefaces.component.imagecropper.ImageCropperRenderer;
import org.primefaces.context.RequestContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;
import org.primefaces.util.DynamicResourceBuilder;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.StringEncrypter;

/**
 *
 * @author Administrator
 */
@FacesRenderer(rendererType = AdvancedImageCropper.RENDER_TYPE, componentFamily = AdvancedImageCropper.COMPONENT_FAMILY)
public class AdvancedImageCropperRender extends ImageCropperRenderer {

    private static final String SB_BUILD = DynamicResourceBuilder.class.getName() + "#build";

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        AdvancedImageCropper cropper = (AdvancedImageCropper) component;
        if (cropper.getImageValue() == null) {
            return super.getConvertedValue(context, component, submittedValue);
        }
        String coords = (String) submittedValue;
        if (isValueBlank(coords)) {
            return null;
        }

        String[] cropCoords = coords.split("_");
        StreamedContent image = cropper.getImageValue();
        String format;
        switch (image.getContentType()) {
            case "image/jpeg":
                format = "jpg";
                break;
            case "image/png":
                format = "png";
                break;
            case "image/gif":
                format = "gif";
                break;
            case "image/bmp":
                format = "bmp";
                break;
            default:
                format = "jpg";
        }

        int x = Integer.parseInt(cropCoords[0]);
        int y = Integer.parseInt(cropCoords[1]);
        int w = Integer.parseInt(cropCoords[2]);
        int h = Integer.parseInt(cropCoords[3]);

        try {
            BufferedImage outputImage = ImageIO.read(image.getStream());
            BufferedImage cropped = outputImage.getSubimage(x, y, w, h);

            ByteArrayOutputStream croppedOutImage = new ByteArrayOutputStream();
            ImageIO.write(cropped, format, croppedOutImage);
            if (image.getStream().markSupported()) {
                image.getStream().reset();
            }
            return new CroppedImage(cropper.getImage(), croppedOutImage.toByteArray(), x, y, w, h);

        } catch (IOException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    protected void encodeMarkup(FacesContext context, ImageCropper cropper) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = cropper.getClientId(context);
        String coordsHolderId = clientId + "_coords";

        writer.startElement("div", cropper);
        writer.writeAttribute("id", clientId, null);

        renderImage(context, (AdvancedImageCropper) cropper, clientId);

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", coordsHolderId, null);
        writer.writeAttribute("name", coordsHolderId, null);
        writer.endElement("input");

        writer.endElement("div");
    }

    private void renderImage(FacesContext context, AdvancedImageCropper cropper, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String alt = cropper.getAlt() == null ? "" : cropper.getAlt();
        String src = getImageSrc(context, cropper);

        writer.startElement("img", null);
        writer.writeAttribute("id", clientId + "_image", null);
        writer.writeAttribute("alt", alt, null);
        writer.writeAttribute("src", src, null);
        writer.endElement("img");
    }

    private boolean determineIfAdvancedRendering(AdvancedImageCropper cropper) {
        if (cropper.getImageValue() == null) {
            return false;
        }
        Boolean advancedMarker = (Boolean) cropper.getAttributes().get(AdvancedRendererHandler.ADVANCED_RENDERING);

        if (advancedMarker != null) {
            return advancedMarker;
        }

        return true;
    }

    protected String getImageSrc(FacesContext context, AdvancedImageCropper cropper) throws IOException {
        if (cropper.getImageValue() == null) {
            return getResourceURL(context, cropper.getImage());
        }
        if (determineIfAdvancedRendering(cropper)) {
            return buildAdvancedImageSrc(context, cropper);
        } else {
            return DynamicResourceBuilder.build(context, cropper.getImageValue(), cropper, true, DynamicContentType.STREAMED_CONTENT, true);
        }
    }

    private String buildAdvancedImageSrc(FacesContext context, AdvancedImageCropper cropper) throws UnsupportedEncodingException {
        String src;

        StreamedContent streamedContent = (StreamedContent) cropper.getImageValue();
        Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent", "advancedPrimefaces", streamedContent.getContentType());
        String resourcePath = resource.getRequestPath();
        StringEncrypter encrypter = RequestContext.getCurrentInstance().getEncrypter();

        ValueExpression expression = ValueExpressionAnalyzer.getExpression(context.getELContext(), cropper.getValueExpression("imageValue"));
        String rid = encrypter.encrypt(expression.getExpressionString() + cropper.getClientId(context));

        GraphicImageManager graphicImageManager = GraphicImageUtil.retrieveManager(context);
        graphicImageManager.registerImage(streamedContent, rid);

        StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);

        builder.append(resourcePath).append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(rid, "UTF-8"));

        for (UIComponent kid : cropper.getChildren()) {
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

        boolean cache = false;

        src += src.contains("?") ? "&" : "?";
        src += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + cache;

        if (!cache) {
            src += "&uid=" + UUID.randomUUID().toString();
        }

        return context.getExternalContext().encodeResourceURL(src);

    }
}
