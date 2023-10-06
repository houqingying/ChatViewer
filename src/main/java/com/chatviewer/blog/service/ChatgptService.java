package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.pojo.ChatgptMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @author ChatViewer
 */
public interface ChatgptService extends IService<ChatgptMessage> {

    /**
     * 选出一个会话中的所有message
     * @param conversationId 会话Id
     * @param showItem 仅查看前 showItem 条
     * @return List<ChatgptMessage>
     */
    List<ChatgptMessage> messagesOfConversation(Long conversationId, Integer showItem);

    /**
     * 提问函数
     * @param useToken 使用token / 使用ApiKey
     * @param conversationId 会话id，以得到上下文信息
     * @param prompt 本次提问问题
     * @return 以SSE的形式返回响应
     */
    SseEmitter sendMsgSse(Boolean useToken, Long conversationId, String prompt);
}
