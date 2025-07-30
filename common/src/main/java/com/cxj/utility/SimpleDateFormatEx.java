package com.cxj.utility;

import com.google.common.base.Strings;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 陈晓靖 on 2018/5/3 17:32
 */
public class SimpleDateFormatEx {
    private static Pattern PT_NUM = Pattern.compile("\\.(\\d{3})(\\d+)");
    private SimpleDateFormat dateFormat;
    private final String pattern;

    public SimpleDateFormatEx(String pattern, Locale locale) {
        this.pattern = pattern;
        this.dateFormat = new SimpleDateFormat(pattern, locale);
    }

    Date parse(String source) throws ParseException {
        String ns = null;
        if (pattern.contains(".S")) {
            int index = source.lastIndexOf('.');
            Matcher matcher = PT_NUM.matcher(source.substring(index));
            if (matcher.find()) {
                StringBuffer sb = new StringBuffer(source.substring(0, index));
                matcher.appendReplacement(sb, ".$1");
                matcher.appendTail(sb);
                source = sb.toString();
                ns = matcher.group(2);
            }
        }
        Date date = dateFormat.parse(source);
        if (ns != null) {
            date = new Timestamp(date.getTime());
            int len = ns.length();
            if (len > 6) {
                ns = ns.substring(0, 6);
            } else if (len < 6) {
                ns = ns + Strings.repeat("0", 6 - len);
            }
            ((Timestamp) date).setNanos(((Timestamp) date).getNanos() + Integer.parseInt(ns));
        }
        return date;
    }
}
