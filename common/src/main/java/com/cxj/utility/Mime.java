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
    TEXT_PLAIN("text/plain"),
    TEXT_CSS("text/css"),
    TEXT_HTML("text/html"),
    APPLICATION_XML("application/xml"),
    APPLICATION_JSON("application/json"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_MSEXCEL("application/vnd.ms-excel"),
    APPLICATION_MSPPT("application/vnd.ms-powerpoint"),
    APPLICATION_MSWORD("application/msword"),
    APPLICATION_DOCM("application/vnd.ms-word.document.macroEnabled.12"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_DOTM("application/vnd.ms-word.template.macroEnabled.12"),
    APPLICATION_DOTX("application/vnd.openxmlformats-officedocument.wordprocessingml.template"),
    APPLICATION_POTM("application/vnd.ms-powerpoint.template.macroEnabled.12"),
    APPLICATION_POTX("application/vnd.openxmlformats-officedocument.presentationml.template"),
    APPLICATION_PPAM("application/vnd.ms-powerpoint.addin.macroEnabled.12"),
    APPLICATION_PPSM("application/vnd.ms-powerpoint.slideshow.macroEnabled.12"),
    APPLICATION_PPSX("application/vnd.openxmlformats-officedocument.presentationml.slideshow"),
    APPLICATION_PPTM("application/vnd.ms-powerpoint.presentation.macroEnabled.12"),
    APPLICATION_PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    APPLICATION_XLAM("application/vnd.ms-excel.addin.macroEnabled.12"),
    APPLICATION_XLSB("application/vnd.ms-excel.sheet.binary.macroEnabled.12"),
    APPLICATION_XLSM("application/vnd.ms-excel.sheet.macroEnabled.12"),
    APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    APPLICATION_XLTM("application/vnd.ms-excel.template.macroEnabled.12"),
    APPLICATION_XLTX("application/vnd.openxmlformats-officedocument.spreadsheetml"),
    APPLICATION_XPS("application/vnd.ms-xpsdocument"),
    APPLICATION_WPS("application/kswps"),
    APPLICATION_ET("application/kset"),
    APPLICATION_DPS("application/ksdps"),
    APPLICATION_X_FONT_WOFF("application/x-font-woff"),
    AUDIO_AMR("audio/amr"),
    AUDIO_AMR_WB("audio/amr-wb"),
    AUDIO_X_WAV("audio/x-wav"),
    AUDIO_MP3("audio/mp3"),
    VEDIO_MPEG4("video/mpeg4"),
    IMAGE_BMP("image/bmp"),
    IMAGE_X_BMP("image/x-bmp"),
    IMAGE_MS_BMP("image/ms-bmp"),
    IMAGE_X_MS_BMP("image/x-ms-bmp"),
    IMAGE_X_WIN_BMP("image/x-windows-bmp"),
    IMAGE_COD("image/cis-cod"),
    IMAGE_GIF("image/gif"),
    IMAGE_IEF("image/ief"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PIPEG("image/pipeg"),
    IMAGE_PNG("image/png"),
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
    IMAGE_XWD("image/x-xwindowdump"),
    IMAGE_CUR("image/vnd.microsoft.icon"),
    APPLICATION_FTL("application/ftl"),
    APPLICATION_FTLX("application/ftlx"),
    APPLICATION_FTLH("application/ftlh"),
    APPLICATION_ANDROID("application/vnd.android.package-archive"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_X_RAR_COMPRESSED("application/x-rar-compressed");

    private final String value;

    Mime(String value) {
        this.value = value;
    }

    public static Mime from(String value) {
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

    public Ext ext() {
        switch (this) {
            case TEXT_PLAIN:
                return Ext.TXT;
            case TEXT_CSS:
                return Ext.CSS;
            case TEXT_HTML:
                return Ext.HTML;
            case APPLICATION_JSON:
                return Ext.JSON;
            case APPLICATION_MSWORD:
                return Ext.DOC;
            case APPLICATION_MSEXCEL:
                return Ext.XLS;
            case APPLICATION_MSPPT:
                return Ext.PPT;
            case APPLICATION_DOCM:
                return Ext.DOCM;
            case APPLICATION_DOCX:
                return Ext.DOCX;
            case APPLICATION_DOTM:
                return Ext.DOTM;
            case APPLICATION_DOTX:
                return Ext.DOTX;
            case APPLICATION_POTM:
                return Ext.POTM;
            case APPLICATION_POTX:
                return Ext.POTX;
            case APPLICATION_PPAM:
                return Ext.PPAM;
            case APPLICATION_PPSM:
                return Ext.PPSM;
            case APPLICATION_PPSX:
                return Ext.PPSX;
            case APPLICATION_PPTM:
                return Ext.PPTM;
            case APPLICATION_PPTX:
                return Ext.PPTX;
            case APPLICATION_XLAM:
                return Ext.XLAM;
            case APPLICATION_XLSB:
                return Ext.XLSB;
            case APPLICATION_XLSM:
                return Ext.XLSM;
            case APPLICATION_XLSX:
                return Ext.XLSX;
            case APPLICATION_XLTM:
                return Ext.XLTM;
            case APPLICATION_XLTX:
                return Ext.XLTX;
            case APPLICATION_XPS:
                return Ext.XPS;
            case APPLICATION_WPS:
                return Ext.WPS;
            case APPLICATION_ET:
                return Ext.ET;
            case APPLICATION_DPS:
                return Ext.DPS;
            case APPLICATION_PDF:
                return Ext.PDF;
            case APPLICATION_XML:
                return Ext.XML;
            case AUDIO_AMR:
                return Ext.AMR;
            case AUDIO_AMR_WB:
                return Ext.AWB;
            case AUDIO_X_WAV:
                return Ext.WAV;
            case AUDIO_MP3:
                return Ext.MP3;
            case VEDIO_MPEG4:
                return Ext.MP4;
            case IMAGE_BMP:
            case IMAGE_X_BMP:
            case IMAGE_MS_BMP:
            case IMAGE_X_MS_BMP:
            case IMAGE_X_WIN_BMP:
                return Ext.BMP;
            case IMAGE_COD:
                return Ext.COD;
            case IMAGE_GIF:
                return Ext.GIF;
            case IMAGE_IEF:
                return Ext.IEF;
            case IMAGE_JPEG:
                return Ext.JPEG;
            case IMAGE_PIPEG:
                return Ext.JFIF;
            case IMAGE_PNG:
                return Ext.PNG;
            case IMAGE_SVG:
                return Ext.SVG;
            case IMAGE_TIFF:
                return Ext.TIFF;
            case IMAGE_RAS:
                return Ext.RAS;
            case IMAGE_CMX:
                return Ext.CMX;
            case IMAGE_ICON:
                return Ext.ICO;
            case IMAGE_PNM:
                return Ext.PNM;
            case IMAGE_PBM:
                return Ext.PBM;
            case IMAGE_PGM:
                return Ext.PGM;
            case IMAGE_PPM:
                return Ext.PPM;
            case IMAGE_RGB:
                return Ext.RGB;
            case IMAGE_XBM:
                return Ext.XBM;
            case IMAGE_XPM:
                return Ext.XPM;
            case IMAGE_XWD:
                return Ext.XWD;
            case APPLICATION_FTL:
                return Ext.FTL;
            case APPLICATION_FTLH:
                return Ext.FTLH;
            case APPLICATION_FTLX:
                return Ext.FTLX;
            case APPLICATION_ANDROID:
                return Ext.APK;
            case APPLICATION_ZIP:
                return Ext.ZIP;
            case APPLICATION_X_RAR_COMPRESSED:
                return Ext.RAR;
            case WILDCARD:
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
