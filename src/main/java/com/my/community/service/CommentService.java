package com.my.community.service;

import com.my.community.Dao.CommentMapper;
import com.my.community.entity.Comment;
import com.my.community.service.serviceimpl.DiscussPostServiceImpl;
import com.my.community.util.CommunityConstant;
import com.my.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostServiceImpl discussPostService;

    /**
     * 通过实体类别查询
     * ENTITY_TYPE_POST = 1时为评论列表
     * ENTITY_TYPE_COMMENT = 2时为回复列表
     *
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return List<Comment>
     */
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 查询评论总数,有效条件status为0
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 按实体类型添加评论或回复,需要添加事务隔离,因为涉及到先后两个对数据的操作(添加成功后,总数才增加)
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);
        //更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

}
