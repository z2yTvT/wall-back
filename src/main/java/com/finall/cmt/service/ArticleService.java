package com.finall.cmt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.finall.cmt.entity.Article;
import com.finall.cmt.vo.ArticleUserVo;

import java.util.List;


public interface ArticleService {

    void insertArticle(Article article);

    void updateArticle(Article article);

    void deleteArticle(int articleId);

    Article selectArticleByArticleId(int articleId);

    List<Article> selectArticleByCategoryId(int categoryId);

    List<Article> selectArticleByLikeCount();

    List<Article> selectArticleByCommentCount();

    List<ArticleUserVo> selectArticleByViewCount();

    List<ArticleUserVo> selectArticleBySchool(String schoolName, int categoryId);

    List<ArticleUserVo> selectArticleByKeyword(String keyword);

    Article selectArticleByTwoUserId(int articleId);

    List<Article> selectAllArtilce();

    List<Article> selectAllArticleByES();

    List<ArticleUserVo> selectAllArticleIndexViewData();

    IPage<ArticleUserVo> selectAllArticleCategoryData(int categoryId);

    ArticleUserVo selectAllArticleDetail(int articleId);

    List<ArticleUserVo> selectArticleByKeywords(String keywords);


}
