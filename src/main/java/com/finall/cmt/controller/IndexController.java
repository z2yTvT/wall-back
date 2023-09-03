package com.finall.cmt.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.finall.cmt.entity.Article;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.LikeKey;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.utils.Result;
import com.finall.cmt.vo.ArticleUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class IndexController {

    private static Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    ArticleService articleService;

    @Autowired
    JedisService jedisService;

    /**
     * 首页
     */
    @GetMapping("/")
    @ResponseBody
    public Result<List<ArticleUserVo>> index() {
        List<ArticleUserVo> articleList = articleService.selectAllArticleIndexViewData();
        return Result.success(articleList);
    }

    /**
     * 文章的详情页
     *
     * @param articleId
     * @return
     */
    @GetMapping("/detail/{articleId}")
    @ResponseBody
    public Result<ArticleUserVo> articleDetail(@PathVariable("articleId") int articleId) {

        Article article = articleService.selectArticleByArticleId(articleId);
        article.setArticleViewCount(article.getArticleViewCount() + 1);
        articleService.updateArticle(article);

        // 这里查出来的数据，是从数据库查询出来的暂时没有like的数据，redis的数据才是实时最新的数据
        ArticleUserVo articleUserVo = articleService.selectAllArticleDetail(articleId);
        String likeKey = LikeKey.LIKE_KEY.getPrefix() + articleId;
        int likeCount = (int) jedisService.scard(likeKey);

        log.info("从Redis获取点赞数为：" + likeCount);
        articleUserVo.setArticleLikeCount(likeCount);

        return Result.success(articleUserVo);
    }

    @GetMapping("/category/{categoryId}")
    @ResponseBody
    public Result<IPage<ArticleUserVo>> articleCategory(@PathVariable("categoryId") int categoryId) {
        IPage<ArticleUserVo> articleUserVoIPage = articleService.selectAllArticleCategoryData(categoryId);
        return Result.success(articleUserVoIPage);
    }
}
