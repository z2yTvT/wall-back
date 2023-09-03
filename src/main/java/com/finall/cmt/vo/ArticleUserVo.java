package com.finall.cmt.vo;

import com.finall.cmt.entity.Article;
import lombok.Data;


@Data
public class ArticleUserVo extends Article {


    private String nickname;

    private String avatar;

    /**
     * 0 表示点赞了， 1 表示未点赞
     */
    private int isLiked;

    @Override
    public String toString() {
        return "ArticleUserVo{" + super.toString() +
                "nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", isLiked=" + isLiked +
                '}';
    }
}
