package com.chatviewer.blog.service.impl;

import com.chatviewer.blog.service.DataService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.chatviewer.blog.constant.RedisConstant.*;

/**
 * 计算统计量
 * @author ChatViewer
 */
@Service
public class DataServiceImpl implements DataService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 将IP计入UV
     * @param ip IP地址
     */
    @Override
    public void recordIp(String ip) {
        // 拼接日期
        String key = redisDailyUvKey(dateFormat.format(new Date()));
        // 加入HyperLogLog
        stringRedisTemplate.opsForHyperLogLog().add(key, ip);
    }

    /**
     * @param start 开始日期，包含
     * @param end 结束日期，包含
     * @return 统计[start, end]时间内的UV
     */
    @Override
    public Long calIntervalUv(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        List<String> keys = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = redisDailyUvKey(dateFormat.format(calendar.getTime()));
            keys.add(key);
            calendar.add(Calendar.DATE, 1);
        }

        String key = redisIntervalUvKey(dateFormat.format(start), dateFormat.format(end));
        stringRedisTemplate.opsForHyperLogLog().union(key, keys.toArray(new String[0]));

        return stringRedisTemplate.opsForHyperLogLog().size(key);
    }


}
