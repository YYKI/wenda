package com.nowcoder.dao;

import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Mapper
public interface UserDao {

    public String TABLE = " USER ";
    public String FIELDS = " (id, name, password, salt, head_url) ";
    public String SELECTED_FIELDS = " id, name, password, salt, head_url ";

    @Insert({"insert into "+ TABLE + FIELDS + "values (#{id},#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Delete({"delete from "+ TABLE +" where id=#{id}"})
    void deleteById(int id);

    @Update({"update "+ TABLE +"set password=#{password} , head_url=#{headUrl} where id=#{id}"})
    void updateUser(User user);

    @Select({"select "+ SELECTED_FIELDS + "from" +TABLE+ "where id=#{id}"})
    User selectById(int id);

    @Select({"select "+ SELECTED_FIELDS + "from" +TABLE+ "where name=#{name}"})
    User selectByName(String name);


}
