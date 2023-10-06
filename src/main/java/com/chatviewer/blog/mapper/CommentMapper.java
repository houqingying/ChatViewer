package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.dto.CommentDto;
import com.chatviewer.blog.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author ChatViewer
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询articleId文章下的评论id列表
     * @param articleId 文章Id
     * @return 评论id列表
     */
    List<Long> queryArticleComments(Long articleId);

    /**
     * 查询articleId文章下userId用户点赞过的评论列表
     * @param articleId 文章Id
     * @param userId 用户Id
     * @return 某个用户点赞过的评论列表
     */
    List<Long> queryLikeComments(Long articleId, Long userId);


    /**
     * 查找某个对象(文章或评论)的直接评论，同时返回评论的发布人信息
     * @param parentId 文章id或一级评论id
     * @param commentType 被回复对象类型，文章或评论
     * @return List<CommentAuthorDto>, CommentAuthorDto继承Comment类，扩充了user相关信息
     */
    List<CommentDto> queryReplyWithAuthor(Long parentId, Integer commentType);
}
