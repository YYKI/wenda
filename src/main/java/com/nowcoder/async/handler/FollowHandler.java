package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.collections.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        if(eventModel.getEntityType()== EntityType.ENTITY_QUESTION){
            message.setContent("用户"+user.getName()+"关注了你的问题,http://localhost:8888/question/"+eventModel.getEntityId());
        }else if (eventModel.getEntityType()== EntityType.ENTITY_USER){
            message.setContent("用户"+user.getName()+"关注了你,http://localhost:8888/user/"+user.getId());
        }
        message.setConversationId("1_"+eventModel.getEntityOwnerId());

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportedTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
