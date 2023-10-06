package com.chatviewer.blog.dto;

import com.chatviewer.blog.pojo.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ChatViewer
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleDto extends Article {
    /**
     * 文章作者昵称
     */
    private String userName;

    /**
     * 文章作者头像
     */
    private String userAvatar;

    /**
     * 如果用户已登录，用户是否点赞了该文章
     */
    Boolean isLike;
}
