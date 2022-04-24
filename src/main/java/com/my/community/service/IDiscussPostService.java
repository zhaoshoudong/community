package com.my.community.service;

import com.my.community.entity.DiscussPost;

import java.util.List;

public interface IDiscussPostService {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDetailPost(int id);

    int updateCommentCount(int id, int commentCount);
}
