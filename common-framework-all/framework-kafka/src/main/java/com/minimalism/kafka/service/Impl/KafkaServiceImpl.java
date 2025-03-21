package com.minimalism.kafka.service.Impl;

import com.minimalism.kafka.config.KafkaConfig;
import com.minimalism.kafka.listener.callback.KafkaListenableFutureCallback;
import com.minimalism.kafka.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

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
        future.addCallback(new KafkaListenableFutureCallback());
    }
}
