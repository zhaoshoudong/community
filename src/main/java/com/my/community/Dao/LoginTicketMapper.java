package com.my.community.Dao;

import com.my.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    @Insert("insert  into login_ticket(user_id, ticket, status, expired) values (#{userId}, #{ticket}, #{status}, #{expired})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int insertTicket(LoginTicket loginTicket);

    @Select("select id, user_id, ticket, status, expired from login_ticket where ticket = #{ticket}")
    LoginTicket findByTicket(String ticket);

    @Update("update login_ticket set status = #{status} where ticket = #{ticket}")
    int updateStatusByTicket(String ticket,int status);
}
