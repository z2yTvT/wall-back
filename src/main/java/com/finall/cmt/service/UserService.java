package com.finall.cmt.service;

import com.finall.cmt.entity.User;

import java.util.List;


public interface UserService {

    void insert(User user);

    User selectByUserId(String userId);

    void updateByUserId(User user);

    void resetAchieveValue();

    List<User> top10LeaderBoard();

}
