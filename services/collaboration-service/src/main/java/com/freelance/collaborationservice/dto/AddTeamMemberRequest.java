package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.ProjectRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTeamMemberRequest {
    
    @NotNull(message = "Collaboration ID is required")
    private Long collaborationId;
    
    @NotNull(message = "Freelancer ID is required")
    private Long freelancerId;
    
    @NotNull(message = "Role is required")
    private ProjectRole role;
}
