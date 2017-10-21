package com.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path={"/followUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        User user = hostHolder.getUser();
        if(user==null){
            return WendaUtil.getJsonString(999);
        }

        boolean ret = followService.follow(user.getId(), EntityType.ENTITY_USER, userId);

        //消息队列发邮件
        EventModel eventModel = new EventModel();
        eventModel.setEventType(EventType.FOLLOW);
        eventModel.setActorId(user.getId());
        eventModel.setEntityType(EntityType.ENTITY_USER);
        eventModel.setEntityId(userId);
        eventModel.setEntityOwnerId(userId);
        eventProducer.fireEvent(eventModel);

        return WendaUtil.getJsonString(ret?0:1, followService.getFolloweeCount(EntityType.ENTITY_USER, user.getId())+"");

    }

    @RequestMapping(path={"/unfollowUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        User user = hostHolder.getUser();
        if(user==null){
            return WendaUtil.getJsonString(999);
        }

        boolean ret = followService.unfollow(user.getId(), EntityType.ENTITY_USER, userId);

        //消息队列发邮件
        EventModel eventModel = new EventModel();
        eventModel.setEventType(EventType.UNFOLLOW);
        eventModel.setActorId(user.getId());
        eventModel.setEntityType(EntityType.ENTITY_USER);
        eventModel.setEntityId(userId);
        eventModel.setEntityOwnerId(userId);
        eventProducer.fireEvent(eventModel);

        return WendaUtil.getJsonString(ret?0:1, followService.getFolloweeCount(EntityType.ENTITY_USER, user.getId())+"");
    }

    @RequestMapping(path={"/followQuestion"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        User user = hostHolder.getUser();
        if(user==null){
            return WendaUtil.getJsonString(999);
        }

        boolean ret = followService.follow(user.getId(), EntityType.ENTITY_QUESTION, questionId);

        //消息队列发邮件
        EventModel eventModel = new EventModel();
        eventModel.setEventType(EventType.FOLLOW);
        eventModel.setActorId(user.getId());
        eventModel.setEntityType(EntityType.ENTITY_QUESTION);
        eventModel.setEntityId(questionId);
        eventModel.setEntityOwnerId(questionService.getQuestionById(questionId).getUserId());
        eventProducer.fireEvent(eventModel);

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJsonString(ret?0:1, info);

    }

    @RequestMapping(path={"/unfollowQuestion"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        User user = hostHolder.getUser();
        if(user==null){
            return WendaUtil.getJsonString(999);
        }

        boolean ret = followService.unfollow(user.getId(), EntityType.ENTITY_QUESTION, questionId);

        //消息队列发邮件
        EventModel eventModel = new EventModel();
        eventModel.setEventType(EventType.UNFOLLOW);
        eventModel.setActorId(user.getId());
        eventModel.setEntityType(EntityType.ENTITY_QUESTION);
        eventModel.setEntityId(questionId);
        eventModel.setEntityOwnerId(questionService.getQuestionById(questionId).getUserId());
        eventProducer.fireEvent(eventModel);

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJsonString(ret?0:1, info);

    }

    @RequestMapping(path={"/user/{uid}/followers"}, method = RequestMethod.GET)
    public String followers(Model model, @PathVariable("uid") int uid){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, uid, 0, 10);
        User user = hostHolder.getUser();
        if(user==null){
            model.addAttribute("followers", getUsersInfo(0,followerIds));
        }else{
            model.addAttribute("followers", getUsersInfo(user.getId(), followerIds));
        }

        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
        model.addAttribute("curUser", userService.getUser(uid));

        return "followers";

    }

    @RequestMapping(path={"/user/{uid}/followees"}, method = RequestMethod.GET)
    public String followees(Model model, @PathVariable("uid") int uid){
        List<Integer> followeeIds = followService.getFollowees(EntityType.ENTITY_USER, uid, 0, 10);
        User user = hostHolder.getUser();
        if(user==null){
            model.addAttribute("followees", getUsersInfo(0,followeeIds));
        }else{
            model.addAttribute("followees", getUsersInfo(user.getId(), followeeIds));
        }

        model.addAttribute("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, uid));
        model.addAttribute("curUser", userService.getUser(uid));

        return "followees";

    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer id : userIds){
            User user = userService.getUser(id);
            if(user==null)
                continue;
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(id));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, id));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, id));

            if(localUserId != 0){
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, id));
            }else{
                vo.set("followed", false);
            }

            userInfos.add(vo);
        }
        return  userInfos;
    }



}
