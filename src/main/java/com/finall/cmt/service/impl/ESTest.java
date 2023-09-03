package com.finall.cmt.service.impl;

import com.finall.cmt.dao.ArticleDao;
import com.finall.cmt.dao.ElasticSearchDao;
import com.finall.cmt.entity.Article;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class ESTest {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ElasticSearchDao elasticSearchDao;

    public String testInsert() {
        List<Article> articles = articleDao.selectAllArticle();
        elasticSearchDao.saveAll(articles);
        return "";
    }
    public void testSelectAll(){
        for (Article ar : elasticSearchDao.findAll()) {
            System.out.println(ar.toString());
        }
    }
}
