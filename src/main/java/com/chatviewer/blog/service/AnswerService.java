package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.dto.ConversationDto;
import com.chatviewer.blog.pojo.Answer;
import com.chatviewer.blog.pojo.Problem;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author ChatViewer
 */
public interface AnswerService extends IService<Answer> {
    /**
     * 查询MySQL中用户对某问题的回答
     * @param problemId 问题Id
     * @param userId 用户Id
     * @return Answer
     */
    Answer getByUserAndProblem(Long problemId, Long userId);

    /**
     * 保存回答；
     * (1) 数据库中不存在，直接保存
     * (2) 数据库中存在，对answerContent、answerAudio进行更新
     * @param answer 待存入answer属性值
     * @return 返回存入数据库的answer
     */
    Answer saveOrUpdateAnswer(Answer answer);

    /**
     * 对前端传过来的，至少包含problemId和userId的Answer，
     * (1) answer表中该项不存在conversation，新建会话，更新conversation表与answer表
     * (2) 装配prompt，作为conversation的第一条消息返回
     * 后续前端将根据conversationId与firstMessage，调用ChatgptController中的会话接口
     * @param answer 必须设置problemId和userId，可设置answerContent
     * @return  ConversationDto(conversationId, firstMessage)
     */
    ConversationDto askGptForHelp(Answer answer);

}