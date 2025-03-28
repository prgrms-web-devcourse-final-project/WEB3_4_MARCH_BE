package com.backend.domain.chat.service.kafka;

import com.backend.domain.chat.dto.request.ChatMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaChatProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(ChatMessageRequest message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            // CompletableFuture 방식 사용
            kafkaTemplate.send("chat-messages", message.getChatroomId().toString(), jsonMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Kafka 전송 성공: {}", jsonMessage);
                        } else {
                            log.error("Kafka 전송 실패", ex);
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패: {}", message, e);
        }
    }
}
