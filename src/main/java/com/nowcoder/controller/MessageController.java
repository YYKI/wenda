package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName, @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null)
                return WendaUtil.getJsonString(999, "未登录");
            User user1 = userService.getUserByName(toName);
            if (user1 == null)
                return WendaUtil.getJsonString(1, "用户不存在");
            User user2 = hostHolder.getUser();
            Message message = new Message();
            message.setContent(content);
            message.setFromId(user2.getId());
            message.setToId(user1.getId());
            message.setCreatedDate(new Date());
            String conversationId = user1.getId() > user2.getId() ? user2.getId() + "_" + user1.getId() : user1.getId() + "_" + user2.getId();
            message.setConversationId(conversationId);
            message.setHasRead(0);
            messageService.addMessage(message);
            return WendaUtil.getJsonString(0);
        } catch (Exception e) {
            logger.error("发送站内信失败" + e.getMessage());
            return WendaUtil.getJsonString(1, "发送站内信失败");
        }

    }

    @RequestMapping(path = {"msg/list"}, method = RequestMethod.GET)
    public String getConversationList(Model model) {
        try {
            User loalUser = hostHolder.getUser();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> msgs = messageService.getConversationList(loalUser.getId(), 0, 4);
            for (Message msg : msgs) {
                int targetId = loalUser.getId() == msg.getFromId() ? msg.getToId() : msg.getFromId();
                ViewObject vo = new ViewObject();
                vo.set("user", userService.getUser(targetId));
                vo.set("message", msg);
                vo.set("unread", messageService.getConvesationUnreadCount(loalUser.getId(), msg.getConversationId()));
                conversations.add(vo);
            }

            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }

        return "letter";
    }

    @RequestMapping(path = {"msg/detail"}, method = RequestMethod.GET)
    public String getConversationDetail(Model model, @Param("conversationId") String conversationId) {
        try {
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> msgs = messageService.getConversationDetail(conversationId, 0, 10);
            for (Message msg : msgs) {
                User user = userService.getUser(msg.getFromId());
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                vo.set("user",user);
                //vo.set("headUrl", user.getHeadUrl());
                //vo.set("userId", user.getId());
                conversations.add(vo);
            }

            model.addAttribute("messages", conversations);
        } catch (Exception e) {
            logger.error("获取站内信详情信息失败" + e.getMessage());
        }
        messageService.updateHasRead(hostHolder.getUser().getId(), conversationId);
        return "letterDetail";
    }
}
