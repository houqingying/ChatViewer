package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单表
 * @author ChatViewer
 */
@Data
@TableName(value = "blog_order")
public class Order {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    Long orderId;

    Long userId;

    Long commodityId;

    /**
     * 付款方式，0-支付宝，1-微信，2-无需支付
     */
    Integer payType;

    /**
     * 0-未支付，1-已支付，2-已取消
     */
    Integer status;

    /**
     * 订单创建时间
     */
    LocalDateTime createTime;

    /**
     * 订单支付时间
     */
    LocalDateTime payTime;
}