package com.finall.cmt.redis;


public class FollowKey extends BasePrefix {

    public FollowKey(String prefix) {
        super(prefix);
    }

    public FollowKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 我的关注
     */
    public static FollowKey followKey = new FollowKey("myFollow");
}
