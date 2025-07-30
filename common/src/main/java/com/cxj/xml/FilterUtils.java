/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Administrator
 */
public class FilterUtils {

    private static String filterString(String input, Map<String, Object> filters) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = filters.getOrDefault(name, "").toString();
            value = value.replaceAll("\\$", "\\\\\\$");
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * is is closed by this api!
     */
    @SuppressWarnings("XFB_XML_FACTORY_BYPASS")
    public static void filterXml(InputStream is, Writer writer, Map<String, Object> filters) throws IOException, SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        XMLReader filter = new XMLFilterImpl(reader) {

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                AttributesImpl attributesImpl = new AttributesImpl(atts);
                int length = attributesImpl.getLength();
                for (int i = 0; i < length; i++) {
                    String value = attributesImpl.getValue(i);
                    attributesImpl.setValue(i, filterString(value, filters));
                }
                super.startElement(uri, localName, qName, attributesImpl); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                String me = new String(ch, start, length);
                me = filterString(me, filters);
                char[] meChars = me.toCharArray();
                char[] chars = new char[ch.length - length + meChars.length];
                System.arraycopy(ch, 0, chars, 0, start);
                System.arraycopy(meChars, 0, chars, start, meChars.length);
                System.arraycopy(ch, start + length, chars, start + meChars.length, ch.length - start - length);
                super.characters(chars, start, meChars.length); //To change body of generated methods, choose Tools | Templates.
            }
        };
        new XMLWriter(filter, writer).parse(new InputSource(is));
    }

    /**
     * reader is closed by this api!
     */
    @SuppressWarnings("XFB_XML_FACTORY_BYPASS")
    public static void filterXml(Reader reader, Writer writer, Map<String, Object> filters) throws IOException, SAXException {
        XMLReader _reader = XMLReaderFactory.createXMLReader();
        XMLReader filter = new XMLFilterImpl(_reader) {

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                AttributesImpl attributesImpl = new AttributesImpl(atts);
                int length = attributesImpl.getLength();
                for (int i = 0; i < length; i++) {
                    String value = attributesImpl.getValue(i);
                    attributesImpl.setValue(i, filterString(value, filters));
                }
                super.startElement(uri, localName, qName, attributesImpl); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                String me = new String(ch, start, length);
                me = filterString(me, filters);
                char[] meChars = me.toCharArray();
                char[] chars = new char[ch.length - length + meChars.length];
                System.arraycopy(ch, 0, chars, 0, start);
                System.arraycopy(meChars, 0, chars, start, meChars.length);
                System.arraycopy(ch, start + length, chars, start + meChars.length, ch.length - start - length);
                super.characters(chars, start, meChars.length); //To change body of generated methods, choose Tools | Templates.
            }
        };
        new XMLWriter(filter, writer).parse(new InputSource(reader));
    }
}
