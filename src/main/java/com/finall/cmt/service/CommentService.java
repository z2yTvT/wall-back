package com.finall.cmt.service;

import com.finall.cmt.entity.Comment;
import com.finall.cmt.vo.CommentUserVo;

import java.util.List;

public interface CommentService {


    void insertComment(Comment comment);

    void deleteComment(int commentId);

    Comment selectCommentById(int commentId);

    List<Comment> selectAllComment(int commentArticleId);

    int selectLastInsertCommentId();

    List<CommentUserVo> selectCommentLists(int articleCommentId);

}
