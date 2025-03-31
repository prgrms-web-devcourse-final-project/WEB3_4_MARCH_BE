package com.backend.global.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String CHAT_TOPIC = "chat-messages";

    @Bean
    public NewTopic chatTopic() {
        return TopicBuilder
                .name(CHAT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
