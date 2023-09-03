package com.finall.cmt.controller;

import com.finall.cmt.async.EventModel;
import com.finall.cmt.async.EventProducer;
import com.finall.cmt.async.EventType;
import com.finall.cmt.entity.Article;
import com.finall.cmt.entity.Comment;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.CommentService;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.CodeMsg;
import com.finall.cmt.utils.Result;
import com.finall.cmt.utils.SensitiveFilter;
import com.finall.cmt.vo.CommentUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    LoginController loginController;

    @Autowired
    JedisService jedisService;

    @Autowired
    ArticleService articleService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 在文章的详情页进行评论, 从前端界面传过来文章id以及评论的内容
     */
    @PostMapping("/insert/comment")
    @ResponseBody
    public Result<Boolean> commentArticle(HttpServletRequest request, @RequestParam(value
            = "articleId", required = false) Integer articleId, @RequestParam(value = "content", required = false) String content) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            return Result.error(CodeMsg.ERROR);
        }

        Comment comment = new Comment();
        comment.setCommentArticleId(articleId);
        comment.setCommentUserId(user.getUserId());
        comment.setCommentContent(sensitiveFilter.filter(content));
        comment.setCommentCreatedTime(new Date());
        commentService.insertComment(comment);

        Article article = articleService.selectArticleByArticleId(articleId);
        article.setArticleCommentCount(article.getArticleCommentCount() + 1);
        articleService.updateArticle(article);

        EventModel eventModel = new EventModel();

        String articleAuthor = articleService.selectArticleByTwoUserId(articleId).getArticleUserId();
        // 获取Comment表中最新的comment_id,即表示当前的comment对象
        int commentId = commentService.selectLastInsertCommentId();
        logger.info("评论的id:" + commentId);

        eventModel.setActorId(user.getUserId()).setEntityType(1).setEntityId(1).setEntityOwnerId(articleAuthor)
                .setEventType(EventType.COMMNET).setExts("articleId", articleId + "").
                setExts("commentId", commentId + "");
        // 将该评论异步通知给文章的作者
        eventProducer.fireEvent(eventModel);

        // 获取文章作者信息，然后更新文章的成就值
        User publishUser = userService.selectByUserId(articleAuthor);
        publishUser.setAchieveValue(publishUser.getAchieveValue() + 10);
        userService.updateByUserId(publishUser);


        return Result.success(true);


    }

    @GetMapping("/comment/list")
    @ResponseBody
    public Result<List<CommentUserVo>> commentArticleLists(int articleId) {
        List<CommentUserVo> commentUserVoList = commentService.selectCommentLists(articleId);
        return Result.success(commentUserVoList);
    }
}
