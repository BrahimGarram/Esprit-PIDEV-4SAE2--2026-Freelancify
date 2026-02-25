package com.freelance.collaborationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type; // TASK_CREATED, TASK_UPDATED, TASK_MOVED, TASK_DELETED, COMMENT_ADDED, etc.
    private Long collaborationId;
    private Object payload;
    private LocalDateTime timestamp;

    public WebSocketMessage(String type, Long collaborationId, Object payload) {
        this.type = type;
        this.collaborationId = collaborationId;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }
}
