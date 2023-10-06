package com.chatviewer.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.dto.ContactPersonDto;
import com.chatviewer.blog.mapper.MessageMapper;
import com.chatviewer.blog.pojo.Message;
import com.chatviewer.blog.service.MessageService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChatViewer
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    MessageMapper messageMapper;

    private static final Integer PAGE_SIZE = 50;

    /**
     * 查询当前登录用户的所有联系者，以及发送的最后一条信息与未读消息数
     * @param userId 用户id
     * @return List<ContactPersonDto>
     */
    @Override
    public List<ContactPersonDto> selectConversations(Long userId) {
        return messageMapper.selectConversations(userId);
    }

    /**
     * 查找用户id1和id2间的最新PAGE_SIZE条信息
     * @param id1 用户1
     * @param id2 用户2
     * @return Page<Message>
     */
    @Override
    public Page<Message> selectMessages(Long id1, Long id2) {

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Message::getFromId, id1, id2);
        queryWrapper.in(Message::getToId, id1, id2);
        queryWrapper.orderByAsc(Message::getMessageId);

        // 求得总项数
        Integer count = count(queryWrapper);
        Page<Message> page = new Page<>(count % PAGE_SIZE == 0 ? count / PAGE_SIZE : count / PAGE_SIZE + 1, PAGE_SIZE);
        page(page, queryWrapper);

        return page;
    }

    /**
     * 保存消息message至数据库中
     * @param message from_id、to_id、content已设置
     */
    @Override
    public void saveMessage(Message message) {
        message.setStatus(0);
        message.setCreateTime(LocalDateTime.now());
        save(message);
    }

    /**
     * 将用户id1和id2间的未读信息为已读
     * @param id1 用户1
     * @param id2 用户2
     */
    @Override
    public void read(Long id1, Long id2) {
        messageMapper.read(id1, id2);
    }
}
