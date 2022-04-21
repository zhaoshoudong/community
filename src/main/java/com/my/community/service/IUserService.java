package com.my.community.service;

import com.my.community.entity.LoginTicket;
import com.my.community.entity.User;

import java.util.Map;

public interface IUserService {
    User findUserById(int id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    Map<String, Object> register(User user);

    Map<String, Object> login(String username, String password, long expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updatePassword(int id, String newPassword);

}
