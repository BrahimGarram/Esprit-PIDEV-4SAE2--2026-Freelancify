package com.freelance.collaborationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeLogRequest {
    
    @NotNull(message = "Task ID is required")
    private Long taskId;
    
    @NotNull(message = "Freelancer ID is required")
    private Long freelancerId;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private String description;
}
