package com.finall.cmt.service.impl;

import com.finall.cmt.dao.UserDao;
import com.finall.cmt.entity.User;
import com.finall.cmt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    UserDao userDao;

    @Override
    public void insert(User user) {
        userDao.insertUser(user);
    }

    @Override
    public User selectByUserId(String userId) {
        return userDao.selectByUserId(userId);
    }

    @Override
    public void updateByUserId(User user) {
        userDao.updateByUserId(user);
    }

    @Override
    public void resetAchieveValue() {
        userDao.resetAchieveValue();
    }

    @Override
    public List<User> top10LeaderBoard() {
        return userDao.top10LeaderBoard();
    }


}
