package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.pojo.Message;
import com.chatviewer.blog.service.MessageService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    MessageService messageService;


    /**
     * 查询当前登录用户的所有联系者(id、name、头像)，以及发送的最后一条信息与未读消息数
     * @return List<ContactPersonDto>
     */
    @GetMapping("")
    public Result<Object> selectConversations() {
        Long userId = ContextHolderUtil.getUserId();
        return Result.success(messageService.selectConversations(userId));
    }


    /**
     * 查找已登录用户与用户id1的最新PAGE_SIZE条信息，已登录用户作为id2
     * @param id1 用户1
     * @return Page<Message>
     */
    @GetMapping("/conversation")
    public Result<Object> selectMessages(Long id1) {
        Long id2 = ContextHolderUtil.getUserId();
        return Result.success(messageService.selectMessages(id1, id2));
    }


    /**
     * 发送消息接口，保存消息message至数据库中
     * @param message to_id、content已设置
     */
    @PostMapping("")
    public Result<Object> sendMessage(@RequestBody Message message) {
        // 设置发送方id
        message.setFromId(ContextHolderUtil.getUserId());
        // 保存消息记录
        messageService.saveMessage(message);
        return Result.success();
    }

    /**
     * 将用户1和用户2(已登录用户)的所有未读消息设置为已读
     */
    @PostMapping("/read")
    public Result<Object> setRead(Long id1) {
        Long id2 = ContextHolderUtil.getUserId();
        messageService.read(id1, id2);
        return Result.success();
    }
}
