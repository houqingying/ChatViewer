package com.chatviewer.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.dto.ArticleDto;
import com.chatviewer.blog.mapper.ArticleMapper;
import com.chatviewer.blog.mapper.UserMapper;
import com.chatviewer.blog.pojo.Article;
import com.chatviewer.blog.pojo.Category;
import com.chatviewer.blog.pojo.User;
import com.chatviewer.blog.service.ArticleService;
import com.chatviewer.blog.service.CategoryService;
import com.chatviewer.blog.service.LikeService;
import com.chatviewer.blog.utils.AliyunOssUtil;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static com.chatviewer.blog.utils.ThreadPoolUtil.pool;

/**
 * @author ChatViewer
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    ArticleMapper articleMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    LikeService likeService;

    @Resource
    CategoryService categoryService;

    @Resource
    AliyunOssUtil aliyunOssUtil;


    @Override
    public ArticleDto getWithAuthor(Long articleId) throws ExecutionException, InterruptedException {
        ArticleDto articleDto = new ArticleDto();

        // UserId从Context中获取，不能放至线程任务里，否则得到的会是null
        Long userId = ContextHolderUtil.getUserId();

        // 任务一: MySQL进行连接，查询带作者的文章信息，复制属性至articleDto
        CompletableFuture<Void> taskQueryArticle = CompletableFuture.runAsync(() ->
                        BeanUtil.copyProperties(
                                articleMapper.getWithAuthor(articleId),
                                articleDto,
                                // 不拷贝like相关属性，避免覆盖
                                "likeCounts", "isLike"), pool);

        // 任务二：Redis查询文章点赞数
        CompletableFuture<Void> taskLikeCounts = CompletableFuture.runAsync(
                () -> articleDto.setLikeCounts(likeService.queryEntityLikeCounts(0, articleId, true)), pool);


        // 任务三：如果用户登录，需要查询文章的点赞状态
        CompletableFuture<Void> taskLikeState = CompletableFuture.runAsync(() -> {
            if (userId != null) {
                articleDto.setIsLike(likeService.queryUserLikeEntity(userId, 0, articleId));
            }
            else {
                articleDto.setIsLike(false);
            }
        }, pool);

        // 使用allOf，使得三个任务执行完，才能继续
        CompletableFuture<Void> combinedTask = CompletableFuture.allOf(taskQueryArticle, taskLikeCounts, taskLikeState);
        combinedTask.get();

        return articleDto;
    }


    @Override
    public Page<ArticleDto> pageWithAuthor(int page, int pageSize, Long categoryId) {
        // 设置好分页所用Page
        Page<Article> pageInfo = new Page<>(page, pageSize);

        // 构造条件构造器, 添加过滤条件和排序条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getUpdateTime);

        // 得到子分类列表，判断是否在子分类中
        if (categoryId != null) {
            List<Long> ids = categoryService.childrenIdOf(categoryId);
            queryWrapper.in(Article::getCategoryId, ids);
        }

        // 查询，转换为输出格式
        this.page(pageInfo, queryWrapper);

        // 拷贝旧Page到新Page，忽略records属性
        Page<ArticleDto> pageInfoPlus = new Page<>();
        BeanUtil.copyProperties(pageInfo, pageInfoPlus, "records");

        // 扩展author信息到article
        List<Article> articles = pageInfo.getRecords();
        List<ArticleDto> articleDtos = new ArrayList<>();
        for (Article article: articles) {
            // 查询用户信息
            Long userId = article.getUserId();
            User user = userMapper.selectById(userId);

            ArticleDto articleDto = new ArticleDto();
            BeanUtil.copyProperties(article, articleDto);

            // 设置user信息
            articleDto.setUserAvatar(user.getUserAvatar());
            articleDto.setUserName(user.getUserName());
            articleDtos.add(articleDto);
        }

        pageInfoPlus.setRecords(articleDtos);
        return pageInfoPlus;
    }


    @Override
    public Result<Object> uploadFile(@RequestBody MultipartFile file) {
        // 从原始文件名得到扩展名
        String originFileName = file.getOriginalFilename();
        assert originFileName != null;
        String extension = originFileName.substring(originFileName.lastIndexOf('.'));
        // 重命名
        String objectName = "article_picture/chat_viewer_" + UUID.randomUUID() + extension;
        try {
            // 调用阿里云OSS工具类，上传文件
            String filePath = aliyunOssUtil.uploadFile(file.getBytes(), objectName);

            // 按Vditor文档要求组装返回结果
            // { "msg": "", "code": 0,
            // "data": { "errFiles": ['filename', 'filename2'], "succMap": { "filename3": "filepath3"}}}
            Map<String, Object> uploadDto = new HashMap<>(2);
            Map<String, Object> successFiles = new HashMap<>(4);
            successFiles.put(originFileName, filePath);
            uploadDto.put("succMap", successFiles);
            uploadDto.put("errFiles", new ArrayList<>());
            Result<Object> res = Result.success(uploadDto);
            res.setCode(0);
            res.setMsg("");
            return res;
        }
        catch (Exception e) {
            log.info("file upload failed { }", e);
        }
        return Result.fail();
    }


    @Override
    public void addArticle(Article article) {
        article.setUserId(ContextHolderUtil.getUserId());
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setLikeCounts(0);
        article.setCommentCounts(0);
        save(article);
    }


    @Override
    public void addCommentCounts(Long articleId) {
        Article article = this.getById(articleId);
        article.setCommentCounts(article.getCommentCounts() + 1);
        this.updateById(article);
    }

}
