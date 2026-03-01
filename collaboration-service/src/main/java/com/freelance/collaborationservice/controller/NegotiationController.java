package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.AgreeToTermsRequest;
import com.freelance.collaborationservice.dto.CounterOfferRequest;
import com.freelance.collaborationservice.dto.SendNegotiationMessageRequest;
import com.freelance.collaborationservice.model.CollaborationRequest;
import com.freelance.collaborationservice.model.NegotiationMessage;
import com.freelance.collaborationservice.model.ProposalRevision;
import com.freelance.collaborationservice.service.NegotiationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NegotiationController
 * 
 * REST API for managing proposal negotiations between freelancers and companies.
 * Supports messaging, counter-offers, and agreement to terms.
 */
@RestController
@RequestMapping("/api/negotiations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NegotiationController {
    
    private final NegotiationService negotiationService;
    
    /**
     * Send a negotiation message
     * 
     * POST /api/negotiations/messages
     */
    @PostMapping("/messages")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<NegotiationMessage> sendMessage(@RequestBody SendNegotiationMessageRequest request) {
        log.info("Sending negotiation message for request {}", request.getCollaborationRequestId());
        NegotiationMessage message = negotiationService.sendMessage(request);
        return ResponseEntity.ok(message);
    }
    
    /**
     * Get all messages for a collaboration request
     * 
     * GET /api/negotiations/{collaborationRequestId}/messages
     */
    @GetMapping("/{collaborationRequestId}/messages")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<List<NegotiationMessage>> getMessages(@PathVariable Long collaborationRequestId) {
        log.info("Getting messages for collaboration request {}", collaborationRequestId);
        List<NegotiationMessage> messages = negotiationService.getMessages(collaborationRequestId);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Get unread message count
     * 
     * GET /api/negotiations/{collaborationRequestId}/unread-count
     */
    @GetMapping("/{collaborationRequestId}/unread-count")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long collaborationRequestId) {
        Long count = negotiationService.getUnreadCount(collaborationRequestId);
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark messages as read
     * 
     * POST /api/negotiations/{collaborationRequestId}/mark-read
     */
    @PostMapping("/{collaborationRequestId}/mark-read")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Long collaborationRequestId,
            @RequestParam Long userId) {
        log.info("Marking messages as read for user {} in request {}", userId, collaborationRequestId);
        negotiationService.markMessagesAsRead(collaborationRequestId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Messages marked as read");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Send a counter-offer
     * 
     * POST /api/negotiations/counter-offer
     */
    @PostMapping("/counter-offer")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<CollaborationRequest> sendCounterOffer(@RequestBody CounterOfferRequest request) {
        log.info("Sending counter-offer for request {}", request.getCollaborationRequestId());
        CollaborationRequest updated = negotiationService.sendCounterOffer(request);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Agree to negotiated terms
     * 
     * POST /api/negotiations/agree
     */
    @PostMapping("/agree")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<CollaborationRequest> agreeToTerms(@RequestBody AgreeToTermsRequest request) {
        log.info("User {} agreeing to terms for request {}", request.getUserId(), request.getCollaborationRequestId());
        CollaborationRequest updated = negotiationService.agreeToTerms(request);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Get revision history
     * 
     * GET /api/negotiations/{collaborationRequestId}/revisions
     */
    @GetMapping("/{collaborationRequestId}/revisions")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN', 'CLIENT')")
    public ResponseEntity<List<ProposalRevision>> getRevisionHistory(@PathVariable Long collaborationRequestId) {
        log.info("Getting revision history for request {}", collaborationRequestId);
        List<ProposalRevision> revisions = negotiationService.getRevisionHistory(collaborationRequestId);
        return ResponseEntity.ok(revisions);
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "negotiation-controller");
        return ResponseEntity.ok(response);
    }
}
