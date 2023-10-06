package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author ChatViewer
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 将用户的所有type类型消息设置为已读
     * @param userId 用户id
     * @param noticeType 消息类型 0-点赞消息 1-评论消息
     */
    @Update("update blog_notice set status = 1 where receiver_id = #{userId} and notice_type = #{noticeType}")
    void readAllOfType(Long userId, int noticeType);
}
