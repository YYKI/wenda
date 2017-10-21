package com.nowcoder.util;

public class RedisKeyUtil {
    private static String SPLIT=":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENTQUEUE";
    private static String BIZ_FOLLOWER = "BIZ_FOLLOWER";
    private static String BIZ_FOLLOWEE = "BIZ_FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";

    public  static String getTimelineKey(int userId){
        return BIZ_TIMELINE+SPLIT+userId;
    }

    public static String getBizEventqueue() {
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    public static String getFolloweeKey(int entityType, int entityId){
        return BIZ_FOLLOWEE+SPLIT+entityType+SPLIT+entityId;
    }

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    public static String getDislikeKey(int entityType, int entityId){
        return BIZ_DISLIKE+SPLIT+entityType+SPLIT+entityId;
    }
}
