package com.my.community.service;

import com.my.community.entity.DiscussPost;

import java.util.List;

public interface IDiscussPostService {
   List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);
   int findDiscussPostRows(int userId);
}
