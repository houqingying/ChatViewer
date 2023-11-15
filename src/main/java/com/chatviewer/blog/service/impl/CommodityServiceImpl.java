package com.chatviewer.blog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.mapper.CommodityMapper;
import com.chatviewer.blog.mapper.OrderMapper;
import com.chatviewer.blog.pojo.Commodity;
import com.chatviewer.blog.pojo.Event;
import com.chatviewer.blog.service.CommodityService;
import com.chatviewer.blog.utils.ContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import static com.chatviewer.blog.constant.RedisConstant.*;
import static com.chatviewer.blog.utils.ThreadPoolUtil.pool;

/**
 * @author ChatViewer
 */
@Slf4j
@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 减库存脚本，保证原子性
     */
    private static final DefaultRedisScript<Long> DECR_STOCK_SCRIPT;
    static {
        // 设置脚本存放位置与返回值类型，注意lua脚本返回数字时默认返回值为Long
        DECR_STOCK_SCRIPT = new DefaultRedisScript<>();
        DECR_STOCK_SCRIPT.setLocation(new ClassPathResource("com/chatviewer/blog/lua/decrStock.lua"));
        DECR_STOCK_SCRIPT.setResultType(Long.class);
    }

    @Resource
    OrderMapper orderMapper;

    @Resource
    RedissonClient redissonClient;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 将商品信息(hash)和购买用户信息(set)从MySQL读入Redis，
     * 使用互斥锁防止缓存击穿造成的MySQL压力过大，只有获取锁的线程有机会从数据库中读取
     * @param commodityId 商品id
     * @throws InterruptedException sleep丢出的异常
     */
    public void writeCommodityToRedis(Long commodityId) throws InterruptedException {

        String seckillCommodityKey = SECKILL_COMMODITY_KEY + commodityId;
        String seckillUserKey = SECKILL_COMMODITY_USER_KEY + commodityId;

        // 如果秒杀商品在redis中，什么都不用做
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(seckillCommodityKey))
        && Boolean.TRUE.equals(stringRedisTemplate.hasKey(seckillUserKey))) {
            return;
        }

        // 如果秒杀商品的信息不在redis中，需要将其读入Redis，加锁，使得并发时只有一个线程完成这个工作
        RLock lock = redissonClient.getLock("lock:" + seckillCommodityKey);
        // 尝试上锁
        boolean gotLock = lock.tryLock();
        if (!gotLock) {
            Thread.sleep(100);
            writeCommodityToRedis(commodityId);
        }
        // 获取锁成功，下面的两个任务可以并发
        try {
            // 任务(1) 从数据库中读取商品信息，以hash对象形式将库存与起始时间写入Redis
            CompletableFuture<Void> taskQueryCommodity = CompletableFuture.runAsync(() -> {
                Commodity commodity = getById(commodityId);
                stringRedisTemplate.opsForHash().put(seckillCommodityKey,"stock", commodity.getStock().toString());
                stringRedisTemplate.opsForHash().put(seckillCommodityKey,"begin", commodity.getBeginTime().format(TIME_FORMATTER));
                stringRedisTemplate.opsForHash().put(seckillCommodityKey,"end", commodity.getEndTime().format(TIME_FORMATTER));
                stringRedisTemplate.expire(seckillCommodityKey, SECKILL_COMMODITY_TTL, TimeUnit.SECONDS);
            }, pool);
            // 任务(2) 从数据库中读取购买用户id的信息
            CompletableFuture<Void> taskQueryUsers = CompletableFuture.runAsync(() -> {
                List<Long> userIds = orderMapper.queryBoughtUsers(commodityId);
                for (Long userId: userIds) {
                    stringRedisTemplate.opsForSet().add(seckillUserKey, userId.toString());
                }}, pool);
            // 执行任务1、2
            CompletableFuture<Void> combinedTask = CompletableFuture.allOf(taskQueryCommodity, taskQueryUsers);
            try {
                combinedTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                log.info("Read Commodity Error Occurred.");
            }
        }
        finally {
            // 释放锁代码放入finally中，保证无论执行成功还是失败，都会释放该锁
            lock.unlock();
        }
    }


    @Override
    public String seckill(Long commodityId) throws InterruptedException {

        // 1、如果Redis中没有秒杀商品的信息，将秒杀商品的信息读入Redis中
        writeCommodityToRedis(commodityId);
        String seckillCommodityKey = SECKILL_COMMODITY_KEY + commodityId;
        String seckillUserKey = SECKILL_COMMODITY_USER_KEY + commodityId;

        // 2、判断秒杀是否未开始或者已结束，只读操作，不会有并发问题，不必写入Redis脚本
        String begin = (String) stringRedisTemplate.opsForHash().get(seckillCommodityKey, "begin");
        String end = (String) stringRedisTemplate.opsForHash().get(seckillCommodityKey, "end");
        assert begin != null;
        if (LocalDateTime.now().isBefore(LocalDateTime.parse(begin, TIME_FORMATTER)) ||
                LocalDateTime.now().isAfter(LocalDateTime.parse(end, TIME_FORMATTER))) {
            return "不在秒杀时间范围内";
        }

        // 3、秒杀的核心在于对库存的判断、以及某个用户是否已购买过，写为lua脚本，避免一人购买多个商品和超卖
        Long stock = stringRedisTemplate.execute(DECR_STOCK_SCRIPT,
                Arrays.asList(seckillCommodityKey, seckillUserKey),
                ContextHolderUtil.getUserId().toString());
        if (stock == null || stock <= 0) {
            return "库存不足或用户已购买，秒杀未成功";
        }

        // 4、此时Redis中已扣减库存，秒杀成功，向RabbitMQ中发送消息以执行后续的订单生成与增加用户token数操作
        // 发送消息的格式：使用和Kafka的Event一样的结构
        Event event = new Event();
        event.setTopic("seckill");
        event.setSenderId(ContextHolderUtil.getUserId());
        event.setEntityId(commodityId);
        rabbitTemplate.convertAndSend("char-viewer.seckill.fanout", "", JSONObject.toJSONString(event));
        return "秒杀成功";
    }
}
