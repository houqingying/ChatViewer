package com.chatviewer.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.dto.ConversationDto;
import com.chatviewer.blog.mapper.AnswerMapper;
import com.chatviewer.blog.mapper.ProblemMapper;
import com.chatviewer.blog.pojo.Answer;
import com.chatviewer.blog.pojo.Conversation;
import com.chatviewer.blog.pojo.Problem;
import com.chatviewer.blog.service.AnswerService;
import com.chatviewer.blog.service.ChatgptService;
import com.chatviewer.blog.service.ConversationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author ChatViewer
 */
@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {

    @Resource
    ConversationService conversationService;

    @Resource
    ProblemMapper problemMapper;


    /**
     * 查询MySQL中用户对某问题的回答
     * @param problemId 问题Id
     * @param userId 用户Id
     * @return Answer
     */
    @Override
    public Answer getByUserAndProblem(Long problemId, Long userId) {
        LambdaQueryWrapper<Answer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Answer::getUserId, userId);
        lambdaQueryWrapper.eq(Answer::getProblemId, problemId);
        return getOne(lambdaQueryWrapper);
    }

    /**
     * 保存回答；
     * (1) 数据库中不存在，直接保存
     * (2) 数据库中存在，对answerContent、answerAudio进行更新
     * @param answer 待存入answer属性值
     * @return 返回存入数据库的answer
     */
    @Override
    public Answer saveOrUpdateAnswer(Answer answer) {
        LambdaQueryWrapper<Answer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Answer::getUserId, answer.getUserId());
        lambdaQueryWrapper.eq(Answer::getProblemId, answer.getProblemId());
        // 是否存在已有回答
        Answer existAnswer = getOne(lambdaQueryWrapper);

        if (existAnswer != null) {
            if (answer.getAnswerContent() != null) {
                existAnswer.setAnswerContent(answer.getAnswerContent());
            }
            if (answer.getAnswerAudio() != null) {
                existAnswer.setAnswerAudio(answer.getAnswerAudio());
            }
            updateById(existAnswer);
            return existAnswer;
        }
        else {
            answer.setCreateTime(LocalDateTime.now());
            save(answer);
            return answer;
        }
    }

    /**
     * 对前端传过来的，至少包含problemId和userId的Answer，
     * (1) answer表中该项不存在conversation，新建会话，更新conversation表与answer表
     * (2) 装配prompt，作为conversation的第一条消息返回
     * 后续前端将根据conversationId与firstMessage，调用ChatgptController中的会话接口
     * @param answer 必须设置problemId和userId，可设置answerContent
     * @return  ConversationDto(conversationId, firstMessage)
     */
    @Override
    public ConversationDto askGptForHelp(Answer answer) {
        Answer answerDb = saveOrUpdateAnswer(answer);

        // 判断是否需要新建对话
        Long conversationId = answerDb.getConversationId();
        // 需要新建对话
        if (conversationId == null) {
            Conversation conversation = new Conversation();
            conversation.setUserId(answer.getUserId());
            conversation.setConversationType(1);
            conversationId = conversationService.createConversation(conversation);
            // 更新answerDb
            answerDb.setConversationId(conversationId);
            updateById(answerDb);
        }

        // 得到问题与回答，装配prompt
        Problem problem = problemMapper.selectById(answer.getProblemId());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("假设你是一位IT专家，对于问题\"");
        stringBuilder.append(problem.getProblemTitle());
        if (answerDb.getAnswerContent() == null) {
            stringBuilder.append("\", 你能否给出解答？");
        }
        else {
            stringBuilder.append("我的回答是:\"");
            stringBuilder.append(answerDb.getAnswerContent());
            stringBuilder.append("\", 你能否进一步完善与扩充我的回答？");
        }
        String prompt = stringBuilder.toString();

        ConversationDto conversationDto = new ConversationDto();
        conversationDto.setConversationId(conversationId);
        conversationDto.setFirstMessage(prompt);
        return conversationDto;
    }


}