package com.chatviewer.blog.pojo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

/**
 * @author ChatViewer
 */
@Data
@TableName(value ="blog_article")
public class Article {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    private String articleTitle;

    private String articleContent;

    private Long categoryId;

    private String articleAbstract;

    private String articlePic;

    private Integer likeCounts;

    private Integer commentCounts;

    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
