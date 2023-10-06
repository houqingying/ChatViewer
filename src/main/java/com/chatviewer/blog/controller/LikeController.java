package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.service.LikeService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/like")
public class LikeController {

    @Resource
    LikeService likeService;

    /**
     * 执行点赞 / 取消点赞操作
     * @param params Map结构
     *               entityType：点赞对象类型，0-文章，1-评论
     *               entityId： 对象id
     */
    @PostMapping("")
    public Result<Object> like(@RequestBody Map<String, Object> params) {
        // 得到存储在context中的userId
        Long userId = ContextHolderUtil.getUserId();
        Integer entityType = (Integer) params.get("entityType");
        Long entityId = Long.parseLong((String) params.get("entityId"));
        likeService.like(userId, entityType, entityId);
        return Result.success();
    }

    /**
     * 查询登录用户对实体的点赞状态
     * @param entityType 点赞对象类型，0-文章，1-评论
     * @param entityId 对象id
     * @return 是否点赞
     */
    @GetMapping("")
    public Result<Object> queryLikeStatus(Integer entityType, Long entityId) {
        Long userId = ContextHolderUtil.getUserId();
        return Result.success(likeService.queryUserLikeEntity(userId, entityType, entityId));
    }

}
