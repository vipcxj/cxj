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

    WOFF("woff"),
    CUR("cur"),
    AMR("amr"),
    APK("apk"),
    AWB("awb"),
    WAV("wav"),
    MP3("mp3"),
    MP4("mp4"),
    CSS("css"),
    TXT("txt"),
    PDF("pdf"),
    XLS("xls"),
    PPT("ppt"),
    DOC("doc"),
    DOT("dot"),
    DOCM("docm"),
    DOCX("docx"),
    DOTM("dotm"),
    DOTX("dotx"),
    POTM("potm"),
    POTX("potx"),
    PPAM("ppam"),
    PPSM("ppsm"),
    PPSX("ppsx"),
    PPTM("pptm"),
    PPTX("pptx"),
    XLAM("xlam"),
    XLSB("xlsb"),
    XLSM("xlsm"),
    XLSX("xlsx"),
    XLTM("xltm"),
    XLTX("xltx"),
    XPS("xps"),
    WPS("wps"),
    ET("et"),
    DPS("dps"),
    JSON("json"),
    XML("xml"),
    HTM("htm"),
    HTML("html"),
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
    PNG("png"),
    PPM("ppm"),
    RGB("rgb"),
    XBM("xbm"),
    XPM("xpm"),
    XWD("xwd"),
    FTL("ftl"),
    FTLH("ftlh"),
    FTLX("ftlx"),
    ZIP("zip"),
    RAR("rar");

    private final String value;

    Ext(String value) {
        this.value = value;
    }

    public static Ext from(String ext) {
        return Enum.valueOf(Ext.class, ext.toUpperCase());
    }

    public Mime mimeType() {
        switch (this) {
            case AMR:
                return Mime.AUDIO_AMR;
            case APK:
                return Mime.APPLICATION_ANDROID;
            case AWB:
                return Mime.AUDIO_AMR_WB;
            case WAV:
                return Mime.AUDIO_X_WAV;
            case MP3:
                return Mime.AUDIO_MP3;
            case MP4:
                return Mime.VEDIO_MPEG4;
            case WOFF:
                return Mime.APPLICATION_X_FONT_WOFF;
            case CUR:
                return Mime.IMAGE_CUR;
            case TXT:
                return Mime.TEXT_PLAIN;
            case CSS:
                return Mime.TEXT_CSS;
            case PDF:
                return Mime.APPLICATION_PDF;
            case XLS:
                return Mime.APPLICATION_MSEXCEL;
            case PPT:
                return Mime.APPLICATION_MSPPT;
            case DOC:
            case DOT:
                return Mime.APPLICATION_MSWORD;
            case DOCM:
                return Mime.APPLICATION_DOCM;
            case DOCX:
                return Mime.APPLICATION_DOCX;
            case DOTM:
                return Mime.APPLICATION_DOTM;
            case DOTX:
                return Mime.APPLICATION_DOTX;
            case POTM:
                return Mime.APPLICATION_POTM;
            case POTX:
                return Mime.APPLICATION_POTX;
            case PPAM:
                return Mime.APPLICATION_PPAM;
            case PPSM:
                return Mime.APPLICATION_PPSM;
            case PPSX:
                return Mime.APPLICATION_PPSX;
            case PPTM:
                return Mime.APPLICATION_PPTM;
            case PPTX:
                return Mime.APPLICATION_PPTX;
            case XLAM:
                return Mime.APPLICATION_XLAM;
            case XLSB:
                return Mime.APPLICATION_XLSB;
            case XLSM:
                return Mime.APPLICATION_XLSM;
            case XLSX:
                return Mime.APPLICATION_XLSX;
            case XLTM:
                return Mime.APPLICATION_XLTM;
            case XLTX:
                return Mime.APPLICATION_XLTX;
            case XPS:
                return Mime.APPLICATION_XPS;
            case WPS:
                return Mime.APPLICATION_WPS;
            case ET:
                return Mime.APPLICATION_ET;
            case DPS:
                return Mime.APPLICATION_DPS;
            case JSON:
                return Mime.APPLICATION_JSON;
            case XML:
                return Mime.APPLICATION_XML;
            case HTM:
            case HTML:
                return Mime.TEXT_HTML;
            case BMP:
                return Mime.IMAGE_BMP;
            case COD:
                return Mime.IMAGE_COD;
            case GIF:
                return Mime.IMAGE_GIF;
            case IEF:
                return Mime.IMAGE_IEF;
            case JPE:
            case JPG:
            case JPEG:
                return Mime.IMAGE_JPEG;
            case JFIF:
                return Mime.IMAGE_PIPEG;
            case SVG:
                return Mime.IMAGE_SVG;
            case TIF:
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
            case PNG:
                return Mime.IMAGE_PNG;
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
            case FTL:
                return Mime.APPLICATION_FTL;
            case FTLH:
                return Mime.APPLICATION_FTLH;
            case FTLX:
                return Mime.APPLICATION_FTLX;
            case ZIP:
                return Mime.APPLICATION_ZIP;
            case RAR:
                return Mime.APPLICATION_X_RAR_COMPRESSED;
            default:
                return Mime.WILDCARD;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
