package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {
    public String TABLE = " message ";
    public String FIELDS = " (id, fromid, toid, content, conversation_id, created_date, has_read) ";
    public String SELECTED_FIELDS = " fromid, toid, content, conversation_id, created_date, has_read ";

    @Insert({"insert into "+ TABLE + FIELDS + "values (#{id},#{fromId},#{toId},#{content},#{conversationId},#{createdDate},#{hasRead})"})
    public int addMeaage(Message message);

    @Select({"select id"+SELECTED_FIELDS+" from "+TABLE+" where conversation_id=#{conversationId} limit #{offset},#{limit}"})
    public List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    @Select({"select"+SELECTED_FIELDS+",count(id) as id from (select * from "+TABLE+"where fromid=#{userId} or toid=#{userId} order by id desc) tt group by conversation_id order by created_date desc limit #{offset},#{limit}"})
    public List<Message> getConversationList(@Param("userId") int userId,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    @Select({"select count(id) from "+TABLE+" where has_read=0 and toid=#{userId} and conversation_id=#{conversationId}"})
    public int getConversationUnreadCount(@Param("userId") int userId,
                                     @Param("conversationId") String conversationId);

    @Update({"update "+TABLE+" set has_read=1 where toid=#{userId} and conversation_id=#{conversationId}"})
    void updateHasRead(@Param("userId") int userId, @Param("conversationId") String conversationId);


}
