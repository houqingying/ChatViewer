package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ChatViewer
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
