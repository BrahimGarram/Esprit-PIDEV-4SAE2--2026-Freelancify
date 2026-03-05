package com.freelance.collaborationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceStatsDTO {
    private Long collaborationId;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer overdueTasks;
    private Integer progressPercentage;
    private Integer totalTeamMembers;
    private Integer totalMilestones;
    private Integer completedMilestones;
    private Integer totalSprints;
    private Integer activeSprints;
    private Map<String, Integer> tasksByStatus;
    private Map<String, Integer> tasksByPriority;
    private Integer totalEstimatedHours;
    private Integer totalActualHours;
    private Double burnRate;
}
