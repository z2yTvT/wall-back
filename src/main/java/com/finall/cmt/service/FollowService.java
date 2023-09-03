package com.finall.cmt.service;

import com.finall.cmt.entity.Follow;

import java.util.List;


public interface FollowService {


    void insertFollow(Follow follow);

    void deleteFollow(String userId, String followId);

    List<Follow> selectAllFollowByUserId(String userId);

    List<Follow> selectAllFollowByFollowId(String followId);
}
