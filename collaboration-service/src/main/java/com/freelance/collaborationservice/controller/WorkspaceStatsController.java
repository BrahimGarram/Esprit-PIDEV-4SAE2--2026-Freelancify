package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.WorkspaceStatsDTO;
import com.freelance.collaborationservice.service.WorkspaceStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WorkspaceStatsDTO> getWorkspaceStats(@PathVariable Long collaborationId) {
        log.info("REST request to get workspace stats for collaboration: {}", collaborationId);
        WorkspaceStatsDTO stats = statsService.getWorkspaceStats(collaborationId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/collaboration/{collaborationId}/team-workload")
    public ResponseEntity<Map<Long, Integer>> getTeamWorkload(@PathVariable Long collaborationId) {
        log.info("REST request to get team workload for collaboration: {}", collaborationId);
        Map<Long, Integer> workload = statsService.getTeamWorkload(collaborationId);
        return ResponseEntity.ok(workload);
    }

    @GetMapping("/collaboration/{collaborationId}/freelancer/{freelancerId}")
    public ResponseEntity<Map<String, Object>> getFreelancerStats(
            @PathVariable Long collaborationId,
            @PathVariable Long freelancerId) {
        log.info("REST request to get freelancer stats for collaboration {} and freelancer {}", 
                collaborationId, freelancerId);
        Map<String, Object> stats = statsService.getFreelancerStats(collaborationId, freelancerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/collaboration/{collaborationId}/milestone-progress")
    public ResponseEntity<Map<String, Object>> getMilestoneProgress(@PathVariable Long collaborationId) {
        log.info("REST request to get milestone progress for collaboration: {}", collaborationId);
        Map<String, Object> progress = statsService.getMilestoneProgress(collaborationId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/sprint/{sprintId}/burndown")
    public ResponseEntity<Map<String, Object>> getSprintBurndown(@PathVariable Long sprintId) {
        log.info("REST request to get sprint burndown for sprint: {}", sprintId);
        Map<String, Object> burndown = statsService.getSprintBurndown(sprintId);
        return ResponseEntity.ok(burndown);
    }
}
