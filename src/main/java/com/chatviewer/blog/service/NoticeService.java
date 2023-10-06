package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.dto.NoticeDto;
import com.chatviewer.blog.dto.NoticeTypeDto;
import com.chatviewer.blog.pojo.Comment;
import com.chatviewer.blog.pojo.Event;
import com.chatviewer.blog.pojo.Notice;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author ChatViewer
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 用户点赞时，调用该函数生成Event事件：topic + 点赞相关信息(userId, entityType, entityId)
     * 其中点赞相关信息将在EventConsumer类中被消费生成Notice对象
     * @param entityType 点赞对象类型，可能为评论或者文章
     * @param entityId 点赞对象Id
     * @return Event事件
     */
    Event genLikeEvent(Integer entityType, Long entityId);

    /**
     * 当用户发表评论时，调用该函数，根据评论对象生成Event
     * @param comment 新发表的评论对象
     * @return Event事件：topic + 评论信息(comment)
     */
    Event genCommentEvent(Comment comment);


    /**
     * 在EventConsumer类中被调用，生成点赞的Notice对象
     * @param userId 点赞操作用户
     * @param entityType 点赞对象类型，可能为评论或者文章
     * @param entityId 点赞对象Id
     * @return Notice
     */
    Notice genLikeNotice(Long userId, Integer entityType, Long entityId);


    /**
     * 在EventConsumer类中被调用，生成评论的Notice对象
     * @param comment 用户新发表的评论
     * @return Notice
     */
    Notice genCommentNotice(Comment comment);

    /**
     * 查询某个用户所有类型的最新消息与未读消息数
     * @param userId 用户Id
     * @return List<NoticeTypeDto> 包含通知类型、最新消息和未读消息数
     * @throws ExecutionException 并行查询点赞通知与评论通知时，CompletableFuture抛出的异常
     * @throws InterruptedException 并行查询点赞通知与评论通知时，CompletableFuture抛出的异常
     */
    List<NoticeTypeDto> noticeBrief(Long userId) throws ExecutionException, InterruptedException;

    /**
     * 查询某个用户某个类型的所有消息
     * @param userId 用户Id
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     * @return List<NoticeDto> 发送方id、昵称、文章id、消息显示内容、消息时间
     */
    List<NoticeDto> selectTypeNotice(Long userId, int noticeType);

    /**
     * 将用户某个类型的消息全部设置为已读
     * @param userId 用户Id
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     */
    void readAllOfType(Long userId, int noticeType);
}
