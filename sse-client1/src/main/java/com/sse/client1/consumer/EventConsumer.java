package com.sse.client1.consumer;

import com.sse.common.model.SSEEventData;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
@Log4j2
public class EventConsumer {

    @PostConstruct
    public void consumeEvent() {
        WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl("http://localhost:8080/sse/server")
                .build()
                .get()
                .uri("/sse/event")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ABCD")
                .retrieve()
                .bodyToFlux(SSEEventData.class)
                .doOnSubscribe(subscription -> log.info("Event subscribed"))
                .retryWhen(Retry.backoff(5, Duration.of(10, ChronoUnit.SECONDS))
                        .doBeforeRetry(retrySignal -> log.warn("Retrying connection attempt {}", retrySignal.totalRetries())))
                .subscribe(sseEventData -> log.info("Event data received with payload {}", sseEventData),
                        throwable -> log.error("Error occurred {}", throwable.getMessage()),
                        () -> log.info("Subscription event completed"));
    }
}
