package com.chatviewer.blog.event;

import cn.hutool.json.JSONUtil;
import com.chatviewer.blog.pojo.Commodity;
import com.chatviewer.blog.pojo.Event;
import com.chatviewer.blog.pojo.Order;
import com.chatviewer.blog.service.CommodityService;
import com.chatviewer.blog.service.OrderService;
import com.chatviewer.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * RabbitMQ的监听器
 * @author ChatViewer
 */
@Slf4j
@Component
public class SpringRabbitListener {

    private static final int PAY_TYPE_ALIPAY = 0;
    private static final int PAY_TYPE_WECHAT = 1;
    private static final int PAY_TYPE_FREE = 2;

    private static final int PAY_STATUS_NO = 0;
    private static final int PAY_STATUS_ALREADY = 1;
    private static final int PAY_STATUS_CANCELED = 2;

    @Resource
    OrderService orderService;

    @Resource
    CommodityService commodityService;

    @Resource
    UserService userService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "seckill.fanout.order-queue"),
            exchange = @Exchange(name = "char-viewer.seckill.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void orderQueueListener(String msg) {
        // 方法上标注RabbitListener注解，表示监听器
        // 生成并保存订单
        // JsonStr转为Bean，得到Event类对象
        Event event = JSONUtil.toBean(msg, Event.class);
        Order order = new Order();
        order.setUserId(event.getSenderId());
        order.setCommodityId(event.getEntityId());
        order.setPayType(PAY_TYPE_FREE);
        order.setStatus(PAY_STATUS_ALREADY);
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now());
        orderService.save(order);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "seckill.fanout.stock-queue"),
            exchange = @Exchange(name = "char-viewer.seckill.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void stockQueueListener(String msg) {
        // 使用乐观锁更新MySQL中的库存
        Event event = JSONUtil.toBean(msg, Event.class);
        Long commodityId = event.getEntityId();
        boolean success = commodityService.update().setSql("stock = stock - 1")
                .eq("commodity_id", commodityId).gt("stock", 0).update();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "seckill.fanout.user-queue"),
            exchange = @Exchange(name = "char-viewer.seckill.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void userQueueListener(String msg) {
        // 根据商品售卖的token数，更改用户的token余额
        Event event = JSONUtil.toBean(msg, Event.class);
        Long userId = event.getSenderId();
        Long commodityId = event.getEntityId();
        Commodity commodity = commodityService.getById(commodityId);
        boolean success = userService.update().setSql("user_token_count = user_token_count + " + commodity.getTokens())
                .eq("user_id", userId).update();
    }
}
