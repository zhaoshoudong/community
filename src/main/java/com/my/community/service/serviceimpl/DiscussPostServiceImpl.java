package com.my.community.service.serviceimpl;

import com.my.community.Dao.DiscussPostMapper;
import com.my.community.entity.DiscussPost;
import com.my.community.service.IDiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements IDiscussPostService {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.findDiscussPosts(userId, offset, limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.findDiscussPostRows(userId);
    }
}
