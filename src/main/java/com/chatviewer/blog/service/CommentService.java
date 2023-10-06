package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.dto.CommentDto;
import com.chatviewer.blog.pojo.Comment;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author ChatViewer
 */
public interface CommentService extends IService<Comment> {

    /**
     * commentContent: 评论内容
     * commentType：类型，对文章的评论(0)，对评论的评论(1)
     * articleId: 该评论在哪篇文章下
     * parentId：上级实体id，如果为对文章的评论，则为文章id；如果为对评论的评论，则为一级评论id
     * targetId: 被回复用户id
     * @param comment 待添加评论
     * @return CommentDto 扩充后的评论信息
     * @throws ExecutionException 并行执行任务，CompletableFuture丢出的异常
     * @throws InterruptedException 并行执行任务，CompletableFuture丢出的异常
     */
    CommentDto addComment(Comment comment) throws ExecutionException, InterruptedException;

    /**
     * (1)查找某篇文章下的所有相关评论，包括对文章的回复，和对评论的回复
     * (2)如果用户已登录，需要额外返回用户点赞的评论id列表
     * @param articleId 文章Id
     * @param userId 用户登录时的用户Id
     * @return 某篇文章下的所有相关回复，组织结构参考：undrawUi文档
     * @throws ExecutionException 并行执行任务，CompletableFuture丢出的异常
     * @throws InterruptedException 并行执行任务，CompletableFuture丢出的异常
     */
    Map<String, Object> commentsOfArticleWithReply(Long articleId, Long userId) throws ExecutionException, InterruptedException;

}
