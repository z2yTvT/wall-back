package com.finall.cmt.service.impl;

import com.finall.cmt.dao.FansDao;
import com.finall.cmt.entity.Fans;
import com.finall.cmt.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FansServiceImpl implements FansService {

    @Autowired(required = false)
    FansDao fansDao;

    @Override
    public void insertFans(Fans fans) {
        fansDao.insertFans(fans);
    }

    @Override
    public void deleteFans(String userId, String fansId) {
        fansDao.deleteFans(userId, fansId);
    }

    @Override
    public List<Fans> selectAllFansByUserId(String userId) {
        return fansDao.selectAllFansByUserId(userId);
    }
}
