package com.finall.cmt.redis;


public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();
}
