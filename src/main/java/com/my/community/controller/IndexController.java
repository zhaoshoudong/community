package com.my.community.controller;

import com.my.community.Dao.DiscussPostMapper;
import com.my.community.Dao.UserMapper;
import com.my.community.entity.DiscussPost;
import com.my.community.entity.Page;
import com.my.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    UserMapper userMapper;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String index(Model model, Page page) {
        //  方法调用之前, Spring MVC 会自动实例化 Model 和 Page,并将 Page 注入 Model
        //  所以,在 thymeleaf 中可以直接访问 Page 对象中的数据w
        page.setRows(discussPostMapper.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostMapper.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> arrayList = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list
            ) {
                Map<String, Object> hashMap = new HashMap<>();
                User user = userMapper.findById(Integer.parseInt(post.getUserId()));
                hashMap.put("user", user);
                hashMap.put("post", post);
                arrayList.add(hashMap);
            }
        }
        model.addAttribute("discussPosts", arrayList);
        return "/pages/index";
    }
}
