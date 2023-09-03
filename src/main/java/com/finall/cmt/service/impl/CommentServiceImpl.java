package com.finall.cmt.service.impl;

import com.finall.cmt.dao.CommentDao;
import com.finall.cmt.entity.Comment;
import com.finall.cmt.service.CommentService;
import com.finall.cmt.vo.CommentUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentServiceImpl implements CommentService {

    @Autowired(required = false)
    CommentDao commentDao;

    @Override
    public void insertComment(Comment comment) {
        commentDao.insertComment(comment);
    }

    @Override
    public void deleteComment(int commentId) {
        commentDao.deleteComment(commentId);
    }

    @Override
    public Comment selectCommentById(int commentId) {
        return commentDao.selectCommentById(commentId);
    }

    @Override
    public List<Comment> selectAllComment(int commentArticleId) {
        return commentDao.selectAllComment(commentArticleId);
    }

    @Override
    public int selectLastInsertCommentId() {
        return commentDao.selectLastInsertCommentId();
    }

    @Override
    public List<CommentUserVo> selectCommentLists(int commentArticleId) {
        return commentDao.selectCommentLists(commentArticleId);
    }
}
