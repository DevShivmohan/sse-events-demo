package com.sse.client1.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sse.common.model.SSEEventData;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static com.sse.common.model.ObjectMapperUtil.objectMapper;

@Service
@Log4j2
public class EventConsumer {

    @KafkaListener(topics = "sse-event", groupId = "consumer-group1")
    public void consumeSensorData(@Payload String sseEventData,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) throws JsonProcessingException {
        log.info("Event received as payload {}", objectMapper.readValue(sseEventData, SSEEventData.class));
    }
}
