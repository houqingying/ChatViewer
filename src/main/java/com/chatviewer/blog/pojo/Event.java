package com.chatviewer.blog.pojo;

import lombok.Data;

import java.util.HashMap;

/**
 * @author ChatViewer
 */
@Data
public class Event {

    /**
     * 主题名，标识发送至Kafka的哪个topic
     */
    private String topic;

    /**
     * 发送者id
     */
    private Long senderId;

    /**
     * 为点赞通知设计的数据：点赞对象类型与id
     */
    private Integer entityType;

    private Long entityId;

    /**
     * 为评论通知设计的数据：传递新增的评论对象
     */
    private Comment comment;
}
