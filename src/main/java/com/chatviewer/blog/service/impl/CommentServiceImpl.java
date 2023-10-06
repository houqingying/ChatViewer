package com.chatviewer.blog.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.dto.CommentDto;
import com.chatviewer.blog.event.EventProducer;
import com.chatviewer.blog.mapper.CommentMapper;
import com.chatviewer.blog.mapper.UserMapper;
import com.chatviewer.blog.pojo.Comment;
import com.chatviewer.blog.pojo.Event;
import com.chatviewer.blog.pojo.User;
import com.chatviewer.blog.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.chatviewer.blog.constant.RedisConstant.redisEntityKey;
import static com.chatviewer.blog.service.impl.LikeServiceImpl.ENTITY_NOT_EXIST_CODE;
import static com.chatviewer.blog.utils.ThreadPoolUtil.pool;

/**
 * @author ChatViewer
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    ArticleService articleService;

    @Resource
    LikeService likeService;

    @Resource
    NoticeService noticeService;

    @Resource
    UserMapper userMapper;

    @Resource
    CommentMapper commentMapper;

    @Resource
    EventProducer eventProducer;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final Integer COMMENT_TYPE_ARTICLE = 0;
    private static final Integer COMMENT_TYPE_COMMENT = 1;


    /**
     * (1)查找某篇文章下的所有相关评论，包括对文章的回复，和对评论的回复
     * (2)如果用户已登录，需要额外返回用户点赞的评论id列表
     * @param articleId 文章Id
     * @param userId 用户登录时的用户Id
     * @return 某篇文章下的所有相关回复，组织结构参考：undrawUi文档
     */
    @Override
    public Map<String, Object> commentsOfArticleWithReply(Long articleId, Long userId) throws ExecutionException, InterruptedException {
        HashMap<String, Object> map = new HashMap<>(4);

        // 1、子任务1：组织该篇文章下的评论列表
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            List<CommentDto> commentDtos = null;
            try {
                commentDtos = commentsOfWithReply(articleId, COMMENT_TYPE_ARTICLE);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            map.put("comments", commentDtos);
        }, pool);

        // 2、子任务2：组织用户的点赞列表
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            List<Long> likeCommentIds = userId != null ?  queryLikeComments(articleId, userId) : new ArrayList<>();
            map.put("likeList", likeCommentIds);
        }, pool);

        CompletableFuture<Void> taskCombined = CompletableFuture.allOf(task1, task2);
        taskCombined.get();

        return map;
    }

    /**
     * 根据parentId和评论类型(回复文章/回复评论)来查找其对应的子回复，包含扩展的发布者user和reply信息
     * @param parentId 文章id或一级评论id
     * @param commentType 被回复对象类型，文章或评论
     * @return List<CommentDto>
     */
    private List<CommentDto> commentsOfWithReply(Long parentId, Integer commentType) throws ExecutionException, InterruptedException {
        // 1、查找MySQL, 找到parentId的【直接子评论】
        // (1)对文章类型，找到直接回复该文章的评论        |---\ SQL语句统一为根据parentId与commentType查找comment表
        // (2)对评论类型，找到该评论下的所有二级评论      |---/
        List<CommentDto> commentDtos = commentMapper.queryReplyWithAuthor(parentId, commentType);

        // 2、遍历子评论，两任务可并发进行
        // 子任务(1) 对文章评论(一级评论)，还需扩充comment的reply信息(二级评论)
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            if (commentType.equals(COMMENT_TYPE_ARTICLE)) {

                for (CommentDto commentDto: commentDtos) {
                    List<CommentDto> list = null;
                    // 对一级评论, 递归调用该函数自身以查找二级评论
                    try {
                        list = commentsOfWithReply(commentDto.getId(), COMMENT_TYPE_COMMENT);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list != null && list.size() > 0) {
                        HashMap<String, Object> reply = new HashMap<>(16);
                        reply.put("total", list.size());
                        reply.put("list", list);
                        commentDto.setReply(reply);
                    }
                }
            }
        }, pool);

        // 子任务(2) 对子评论列表，若评论点赞状态在Redis中，需要更新点赞数信息
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            for (CommentDto commentDto: commentDtos) {
                // 更新点赞信息：read为false表示不从MySQL中将点赞信息读入Redis
                Integer likes = likeService.queryEntityLikeCounts(COMMENT_TYPE_COMMENT, commentDto.getId(), false);
                if (!likes.equals(ENTITY_NOT_EXIST_CODE)) {
                    commentDto.setLikes(likes);
                }
            }
        }, pool);

        CompletableFuture<Void> taskCombined = CompletableFuture.allOf(task1, task2);
        taskCombined.get();

        return commentDtos;
    }


    /**
     * 查找某篇文章下某个用户点赞的所有评论Id
     * @param articleId 文章id
     * @param userId 登录用户id
     * @return List<Long>，点赞过的评论id列表
     */
    public List<Long> queryLikeComments(Long articleId, Long userId) {
        // 该篇文章下的所有评论, TODO: 可以以后做分页
        List<Long> ids = commentMapper.queryArticleComments(articleId);
        // 读取MySQL中的点赞情况
        List<Long> likeIds = commentMapper.queryLikeComments(articleId, userId);

        // 检查该文章下的评论，在Redis中是否有更新
        for (Long id: ids) {
            // 如果该条comment的情况在Redis中，以Redis的点赞情况为准
            if (BooleanUtil.isTrue(stringRedisTemplate.hasKey(redisEntityKey(1, id)))) {
                // (1)如果Redis中存在而MySQL中不存在，加入likeIds
                if (BooleanUtil.isTrue(stringRedisTemplate.opsForSet().isMember(redisEntityKey(1, id), userId.toString()))
                        && !likeIds.contains(id)) {
                    likeIds.add(id);
                }
                // (2)如果Redis中不存在而MySQL中存在，从likeIds中删去
                else if (BooleanUtil.isFalse(stringRedisTemplate.opsForSet().isMember(redisEntityKey(1, id), userId.toString()))) {
                    likeIds.remove(id);
                }
            }
        }
        return likeIds;
    }

    /**
     * commentContent: 评论内容
     * commentType：类型，对文章的评论(0)，对评论的评论(1)
     * articleId: 该评论在哪篇文章下
     * parentId：上级实体id，如果为对文章的评论，则为文章id；如果为对评论的评论，则为一级评论id
     * targetId: 被回复用户id
     * @param comment 待添加评论
     * @return CommentDto 扩充后的评论信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentDto addComment(Comment comment) {
        // 如果评论的是文章对象，文章对象的评论数加1，不统计评论的评论数
        if (comment.getCommentType().equals(COMMENT_TYPE_ARTICLE)) {
            articleService.addCommentCounts(comment.getParentId());
        }
        // 初始化点赞数
        comment.setLikeCounts(0);
        comment.setCreateTime(LocalDateTime.now());
        this.save(comment);

        // 生成通知消息
        Event event = noticeService.genCommentEvent(comment);
        eventProducer.sendMsg(event);

        // 返回前端undrawUi文档所需的CommentDto
        CommentDto commentDto = comment.toDto();

        // 扩充用户信息
        Long userId = comment.getUserId();
        User user = userMapper.selectById(userId);
        commentDto.setUser(user.toUseDtoForComment());

        return commentDto;
    }

}
