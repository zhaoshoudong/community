package com.my.community.service;

import com.my.community.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User findById(int id);
    User findByUsername(String username);
    User findByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);

}
