package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.dto.ConversationDto;
import com.chatviewer.blog.pojo.ChatgptMessage;
import com.chatviewer.blog.pojo.Conversation;
import com.chatviewer.blog.service.ChatgptService;
import com.chatviewer.blog.service.ConversationService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/gpt")
public class ChatgptController {

    @Resource
    ConversationService conversationService;

    @Resource
    ChatgptService chatgptService;

    /**
     * 获取用户与ChatGPT的所有会话列表，每个会话项包括conversationId、firstMessage
     * @return  Result(List<ConversationDto>)
     */
    @GetMapping("/conversation")
    public Result<Object> getConversation() {
        Long userId = ContextHolderUtil.getUserId();
        List<ConversationDto> conversationDtoList = conversationService.conversationsOf(userId);
        return Result.success(conversationDtoList);
    }

    /**
     * 已登录用户新建对话，通过携带的token解析userId
     * @param conversation 结构体中携带conversationType参数
     * @return 新建对话的conversationId
     */
    @PostMapping("/conversation")
    public Result<Object> createConversation(@RequestBody Conversation conversation) {
        Long conversationId = conversationService.createConversation(conversation);
        return Result.success(conversationId);
    }

    /**
     * 已登录用户删除属于自己ID为conversationId的对话及所有消息
     * @param conversationId 会话id
     * @return 成功时返回成功信息
     */
    @DeleteMapping("/conversation/{id}")
    public Result<Object> delete(@PathVariable("id") Long conversationId) {
        conversationService.deleteWithMsg(conversationId);
        return Result.success();
    }

    /**
     * 查找登录用户某个会话下的所有消息记录
     * @param conversationId 会话id
     * @return List<ChatgptMessage>
     */
    @GetMapping("/chat/message")
    public Result<Object> messagesOf(Long conversationId) {
        List<ChatgptMessage> messages = chatgptService.messagesOfConversation(conversationId, null);
        return Result.success(messages);
    }

    /**
     * ChatGPT流式对话接口
     * @param map 发送消息参数 {useToken、conversationId、prompt}
     * @return SseEmitter流
     */
    @PostMapping(path="/chat", produces="text/event-stream;charset=utf-8")
    public SseEmitter sendMsg(@RequestBody Map<String, Object> map) {
        // true 使用账户token数；false 使用账户自定义ApiKey
        Boolean useToken = (Boolean) map.get("useToken");
        Long conversationId = map.containsKey("conversationId") ?
                Long.parseLong((String) map.get("conversationId")) : null;
        // 发送消息内容
        String prompt = (String) map.get("prompt");

        return chatgptService.sendMsgSse(useToken, conversationId, prompt);
    }

}
