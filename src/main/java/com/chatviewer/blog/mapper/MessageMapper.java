package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.dto.ContactPersonDto;
import com.chatviewer.blog.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @author ChatViewer
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询当前用户的所有联系者，以及发送的最后一条信息与未读消息数
     * @param id 用户id
     * @return List<ContactPersonDto>
     */
    List<ContactPersonDto> selectConversations(Long id);

    /**
     * 将用户id1和id2间的未读信息为已读
     * @param id1 用户1
     * @param id2 用户2
     */
    void read(Long id1, Long id2);
}
