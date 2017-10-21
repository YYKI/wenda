package com.nowcoder.dao;


import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedDao {
    public String TABLE = " feed ";
    public String FIELDS = " (id, type, user_id, created_date, data) ";
    public String SELECTED_FIELDS = " id, type, user_id, created_date, data ";

    @Insert({"insert into "+ TABLE + FIELDS + "values (#{id},#{type},#{userId},#{createdDate},#{data})"})
    int addFeed(Feed feed);

    @Select({"select "+ SELECTED_FIELDS + "from" +TABLE+ "where id=#{id}"})
    Feed selectById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);

}
