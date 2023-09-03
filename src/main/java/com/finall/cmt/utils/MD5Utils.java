package com.finall.cmt.utils;


import org.apache.commons.codec.digest.DigestUtils;


public class MD5Utils {
    // 使用MD5对密码进行加密操作
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }
}
