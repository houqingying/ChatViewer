package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.dto.ArticleDto;
import com.chatviewer.blog.pojo.Article;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author ChatViewer
 */
public interface ArticleService extends IService<Article> {

    /**
     * 根据articleId，返回文章信息、作者信息、Redis点赞数、如果用户已登录，同时返回文章的点赞状态
     * @param articleId 文章Id
     * @return ArticleDto，继承Article类，扩展userName、UserAvatar、isLike
     * @throws ExecutionException 使用多线程执行任务，CompletableFuture类get方法抛出的异常
     * @throws InterruptedException 使用多线程执行任务，CompletableFuture类get方法抛出的异常
     */
    ArticleDto getWithAuthor(Long articleId) throws ExecutionException, InterruptedException;


    /**
     * 返回分页文章信息与作者信息
     * @param page 页面id
     * @param pageSize 每页item数
     * @param categoryId 是否需要查询分类
     * @return Page<ArticleDto>
     */
    Page<ArticleDto> pageWithAuthor(@RequestParam int page, @RequestParam int pageSize, Long categoryId);


    /**
     * 上传文件至阿里云OSS服务器
     * @param file 待上传文件
     * @return 前端Vditor需要的数据类型
     * { "msg": "", "code": 0,
     * "data": { "errFiles": ['filename', 'filename2'], "succMap": { "filename3": "filepath3"}}}
     */
    Result<Object> uploadFile(MultipartFile file);


    /**
     * 新增文章至数据库
     * @param article 待保存文章实体
     */
    void addArticle(Article article);


    /**
     * 增加评论数
     * @param articleId 文章id
     */
    void addCommentCounts(Long articleId);
}
