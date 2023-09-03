package com.finall.cmt.vo;

import com.finall.cmt.entity.Comment;
import lombok.Data;


@Data
public class CommentUserVo extends Comment {

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;
}
