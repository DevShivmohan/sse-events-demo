package com.sse.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sse.common.model.EventType;
import com.sse.common.model.SSEEventData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.sse.common.model.ObjectMapperUtil.objectMapper;

@RestController
@RequestMapping("/sse")
@Log4j2
@AllArgsConstructor
public class SSEController {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 10 * 1000)
    public void emitEvent() throws JsonProcessingException {
        final var eventPayload = SSEEventData.builder()
                .event(EventType.ALERT)
                .uuid(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now().toString())
                .build();
        kafkaTemplate.send("sse-event", objectMapper.writeValueAsString(eventPayload))
                .whenComplete((stringStringSendResult, throwable) -> {
                    if (Objects.isNull(throwable)) {
                        log.info("SSE Event generated and sent {}", stringStringSendResult.getProducerRecord().value());
                    } else {
                        log.error("Error occurred during event sending {}", throwable.getMessage());
                    }
                });
    }
}
