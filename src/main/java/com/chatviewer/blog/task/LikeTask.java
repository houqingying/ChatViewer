package com.chatviewer.blog.task;

import com.chatviewer.blog.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 点赞的定时任务
 * @author ChatViewer
 */
@Slf4j
public class LikeTask extends QuartzJobBean {

    @Resource
    LikeService likeService;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void executeInternal(@NotNull JobExecutionContext jobExecutionContext) {
        log.info("LikeTask-------- {}", sdf.format(new Date()));
        //将 Redis 里的点赞信息同步到数据库里
        likeService.updateLikeItemsFromRedis();
        likeService.updateLikeCountsFromRedis();
    }
}


