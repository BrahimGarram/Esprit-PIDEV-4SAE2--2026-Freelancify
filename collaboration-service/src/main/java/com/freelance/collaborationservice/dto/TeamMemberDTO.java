package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDTO {
    private Long id;
    private Long collaborationId;
    private Long freelancerId;
    private String freelancerName;
    private String freelancerEmail;
    private ProjectRole role;
    private Boolean isActive;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Integer assignedTasksCount;
    private Integer completedTasksCount;
}
