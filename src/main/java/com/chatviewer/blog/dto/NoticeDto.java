package com.chatviewer.blog.dto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ChatViewer
 */
@Data
public class NoticeDto {
    /**
     * 发送方ID
     */
    private Long senderId;

    /**
     * 发送方名称
     */
    private String senderName;

    /**
     * 在哪篇文章下进行的操作
     */
    private Long articleId;

    /**
     * 消息显示内容
     */
    private String content;

    /**
     * 本地时间
     */
    private LocalDateTime createTime;
}
