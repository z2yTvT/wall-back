package com.finall.cmt.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.finall.cmt.dao.ArticleDao;
import com.finall.cmt.dao.ElasticSearchDao;
import com.finall.cmt.entity.Article;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.LikeKey;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.UserService;
import com.finall.cmt.vo.ArticleUserVo;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ArticleServiceImpl implements ArticleService {

    private static Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired(required = false)
    private ArticleDao articleDao;

    @Autowired(required = false)
    private ElasticSearchDao elasticSearchDao;

    @Autowired
    UserService userService;

    @Autowired
    JedisService jedisService;


    @Override
    public void insertArticle(Article article) {
        articleDao.insertArticle(article);
    }

    @Override
    public void updateArticle(Article article) {
        articleDao.updateArticle(article);
    }

    @Override
    public void deleteArticle(int articleId) {
        articleDao.deleteArticle(articleId);
    }

    @Override
    public Article selectArticleByArticleId(int articleId) {
        return articleDao.selectArticleByArticleId(articleId);
    }

    @Override
    public List<Article> selectArticleByCategoryId(int categoryId) {
        return articleDao.selectArticleByCategoryId(categoryId);
    }

    @Override
    public List<Article> selectArticleByLikeCount() {
        return articleDao.selectArticleByLikeCount();
    }

    @Override
    public List<Article> selectArticleByCommentCount() {
        return articleDao.selectArticleByCommentCount();
    }

    @Override
    public List<ArticleUserVo> selectArticleByViewCount() {
        return articleDao.selectArticleByViewCount();
    }

    @Override
    public List<ArticleUserVo> selectArticleBySchool(String schoolName, int categoryId) {
        return articleDao.selectArticleBySchool(schoolName, categoryId);
    }

    @Override
    public List<ArticleUserVo> selectArticleByKeyword(String keyword) {
        // 这个是从数据库查询出来除了点赞数量的数据， 点赞量从Redis中获取
        List<Article> articleList = articleDao.selectArticleByKeyword(keyword);
        return dealWithArticleVo(articleList);
    }

    @Override
    public Article selectArticleByTwoUserId(int articleId) {
        return articleDao.selectArticleByTwoUserId(articleId);
    }

    @Override
    public List<Article> selectAllArtilce() {
        return articleDao.selectAllArticle();
    }

    @Override
    public List<Article> selectAllArticleByES() {


        List<Article> articleList = Lists.newArrayList(elasticSearchDao.findAll());
        return articleList;
    }

    @Override
    public List<ArticleUserVo> selectAllArticleIndexViewData() {
        return articleDao.selectAllArticleIndexViewData();
    }

    @Override
    public IPage<ArticleUserVo> selectAllArticleCategoryData(int categoryId) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ArticleUserVo> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(1, -1);
        IPage<ArticleUserVo> res =  articleDao.selectAllArtilceCategoryData(page,categoryId);
        return res;
    }


    @Override
    public ArticleUserVo selectAllArticleDetail(int articleId) {
        return articleDao.selectAllArticleDetail(articleId);
    }

    @Override
    public List<ArticleUserVo> selectArticleByKeywords(String keywords) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery()
                // 从文章标题中查询
                .should(QueryBuilders.matchPhraseQuery("article_title", keywords))
                // 从文章内容中查询
                .should(QueryBuilders.matchPhraseQuery("article_content", keywords));

        Page<Article> search = (Page<Article>) elasticSearchDao.search(builder);
//        elasticSearchDao.findArticleByArticleContent(keywords)
        List<Article> articleList = search.getContent();
        return dealWithArticleVo(articleList);
    }

    public List<ArticleUserVo> dealWithArticleVo(List<Article> articleList) {
        List<ArticleUserVo> articleUserVos = new ArrayList<>();
        for (Article article : articleList) {
            // 找到这篇文章的发布者
            User user = userService.selectByUserId(article.getArticleUserId());

            System.out.println("user--------------" + user);
            ArticleUserVo articleUserVo = new ArticleUserVo();
            articleUserVo.setArticleId(article.getArticleId());
            articleUserVo.setArticleTitle(article.getArticleTitle());
            articleUserVo.setArticleSummary(article.getArticleSummary());
            articleUserVo.setArticleContent(article.getArticleContent());
            articleUserVo.setArticleViewCount(article.getArticleViewCount());
            // 这里需要单独处理下点赞的数量，先从Redis中那这个数据，如果没有再从MYSQL中去取
            // 这里文章点赞的Redis的设计是 LikeKey:like+articleId, =>LikeKey.getPrefix() + articleId
            int articleLikeCount = 0;
            if (jedisService.exists(LikeKey.LIKE_KEY, article.getArticleId() + "")) {
                // 这里说明在Redis中存在Key
                articleLikeCount = (int) jedisService.scard(LikeKey.LIKE_KEY.getPrefix() + article.getArticleId() + "");
            }
            articleUserVo.setArticleLikeCount(articleLikeCount);
            articleUserVo.setArticleCommentCount(article.getArticleCommentCount());
            articleUserVo.setCreatedTime(article.getCreatedTime());
            articleUserVo.setUpdateTime(article.getUpdateTime());
            articleUserVo.setIsDeleted(article.getIsDeleted());
            articleUserVo.setArticleCategoryId(article.getArticleCategoryId());
            articleUserVo.setArticleUserId(article.getArticleUserId());
            articleUserVo.setNickname(user.getNickname());
            articleUserVo.setAvatar(user.getAvatar());
            articleUserVos.add(articleUserVo);
        }

        return articleUserVos;
    }
}
