package com.finall.cmt.service.impl;

import com.finall.cmt.dao.FollowDao;
import com.finall.cmt.entity.Follow;
import com.finall.cmt.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FollowServiceImpl implements FollowService {

    @Autowired(required = false)
    FollowDao followDao;


    @Override
    public void insertFollow(Follow follow) {
        followDao.insertFollow(follow);
    }

    @Override
    public void deleteFollow(String userId, String followId) {
        followDao.deleteFollow(userId, followId);
    }

    @Override
    public List<Follow> selectAllFollowByUserId(String userId) {
        return followDao.selectAllFollowByUserId(userId);
    }

    @Override
    public List<Follow> selectAllFollowByFollowId(String followId) {
        return followDao.selectAllFollowByFollowId(followId);
    }
}
