package com.xuecheng.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Md5Util {
    private static final int BUFFER_SIZE = 8192;

    /**
     * 计算输入流的 MD5 值
     */
    public static String getFileMd5(InputStream inputStream) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = inputStream.read(buffer)) != -1) {
            md.update(buffer, 0, n);
        }
        byte[] digest = md.digest();

        // 转换成 32 位十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 计算字符串的 MD5 值
     */
    public static String getStringMd5(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
