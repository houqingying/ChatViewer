package com.chatviewer.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ChatViewer
 */
@Data
public class ContactPersonDto {

    /**
     * 联系人ID
     */
    Long id;

    String name;

    String headImg;

    /**
     * 最后一条消息内容
     */
    String lastMsg;

    /**
     * 最后一次发送时间
     */
    LocalDateTime createTime;

    /**
     * 未读消息数
     */
    Integer unReadCounts;

}
