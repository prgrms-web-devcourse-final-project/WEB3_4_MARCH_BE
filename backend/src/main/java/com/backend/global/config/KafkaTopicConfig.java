package com.backend.global.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka 토픽 설정 클래스입니다.
 * - 채팅 메시지를 처리할 Kafka 토픽을 미리 생성합니다.
 */
@Configuration
public class KafkaTopicConfig {

    // Kafka에서 사용할 채팅 토픽 이름 상수
    public static final String CHAT_TOPIC = "chat-messages";

    /**
     * Kafka 토픽을 생성하는 빈입니다.
     * - 이름: chat-messages
     * - 파티션: 1개
     * - 복제 수: 1 (개발/테스트 환경 기준)
     */
    @Bean
    public NewTopic chatTopic() {
        return TopicBuilder
                .name(CHAT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
