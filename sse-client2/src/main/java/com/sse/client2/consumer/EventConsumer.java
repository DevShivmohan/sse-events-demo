package com.sse.client2.consumer;

import com.sse.common.model.SSEEventData;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
public class EventConsumer {

    private static final String URL = "ws://localhost:8080/sse/server/sse/websocket"; // Server WebSocket endpoint

    @PostConstruct
    public void connect() throws ExecutionException, InterruptedException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        final List<MessageConverter> converters = new ArrayList<>();
        converters.add(new StringMessageConverter());
        converters.add(new MappingJackson2MessageConverter());
        stompClient.setMessageConverter(new CompositeMessageConverter(converters));

        StompSession session = stompClient.connectAsync(URL, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);
                log.info("Client connected id {}",session.getSessionId());
            }
        }).get();


        session.subscribe("/topic/event", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return SSEEventData.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("SSE event received {}", payload);
            }
        });

        session.send("/app/send", "Hello server");
//        session.send("/app/send", SSEEventData.builder().event(EventType.ACK).generatedAt(LocalDateTime.now().toString()).uuid(UUID.randomUUID().toString()).build());
    }
}
