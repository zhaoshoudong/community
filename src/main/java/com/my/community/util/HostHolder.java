package com.my.community.util;

import com.my.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用于持有用户信息,用于代替session对象
 */
@Component
public class HostHolder {
    ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void remove() {
        users.remove();
    }
}
