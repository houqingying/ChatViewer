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
@TableName(value = "blog_message")
public class Message {
    @TableId(type = IdType.ASSIGN_ID)
    Long messageId;

    Long fromId;

    Long toId;

    String content;

    /**
     * 标识消息的状态：0 未读， 1 已读，2 删除
     */
    Integer status;

    LocalDateTime createTime;
}
