package com.sse.server.controller;

import com.sse.common.model.EventType;
import com.sse.common.model.SSEEventData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@Log4j2
@AllArgsConstructor
public class SSEController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/send")
//    @SendTo("/topic/event")
    public String broadcastMessage(String message) {
        log.info("Message received from client {}", message);
        return message;
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void emitEvent() {
        simpMessagingTemplate.convertAndSend("/topic/event", SSEEventData.builder().event(EventType.ALERT).generatedAt(LocalDateTime.now().toString()).uuid(UUID.randomUUID().toString()).build());
        log.info("Event emitted");
    }
}
