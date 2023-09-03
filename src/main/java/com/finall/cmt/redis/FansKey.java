package com.finall.cmt.redis;


public class FansKey extends BasePrefix {

    public FansKey(String prefix) {
        super(prefix);
    }

    public FansKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static FansKey fansKey = new FansKey("myFans");
}
