package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class CommentController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;

    private static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @RequestMapping(path={"/addComment"}, method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){

        try{
            User user = hostHolder.getUser();
            if(user==null)
                return "redirect:/reglogin";
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setUserId(user.getId());
            comment.setStatus(0);
            commentService.addComment(comment);

            EventModel eventModel = new EventModel();
            eventModel.setEventType(EventType.COMMENT);
            eventModel.setActorId(user.getId());
            eventModel.setEntityType(EntityType.ENTITY_QUESTION);
            eventModel.setEntityId(questionId);
            eventModel.setEntityOwnerId(questionService.getQuestionById(questionId).getUserId());
            eventProducer.fireEvent(eventModel);

            //更新评论数,在写时更改，而不是读（读更加频繁）
            int count = commentService.getCommentCount(questionId,EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(count, questionId);
        }catch(Exception e){
            logger.error("提交回答失败"+e.getMessage());
        }

        return "redirect:/question/"+questionId;


    }
}
