package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.jws.soap.SOAPBinding;

@Mapper
public interface LoginTicketDao {
    String TABLE = " loginticket ";
    String FIELDS = " id, user_id, expired, status, ticket ";

    @Insert({"insert into ", TABLE, "(", FIELDS,
            ") values (#{id},#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket loginTicket);

    @Select({"select "+ FIELDS + "from" +TABLE+ "where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update "+TABLE+"set status=1 where user_id=#{userId}"})
    void updateTicket(int userId);
}
