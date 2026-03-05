package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.MilestoneStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneDTO {
    private Long id;
    private Long collaborationId;
    private String title;
    private String description;
    private Integer orderIndex;
    private LocalDateTime dueDate;
    private BigDecimal paymentAmount;
    private MilestoneStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer progressPercentage;
}
