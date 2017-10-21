package com.nowcoder.service;

import com.nowcoder.dao.CommentDao;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDao.selectByEnrtity(entityId, entityType);
    }

    public int addComment(Comment comment){
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment);
    }

    public Comment getCommentById(int id){
        return commentDao.selectById(id);
    }

    public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }

    public int getCommentCount(int entityId, int entityType){
        return  commentDao.countComments(entityId, entityType);
    }

    public void deleteComment(int id){
        commentDao.updateStatus(id);
    }
}
