package com.finall.cmt.controller;

import com.finall.cmt.async.EventModel;
import com.finall.cmt.async.EventProducer;
import com.finall.cmt.async.EventType;
import com.finall.cmt.entity.Article;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.LikeKey;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.LikeService;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.CodeMsg;
import com.finall.cmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LikeController {

    @Autowired
    JedisService jedisService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    LoginController loginController;

    @Autowired
    LikeService likeService;

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;


    @GetMapping("/like")
    @ResponseBody
    public Result<Long> likeArticle(HttpServletRequest request, int articleId) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            return Result.error(CodeMsg.ERROR);
        }
        // 进入这里说明用户是登录过了，然后把用户的相关信息存储到Redis的Set中
        String real = LikeKey.LIKE_KEY.getPrefix() + articleId;
        // userId可以防止用户重复点赞
        long likeCount = likeService.like(real, user.getUserId());

        Article article = articleService.selectArticleByTwoUserId(articleId);
        String articleAuthor = article.getArticleUserId();
        EventModel eventModel = new EventModel();
        // 点完赞之后，理解异步通知给用户
        eventProducer.fireEvent(eventModel.setEventType(EventType.LIKE).setActorId(user.getUserId())
                .setEntityType(1).setEntityId(2).setEntityOwnerId(articleAuthor).setExts("articleId", articleId + ""));
        // 点完站之后，articleCount的数据也会对应进行增加，这里使用Quartz设置每多长时间将redis中的数据
        // 更新到mysql中，在优化阶段每隔一小时将redis中存的点赞数量更新到数据库中去
        // article.setArticleLikeCount(article.getArticleLikeCount() + 1);

        // 首先根据文章的id, 查找出这篇文章的发布者，然后通过文章发布者的id查找出user对象，然后更新其成就值
        User publishUser = userService.selectByUserId(articleAuthor);
        publishUser.setAchieveValue(publishUser.getAchieveValue() + 5);
        userService.updateByUserId(publishUser);
        return Result.success(likeCount);
    }


    @GetMapping("/dislike")
    @ResponseBody
    public Result<Long> dislikeArticle(HttpServletRequest request, int articleId) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            return Result.error(CodeMsg.ERROR);
        }

        // 进入这里说明用户是登录过了，然后把用户的相关信息存储到Redis的Set中
        String real = LikeKey.LIKE_KEY.getPrefix() + articleId;
        // 这里的值，应该是userId，因为使用userId可以防止用户重复点赞
        long likeCount = likeService.dislike(real, user.getUserId());

        return Result.success(likeCount);
    }
}
