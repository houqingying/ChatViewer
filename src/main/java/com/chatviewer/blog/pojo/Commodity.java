package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购买网站的token商品，分为限量的免费商品和不限量的付费商品
 * @author ChatViewer
 */
@Data
@TableName(value = "blog_commodity")
public class Commodity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    Long commodityId;

    /**
     * 商品名称
     */
    String commodityName;

    /**
     * 使用规则
     */
    String commodityRules;

    /**
     * 售卖价格
     */
    Integer commodityPrice;

    /**
     * 包含token数
     */
    Integer tokens;

    /**
     * 商品类型，不限量-0，限量-1
     */
    Integer commodityType;

    /**
     * 限量库存
     */
    Integer stock;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    LocalDateTime createTime;
}