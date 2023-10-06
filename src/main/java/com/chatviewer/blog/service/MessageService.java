package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.dto.ContactPersonDto;
import com.chatviewer.blog.pojo.Message;
import java.util.List;

/**
 * @author ChatViewer
 */
public interface MessageService extends IService<Message> {

    /**
     * 查询当前登录用户的所有联系者(id、name、头像)，以及发送的最后一条信息与未读消息数
     * @param userId 用户id
     * @return List<ContactPersonDto>
     */
    List<ContactPersonDto> selectConversations(Long userId);

    /**
     * 查找用户id1和id2间的最新PAGE_SIZE条信息
     * @param id1 用户1
     * @param id2 用户2
     * @return Page<Message>
     */
    Page<Message> selectMessages(Long id1, Long id2);

    /**
     * 保存消息message至数据库中
     * @param message from_id、to_id、content已设置，还需设置创建时间与读取状态
     */
    void saveMessage(Message message);

    /**
     * 将用户1和用户2的所有未读消息设置为已读
     * @param id1 用户1
     * @param id2 用户2
     */
    void read(Long id1, Long id2);
}
