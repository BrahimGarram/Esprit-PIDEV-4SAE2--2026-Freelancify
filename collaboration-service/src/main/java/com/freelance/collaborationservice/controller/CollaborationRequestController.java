package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CollaborationRequestDTO;
import com.freelance.collaborationservice.dto.CreateCollaborationRequestDTO;
import com.freelance.collaborationservice.dto.UpdateCollaborationRequestStatusDTO;
import com.freelance.collaborationservice.dto.UpdateProposalDTO;
import com.freelance.collaborationservice.service.CollaborationRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaboration-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CollaborationRequestController {

    private final CollaborationRequestService collaborationRequestService;

    /**
     * Create a collaboration request (apply to a collaboration)
     * Only FREELANCER and ADMIN can apply
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<CollaborationRequestDTO> create(@Valid @RequestBody CreateCollaborationRequestDTO dto) {
        CollaborationRequestDTO created = collaborationRequestService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get collaboration request by ID
     * All authenticated users can view
     */
    @GetMapping("/{id}")
    public ResponseEntity<CollaborationRequestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collaborationRequestService.getById(id));
    }

    /**
     * Get requests for a specific collaboration
     * ENTERPRISE (owner) and ADMIN can view all requests for their collaborations
     */
    @GetMapping("/collaboration/{collaborationId}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<CollaborationRequestDTO>> getByCollaborationId(@PathVariable Long collaborationId) {
        return ResponseEntity.ok(collaborationRequestService.getByCollaborationId(collaborationId));
    }

    /**
     * Get requests by freelancer
     * FREELANCER can view their own requests, ADMIN can view all
     */
    @GetMapping("/freelancer/{freelancerId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<List<CollaborationRequestDTO>> getByFreelancerId(@PathVariable Long freelancerId) {
        return ResponseEntity.ok(collaborationRequestService.getByFreelancerId(freelancerId));
    }

    /**
     * Get all collaboration requests (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CollaborationRequestDTO>> getAll(
            @RequestParam(required = false) Long collaborationId,
            @RequestParam(required = false) Long freelancerId) {
        if (collaborationId != null) {
            return ResponseEntity.ok(collaborationRequestService.getByCollaborationId(collaborationId));
        }
        if (freelancerId != null) {
            return ResponseEntity.ok(collaborationRequestService.getByFreelancerId(freelancerId));
        }
        return ResponseEntity.ok(collaborationRequestService.getAll());
    }

    /**
     * Update request status (accept/reject)
     * Only ENTERPRISE (owner) and ADMIN can update status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<CollaborationRequestDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Long companyId,
            @Valid @RequestBody UpdateCollaborationRequestStatusDTO dto) {
        return ResponseEntity.ok(collaborationRequestService.updateStatus(id, dto, companyId));
    }

    /**
     * Withdraw a request
     * Only FREELANCER (owner) and ADMIN can withdraw
     */
    @DeleteMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<Void> withdraw(@PathVariable Long id, @RequestParam Long freelancerId) {
        collaborationRequestService.withdraw(id, freelancerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update proposal
     * Only FREELANCER (owner) and ADMIN can update their proposal
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<CollaborationRequestDTO> updateProposal(
            @PathVariable Long id,
            @RequestParam Long freelancerId,
            @Valid @RequestBody UpdateProposalDTO body) {
        return ResponseEntity.ok(collaborationRequestService.updateProposal(id, body.getProposalMessage(), body.getProposedPrice(), freelancerId));
    }
}
