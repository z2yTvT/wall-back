package com.finall.cmt.dao;

import com.finall.cmt.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchDao extends ElasticsearchRepository<Article, Integer> {
    List<Article> findArticleByArticleContent(String content);

}
