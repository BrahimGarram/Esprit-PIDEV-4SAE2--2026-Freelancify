package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.TaskPriority;
import com.freelance.collaborationservice.model.TaskStatus;
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
public class TaskDTO {
    private Long id;
    private Long collaborationId;
    private String title;
    private String description;
    private Long assignedFreelancerId;
    private String assignedFreelancerName;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime deadline;
    private Integer estimatedHours;
    private Integer actualHours;
    private String attachments;
    private Long milestoneId;
    private String milestoneName;
    private Long parentTaskId;
    private Long sprintId;
    private String sprintName;
    private List<Long> dependsOnTaskIds;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Integer commentCount;
    private List<TaskDTO> subtasks;
}
