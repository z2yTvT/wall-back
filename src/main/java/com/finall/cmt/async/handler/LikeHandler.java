package com.finall.cmt.async.handler;

import com.finall.cmt.async.EventHandler;
import com.finall.cmt.async.EventModel;
import com.finall.cmt.async.EventType;
import com.finall.cmt.entity.Article;
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
public class LikeHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(LikeHandler.class);

    @Autowired
    JedisService jedisService;

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @Autowired
    NoticeService noticeService;

    /**
     * 主要是完成通知，通知用户谁给给点了赞
     *
     * @param eventModel
     */
    @Override
    public void doHandler(EventModel eventModel) {

        String fromId = eventModel.getActorId();
        User user = userService.selectByUserId(fromId);

        String toId = eventModel.getEntityOwnerId();

        int articleId = Integer.valueOf(eventModel.getExts("articleId"));
        logger.info(toId);
        logger.info(String.valueOf(articleId));

        Article article = articleService.selectArticleByTwoUserId(articleId);
        logger.info("文章信息为:{}", article);

        Notice notice = new Notice();
        notice.setFromId(fromId);
        notice.setToId(toId);
        notice.setCreatedDate(new Date());
        notice.setContent(user.getNickname() + "点赞了您的文章：" + article.getArticleTitle());
        notice.setConversationId(fromId + "_" + toId);
        logger.info("notice:{}", notice);
        noticeService.insertNotice(notice);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        // 只关注点赞的事件
        return Arrays.asList(EventType.LIKE);
    }
}
