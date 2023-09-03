package com.finall.cmt.service;

import com.finall.cmt.entity.Notice;

import java.util.List;


public interface NoticeService {

    void insertNotice(Notice notice);

    Notice selectNotice();

    void updateAllNoticeHasRead(String userId);

    int countNoticeHasRead(String userId);

    List<Notice> noticeList(String userId);
}
