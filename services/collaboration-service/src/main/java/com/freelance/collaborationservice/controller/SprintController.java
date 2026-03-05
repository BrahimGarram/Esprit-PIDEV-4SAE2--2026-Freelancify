package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CreateSprintRequest;
import com.freelance.collaborationservice.dto.SprintDTO;
import com.freelance.collaborationservice.model.SprintStatus;
import com.freelance.collaborationservice.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<SprintDTO> createSprint(@Valid @RequestBody CreateSprintRequest request) {
        log.info("REST request to create sprint: {}", request.getName());
        SprintDTO sprint = sprintService.createSprint(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sprint);
    }

    @PutMapping("/{sprintId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<SprintDTO> updateSprint(
            @PathVariable Long sprintId,
            @Valid @RequestBody CreateSprintRequest request) {
        log.info("REST request to update sprint: {}", sprintId);
        SprintDTO sprint = sprintService.updateSprint(sprintId, request);
        return ResponseEntity.ok(sprint);
    }

    @PatchMapping("/{sprintId}/status")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<SprintDTO> updateSprintStatus(
            @PathVariable Long sprintId,
            @RequestParam SprintStatus status) {
        log.info("REST request to update sprint {} status to {}", sprintId, status);
        SprintDTO sprint = sprintService.updateSprintStatus(sprintId, status);
        return ResponseEntity.ok(sprint);
    }

    @PostMapping("/{sprintId}/start")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<SprintDTO> startSprint(@PathVariable Long sprintId) {
        log.info("REST request to start sprint: {}", sprintId);
        SprintDTO sprint = sprintService.startSprint(sprintId);
        return ResponseEntity.ok(sprint);
    }

    @PostMapping("/{sprintId}/complete")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<SprintDTO> completeSprint(@PathVariable Long sprintId) {
        log.info("REST request to complete sprint: {}", sprintId);
        SprintDTO sprint = sprintService.completeSprint(sprintId);
        return ResponseEntity.ok(sprint);
    }

    @DeleteMapping("/{sprintId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long sprintId) {
        log.info("REST request to delete sprint: {}", sprintId);
        sprintService.deleteSprint(sprintId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sprintId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<SprintDTO> getSprint(@PathVariable Long sprintId) {
        log.info("REST request to get sprint: {}", sprintId);
        SprintDTO sprint = sprintService.getSprintById(sprintId);
        return ResponseEntity.ok(sprint);
    }

    @GetMapping("/collaboration/{collaborationId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<List<SprintDTO>> getSprintsByCollaboration(@PathVariable Long collaborationId) {
        log.info("REST request to get sprints for collaboration: {}", collaborationId);
        List<SprintDTO> sprints = sprintService.getSprintsByCollaboration(collaborationId);
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/collaboration/{collaborationId}/active")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<SprintDTO> getActiveSprint(@PathVariable Long collaborationId) {
        log.info("REST request to get active sprint for collaboration: {}", collaborationId);
        Optional<SprintDTO> sprint = sprintService.getActiveSprint(collaborationId);
        return sprint.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/collaboration/{collaborationId}/status/{status}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<List<SprintDTO>> getSprintsByStatus(
            @PathVariable Long collaborationId,
            @PathVariable SprintStatus status) {
        log.info("REST request to get sprints by status {} for collaboration: {}", status, collaborationId);
        List<SprintDTO> sprints = sprintService.getSprintsByStatus(collaborationId, status);
        return ResponseEntity.ok(sprints);
    }
}
