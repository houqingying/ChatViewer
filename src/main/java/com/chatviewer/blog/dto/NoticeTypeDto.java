package com.chatviewer.blog.dto;

import lombok.Data;

/**
 * @author ChatViewer
 */
@Data
public class NoticeTypeDto {

    /**
     * 通知类型
     */
    private Integer noticeType;

    /**
     * 最新消息
     */
    private NoticeDto latestNotice;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

}
