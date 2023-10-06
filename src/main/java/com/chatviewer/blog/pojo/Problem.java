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
@TableName(value ="blog_problem")
public class Problem {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long problemId;

    private String problemTitle;

    private String problemAnswer;

    private Long categoryId;

    private LocalDateTime createTime;
}
