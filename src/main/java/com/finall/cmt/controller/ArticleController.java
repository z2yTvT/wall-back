package com.finall.cmt.controller;

import com.finall.cmt.dao.ElasticSearchDao;
import com.finall.cmt.entity.Article;
import com.finall.cmt.entity.Category;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.CategoryService;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.CodeMsg;
import com.finall.cmt.utils.Result;
import com.finall.cmt.utils.SensitiveFilter;
import com.finall.cmt.vo.ArticleUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 设计：
 * 当用户发表一篇文章的时候，成就值+10分
 * 当用户的文章被别人点赞一次之后，成就值+5分
 * 当用户的文章被别人评论一次之后，成就值+5分
 * 当用户被一个人关注后，成就值+10分
 */
@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @Autowired
    JedisService jedisService;

    @Autowired
    LoginController loginController;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private ElasticSearchDao elasticSearchDao;

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    @PostMapping("/insert")
    @ResponseBody
    @Transient
    public Result<Boolean> insertArticle(HttpServletRequest request, @RequestBody Article article) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            log.info("用户未登录");
            return Result.error(CodeMsg.NOT_LOGIN);
        }

        log.info(article.toString());
        Category category = categoryService.selectCategoryByName(article.getArticleCategoryName());
        log.info("category对象为：{}", category.toString());

        article.setArticleCategoryId(category.getCategoryId());
        article.setCreatedTime(new Date());
        article.setUpdateTime(new Date());
        article.setArticleUserId(user.getUserId());

        // 发表一篇文章用户的成就值+10分
        User publishUser = userService.selectByUserId(user.getUserId());
        publishUser.setAchieveValue(publishUser.getAchieveValue() + 10);
        userService.updateByUserId(publishUser);
        article.setArticleContent(sensitiveFilter.filter(article.getArticleContent()));
        article.setArticleTitle(sensitiveFilter.filter(article.getArticleTitle()));
        articleService.insertArticle(article);
        elasticSearchDao.save(article);
        return Result.success(true);
    }

    @GetMapping("/can/edit")
    @ResponseBody
    public Result<Boolean> canEditArticle(HttpServletRequest request, int articleId) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            log.info("用户未登录");
            return Result.error(CodeMsg.NOT_LOGIN);
        }
        Article article = articleService.selectArticleByArticleId(articleId);
        if (article.getArticleUserId().equals(user.getUserId())) {
            return Result.success(true);
        } else {
            return Result.success(false);
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public Result<Boolean> editArticle(@RequestBody Article article) {
        articleService.updateArticle(article);
        return Result.success(true);
    }

    @GetMapping("/delete")
    @ResponseBody
    public Result<Boolean> deleteArticle(HttpServletRequest request, int articleId) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            log.info("用户未登录");
            return Result.error(CodeMsg.NOT_LOGIN);
        }
        Article article = articleService.selectArticleByArticleId(articleId);
        if (article.getArticleUserId().equals(user.getUserId()) || "1".equals(user.getUserId())) {
            articleService.deleteArticle(articleId);
            return Result.success(true);
        } else {
            return Result.success(false);
        }

    }

    @GetMapping("/search")
    @ResponseBody
    public Result<List<ArticleUserVo>> searchArticle(String keyword) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ArticleUserVo> articleList = articleService.selectArticleByKeywords(keyword);
        stopWatch.stop();
        log.info("使用ES来搜索文章的耗时为：{}ms", stopWatch.getTotalTimeMillis());
        return Result.success(articleList);
    }

    @GetMapping("/search/mysql")
    @ResponseBody
    public Result<List<ArticleUserVo>> searchArticleByMysql(String keyword) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ArticleUserVo> articleList = articleService.selectArticleByKeyword(keyword);
        stopWatch.stop();
        log.info("使用Mysql的模糊查询来搜索文章的耗时为：{}ms", stopWatch.getTotalTimeMillis());
        return Result.success(articleList);
    }

    /**
     * 展示10条热点文章，热点文章是根据文章的浏览量来进行排序
     * @return
     */
    @GetMapping("/hotspot")
    @ResponseBody
    public Result<List<ArticleUserVo>> hostSpotArticle() {
        List<ArticleUserVo> articleList = articleService.selectArticleByViewCount();
        return Result.success(articleList);
    }

    @GetMapping("/select/school/category/{categoryId}")
    @ResponseBody
    public Result<List<ArticleUserVo>> SameSchoolArticle(HttpServletRequest request, @PathVariable("categoryId") int categoryId) {

        User user = loginController.getUserInfo(request);
        log.info("userInfo：{}",user.toString());
        String schoolName = user.getSchool();
        log.info("schoolName is :{}", schoolName);

        List<ArticleUserVo> articleUserVoList = articleService.selectArticleBySchool(schoolName,categoryId);

        return Result.success(articleUserVoList);
    }
}
