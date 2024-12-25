package com.sse.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SSEEventData {
    private String uuid;
    private EventType event;
    private String generatedAt;
}
