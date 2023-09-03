package com.finall.cmt.service;

import com.finall.cmt.entity.Fans;

import java.util.List;


public interface FansService {

    void insertFans(Fans fans);

    void deleteFans(String userId, String followId);

    List<Fans> selectAllFansByUserId(String userId);
}
