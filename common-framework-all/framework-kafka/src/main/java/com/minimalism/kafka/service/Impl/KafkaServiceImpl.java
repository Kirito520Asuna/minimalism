package com.minimalism.kafka.service.Impl;

import com.minimalism.kafka.config.KafkaConfig;
import com.minimalism.kafka.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author yan
 * @Date 2025/3/14 23:41:38
 * @Description
 */
@Slf4j
@Service
@ConditionalOnBean(KafkaConfig.class)
public class KafkaServiceImpl implements KafkaService {
    @Override
    public void send(String topic, String json) {
        ListenableFuture future = getTemplate().send(topic, json);

        future.addCallback(
                new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        ProducerRecord<String, String> producerRecord = result.getProducerRecord();
                        RecordMetadata recordMetadata = result.getRecordMetadata();
                        Integer partition = producerRecord.partition();
                        String key = producerRecord.key();
                        String value = producerRecord.value();
                        long offset = recordMetadata.offset();
                        String topic = recordMetadata.topic();
                        // 处理成功发送后的逻辑
                        log.info("Kafka message sent successfully：");
                        log.info("Topic: " + topic);
                        log.info("Partition: " + partition);
                        log.info("Offset: " + offset);
                        log.info("Key: " + key);
                        log.info("Value: " + value);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error(ex.getMessage(), ex);
                    }

                }
        );
    }
}
