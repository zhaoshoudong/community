package com.my.community.controller;

import com.my.community.entity.DiscussPost;
import com.my.community.entity.User;
import com.my.community.service.serviceimpl.DiscussPostServiceImpl;
import com.my.community.service.serviceimpl.UserServiceImpl;
import com.my.community.util.CommunityUtils;
import com.my.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {
    @Autowired
    private DiscussPostServiceImpl discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    UserServiceImpl userService;

    /**
     * @param title
     * @param content
     * @return 返回 JSON 格式的字符串给前端
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            CommunityUtils.getJSONString(403, "您还没有登录!");
        }
        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return CommunityUtils.getJSONString(0, "发布成功!");
    }

    /**
     * 查看帖子详细信息
     *
     * @param discussPostId
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String findDetailPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        DiscussPost post = discussPostService.findDetailPost(discussPostId);
        model.addAttribute("postDetail", post);
        User userById = userService.findUserById(Integer.parseInt(post.getUserId()));
        model.addAttribute("user", userById);

        return "/site/discuss-detail";
    }
}
