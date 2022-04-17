package com.my.community.service.serviceimpl;

import com.my.community.Dao.UserMapper;
import com.my.community.entity.User;
import com.my.community.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User findUserById(int id) {
        return userMapper.findUserById(id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userMapper.findUserByEmail(email);
    }

    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public int updateStatus(int id, int status) {
        return userMapper.updateStatus(id,status);
    }

    @Override
    public int updateHeader(int id, String headerUrl) {
        return userMapper.updateHeader(id, headerUrl);
    }
}
