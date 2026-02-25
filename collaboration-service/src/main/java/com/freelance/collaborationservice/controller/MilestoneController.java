package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CreateMilestoneRequest;
import com.freelance.collaborationservice.dto.MilestoneDTO;
import com.freelance.collaborationservice.model.MilestoneStatus;
import com.freelance.collaborationservice.service.MilestoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/milestones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<MilestoneDTO> createMilestone(@Valid @RequestBody CreateMilestoneRequest request) {
        log.info("REST request to create milestone: {}", request.getTitle());
        MilestoneDTO milestone = milestoneService.createMilestone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(milestone);
    }

    @PutMapping("/{milestoneId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<MilestoneDTO> updateMilestone(
            @PathVariable Long milestoneId,
            @Valid @RequestBody CreateMilestoneRequest request) {
        log.info("REST request to update milestone: {}", milestoneId);
        MilestoneDTO milestone = milestoneService.updateMilestone(milestoneId, request);
        return ResponseEntity.ok(milestone);
    }

    @PatchMapping("/{milestoneId}/status")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<MilestoneDTO> updateMilestoneStatus(
            @PathVariable Long milestoneId,
            @RequestParam MilestoneStatus status) {
        log.info("REST request to update milestone {} status to {}", milestoneId, status);
        MilestoneDTO milestone = milestoneService.updateMilestoneStatus(milestoneId, status);
        return ResponseEntity.ok(milestone);
    }

    @PostMapping("/{milestoneId}/auto-update-status")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Void> autoUpdateMilestoneStatus(@PathVariable Long milestoneId) {
        log.info("REST request to auto-update milestone status: {}", milestoneId);
        milestoneService.autoUpdateMilestoneStatus(milestoneId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{milestoneId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long milestoneId) {
        log.info("REST request to delete milestone: {}", milestoneId);
        milestoneService.deleteMilestone(milestoneId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{milestoneId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<MilestoneDTO> getMilestone(@PathVariable Long milestoneId) {
        log.info("REST request to get milestone: {}", milestoneId);
        MilestoneDTO milestone = milestoneService.getMilestoneById(milestoneId);
        return ResponseEntity.ok(milestone);
    }

    @GetMapping("/collaboration/{collaborationId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByCollaboration(@PathVariable Long collaborationId) {
        log.info("REST request to get milestones for collaboration: {}", collaborationId);
        List<MilestoneDTO> milestones = milestoneService.getMilestonesByCollaboration(collaborationId);
        return ResponseEntity.ok(milestones);
    }

    @GetMapping("/collaboration/{collaborationId}/status/{status}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByStatus(
            @PathVariable Long collaborationId,
            @PathVariable MilestoneStatus status) {
        log.info("REST request to get milestones by status {} for collaboration: {}", status, collaborationId);
        List<MilestoneDTO> milestones = milestoneService.getMilestonesByStatus(collaborationId, status);
        return ResponseEntity.ok(milestones);
    }
}
