package com.finall.cmt.utils;


import java.util.Random;


public class GenerateRandomCode {

    /**
     * 6位的验证码
     */
    private static int VERIFY_NUM = 6;
    /**
     * 随机生成一个6位数的验证码
     *
     * @return
     */
    public static String generateRandomVerificationCode() {

        Random random = new Random();
        StringBuilder randomCode = new StringBuilder();

        for (int i = 0; i < VERIFY_NUM; i++) {
            randomCode.append(random.nextInt(10));
        }
        System.out.println("你的验证码为:" + randomCode.toString());
        return randomCode.toString();

    }

}
