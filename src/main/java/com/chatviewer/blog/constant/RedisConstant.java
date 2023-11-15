package com.chatviewer.blog.constant;

/**
 * @author ChatViewer
 * 存储保存在Redis的变量名
 */
public class RedisConstant {
    public static final String USER_CODE_KEY = "user:login:code:";
    public static final Long USER_CODE_TTL = 15L;

    public static final String USER_TOKEN_KEY = "user:login:token:";
    public static final Long USER_TOKEN_TTL = 1440L;

    public static final String USER_INFO_KEY = "user:info:";
    public static final Long USER_INFO_TTL = 120L;

    public static final String CATEGORY_ALL_KEY = "category:all";
    public static final Long CATEGORY_ALL_TTL = 360L;

    /**
     * hash，存储商品信息
     */
    public static final String SECKILL_COMMODITY_KEY = "seckill:commodity:";
    public static final Long SECKILL_COMMODITY_TTL = 3600L;

    /**
     * set，存储购买用户的信息
     */
    public static final String SECKILL_COMMODITY_USER_KEY = "seckill:commodity:user:";
    public static final Long SECKILL_COMMODITY_USER_TTL = 3600L;


    public static final String LIKE_ENTITY_KEY = "like:entity:";
    public static final Long LIKE_ENTITY_TTL = 24L;
    public static final String LIKE_ENTITY_COUNT_KET = "like:entity:count:";
    public static final Long LIKE_ENTITY_COUNT_TTL = 24L;
    public static final String LIKE_ENTITY_UPDATE_KEY = "like:entity:update:";
    public static final Long LIKE_ENTITY_UPDATE_TTL = 3L;
    public static final Integer LIKE_SCAN_COUNT = 1000;

    /**
     * @param entityType 实体类型 0 文章；1 评论
     * @param entityId 实体Id
     */
    public static String redisEntityKey(Integer entityType, Long entityId) {
        // 编码规则：like:entity:0/1:id
        return LIKE_ENTITY_KEY + entityType + ":" + entityId;
    }

    public static String redisEntityCountKey(Integer entityType, Long entityId) {
        // 编码规则：like:entity:count:0/1:id
        return LIKE_ENTITY_COUNT_KET + entityType + ":" + entityId;
    }

    public static String redisEntityUpdateKey(Integer entityType, Long entityId, Long userId) {
        // 编码规则：like:entity:update:0/1:entityId:userId
        return LIKE_ENTITY_UPDATE_KEY + entityType + ":" + entityId + ":" + userId;
    }


    public static final String UV_KEY = "uv:";
    public static final String DAU_KEY = "dau:";

    public static String redisDailyUvKey(String date) {
        return UV_KEY + date;
    }

    public static String redisIntervalUvKey(String startDate, String endDate) {
        return UV_KEY + startDate + ":" + endDate;
    }

    public static String redisDailyDauKey(String date) {
        return DAU_KEY + date;
    }

    public static String redisIntervalDauKey(String startDate, String endDate) {
        return DAU_KEY + startDate + ":" + endDate;
    }

}
