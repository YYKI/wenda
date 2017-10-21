package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path={"/pushfeeds"}, method = RequestMethod.GET)
    private String getPushFeeds(Model model){
        User user = hostHolder.getUser();
        int localUserId = user==null ? 0 : user.getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<Feed>  feeds = new ArrayList<>();
        for(String feedId : feedIds){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if(feed!=null)
                feeds.add(feed);
        }

        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path={"/pullfeeds"}, method = RequestMethod.GET)
    private String getPullFeeds(Model model){
        User user = hostHolder.getUser();
        int localUserId = user==null ? 0 : user.getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId!=0){
            followees = followService.getFollowees(EntityType.ENTITY_USER, localUserId, Integer.MAX_VALUE);
        }

        List<Feed>  feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);

        model.addAttribute("feeds", feeds);
        return "feeds";

    }
}
