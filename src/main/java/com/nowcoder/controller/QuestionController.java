package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;


    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @RequestMapping(path={"/question/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam(value="title") String title,
                            @RequestParam(value="content") String content){

        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            if(hostHolder.getUser()==null){
                return WendaUtil.getJsonString(999);
            }
            question.setUserId(hostHolder.getUser().getId());
            questionService.addQuestion(question);
            return WendaUtil.getJsonString(0);
        }catch(Exception e){
            logger.error("发布问题失败"+e.getMessage());
        }
        return  WendaUtil.getJsonString(1,"失败");
    }

    @RequestMapping(path={"/question/{qid}"}, method={RequestMethod.GET})
    public String questionDetial(Model model, @PathVariable("qid") int qid){
        Question question = questionService.getQuestionById(qid);
        model.addAttribute("question", question);
        List<Comment> comments = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for(Comment comment : comments){
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            vo.set("user", userService.getUser(comment.getUserId()));
            vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,comment.getId()));
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_QUESTION, comment.getId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

        List<ViewObject> followUsers = new ArrayList<>();
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION,qid,10);
        for(int userId : users){
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if(u==null)
                continue;
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }


        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }


        return "detail";
    }
}
