package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chatviewer.blog.dto.CommentDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ChatViewer
 */
@Data
@TableName(value = "blog_comment")
public class Comment {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    Long commentId;

    String commentContent;

    Integer commentType;

    Long articleId;

    Long parentId;

    Long targetId;

    Long userId;

    Integer likeCounts;

    LocalDateTime createTime;

    public CommentDto toDto( ) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(getCommentId());
        commentDto.setParentId(getParentId());
        commentDto.setUid(getUserId());
        commentDto.setContent(getCommentContent());
        commentDto.setLikes(getLikeCounts());
        commentDto.setCreateTime(getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return commentDto;
    }
}
