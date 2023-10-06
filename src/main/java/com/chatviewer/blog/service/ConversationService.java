package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.dto.ConversationDto;
import com.chatviewer.blog.pojo.Conversation;

import java.util.List;

/**
 * @author ChatViewer
 */
public interface ConversationService extends IService<Conversation> {

    /**
     * 获取用户与ChatGPT的所有会话列表，每个会话项包括conversationId、firstMessage
     * @param userId 待查询用户id
     * @return List<ConversationDto>
     */
    List<ConversationDto> conversationsOf(Long userId);

    /**
     * 已登录用户新建对话，通过携带的token解析userId
     * @param conversation 结构体中携带conversationType参数
     * @return 新建对话的conversationId
     */
    Long createConversation(Conversation conversation);

    /**
     * 已登录用户删除属于自己ID为conversationId的对话及所有消息
     * @param conversationId 会话id
     */
    void deleteWithMsg(Long conversationId);
}
