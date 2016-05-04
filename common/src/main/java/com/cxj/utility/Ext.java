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
public enum Ext {

    BMP("bmp"),
    COD("cod"),
    GIF("gif"),
    IEF("ief"),
    JPE("jpe"),
    JPEG("jpeg"),
    JPG("jpg"),
    JFIF("jfif"),
    SVG("svg"),
    TIF("tif"),
    TIFF("tiff"),
    RAS("ras"),
    CMX("cmx"),
    ICO("ico"),
    PNM("pnm"),
    PBM("pbm"),
    PGM("pgm"),
    PPM("ppm"),
    RGB("rgb"),
    XBM("xbm"),
    XPM("xpm"),
    XWD("xwd");

    private final String value;

    private Ext(String value) {
        this.value = value;
    }

    public static Ext from(String ext) {
        return Enum.valueOf(Ext.class, ext.toUpperCase());
    }

    public Mime mimeType() {
        switch (this) {
            case BMP:
                return Mime.IMAGE_BMP;
            case COD:
                return Mime.IMAGE_COD;
            case GIF:
                return Mime.IMAGE_GIF;
            case IEF:
                return Mime.IMAGE_IEF;
            case JPE:
                return Mime.IMAGE_JPEG;
            case JPEG:
                return Mime.IMAGE_JPEG;
            case JPG:
                return Mime.IMAGE_JPEG;
            case JFIF:
                return Mime.IMAGE_PIPEG;
            case SVG:
                return Mime.IMAGE_SVG;
            case TIF:
                return Mime.IMAGE_TIFF;
            case TIFF:
                return Mime.IMAGE_TIFF;
            case RAS:
                return Mime.IMAGE_RAS;
            case CMX:
                return Mime.IMAGE_CMX;
            case ICO:
                return Mime.IMAGE_ICON;
            case PNM:
                return Mime.IMAGE_PNM;
            case PBM:
                return Mime.IMAGE_PBM;
            case PGM:
                return Mime.IMAGE_PGM;
            case PPM:
                return Mime.IMAGE_PPM;
            case RGB:
                return Mime.IMAGE_RGB;
            case XBM:
                return Mime.IMAGE_XBM;
            case XPM:
                return Mime.IMAGE_XPM;
            case XWD:
                return Mime.IMAGE_XWD;
            default:
                return Mime.WILDCARD;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
