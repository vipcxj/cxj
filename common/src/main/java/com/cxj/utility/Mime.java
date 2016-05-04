/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

/**
 *
 * @author Administrator
 */
public enum Mime {

    WILDCARD("*/*"),
    IMAGE_BMP("image/bmp"),
    IMAGE_COD("image/cis-cod"),
    IMAGE_GIF("image/gif"),
    IMAGE_IEF("image/ief"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PIPEG("image/pipeg"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_TIFF("image/tiff"),
    IMAGE_RAS("image/x-cmu-raster"),
    IMAGE_CMX("image/x-cmx"),
    IMAGE_ICON("image/x-icon"),
    IMAGE_PNM("image/x-portable-anymap"),
    IMAGE_PBM("image/x-portable-bitmap"),
    IMAGE_PGM("image/x-portable-graymap"),
    IMAGE_PPM("image/x-portable-pixmap"),
    IMAGE_RGB("image/x-rgb"),
    IMAGE_XBM("image/x-xbitmap"),
    IMAGE_XPM("image/x-xpixmap"),
    IMAGE_XWD("image/x-xwindowdump");

    private final String value;

    private Mime(String value) {
        this.value = value;
    }

    public Mime from(String value) {
        if (value == null) {
            return WILDCARD;
        }
        for (Mime mime : Mime.values()) {
            if (value.toLowerCase().equals(mime.value.toLowerCase())) {
                return mime;
            }
        }
        return WILDCARD;
    }

    @Override
    public String toString() {
        return value;
    }

}
