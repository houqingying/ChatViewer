package com.chatviewer.blog.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.SseHelper;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * GPTStream中发送prompt，接收回复时使用到的监听器
 * @author PlexPt
 */
@Slf4j
@RequiredArgsConstructor
public class GptEventSourceListener extends EventSourceListener {

    final SseEmitter sseEmitter;

    String last = "";

    private static final String FINISH_ANSWER_HINT = "[DONE]";
    private static final String SERVER_ERROR_HINT = "The server had an error processing your request.";
    private static final String TOKEN_KEY_EMPTY_HINT = "You didn't provide an API key.";
    private static final String KEY_INCORRECT_HINT = "Incorrect API key provided:";
    private static final String KEY_BAN_HINT = "This key is associated with a deactivated account";


    /**
     * 会话关闭时的回调函数
     */
    @Setter
    Consumer<String> onComplete = s -> {

    };


    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {

    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
        log.info("回答中：{}", data);
        // 判断是否回答完成，回答完成可直接返回
        if (FINISH_ANSWER_HINT.equals(data)) {
            log.info("回答完成：" + last);
            // 传入最终ChatGPT回复的消息，调用回调函数，该回调函数在ChatgptServiceImpl类中被设置，完成保存消息记录与扣除token的功能
            onComplete.accept(last);
            // 关闭sseEmitter流
            SseHelper.complete(sseEmitter);
            return;
        }
        // 读取Json
        ChatCompletionResponse completionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
        Message delta = completionResponse.getChoices().get(0).getDelta();
        String text = delta.getContent();
        if (text != null) {
            last += text;
            // 发送增量消息
            sseEmitter.send(delta);
        }
    }


    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        // 关闭sseEmitter
        SseHelper.complete(sseEmitter);
    }


    /**
     * 当调用过程出现异常时，报告错误信息
     */
    @SneakyThrows
    @Override
    public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            String msg = body.string();
            HashMap<String, String> map = new HashMap<>(4);
            if (StrUtil.contains(msg, SERVER_ERROR_HINT)) {
                map.put("content", "服务器错误，请重试");
            }
            if (StrUtil.contains(msg, TOKEN_KEY_EMPTY_HINT)) {
                map.put("content", "Token数不足，或Api-Key为空，可点击导航栏中头像进行相应设置。");
            }
            if (StrUtil.contains(msg, KEY_INCORRECT_HINT)) {
                map.put("content", "Key错误。可点击导航栏中头像进行相应设置。");
            }
            if (StrUtil.contains(msg, KEY_BAN_HINT)) {
                map.put("content", "Key被OpenAi禁用");
            }
            sseEmitter.send(map);
            log.error("OpenAI  sse连接异常data：{}，异常：{}", msg, t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
        SseHelper.complete(sseEmitter);
    }
}
