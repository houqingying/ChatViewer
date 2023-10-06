package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author ChatViewer
 */
@Data
@TableName(value = "blog_like")
public class Like {

    @TableId(type = IdType.ASSIGN_ID)
    private Long likeId;

    private Long userId;

    private Integer entityType;

    private Long entityId;

    /**
     * 点赞的状态：0取消点赞，1点赞
     */
    private Integer status;

    private LocalDateTime createTime;

}
