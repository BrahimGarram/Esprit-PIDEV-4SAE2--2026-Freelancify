package com.freelance.collaborationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMilestoneRequest {
    
    @NotNull(message = "Collaboration ID is required")
    private Long collaborationId;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    private String description;
    
    private Integer orderIndex = 0;
    
    private LocalDateTime dueDate;
    
    private BigDecimal paymentAmount;
}
