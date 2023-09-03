package com.finall.cmt.service.impl;

import com.finall.cmt.dao.NoticeDao;
import com.finall.cmt.entity.Notice;
import com.finall.cmt.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired(required = false)
    NoticeDao noticeDao;

    @Override
    public void insertNotice(Notice notice) {
        noticeDao.insertNotice(notice);
    }

    @Override
    public Notice selectNotice() {
        return noticeDao.selectAllNotice();
    }

    @Override
    public void updateAllNoticeHasRead(String userId) {
        noticeDao.updateAllNoticeHasRead(userId);
    }

    @Override
    public int countNoticeHasRead(String userId) {
        return noticeDao.countNoticeHasRead(userId);
    }

    @Override
    public List<Notice> noticeList(String userId) {
        return noticeDao.noticeList(userId);
    }
}
