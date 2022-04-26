package com.my.community.Dao;

import com.my.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户所有会话列表
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户所有会话数量
    int selectConversationsCount(int userId);

    // 查询当前用户会话的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    int selectUnreadLetters(int userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息状态(是否已读 0代表未读 1代表已读)
    int updateStatus(List<Integer> ids, int status);
}
