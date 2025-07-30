/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Administrator
 */
public class ConversionHelper {

    final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(@Nonnull byte[] bytes) {
        return bytesToHex(bytes, false);
    }

    public static String bytesToHex(@Nonnull byte[] bytes, boolean head) {
        assert bytes.length > 0;
        int offset = head ? 2 : 0;
        char[] hexChars = new char[bytes.length * 2 + offset];
        if (head) {
            hexChars[0] = '0';
            hexChars[1] = 'x';
        }
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2 + offset] = hexArray[v >>> 4];
            hexChars[j * 2 + 1 + offset] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(@Nonnull String s) {
        s = s.trim();
        boolean head = false;
        if (s.startsWith("0x")) {
            head = true;
        }
        int offset = head ? 2 : 0;
        int len = s.length() - offset;
        if ((len & 1) == 1) {
            s = StringUtils.leftPad(s.substring(2), ++len, '0');
            offset = 0;
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i + offset), 16) << 4)
                    + Character.digit(s.charAt(i + 1 + offset), 16));
        }
        return data;
    }
}
