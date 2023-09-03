package com.finall.cmt.redis;


public class VerifyCodeKey extends BasePrefix {

    public VerifyCodeKey(String prefix) {
        super(prefix);
    }

    public VerifyCodeKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static VerifyCodeKey verifyCodeKeyRegister = new VerifyCodeKey(180, "register");
    public static VerifyCodeKey verifyCodeKeyLogin = new VerifyCodeKey(180, "login");
}
