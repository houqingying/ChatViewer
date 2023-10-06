package com.chatviewer.blog.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.event.EventProducer;
import com.chatviewer.blog.mapper.LikeMapper;
import com.chatviewer.blog.pojo.Event;
import com.chatviewer.blog.pojo.Like;
import com.chatviewer.blog.service.LikeService;
import com.chatviewer.blog.service.NoticeService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static com.chatviewer.blog.constant.RedisConstant.*;

/**
 * @author ChatViewer
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Resource
    LikeMapper likeMapper;

    @Resource
    NoticeService noticeService;

    @Resource
    EventProducer eventProducer;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public static final Integer ENTITY_NOT_EXIST_CODE = -1;

    /**
     * 用户对实体点赞 / 取消点赞，当操作为用户点赞时，生成
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    @Override
    public void like(Long userId, Integer entityType, Long entityId) {
        String redisEntityKey = redisEntityKey(entityType, entityId);
        String redisCountKey = redisEntityCountKey(entityType, entityId);
        String redisUpdateKey = redisEntityUpdateKey(entityType, entityId, userId);
        // 如果该实体不在redis中，先加入redis
        readEntityCondition(entityType, entityId);
        // 如果redis中不存在该项，或者未点赞，进行点赞操作
        if (!BooleanUtil.isTrue(stringRedisTemplate.hasKey(redisEntityKey)) ||
                !BooleanUtil.isTrue(stringRedisTemplate.opsForSet().isMember(redisEntityKey, userId.toString()))) {
            stringRedisTemplate.opsForSet().add(redisEntityKey, userId.toString());
            stringRedisTemplate.opsForValue().increment(redisCountKey);
            stringRedisTemplate.opsForValue().set(redisUpdateKey, "1", LIKE_ENTITY_UPDATE_TTL, TimeUnit.HOURS);
            // 如果为点赞操作，生成Kafka待发送消息，取消点赞则不必提示用户
            Event likeEvent = noticeService.genLikeEvent(entityType, entityId);
            eventProducer.sendMsg(likeEvent);
        }
        else {
            stringRedisTemplate.opsForSet().remove(redisEntityKey, userId.toString());
            stringRedisTemplate.opsForValue().decrement(redisCountKey);
            stringRedisTemplate.opsForValue().set(redisUpdateKey, "0", LIKE_ENTITY_UPDATE_TTL, TimeUnit.HOURS);
        }

    }

    /**
     * 将某个实体的点赞用户情况、点赞数，从MySQL读入Redis，
     * 点赞用户情况存储结构为Set；
     * 如果已经存在于Redis中，则更新缓存有效时间
     */
    @Override
    public void readEntityCondition(Integer entityType, Long entityId) {
        String redisKey = redisEntityKey(entityType, entityId);
        String redisCountKey = redisEntityCountKey(entityType, entityId);
        // 如果Redis中已经存在该实体的信息，更新有效时间即可
        if (BooleanUtil.isTrue(stringRedisTemplate.hasKey(redisKey))) {
            stringRedisTemplate.expire(redisKey, LIKE_ENTITY_TTL, TimeUnit.HOURS);
            stringRedisTemplate.expire(redisCountKey, LIKE_ENTITY_COUNT_TTL, TimeUnit.HOURS);
            return;
        }
        // 否则需要查找出该实体的所有点赞用户信息
        LambdaQueryWrapper<Like> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Like::getEntityType, entityType);
        lambdaQueryWrapper.eq(Like::getEntityId, entityId);
        lambdaQueryWrapper.eq(Like::getStatus, 1);
        lambdaQueryWrapper.select(Like::getUserId);
        List<Like> userIds = list(lambdaQueryWrapper);
        // 将已点赞用户存入redis
        for (Like like: userIds) {
            stringRedisTemplate.opsForSet().add(redisKey, like.getUserId().toString());
        }
        // 将实体点赞数存入redis
        stringRedisTemplate.opsForValue().set(redisCountKey, String.valueOf(userIds.size()));
        // 设置有效期
        stringRedisTemplate.expire(redisKey, LIKE_ENTITY_TTL, TimeUnit.HOURS);
        stringRedisTemplate.expire(redisCountKey, LIKE_ENTITY_COUNT_TTL, TimeUnit.HOURS);
    }

    /**
     * 读取某个实体的点赞数，如果该实体不在redis中，存入Redis
     * @param entityType 类型
     * @param entityId ID
     * @param read 控制位，是否从MySQL中读入并存入Redis
     * @return 点赞数，如果read=false且查找数据在Redis中不存在，返回-1
     */
    @Override
    public Integer queryEntityLikeCounts(Integer entityType, Long entityId, boolean read) {
        String redisCountKey = redisEntityCountKey(entityType, entityId);
        if (read) {
            readEntityCondition(entityType, entityId);
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisCountKey)) ?
                Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(redisCountKey))) : ENTITY_NOT_EXIST_CODE;
    }

    /**
     * 查询某个用户对某个实体的点赞情况，返回true or false
     */
    @Override
    public Boolean queryUserLikeEntity(Long userId, Integer entityType, Long entityId) {
        String redisKey = redisEntityKey(entityType, entityId);
        readEntityCondition(entityType, entityId);
        return stringRedisTemplate.opsForSet().isMember(redisKey, userId.toString());
    }


    /**
     * @return redis中所有需要update的like数据List<Like>
     */
    public List<Like> allLikesToUpdateFromRedis() {
        ArrayList<Like> likes = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        // 扫描Redis，得到Like数据
        ScanOptions scanOptions = ScanOptions.scanOptions().count(LIKE_SCAN_COUNT).match(LIKE_ENTITY_UPDATE_KEY+"*").build();
        Cursor<String> cursor = stringRedisTemplate.scan(scanOptions);
        while (cursor.hasNext()) {
            String key = cursor.next();
            keys.add(key);
            String[] split = key.split(":");
            // 创建like对象
            Like like = new Like();
            like.setEntityType(Integer.parseInt(split[3]));
            like.setEntityId(Long.parseLong(split[4]));
            like.setUserId(Long.parseLong(split[5]));
            like.setStatus(Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key))));
            like.setCreateTime(LocalDateTime.now());
            likes.add(like);
        }
        // 删除Redis中的所有update键
        stringRedisTemplate.delete(keys);
        return likes;
    }

    /**
     * 更新Redis中的Like记录到MySQL
     */
    @Override
    public void updateLikeItemsFromRedis() {
        List<Like> likes = allLikesToUpdateFromRedis();
        for (Like like: likes) {
            LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Like::getUserId, like.getUserId());
            queryWrapper.eq(Like::getEntityId, like.getEntityId());
            Like likeInMySql = getOne(queryWrapper);
            if (likeInMySql == null) {
                save(like);
            }
            else {
                likeInMySql.setStatus(like.getStatus());
                likeInMySql.setCreateTime(like.getCreateTime());
                updateById(likeInMySql);
            }
        }
    }

    /**
     * 更新Redis中的Count数据到MySQL
     */
    @Override
    public void updateLikeCountsFromRedis() {
        // 扫描Redis，得到Count数据
        ScanOptions scanOptions = ScanOptions.scanOptions().count(LIKE_SCAN_COUNT).match(LIKE_ENTITY_COUNT_KET+"*").build();
        Cursor<String> cursor = stringRedisTemplate.scan(scanOptions);
        while (cursor.hasNext()) {
            String key = cursor.next();
            String[] split = key.split(":");
            // 待更新对象
            int entityType = Integer.parseInt(split[3]);
            long entityId = Long.parseLong(split[4]);
            int likeCounts = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key)));
            // 更新MySQL
            likeMapper.updateLikeCounts(entityType, entityId, likeCounts);
        }
    }

}
