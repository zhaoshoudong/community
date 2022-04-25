package com.my.community.controller;

import com.my.community.entity.Message;
import com.my.community.entity.Page;
import com.my.community.entity.User;
import com.my.community.service.MessageService;
import com.my.community.service.serviceimpl.UserServiceImpl;
import com.my.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserServiceImpl userService;

    // 私信列表
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
        //查询未读消息数量
        int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(), null);
        model.addAttribute("unreadLetterCount", unreadLetterCount);
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/message/letter/detail/" + conversationId);
        page.setRows(messageService.findLettersCount(conversationId));

        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        for (Message message : lettersList
        ) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("letter", message);
            hashMap.put("fromUser", userService.findUserById(message.getFromId()));
            letters.add(hashMap);
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        return "/site/letter-detail";
    }

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

}
