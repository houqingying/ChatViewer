package com.chatviewer.blog.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chatviewer.blog.pojo.Comment;
import lombok.Data;
import java.util.HashMap;

/**
 * @author ChatViewer
 */
@Data
public class CommentDto{

    Long id;

    Long parentId;

    Long uid;

    String content;

    Integer likes;

    String createTime;

    UserDtoForComment user;

    HashMap<String, Object> reply;

}
