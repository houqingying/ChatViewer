package com.chatviewer.blog.controller;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.dto.ConversationDto;
import com.chatviewer.blog.pojo.Answer;
import com.chatviewer.blog.pojo.Article;
import com.chatviewer.blog.pojo.Problem;
import com.chatviewer.blog.service.AnswerService;
import com.chatviewer.blog.service.ProblemService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/problem")
public class ProblemController {

    @Resource
    ProblemService problemService;

    @Resource
    AnswerService answerService;

    /**
     * 根据分类查询问题列表
     * @param page 页面序号
     * @param pageSize 页面大小
     * @param categoryId 分类id
     * @return Page<Problem>
     */
    @GetMapping("/page")
    public Result<Object> queryProblemPage(int page, int pageSize, Long categoryId) {
        Page<Problem> pageInfo = problemService.pageOfCategory(page, pageSize, categoryId);
        return Result.success(pageInfo);
    }

    /**
     * 添加问题
     * @param map 属性基本与Problem相同，但没有categoryId，
     *            而是articleCategoryList，形如["123", "456", "789"]，表示有层次的目录列表
     * @return 操作是否成功
     */
    @PostMapping("/add")
    public Result<Object> addProblem(@RequestBody Map<String, Object> map) {
        Problem problem = BeanUtil.mapToBean(map, Problem.class, false, CopyOptions.create());
        // 从分类列表取得分类Id
        List<String> categoryList = (List<String>) map.get("categoryList");
        problem.setCategoryId(Long.parseLong(categoryList.get(categoryList.size() - 1)));
        problem.setCreateTime(LocalDateTime.now());
        problemService.save(problem);
        return Result.success();
    }

    /**
     * 查询问题
     * @param problemId 问题id
     * @return Problem
     */
    @GetMapping("")
    public Result<Object> getById(Long problemId) {
        Problem problem = problemService.getById(problemId);
        return Result.success(problem);
    }

    /**
     * 查询MySQL中已登录用户对某问题的回答
     * @param problemId 问题id
     * @return Answer
     */
    @GetMapping("/answer")
    public Result<Object> getAnswer(Long problemId) {
        Long userId = ContextHolderUtil.getUserId();
        Answer answer = answerService.getByUserAndProblem(problemId, userId);
        return Result.success(answer);
    }

    /**
     * 保存回答；
     * (1) 数据库中不存在，直接保存
     * (2) 数据库中存在，对answerContent、answerAudio进行更新
     * @param answer 待存入answer属性值
     * @return 返回存入数据库的answer
     */
    @PostMapping("/answer")
    public Result<Object> saveOrUpdateAnswer(@RequestBody Answer answer) {
        answer.setUserId(ContextHolderUtil.getUserId());
        return Result.success(answerService.saveOrUpdateAnswer(answer));
    }

    /**
     * 对前端传过来的，至少包含problemId的Answer，
     * (1) answer表中该项不存在conversation，新建会话，更新conversation表与answer表
     * (2) 装配prompt，作为conversation的第一条消息返回
     * 后续前端将根据conversationId与firstMessage，调用ChatgptController中的会话接口
     * @param answer 必须设置problemId，可设置answerContent
     * @return  ConversationDto(conversationId, firstMessage)
     */
    @PostMapping("/help")
    public Result<Object> askGptForHelp(@RequestBody Answer answer) {
        answer.setUserId(ContextHolderUtil.getUserId());
        return Result.success(answerService.askGptForHelp(answer));
    }

}
