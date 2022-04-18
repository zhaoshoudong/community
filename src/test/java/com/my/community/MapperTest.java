package com.my.community;

import com.my.community.Dao.DiscussPostMapper;
import com.my.community.Dao.LoginTicketMapper;
import com.my.community.Dao.UserMapper;
import com.my.community.entity.DiscussPost;
import com.my.community.entity.LoginTicket;
import com.my.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * UserMapper 用户接口测试
     */
    @Test
    public void testSelectUser() {
        User user = userMapper.findUserById(101);
        System.out.println("按id查找");
        System.out.println(user);
        System.out.println("按姓名查找");
        User liubei = userMapper.findUserByUsername("liubei");
        System.out.println(liubei);
        System.out.println("按email查找");
        User user1 = userMapper.findUserByEmail("nowcoder101@sina.com");
        System.out.println(user1);
    }

    /**
     * 测试插入用户
     */
    @Test
    public void testInsetUser() {
        System.out.println("插入用户");
        User user2 = new User();
        user2.setUsername("刘能");
        user2.setPassword("123456");
        user2.setSalt("49f10");
        user2.setEmail("nowcoder11@sina.com");
        user2.setType(1);
        user2.setStatus(1);
        user2.setActivationCode(null);
        user2.setHeaderUrl("http://static.nowcoder.com/images/head/notify.png");
        user2.setCreateTime(new Date());
        int i = userMapper.insertUser(user2);
        System.out.println(i);
    }

    /**
     * 修改用户头像及状态
     */
    @Test
    public void testUpdateUser() {
        int i = userMapper.updateHeader(150, "http://images.nowcoder.com/head/180t.png");
        System.out.println(i);

        int i1 = userMapper.updateStatus(150, 0);
        System.out.println(i1);
    }

    /**
     * DiscussPostMapper文章接口测试
     */
    @Test
    public void testDiscussPost() {
        List<DiscussPost> discussPosts = discussPostMapper.findDiscussPosts(149, 0, 10);
        for (DiscussPost myList : discussPosts
        ) {
            System.out.println(myList);
        }
        int rows = discussPostMapper.findDiscussPostRows(149);
        System.out.println(rows);

    }

    /**
     * 测试插入loginTicket凭证
     */
    @Test
    public void testLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        int i = loginTicketMapper.insertTicket(loginTicket);
        System.out.println(i);
        System.out.println(loginTicket);
    }

    /**
     * 测试根据ticke字段查询loginTicket表
     */
    @Test
    public void testSelectTicket(){
        LoginTicket loginTicket = loginTicketMapper.findByTicket("abc");
        System.out.println(loginTicket);
    }

    /**
     * 测试根据ticket字段查询LoginTicket表
     * 测试根据ticket字段修稿LoginTicket表
     */
    @Test
    public void testUpdateTicket(){
        LoginTicket loginTicket = loginTicketMapper.findByTicket("abc");
        System.out.println(loginTicket);
        int def = loginTicketMapper.updateStatusByTicket("abc",0);
        System.out.println(def);
        loginTicket = loginTicketMapper.findByTicket("abc");
        System.out.println(loginTicket);
    }
}
