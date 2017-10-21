package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        Date date = new Date();

        //实体的粉丝增加当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(followerKey, date.getTime(), userId+"");
        tx.zadd(followeeKey, date.getTime(), entityId+"");

        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size()==2 && (Long)ret.get(0)>0 && (Long)ret.get(1)>0;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        Date date = new Date();

        //实体的粉丝删除当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerKey, userId+"");
        tx.zrem(followeeKey,  entityId+"");

        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size()==2 && (Long)ret.get(0)>0 && (Long)ret.get(1)>0;
    }

    private List<Integer> getIdsFromSet(Set<String> set){
        List<Integer> list = new ArrayList<>();
        for(String s : set){
            list.add(Integer.valueOf(s));
        }
        return list;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return  getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));

    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return  getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, limit));
    }

    public List<Integer> getFollowees(int entityType, int userId, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return  getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));

    }

    public List<Integer> getFollowees(int entityType, int userId, int offset, int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return  getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, limit));
    }

    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return jedisAdapter.zcard(followeeKey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return  jedisAdapter.zscore(followerKey, userId+"")!=null;
    }


}
