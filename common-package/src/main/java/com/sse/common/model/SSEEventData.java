package com.sse.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SSEEventData {
    private String uuid;
    private EventType event;
    private LocalDateTime generatedAt;
}
