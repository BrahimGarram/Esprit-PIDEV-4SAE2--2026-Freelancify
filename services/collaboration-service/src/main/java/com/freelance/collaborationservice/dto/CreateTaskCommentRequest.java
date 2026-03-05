package com.freelance.collaborationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskCommentRequest {
    
    @NotNull(message = "Task ID is required")
    private Long taskId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private List<Long> mentionedUserIds;
    
    private String attachments;
}
