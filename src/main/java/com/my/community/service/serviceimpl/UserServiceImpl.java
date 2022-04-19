package com.my.community.service.serviceimpl;

import com.my.community.Dao.LoginTicketMapper;
import com.my.community.Dao.UserMapper;
import com.my.community.entity.LoginTicket;
import com.my.community.entity.User;
import com.my.community.service.IUserService;
import com.my.community.util.CommunityConstant;
import com.my.community.util.MD5Util;
import com.my.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements IUserService, CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;
    @Autowired
    LoginTicketMapper loginTicketMapper;
    @Value("${community.path.domin}")
    private String doMin;
    @Value("server.servlet.context-path")
    private String contextPath;

    /**
     * 注册
     *
     * @return
     */
    @Override
    public Map<String, Object> register(User user) {
        HashMap<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        //验证帐号
        User userByUsername = userMapper.findUserByUsername(user.getUsername());
        if (userByUsername != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        //验证邮箱
        User userByEmail = userMapper.findUserByEmail(user.getEmail());
        if (userByEmail != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }
        //注册用户
        String salt = MD5Util.generateUUID().substring(0, 5);
        user.setSalt(salt);
        user.setPassword(MD5Util.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(MD5Util.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = doMin + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活帐号", content);
        return map;
    }

    /**
     * 通过发送给用户邮件的链接激活帐号
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.findUserById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        User user = userMapper.findUserByUsername(username);
        if (user == null) {
            map.put("usernameMsg", "该用户未注册!");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }
        //验证密码
        password = MD5Util.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误!");
            return map;
        }
        //生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(MD5Util.generateUUID());
        loginTicket.setStatus(0);
        //过期时间,当前系统时间 + expiredSeconds 秒
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        int i = loginTicketMapper.insertTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket
     */
    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatusByTicket(ticket,1);
    }


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
        return userMapper.updateStatus(id, status);
    }

    @Override
    public int updateHeader(int id, String headerUrl) {
        return userMapper.updateHeader(id, headerUrl);
    }


}
