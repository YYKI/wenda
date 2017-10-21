package com.nowcoder.service;

import com.nowcoder.dao.QuestionDao;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;
    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDao.selectLatestQuestions(userId, offset, limit);

    }

    public void addQuestion(Question question){
        question.setContent(sensitiveService.filter(question.getContent()));
        questionDao.addQuestion(question);
    }

    public Question getQuestionById(int id){
        return questionDao.getQuestionById(id);
    }

    public void updateCommentCount(int count, int id){
        questionDao.updateCommentsCount(count, id);
    }
}
