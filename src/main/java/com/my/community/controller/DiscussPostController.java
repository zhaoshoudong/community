package com.my.community.controller;

import com.my.community.entity.Comment;
import com.my.community.entity.DiscussPost;
import com.my.community.entity.Page;
import com.my.community.entity.User;
import com.my.community.service.CommentService;
import com.my.community.service.serviceimpl.DiscussPostServiceImpl;
import com.my.community.service.serviceimpl.UserServiceImpl;
import com.my.community.util.CommunityConstant;
import com.my.community.util.CommunityUtils;
import com.my.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostServiceImpl discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    private CommentService commentService;

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
     * 查看帖子详情
     *
     * @param discussPostId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String findDetailPost(@PathVariable("discussPostId") int discussPostId, Page page, Model model) {
        // 帖子
        DiscussPost post = discussPostService.findDetailPost(discussPostId);
        model.addAttribute("postDetail", post);
        // 作者
        User userById = userService.findUserById(Integer.parseInt(post.getUserId()));
        model.addAttribute("user", userById);

        // 评论分页信息
        page.setLimit(5);
        page.setRows(post.getCommentCount());
        page.setPath("/discussPost/detail/" + discussPostId);

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList
            ) {
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList
                    ) {
                        HashMap<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                    commentVo.put("replys", replyVoList);
                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}
