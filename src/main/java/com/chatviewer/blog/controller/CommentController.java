package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.pojo.Comment;
import com.chatviewer.blog.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {

    @Resource
    CommentService commentService;

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
    @PostMapping("")
    public Result<Object> addComment(@RequestBody Comment comment) throws ExecutionException, InterruptedException {
        Long userId = ContextHolderUtil.getUserId();
        comment.setUserId(userId);
        return Result.success(commentService.addComment(comment));
    }

    /**
     * (1)查找某篇文章下的所有相关评论，包括一级评论(对文章的评论)和二级评论(对评论的评论)两层结构
     * (2)如果用户已登录，需要额外返回用户点赞的评论id列表
     * @param articleId 文章Id
     * @return 某篇文章下的所有相关回复，组织结构参考：undrawUi文档
     */
    @GetMapping("")
    public Result<Object> commentsOfArticle(Long articleId) throws ExecutionException, InterruptedException {
        Long userId = ContextHolderUtil.getUserId();
        return Result.success(commentService.commentsOfArticleWithReply(articleId, userId));
    }
}
