package com.finall.cmt.utils;


import java.util.UUID;

/**
 * 通用的工具类
 */

public class CommonUtils {

    /**
     * 生成随机的uuid值，用做token
     */

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(uuid());
    }
}
