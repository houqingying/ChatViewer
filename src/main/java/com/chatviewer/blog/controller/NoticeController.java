package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.service.NoticeService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    NoticeService noticeService;

    /**
     * 查询已登录用户所有类型的最新消息与未读消息数，作为消息摘要
     * @return List<NoticeTypeDto> 包含通知类型、最新消息和未读消息数
     */
    @GetMapping("/brief")
    public Result<Object> noticeBrief() throws ExecutionException, InterruptedException {
        Long userId = ContextHolderUtil.getUserId();
        return Result.success(noticeService.noticeBrief(userId));
    }

    /**
     * 查询已登录用户某个类型的所有消息
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     * @return List<NoticeDto> 发送方id、昵称、文章id、消息显示内容、消息时间
     */
    @GetMapping("")
    public Result<Object> noticeOfType(int noticeType) {
        Long userId = ContextHolderUtil.getUserId();
        return Result.success(noticeService.selectTypeNotice(userId, noticeType));
    }

    /**
     * 将登录用户某个类型的消息全部设置为已读
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     */
    @PutMapping("/read")
    public Result<Object> readAllOfType(int noticeType) {
        Long userId = ContextHolderUtil.getUserId();
        noticeService.readAllOfType(userId, noticeType);
        return Result.success();
    }
}
