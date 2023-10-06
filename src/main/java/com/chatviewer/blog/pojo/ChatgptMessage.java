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
@TableName(value = "blog_chatgpt_message")
public class ChatgptMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long messageId;

    private Integer messageDirection;

    private Long conversationId;

    private String content;

    private LocalDateTime createTime;

}
