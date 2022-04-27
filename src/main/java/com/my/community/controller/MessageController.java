package com.my.community.controller;

import com.my.community.entity.Message;
import com.my.community.entity.Page;
import com.my.community.entity.User;
import com.my.community.service.MessageService;
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

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserServiceImpl userService;

    /**
     * 私信列表
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/message/letter/list/");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList
            ) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("conversation", message);
                hashMap.put("letterCount", messageService.findLettersCount(message.getConversationId()));
                hashMap.put("unreadCount", messageService.findUnreadLetterCount(user.getId(), message.getConversationId()));
                int target = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                hashMap.put("target", userService.findUserById(target));
                conversations.add(hashMap);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询未读消息数量
        int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(), null);
        model.addAttribute("unreadLetterCount", unreadLetterCount);
        return "/site/letter";
    }

    /**
     * 私信详情 (访问路径为私信列表页面动态传过来的当前私信 conversationId)
     *
     * @param conversationId
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/message/letter/detail/" + conversationId);
        page.setRows(messageService.findLettersCount(conversationId));

        //私信列表
        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (lettersList != null) {
            for (Message message : lettersList
            ) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("letter", message);
                hashMap.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(hashMap);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));

        //将未读的消息设置为已读
        List<Integer> ids = getLetterIds(lettersList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    // 私信目标(消息发送给谁,conversation 字段记录了 from_id  和 to_id 截取后可以得到to_id)
    private User getLetterTarget(String conversation) {
        String[] split = conversation.split("_");
        int id0 = Integer.parseInt(split[0]);
        int id1 = Integer.parseInt(split[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    // 获取未读消息的id
    private List<Integer> getLetterIds(List<Message> lettersList) {
        List<Integer> ids = new ArrayList<>();
        for (Message message : lettersList) {
            if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                System.out.println(message.getId());
                ids.add(message.getId());
            }
        }
        return ids;
    }

    /**
     * 发送私信
     *
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        Integer.valueOf("abc");
        User target = userService.findUserByUsername(toName);
        if (target == null) {
            return CommunityUtils.getJSONString(1, "用户不存在!");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setConversationId(message.getFromId() > message.getToId() ? message.getFromId() + "_" + message.getToId() : message.getToId() + "_" + message.getFromId());
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtils.getJSONString(0);

    }


}
