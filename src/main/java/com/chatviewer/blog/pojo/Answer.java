package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ChatViewer
 */
@Data
@TableName(value ="blog_answer")
public class Answer {
    @TableId(type = IdType.ASSIGN_ID)
    private Long answerId;

    private Long problemId;

    private Long userId;

    private Long conversationId;

    private String answerContent;

    private String answerAudio;

    private LocalDateTime createTime;
}
