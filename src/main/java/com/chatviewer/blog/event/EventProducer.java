package com.chatviewer.blog.event;

import com.alibaba.fastjson.JSONObject;
import com.chatviewer.blog.pojo.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用于发送Event事件，作为Kafka的生产者
 * @author ChatViewer
 */
@Component
public class EventProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMsg(Event event) {
        // 将消息发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
