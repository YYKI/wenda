package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDao {

    public String TABLE = " comment ";
    public String FIELDS = " (id, content, user_id, created_date, entity_id, entity_type, status) ";
    public String SELECTED_FIELDS = " id, content, user_id, created_date, entity_id, entity_type, status ";

    @Insert({"insert into "+ TABLE + FIELDS + "values (#{id},#{content},#{userId},#{createdDate},#{entityId},#{entityType},#{status})"})
    public int addComment(Comment comment);

    @Select({"select "+ SELECTED_FIELDS +"from"+TABLE+"where entity_id=#{entityId} and entity_type=#{entityType} order by id desc"})
    public List<Comment> selectByEnrtity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select "+ SELECTED_FIELDS +"from"+TABLE+"where id=#{id} "})
    public Comment selectById(@Param("id") int id);


    @Update({"update "+TABLE+"set status=1 where id=#{id}"})
    public void updateStatus(@Param("id") int id);

    @Select({"select count(id) from "+TABLE+" where entity_id=#{entityId} and entity_type=#{entityType}"})
    public int countComments(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
}
