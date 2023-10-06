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
@TableName(value = "blog_conversation")
public class Conversation {

    @TableId(type = IdType.ASSIGN_ID)
    private Long conversationId;

    private Long userId;

    private Integer conversationType;

    private LocalDateTime createTime;

}
