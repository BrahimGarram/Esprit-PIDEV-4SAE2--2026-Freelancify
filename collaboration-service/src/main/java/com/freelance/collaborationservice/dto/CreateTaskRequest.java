package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.TaskPriority;
import com.freelance.collaborationservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    
    @NotNull(message = "Collaboration ID is required")
    private Long collaborationId;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 300, message = "Title must be between 3 and 300 characters")
    private String title;
    
    private String description;
    
    @NotNull(message = "Assigned freelancer ID is required")
    private Long assignedFreelancerId;
    
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    private TaskStatus status = TaskStatus.TODO;
    
    private LocalDateTime deadline;
    
    private Integer estimatedHours = 0;
    
    private String attachments;
    
    private Long milestoneId;
    
    private Long parentTaskId;
    
    private Long sprintId;
    
    private List<Long> dependsOnTaskIds;
    
    private Integer orderIndex = 0;
}
