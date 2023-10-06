package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chatviewer.blog.dto.UserDtoForComment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author ChatViewer
 */
@Data
@TableName(value ="blog_user")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userPhone;

    private String userPassword;

    private String userAvatar;

    private Integer userTokenCount;

    private String userApiKey;

    public UserDtoForComment toUseDtoForComment() {
        UserDtoForComment userDtoForComment = new UserDtoForComment();
        userDtoForComment.setUsername(userName);
        userDtoForComment.setAvatar(userAvatar);
        return userDtoForComment;
    }

}
