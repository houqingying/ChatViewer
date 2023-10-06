package com.chatviewer.blog.event;

import cn.hutool.json.JSONUtil;
import com.chatviewer.blog.pojo.Event;
import com.chatviewer.blog.pojo.Notice;
import com.chatviewer.blog.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

import static com.chatviewer.blog.constant.KafkaConstant.*;

/**
 * 用于接收Event事件，作为Kafka的消费者
 * @author ChatViewer
 */
@Slf4j
@Component
public class EventConsumer{

    @Resource
    NoticeService noticeService;

    @KafkaListener(topics = {TOPIC_LIKE, TOPIC_COMMENT})
    public void consumeLikeEvent(ConsumerRecord<String, String> record) {

        // 查看传过来的record是否为空
        if (record == null || record.value() == null) {
            log.error("message is empty!");
            return;
        }

        // JsonStr转为Bean，得到Event类对象
        Event event = JSONUtil.toBean(record.value(), Event.class);
        String topic = event.getTopic();

        // 读取event携带数据，生成并保存Notice
        Notice notice;
        if (TOPIC_LIKE.equals(topic)) {
            Long userId = event.getSenderId();
            Integer entityType = event.getEntityType();
            Long entityId = event.getEntityId();
            notice = noticeService.genLikeNotice(userId, entityType, entityId);
        }
        else {
            notice = noticeService.genCommentNotice(event.getComment());
        }

        if (!notice.getSenderId().equals(notice.getReceiverId())) {
            noticeService.save(notice);
        }
    }

}
