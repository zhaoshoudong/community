package com.my.community.service.serviceimpl;

import com.my.community.Dao.DiscussPostMapper;
import com.my.community.entity.DiscussPost;
import com.my.community.service.IDiscussPostService;
import com.my.community.util.HostHolder;
import com.my.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements IDiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.findDiscussPosts(userId, offset, limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.findDiscussPostRows(userId);
    }

    /**
     * 发布文章
     *
     * @param discussPost
     * @return
     */
    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("文章信息不能为空!");
        }
        //转义 html 标签,防止恶意提交
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        String title = sensitiveFilter.filter(discussPost.getTitle());
        String content = sensitiveFilter.filter(discussPost.getContent());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        return discussPostMapper.addDiscussPost(discussPost);
    }

    /**
     * 根据帖子id查询帖子的详细信息
     *
     * @param id
     * @return
     */
    @Override
    public DiscussPost findDetailPost(int id) {
        return discussPostMapper.findDetailPost(id);
    }

    /**
     * 添加回贴时,同时要修改回帖总数
     *
     * @param id
     * @param commentCount
     * @return
     */
    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

}
