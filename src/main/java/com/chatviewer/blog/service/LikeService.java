package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.pojo.Like;

/**
 * @author ChatViewer
 */
public interface LikeService extends IService<Like> {

    /**
     * 对文章/评论实体进行点赞/取消点赞
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    void like(Long userId, Integer entityType, Long entityId);

    /**
     * 将某个实体的点赞信息读入redis中
     * @param entityType 类型
     * @param entityId ID
     */
    void readEntityCondition(Integer entityType, Long entityId);

    /**
     * 读取某个实体的点赞数，如果该实体不在redis中，存入Redis
     * @param entityType 类型
     * @param entityId ID
     * @param read 控制位，是否从MySQL中读入并存入Redis
     * @return 点赞数，如果read=false且查找数据在Redis中不存在，返回-1
     */
    Integer queryEntityLikeCounts(Integer entityType, Long entityId, boolean read);


    /**
     * 查询询某个用户对某个实体的点赞情况
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return true-点赞 or false-未点赞
     */
    Boolean queryUserLikeEntity(Long userId, Integer entityType, Long entityId);


    /**
     * 将Redis中的数据更新到MySQL
     */
    void updateLikeItemsFromRedis();

    /**
     * 将Redis中的Count数据更新到MySQL
     */
    void updateLikeCountsFromRedis();
}
