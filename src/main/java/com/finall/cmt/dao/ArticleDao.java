package com.finall.cmt.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.finall.cmt.entity.Article;
import com.finall.cmt.vo.ArticleUserVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ArticleDao extends BaseMapper<Article> {

    String FILED_VALUE = "article_title, article_summary, article_content, article_view_count, " +
            "            article_like_count, article_comment_count, created_time, update_time, is_deleted," +
            "            article_category_id, article_category_name, article_user_id";
    String SELECT_VALUE = " article_id, " + FILED_VALUE;

    String NOT_LIKE_COUNT_SELECT_VALUE = "article_title, article_summary, article_content, article_view_count, " +
            "      article_comment_count, created_time, update_time, is_deleted, " +
            "      article_category_id, article_category_name, article_user_id";

    /**
     * 插入数据
     *
     * @param article
     */
    @Insert("insert into article(article_title, article_summary, article_content, article_view_count, " +
            "article_like_count, article_comment_count, created_time, update_time, is_deleted, article_category_id, " +
            "article_category_name, article_user_id) values(#{articleTitle}," +
            "#{articleSummary}, #{articleContent}, #{articleViewCount}, #{articleLikeCount}, #{articleCommentCount}," +
            "#{createdTime}, #{updateTime}, #{isDeleted}, #{articleCategoryId}, #{articleCategoryName}, #{articleUserId})")
    void insertArticle(Article article);


    @Update("<script>" +
            "update article " +
            "<set>" +
            "<if test ='articleTitle != null'>article_title = #{articleTitle},</if>" +
            "<if test ='articleSummary != null'>article_summary = #{articleSummary},</if>" +
            "<if test ='articleContent != null'>article_content = #{articleContent},</if>" +
            "<if test ='articleViewCount != null'>article_view_count = #{articleViewCount},</if>" +
            "<if test ='articleLikeCount != null'>article_like_count = #{articleLikeCount},</if>" +
            "<if test ='articleCommentCount != null'>article_comment_count = #{articleCommentCount},</if>" +
            "<if test ='createdTime != null'>created_time = #{createdTime},</if>" +
            "<if test ='updateTime != null'>update_time = #{updateTime},</if>" +
            "<if test ='isDeleted != null'>is_deleted = #{isDeleted},</if>" +
            "<if test ='articleCategoryName != null'>article_category_name = #{articleCategoryName}</if>" +
            "</set>" +
            "where article_id = #{articleId}" +
            "</script>")
    void updateArticle(Article article);

    /**
     * 这里的删除只做逻辑删除 0：未删除 1：删除
     *
     * @param articleId
     */
    @Update("update article set is_deleted = 1 where article_id=#{articleId}")
    //@Delete("delete from article where article_id = #{articleId}")
    void deleteArticle(int articleId);

    @Select("select " + SELECT_VALUE + " from article where article_id = #{articleId} and is_deleted = 0")
    Article selectArticleByArticleId(int articleId);

    /**
     * 根据不同的分类id来查找对应的文章，默认按照发表文章的更新时间来进行排序
     */
    @Select("select " + SELECT_VALUE + " from article where article_category_id = #{categoryId} and " +
            "is_deleted = 0 order by update_time")
    List<Article> selectArticleByCategoryId(int categoryId);


    /**
     * 根据点赞数量进行排序，如果点赞数相同的话，然后在按照更新时间进行排序
     *
     * @return
     */
    @Select("select " + SELECT_VALUE + " from article order by article_like_count, update_time where " +
            "is_deleted = 0")
    List<Article> selectArticleByLikeCount();

    /**
     * 根据评论数量进行排序，如果点赞数相同的话，然后在按照更新时间进行排序
     *
     * @return
     */
    @Select("select " + SELECT_VALUE + " from article order by article_like_count, update_time where " +
            "is_deleted = 0")
    List<Article> selectArticleByCommentCount();


    /**
     * 根据浏览数量进行排序，如果点赞数相同的话，然后在按照更新时间进行排序
     *
     * @return
     */
    @Select("select article.*, user.nickname, user.avatar from article inner join " +
            "user where article.article_user_id = user.user_id and article.is_deleted = 0 " +
            "order by article.article_view_count desc, article.update_time desc limit 0, 10")
    List<ArticleUserVo> selectArticleByViewCount();

    @Select("select article.*, user.nickname, user.avatar, user.school from article inner join user where " +
            "article.article_user_id = user.user_id and user.school = #{schoolName} and " +
            "article.article_category_id = #{categoryId} and " +
            "article.is_deleted = 0 order by article.update_time")
    List<ArticleUserVo> selectArticleBySchool(String schoolName, int categoryId);


    @Select("select " + "article_id," + NOT_LIKE_COUNT_SELECT_VALUE + " from article " +
            "left join user as u on u.user_id = article.article_user_id " +
            "where article_title like " +
            "concat('%',#{keyword},'%') " +
            "or article_content like concat('%',#{keyword},'%')" +
            "or u.nickname like concat('%',#{keyword},'%')" +
            "and is_deleted = 0")
    List<Article> selectArticleByKeyword(String keyword);



    @Select("select * from article, user where article_user_id = user_id and article_id = #{articleId} and is_deleted = 0")
    Article selectArticleByTwoUserId(int articleId);

    @Select("select * from article where is_deleted = 0")
    List<Article> selectAllArticle();

    @Select("select article.*, user.* from article inner join user where article_user_id = user_id and is_deleted = 0")
    List<ArticleUserVo> selectAllArticleIndexViewData();


    @Select("select article.*, user.* from article inner join user where article_user_id = user_id " +
            "and article_category_id = #{categoryId} and article.is_deleted = 0 order by update_time desc")
    IPage<ArticleUserVo> selectAllArtilceCategoryData(Page<ArticleUserVo> page, int categoryId);


    @Select("select " + NOT_LIKE_COUNT_SELECT_VALUE + " , user.* from article inner join user where article_user_id = user_id " +
            "and article_id = #{articleId} and article.is_deleted = 0")
    ArticleUserVo selectAllArticleDetail(int articleId);

}
