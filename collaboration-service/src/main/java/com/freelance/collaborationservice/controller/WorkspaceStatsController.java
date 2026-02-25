package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.WorkspaceStatsDTO;
import com.freelance.collaborationservice.service.WorkspaceStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workspace-stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class WorkspaceStatsController {

    private final WorkspaceStatsService statsService;

    @GetMapping("/collaboration/{collaborationId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<WorkspaceStatsDTO> getWorkspaceStats(@PathVariable Long collaborationId) {
        log.info("REST request to get workspace stats for collaboration: {}", collaborationId);
        try {
            WorkspaceStatsDTO stats = statsService.getWorkspaceStats(collaborationId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting workspace stats for collaboration {}: {}", collaborationId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/collaboration/{collaborationId}/team-workload")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Map<Long, Integer>> getTeamWorkload(@PathVariable Long collaborationId) {
        log.info("REST request to get team workload for collaboration: {}", collaborationId);
        try {
            Map<Long, Integer> workload = statsService.getTeamWorkload(collaborationId);
            return ResponseEntity.ok(workload);
        } catch (Exception e) {
            log.error("Error getting team workload for collaboration {}: {}", collaborationId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/collaboration/{collaborationId}/freelancer/{freelancerId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getFreelancerStats(
            @PathVariable Long collaborationId,
            @PathVariable Long freelancerId) {
        log.info("REST request to get freelancer stats for collaboration {} and freelancer {}", 
                collaborationId, freelancerId);
        try {
            Map<String, Object> stats = statsService.getFreelancerStats(collaborationId, freelancerId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting freelancer stats for collaboration {} and freelancer {}: {}", 
                    collaborationId, freelancerId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/collaboration/{collaborationId}/milestone-progress")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getMilestoneProgress(@PathVariable Long collaborationId) {
        log.info("REST request to get milestone progress for collaboration: {}", collaborationId);
        try {
            Map<String, Object> progress = statsService.getMilestoneProgress(collaborationId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            log.error("Error getting milestone progress for collaboration {}: {}", collaborationId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/sprint/{sprintId}/burndown")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getSprintBurndown(@PathVariable Long sprintId) {
        log.info("REST request to get sprint burndown for sprint: {}", sprintId);
        try {
            Map<String, Object> burndown = statsService.getSprintBurndown(sprintId);
            return ResponseEntity.ok(burndown);
        } catch (Exception e) {
            log.error("Error getting sprint burndown for sprint {}: {}", sprintId, e.getMessage(), e);
            throw e;
        }
    }
}
