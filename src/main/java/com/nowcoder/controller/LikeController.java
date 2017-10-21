package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    private static Logger logger = LoggerFactory.getLogger(LikeController.class);


    @RequestMapping(path={"/like"}, method= RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        User user = hostHolder.getUser();
        if(user==null)
            return WendaUtil.getJsonString(999);

        Comment comment = commentService.getCommentById(commentId);
        EventModel eventModel = new EventModel();
        eventModel.setActorId(user.getId());
        eventModel.setEntityId(commentId);
        eventModel.setEntityType(EntityType.ENTITY_QUESTION);
        eventModel.setEntityOwnerId(comment.getUserId());
        eventModel.setExt("questionId", comment.getEntityId()+"");
        eventModel.setEventType(EventType.LIKE);
        eventProducer.fireEvent(eventModel);

        long likeCount = likeService.like(user.getId(), EntityType.ENTITY_QUESTION, commentId);
        return WendaUtil.getJsonString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path={"/dislike"}, method= RequestMethod.POST)
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        User user = hostHolder.getUser();
        if(user==null)
            return WendaUtil.getJsonString(999);

        long dislikeCount = likeService.dislike(user.getId(), EntityType.ENTITY_QUESTION, commentId);
        return WendaUtil.getJsonString(0, String.valueOf(dislikeCount));
    }
}
