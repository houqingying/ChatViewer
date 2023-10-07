package com.chatviewer.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.event.GptEventSourceListener;
import com.chatviewer.blog.exception.NoPermissionException;
import com.chatviewer.blog.exception.NoTokenCountException;
import com.chatviewer.blog.mapper.ChatgptMessageMapper;
import com.chatviewer.blog.mapper.ConversationMapper;
import com.chatviewer.blog.mapper.UserMapper;
import com.chatviewer.blog.pojo.ChatgptMessage;
import com.chatviewer.blog.pojo.Conversation;
import com.chatviewer.blog.pojo.User;
import com.chatviewer.blog.service.ChatgptService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.Proxys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatGPT消息服务
 * @author ChatViewer
 */
@Slf4j
@Service
public class ChatgptServiceImpl extends ServiceImpl<ChatgptMessageMapper, ChatgptMessage> implements ChatgptService {

    @Resource
    UserMapper userMapper;

    @Resource
    ConversationMapper conversationMapper;

    /**
     * ChatGPT的APIKey，获取方法：Clash科学上网 + VISA卡 + ChatGPT官网绑定卡号，得到API Key
     */
    @Value("${my-conf.gpt-key}")
    private String apiKey;

    /**
     * 代表消息发送方向
     */
    private static final int DIRECTION_QUESTION = 0;
    private static final int DIRECTION_ANSWER = 1;

    /**
     * 发送消息时加入的上下文信息数
     */
    private static final int CONTEXT_MESSAGE_NUM = 2;

    @Override
    public List<ChatgptMessage> messagesOfConversation(Long conversationId, Integer showItem) {
        // 1、校验会话是否属于当前登录用户
        Long userId = ContextHolderUtil.getUserId();
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (userId == null) {
            throw new NoPermissionException("用户未登录！不能查询会话！");
        }
        if (!userId.equals(conversation.getUserId())) {
            throw new NoPermissionException("不能查询其他用户的会话！");
        }
        // 2、查询conversation的最新showItem条会话
        LambdaQueryWrapper<ChatgptMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatgptMessage::getConversationId, conversationId);
        lambdaQueryWrapper.orderByAsc(ChatgptMessage::getMessageId);
        lambdaQueryWrapper.last(showItem != null, "limit " + showItem);
        return list(lambdaQueryWrapper);
    }

    /**
     * 将本项目的数据类型ChatgptMessage转为SDK需要的Message类格式，可以Ctrl+戳戳查看Message类型
     * @param src 数据库格式消息列表
     * @return SDK所需格式消息列表
     */
    private List<Message> transform(List<ChatgptMessage> src) {
        List<Message> tgt = new ArrayList<>();
        // 对src列表中的每条消息
        for (ChatgptMessage message: src) {
            // 根据消息类型调用对应的方法，向tgt中加入所需类型消息
            tgt.add(message.getMessageDirection() == DIRECTION_QUESTION ?
                    // 用户发送的消息
                    Message.of(message.getContent())
                    // GPT发送的消息
                    : Message.ofAssistant(message.getContent()));
        }
        return tgt;
    }


    @Override
    public SseEmitter sendMsgSse(Boolean useToken, Long conversationId, String prompt) {

        // 获取SecurityContextHolder中的用户id, 判断该会话是否属于当前用户
        Long userId = ContextHolderUtil.getUserId();
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (userId == null) {
            throw new NoPermissionException("用户未登录！不能向GPT发送消息！");
        }
        if (!userId.equals(conversation.getUserId())) {
            throw new NoPermissionException("不能更改其他用户的会话！");
        }

        // 得到用户
        User user = userMapper.selectById(userId);

        // 1、设置api key
        String key;
        if (useToken) {
            // 如果选择了token支付方式，检查token数
            if (user.getUserTokenCount() > 0 ) {
                key = apiKey;
            }
            else {
                throw new NoTokenCountException("用户余额不足");
            }
        }
        else {
            // 使用用户账户设置的API Key
            key = user.getUserApiKey();
        }

        // 2、设置代理
        Proxy proxy = Proxys.http("127.0.0.1", 7890);

        // 3、借助SDK工具，实例化ChatGPTStream工具类对象
        ChatGPTStream chatgptStream = ChatGPTStream.builder()
                .timeout(50)
                .apiKey(key)
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

        // 4、实例化流式输出类，设置监听，从而在所有消息输出完成后回调
        SseEmitter sseEmitter = new SseEmitter(-1L);
        GptEventSourceListener listener = new GptEventSourceListener(sseEmitter);

        // 5、加入历史消息记录，提供上下文信息
        // Message为SDK包中的数据结构
        List<Message> messages = conversationId != null ?
                // 第二个参数控制加入的上下文信息数，transform将ChatgptMessage转为SDK需要的格式
                transform(messagesOfConversation(conversationId, CONTEXT_MESSAGE_NUM))
                : new ArrayList<>();

        // 6、加入本次提问问题
        Message message = Message.of(prompt);
        messages.add(message);

        // 7、设置完成时的回调函数
        listener.setOnComplete(msg -> {
            // 保存历史信息
            saveMessage(conversationId, prompt, DIRECTION_QUESTION);
            saveMessage(conversationId, msg, DIRECTION_ANSWER);
            // 如果使用token支付，扣除token，更新账户信息
            if (useToken) {
                user.setUserTokenCount(user.getUserTokenCount() - 1);
                userMapper.updateById(user);
            }
        });

        // 8、提问
        chatgptStream.streamChatCompletion(messages, listener);

        return sseEmitter;
    }

    /**
     * 将消息保存至数据库中
     * @param conversationId 会话id
     * @param msg 消息内容
     * @param messageDirection 消息方向
     */
    public void saveMessage(Long conversationId, String msg, Integer messageDirection) {
        ChatgptMessage message = new ChatgptMessage();
        message.setContent(msg);
        message.setMessageDirection(messageDirection);
        message.setConversationId(conversationId);
        message.setCreateTime(LocalDateTime.now());
        save(message);
    }

}
