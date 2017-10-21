package com.nowcoder.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.soap.SOAPBinding;
import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;

    private String buildFeedData(EventModel model){
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(model.getActorId());
        if(actor==null)
            return  null;

        map.put("userId", actor.getId()+"");
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if(model.getEventType()==EventType.COMMENT||
                (model.getEventType()==EventType.FOLLOW&&model.getEntityType()== EntityType.ENTITY_QUESTION)){
            Question question = questionService.getQuestionById(model.getEntityId());
            if(question==null)
                return null;

            map.put("questionId", question.getId()+"");
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return  null;
    }
    @Override
    public void doHandle(EventModel eventModel) {

        Feed feed = new Feed();
        feed.setType(eventModel.getEventType().getValue());
        feed.setCreatedDate(new Date());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        feedService.addFeed(feed);

        //推送
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);
        for(int id : followers){
            jedisAdapter.lpush(RedisKeyUtil.getTimelineKey(id), feed.getId()+"");
        }

    }

    @Override
    public List<EventType> getSupportedTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.COMMENT});
    }
}
