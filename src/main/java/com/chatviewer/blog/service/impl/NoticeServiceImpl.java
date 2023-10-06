package com.chatviewer.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.dto.NoticeDto;
import com.chatviewer.blog.dto.NoticeTypeDto;
import com.chatviewer.blog.mapper.ArticleMapper;
import com.chatviewer.blog.mapper.CommentMapper;
import com.chatviewer.blog.mapper.NoticeMapper;
import com.chatviewer.blog.mapper.UserMapper;
import com.chatviewer.blog.pojo.*;
import com.chatviewer.blog.service.NoticeService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.chatviewer.blog.constant.KafkaConstant.*;
import static com.chatviewer.blog.utils.ThreadPoolUtil.pool;

/**
 * @author ChatViewer
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Resource
    CommentMapper commentMapper;

    @Resource
    ArticleMapper articleMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    NoticeMapper noticeMapper;


    /**
     * 用户点赞时，调用该函数生成Event事件：topic + 点赞相关信息(userId, entityType, entityId)
     * 其中点赞相关信息将在EventConsumer类中被消费生成Notice对象
     * @param entityType 点赞对象类型，可能为评论或者文章
     * @param entityId 点赞对象Id
     * @return Event事件
     */
    @Override
    public Event genLikeEvent(Integer entityType, Long entityId) {
        Event event = new Event();
        event.setTopic(TOPIC_LIKE);
        // 发送者为点赞的已登录用户
        event.setSenderId(ContextHolderUtil.getUserId());
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        return event;
    }


    /**
     * 当用户发表评论时，调用该函数，根据评论对象生成Event
     * @param comment 新发表的评论对象
     * @return Event事件：topic + 评论信息(comment)
     */
    @Override
    public Event genCommentEvent(Comment comment) {
        Event event = new Event();
        event.setTopic(TOPIC_COMMENT);
        event.setComment(comment);
        return event;
    }

    /**
     * 在EventConsumer类中被调用，生成点赞的Notice对象
     * @param userId 点赞操作用户
     * @param entityType 点赞对象类型，可能为评论或者文章
     * @param entityId 点赞对象Id
     * @return Notice
     */
    @Override
    public Notice genLikeNotice(Long userId, Integer entityType, Long entityId) {
        // 生成Notice，设置通知类型(点赞通知)、发送人、被点赞实体类型与id、通知状态为未读、通知时间
        Notice notice = new Notice();
        notice.setNoticeType(NOTICE_LIKE_TYPE);
        notice.setSenderId(userId);
        notice.setEntityType(entityType);
        notice.setEntityId(entityId);
        notice.setStatus(0);
        notice.setCreateTime(LocalDateTime.now());

        // 查找被点赞文章或评论的发布人，作为消息接收者；
        // 设置ArticleId
        if (entityType == 0) {
            // 查找文章实体
            LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Article::getArticleId, entityId);
            Article article = articleMapper.selectOne(lambdaQueryWrapper);
            // 设置消息接收人为文章作者
            notice.setReceiverId(article.getUserId());
            notice.setArticleId(article.getArticleId());
        }
        if (entityType == 1) {
            // 查找评论实体
            LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Comment::getCommentId, entityId);
            Comment comment = commentMapper.selectOne(lambdaQueryWrapper);
            // 设置消息接收人为评论发布人
            notice.setReceiverId(comment.getUserId());
            notice.setArticleId(comment.getArticleId());
        }
        return notice;
    }


    /**
     * 在EventConsumer类中被调用，生成评论的Notice对象
     * @param comment 用户新发表的评论
     * @return Notice
     */
    @Override
    public Notice genCommentNotice(Comment comment) {
        // 生成Notice，设置通知类型(评论通知)、发送人、
        // 发布的评论实体类型(评论文章/评论回复)与id、通知状态为未读、通知时间
        Notice notice = new Notice();
        notice.setNoticeType(NOTICE_COMMENT_TYPE);
        notice.setSenderId(comment.getUserId());
        notice.setEntityType(comment.getCommentType());
        notice.setEntityId(comment.getCommentId());
        notice.setStatus(0);
        notice.setCreateTime(LocalDateTime.now());

        // 设置通知的接收人
        if (comment.getCommentType() == 0) {
            // 如果是对文章的回复，查找文章实体的作者
            LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Article::getArticleId, comment.getArticleId());
            Article article = articleMapper.selectOne(lambdaQueryWrapper);
            // 设置消息接收人
            notice.setReceiverId(article.getUserId());
        }
        else if (comment.getCommentType() == 1) {
            // 如果是对评论的回复，查找评论targetId的作者
            LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Comment::getCommentId, comment.getTargetId());
            Comment target = commentMapper.selectOne(lambdaQueryWrapper);
            // 设置消息接收人
            notice.setReceiverId(target.getUserId());
        }

        // 设置文章ID
        notice.setArticleId(comment.getArticleId());

        return notice;
    }


    /**
     * 将数据库里的Notice数据转为前端所需数据
     * 在此处设置好前端需要展示的文字内容，前端直接读取即可，模板为：
     *     [点赞了你的文章《XXX》] [点赞了你在文章《XXX》下的评论]
     *     [回复了你的文章《XXX》] [回复了你在文章《XXX》下的评论]
     * @param notice 通知
     * @return Dto(createTime, senderId, senderName, articleId, content)
     */
    public NoticeDto toDto(Notice notice) {
        if (notice == null) {
            return null;
        }

        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setCreateTime(notice.getCreateTime());
        noticeDto.setSenderId(notice.getSenderId());

        // 1、查找发送方用户名，设置senderName
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, notice.getSenderId());
        User sender = userMapper.selectOne(queryWrapper);
        noticeDto.setSenderName(sender.getUserName());


        // 2、设置articleId，根据articleId得到对应的文章实体，为通知显示内容提供文章名称
        noticeDto.setArticleId(notice.getArticleId());
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<>();
        qw.eq(Article::getArticleId, notice.getArticleId());
        Article article = articleMapper.selectOne(qw);


        // 3、设置通知显示内容
        // [点赞了你的文章《XXX》] [点赞了你在文章《XXX》下的评论]
        // [回复了你的文章《XXX》] [回复了你在文章《XXX》下的评论]
        Integer noticeType = notice.getNoticeType();
        StringBuilder sBuilder = new StringBuilder();
        // 点赞通知 or 评论通知
        if (noticeType.equals(NOTICE_LIKE_TYPE)) {
            sBuilder.append("点赞了你");
        }
        else if (noticeType.equals(NOTICE_COMMENT_TYPE)) {
            sBuilder.append("回复了你");
        }
        // 针对文章实体 or 评论实体
        if (notice.getEntityType() == 0) {
            sBuilder.append("的文章《");
            sBuilder.append(article.getArticleTitle()).append("》");
        }
        else {
            sBuilder.append("在文章《");
            sBuilder.append(article.getArticleTitle()).append("》");
            sBuilder.append("下的评论");
        }
        noticeDto.setContent(sBuilder.toString());

        return noticeDto;
    }


    /**
     * 查询某个用户所有类型的最新消息与未读消息数
     * @param userId 用户Id
     * @return List<NoticeTypeDto> 包含通知类型、最新消息和未读消息数
     */
    @Override
    public List<NoticeTypeDto> noticeBrief(Long userId) throws ExecutionException, InterruptedException {
        List<NoticeTypeDto> briefs = new ArrayList<>();

        // 两类消息的搜索可以并行
        CompletableFuture<Void> likeNoticeSearch = CompletableFuture.runAsync(() ->{
            NoticeTypeDto brief = new NoticeTypeDto();
            brief.setNoticeType(NOTICE_LIKE_TYPE);
            brief.setLatestNotice(selectLatestNotice(userId, NOTICE_LIKE_TYPE));
            brief.setUnreadCount(selectUnreadCount(userId, NOTICE_LIKE_TYPE));
            // 如果该类别确实有消息
            if (brief.getLatestNotice() != null) {
                briefs.add(brief);
            }
        }, pool);

        CompletableFuture<Void> commentNoticeSearch = CompletableFuture.runAsync(() ->{
            NoticeTypeDto brief = new NoticeTypeDto();
            brief.setNoticeType(NOTICE_COMMENT_TYPE);
            brief.setLatestNotice(selectLatestNotice(userId, NOTICE_COMMENT_TYPE));
            brief.setUnreadCount(selectUnreadCount(userId, NOTICE_COMMENT_TYPE));
            if (brief.getLatestNotice() != null) {
                briefs.add(brief);
            }
        }, pool);

        // 使用allOf，使得三个任务执行完，才能继续
        CompletableFuture<Void> combinedTask = CompletableFuture.allOf(likeNoticeSearch, commentNoticeSearch);
        combinedTask.get();
        return briefs;
    }


    /**
     * 查询某个用户某个类型的最新消息
     * @param userId 用户Id
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     * @return 返回NoticeDto
     */
    public NoticeDto selectLatestNotice(Long userId, int noticeType) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, userId);
        queryWrapper.eq(Notice::getNoticeType, noticeType);
        queryWrapper.orderByAsc(Notice::getNoticeId);
        queryWrapper.last("limit 1");
        Notice notice = getOne(queryWrapper);
        return toDto(notice);
    }


    /**
     * 查询某个用户某个类型的所有消息
     * @param userId 用户Id
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     * @return List<NoticeDto> 发送方id、昵称、文章id、消息显示内容、消息时间
     */
    @Override
    public List<NoticeDto> selectTypeNotice(Long userId, int noticeType) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, userId);
        queryWrapper.eq(Notice::getNoticeType, noticeType);
        queryWrapper.orderByAsc(Notice::getNoticeId);
        List<Notice> notices = list(queryWrapper);
        // 使用stream
        return notices.parallelStream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    /**
     * 查询某个用户某个类型的未读消息数
     * @param userId 用户Id
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     * @return 返回NoticeDto 发送方id、昵称、文章id、消息显示内容、消息时间
     */
    public Integer selectUnreadCount(Long userId, int noticeType) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, userId);
        queryWrapper.eq(Notice::getNoticeType, noticeType);
        queryWrapper.eq(Notice::getStatus, 0);
        return count(queryWrapper);
    }

    /**
     * 将用户某个类型的消息全部设置为已读
     * @param userId 用户Id
     * @param noticeType 消息类型 0 点赞消息 1 评论消息
     */
    @Override
    public void readAllOfType(Long userId, int noticeType) {
        noticeMapper.readAllOfType(userId, noticeType);
    }
}
