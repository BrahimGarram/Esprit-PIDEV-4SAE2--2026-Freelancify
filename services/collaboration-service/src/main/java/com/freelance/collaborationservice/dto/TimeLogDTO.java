package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.TimeLogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLogDTO {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long freelancerId;
    private String freelancerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String description;
    private TimeLogStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
