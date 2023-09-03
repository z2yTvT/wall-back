package com.finall.cmt.service.impl;

import com.finall.cmt.entity.Article;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.LikeKey;
import com.finall.cmt.service.ArticleService;
import com.finall.cmt.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Set;


@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    JedisService jedisService;

    @Autowired
    ArticleService articleService;

    private static final Logger log = LoggerFactory.getLogger(LikeServiceImpl.class);

    @Override
    public long like(String key, String userId) {
        jedisService.sadd(key, userId);
        return jedisService.scard(key);
    }

    @Override
    public long dislike(String key, String value) {
        jedisService.srem(key, value);
        return jedisService.scard(key);
    }

    @Override
    public long likeCount(String key) {
        return jedisService.scard(key);
    }

    @Override
    public Set<String> likeCountUserId(String key) {
        return jedisService.smembers(key);
    }

    @Override
    public void transLikedCountFromRedis2DB() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<String> matchLikeKey = jedisService.scan(LikeKey.LIKE_KEY.getPrefix() + "*");
        for(String s : matchLikeKey){
            // 这里从LikeKey:like是12位数，所以12位开始遍历文章ID
            int articleId = Integer.valueOf(s.substring(12));
            Article article = articleService.selectArticleByArticleId(articleId);
            long likeCount = jedisService.scard(s);
            article.setArticleLikeCount((int) likeCount);
            articleService.updateArticle(article);
        }
        stopWatch.stop();
        log.info("每分钟将Redis的点赞数量更新至DB中,耗时{}ms", stopWatch.getTotalTimeMillis());


    }


}
