package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.TaskPriority;
import com.freelance.collaborationservice.model.TaskStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {
    
    @Size(min = 3, max = 300, message = "Title must be between 3 and 300 characters")
    private String title;
    
    private String description;
    
    private Long assignedFreelancerId;
    
    private TaskPriority priority;
    
    private TaskStatus status;
    
    private LocalDateTime deadline;
    
    private Integer estimatedHours;
    
    private Integer actualHours;
    
    private String attachments;
    
    private Long milestoneId;
    
    private Long sprintId;
    
    private List<Long> dependsOnTaskIds;
    
    private Integer orderIndex;
}
