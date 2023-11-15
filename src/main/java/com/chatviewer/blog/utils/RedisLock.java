package com.chatviewer.blog.utils;

import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


/**
 * 如果想自己实现分布式锁，可以使用该代码替换Redisson，否则忽略该文件即可
 * @author ChatViewer
 */
public class RedisLock {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        // 脚本存放位置
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("com/chatviewer/blog/lua/unlock.lua"));
        // 返回值
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = "thread:";

    private final String name;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean tryLock(Long timeOutSec) {
        try {
            // 用线程的Id当作key，实现线程间的互斥
            String threadId = ID_PREFIX + Thread.currentThread().getId();
            // 设置timeOutSec，避免发生意外状况时，锁无法释放，一直被某个dead的线程持有
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(KEY_PREFIX + name, threadId, timeOutSec, TimeUnit.SECONDS);
            return BooleanUtil.isTrue(success);
        }
        catch (Exception e) {
            return false;
        }
    }

    public void unlock() {
        // 调用lua脚本，在释放锁之前，先检查这把锁是否为当前线程持有的锁，防止误删锁
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(KEY_PREFIX + name), threadId);
    }

}
