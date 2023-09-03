package com.finall.cmt.async.handler;

import com.finall.cmt.async.EventHandler;
import com.finall.cmt.async.EventModel;
import com.finall.cmt.async.EventType;
import com.finall.cmt.entity.Article;
import com.finall.cmt.entity.Comment;
import com.finall.cmt.entity.Notice;
import com.finall.cmt.entity.User;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.CommentService;
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
public class CommentHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommentHandler.class);

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    CommentService commentService;

    @Override
    public void doHandler(EventModel eventModel) {

        String fromId = eventModel.getActorId();
        User user = userService.selectByUserId(fromId);
        String toId = eventModel.getEntityOwnerId();
        int articleId = Integer.valueOf(eventModel.getExts("articleId"));
        Article article = articleService.selectArticleByTwoUserId(articleId);
        logger.info("文章信息为:{}", article);

        int commentId = Integer.valueOf(eventModel.getExts("commentId"));
        Comment comment = commentService.selectCommentById(commentId);
        logger.info("评论信息为:{}", comment);

        Notice notice = new Notice();
        notice.setFromId(fromId);
        notice.setToId(toId);
        notice.setCreatedDate(new Date());
        notice.setContent(user.getNickname() + "评论您的文章：" + article.getArticleTitle() +
                ",评论的内容为：" + comment.getCommentContent());
        notice.setConversationId(fromId + "_" + toId);
        logger.info("notice:{}", notice);
        noticeService.insertNotice(notice);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMNET);
    }
}
