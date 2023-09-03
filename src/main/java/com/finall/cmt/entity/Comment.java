package com.finall.cmt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class Comment {

    private int commentId;

    private int commentArticleId;

    private String commentUserId;

    private String commentContent;

    private int commentLikeCount;

    private int commentCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commentCreatedTime;


}
