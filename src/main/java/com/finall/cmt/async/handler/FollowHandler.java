package com.finall.cmt.async.handler;

import com.finall.cmt.async.EventHandler;
import com.finall.cmt.async.EventModel;
import com.finall.cmt.async.EventType;
import com.finall.cmt.entity.Notice;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.NoticeService;
import com.finall.cmt.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
public class FollowHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(FollowHandler.class);

    @Autowired
    JedisService jedisService;

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @Autowired
    NoticeService noticeService;

    @Override
    public void doHandler(EventModel eventModel) {
        String fromId = eventModel.getActorId();
        String toId = eventModel.getEntityOwnerId();
        User userFrom = userService.selectByUserId(fromId);

        Notice notice = new Notice();
        notice.setFromId(fromId);
        notice.setToId(toId);
        notice.setContent(userFrom.getNickname() + "关注了用户你");
        notice.setConversationId(fromId + "_" + toId);
        notice.setCreatedDate(new Date());
        notice.setHasRead(0);
        logger.info("notice:{}", notice);
        noticeService.insertNotice(notice);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
