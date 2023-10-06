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
@TableName(value = "blog_notice")
public class Notice {
    @TableId(type = IdType.ASSIGN_ID)
    private Long noticeId;

    /**
     * 0 点赞通知消息
     * 1 评论通知消息
     */
    private Integer noticeType;

    /**
     * 发送方用户Id
     */
    private Long senderId;


    /**
     * 接收方用户Id
     */
    private Long receiverId;


    private Integer entityType;

    private Long entityId;

    private Long articleId;

    /**
     * 扩展内容，存储消息中需要携带的其他信息；暂时未用
     */
    private String noticeContent;

    /**
     * 已读为1，未读为0
     */
    private Integer status;

    /**
     * 通知创建时间
     */
    private LocalDateTime createTime;

}
