package com.junliang.spring.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 请使用下面的工具类
 * org.apache.commons.codec.digest.DigestUtils
 *
 */
public class DigestUtil {

    private static final String MD2 = "MD2";

    private static final String MD5 = "MD5";

    private static final String SHA1 = "SHA-1";

    private static final String SHA256 = "SHA-256";

    private static final String SHA512 = "SHA-512";


    public static String md2(String value) {
        return digest(value, MD2);
    }

    public static String md5(String value) {
        return digest(value, MD5);
    }

    public static String sha1(String value) {
        return digest(value, SHA1);
    }

    public static String sha256(String value) {
        return digest(value, SHA256);
    }

    public static String sha512(String value) {
        return digest(value, SHA512);
    }

    public static String digest(String data, String algorithm) {
        // 是否是有效字符串
        if (StringUtils.isEmpty(data))
            return data;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            byte[] e = md.digest(data.getBytes("utf-8"));
            return toHexString(e);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return data;
        }
    }

    private static String toHexString(byte bytes[]) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte aByte : bytes) {
            stmp = Integer.toHexString(aByte & 0xff);
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString();
    }
}
