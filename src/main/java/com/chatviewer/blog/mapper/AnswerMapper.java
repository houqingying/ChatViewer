package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.Answer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author ChatViewer
 */
@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {

    /**
     * 在删除会话时调用，将answer中的conversationId置为null
     * @param conversationId 被删除的会话id
     */
    @Update("update blog_answer set conversation_id = null where conversation_id = #{conversationId}")
    void clearConversation(Long conversationId);

}
