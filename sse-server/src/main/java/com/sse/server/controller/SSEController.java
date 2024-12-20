package com.sse.server.controller;

import com.sse.common.model.EventType;
import com.sse.common.model.SSEEventData;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/sse")
@Log4j2
public class SSEController {
    private final Sinks.Many<SSEEventData> sseEventSink = Sinks.many().replay().limit(1);

    @GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SSEEventData> streamEvents() {
        return sseEventSink.asFlux();
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void emitEvent() {
        if (sseEventSink.tryEmitNext(SSEEventData.builder().event(EventType.ALERT).generatedAt(LocalDateTime.now()).uuid(UUID.randomUUID().toString()).build()) == Sinks.EmitResult.OK) {
            log.info("Event emitted");
        } else {
            log.error("Event emitting failed");
        }
    }
}
