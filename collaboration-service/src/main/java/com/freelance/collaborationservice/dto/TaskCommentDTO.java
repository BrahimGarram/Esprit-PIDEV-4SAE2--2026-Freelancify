package com.freelance.collaborationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCommentDTO {
    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private String content;
    private List<Long> mentionedUserIds;
    private String attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
