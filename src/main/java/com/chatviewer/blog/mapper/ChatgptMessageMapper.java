package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.ChatgptMessage;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author ChatViewer
 */
@Mapper
public interface ChatgptMessageMapper extends BaseMapper<ChatgptMessage> {
}
