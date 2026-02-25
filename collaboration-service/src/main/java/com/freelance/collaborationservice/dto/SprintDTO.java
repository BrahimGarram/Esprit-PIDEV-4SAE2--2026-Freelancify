package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.SprintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintDTO {
    private Long id;
    private Long collaborationId;
    private String name;
    private String goal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationWeeks;
    private SprintStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer progressPercentage;
    private Integer totalEstimatedHours;
    private Integer totalActualHours;
}
