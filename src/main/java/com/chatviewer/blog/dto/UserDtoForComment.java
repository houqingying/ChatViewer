package com.chatviewer.blog.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ChatViewer
 */
@Data
public class UserDtoForComment {

    String username;

    String avatar;

}
