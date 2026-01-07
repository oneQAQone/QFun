package com.tencent.commonsdk.util;

public class HexUtil {
    private static final char[] digits;
    public static final byte[] emptybytes;

    static {
        digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        emptybytes = new byte[0];
    }

    public static String byte2HexStr(byte b) {
        char[] cArr = digits;
        return new String(new char[]{cArr[((byte) (b >>> 4)) & 15], cArr[b & 15]});
    }

    public static String bytes2HexStr(byte[] bArr) {
        if (bArr != null && bArr.length != 0) {
            char[] cArr = new char[bArr.length * 2];
            for (int i = 0; i < bArr.length; i++) {
                byte b = bArr[i];
                int i2 = i * 2;
                char[] cArr2 = digits;
                cArr[i2 + 1] = cArr2[b & 15];
                cArr[i2] = cArr2[((byte) (b >>> 4)) & 15];
            }
            return new String(cArr);
        }
        return null;
    }

    public static byte char2Byte(char c) {
        int i;
        if (c >= '0' && c <= '9') {
            i = c - '0';
        } else {
            char c2 = 'a';
            if (c < 'a' || c > 'f') {
                c2 = 'A';
                if (c < 'A' || c > 'F') {
                    return (byte) 0;
                }
            }
            i = (c - c2) + 10;
        }
        return (byte) i;
    }

    public static byte hexStr2Byte(String str) {
        if (str == null || str.length() != 1) {
            return (byte) 0;
        }
        return char2Byte(str.charAt(0));
    }

    public static byte[] hexStr2Bytes(String str) {
        if (str != null && !str.isEmpty()) {
            int length = str.length() / 2;
            byte[] bArr = new byte[length];
            for (int i = 0; i < length; i++) {
                int i2 = i * 2;
                bArr[i] = (byte) ((char2Byte(str.charAt(i2)) * 16) + char2Byte(str.charAt(i2 + 1)));
            }
            return bArr;
        }
        return emptybytes;
    }
}
