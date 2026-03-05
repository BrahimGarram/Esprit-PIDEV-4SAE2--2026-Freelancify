package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.AgreeToTermsRequest;
import com.freelance.collaborationservice.dto.CounterOfferRequest;
import com.freelance.collaborationservice.dto.SendNegotiationMessageRequest;
import com.freelance.collaborationservice.model.CollaborationRequest;
import com.freelance.collaborationservice.model.NegotiationMessage;
import com.freelance.collaborationservice.model.ProposalRevision;
import com.freelance.collaborationservice.repository.CollaborationRequestRepository;
import com.freelance.collaborationservice.repository.NegotiationMessageRepository;
import com.freelance.collaborationservice.repository.ProposalRevisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NegotiationService
 * 
 * Handles all negotiation-related operations including:
 * - Sending messages between freelancers and companies
 * - Counter-offers and revisions
 * - Agreement to terms
 * - Negotiation history tracking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NegotiationService {
    
    private final CollaborationRequestRepository collaborationRequestRepository;
    private final NegotiationMessageRepository negotiationMessageRepository;
    private final ProposalRevisionRepository proposalRevisionRepository;
    
    /**
     * Send a negotiation message
     */
    @Transactional
    public NegotiationMessage sendMessage(SendNegotiationMessageRequest request) {
        log.info("Sending negotiation message for request {}", request.getCollaborationRequestId());
        
        // Verify collaboration request exists
        CollaborationRequest collabRequest = collaborationRequestRepository
                .findById(request.getCollaborationRequestId())
                .orElseThrow(() -> new RuntimeException("Collaboration request not found"));
        
        // Create message
        NegotiationMessage message = new NegotiationMessage();
        message.setCollaborationRequestId(request.getCollaborationRequestId());
        message.setSenderId(request.getSenderId());
        message.setSenderType(request.getSenderType());
        message.setMessageType(request.getMessageType());
        message.setMessage(request.getMessage());
        message.setMetadata(request.getMetadata());
        message.setIsRead(false);
        
        // Update negotiation status if this is the first message
        if (collabRequest.getNegotiationStatus() == CollaborationRequest.NegotiationStatus.INITIAL) {
            collabRequest.setNegotiationStatus(CollaborationRequest.NegotiationStatus.NEGOTIATING);
            collaborationRequestRepository.save(collabRequest);
        }
        
        return negotiationMessageRepository.save(message);
    }
    
    /**
     * Send a counter-offer
     */
    @Transactional
    public CollaborationRequest sendCounterOffer(CounterOfferRequest request) {
        log.info("Sending counter-offer for request {}", request.getCollaborationRequestId());
        
        CollaborationRequest collabRequest = collaborationRequestRepository
                .findById(request.getCollaborationRequestId())
                .orElseThrow(() -> new RuntimeException("Collaboration request not found"));
        
        // Create revision record
        ProposalRevision revision = new ProposalRevision();
        revision.setCollaborationRequestId(request.getCollaborationRequestId());
        revision.setRevisionNumber(collabRequest.getCounterOfferCount() + 1);
        revision.setRevisedBy(request.getCounterOfferedBy());
        revision.setRevisedByType("COMPANY".equals(request.getCounterOfferedByType()) ? 
                ProposalRevision.ReviserType.COMPANY : ProposalRevision.ReviserType.FREELANCER);
        
        // Track what changed
        revision.setPreviousPrice(collabRequest.getCounterOfferPrice() != null ? 
                collabRequest.getCounterOfferPrice() : collabRequest.getProposedPrice());
        revision.setNewPrice(request.getCounterOfferPrice());
        revision.setPreviousTimeline(collabRequest.getCounterOfferTimeline());
        revision.setNewTimeline(request.getCounterOfferTimeline());
        revision.setRevisionMessage(request.getCounterOfferMessage());
        
        // Convert milestone proposals to JSON format
        if (request.getProposedMilestones() != null) {
            List<Map<String, Object>> milestones = request.getProposedMilestones().stream()
                    .map(m -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", m.getName());
                        map.put("percentage", m.getPercentage());
                        map.put("deliverables", m.getDeliverables());
                        map.put("duration", m.getDuration());
                        return map;
                    })
                    .collect(Collectors.toList());
            revision.setNewMilestones(milestones);
            collabRequest.setProposedMilestones(milestones);
        }
        
        proposalRevisionRepository.save(revision);
        
        // Update collaboration request with counter-offer
        collabRequest.setCounterOfferPrice(request.getCounterOfferPrice());
        collabRequest.setCounterOfferTimeline(request.getCounterOfferTimeline());
        collabRequest.setCounterOfferMessage(request.getCounterOfferMessage());
        collabRequest.setCounterOfferedBy(request.getCounterOfferedBy());
        collabRequest.setCounterOfferedAt(LocalDateTime.now());
        collabRequest.setCounterOfferCount(collabRequest.getCounterOfferCount() + 1);
        collabRequest.setNegotiationStatus(CollaborationRequest.NegotiationStatus.COUNTER_OFFERED);
        
        // Reset agreement flags when new counter-offer is made
        collabRequest.setFreelancerAgreed(false);
        collabRequest.setCompanyAgreed(false);
        collabRequest.setFreelancerAgreedAt(null);
        collabRequest.setCompanyAgreedAt(null);
        
        CollaborationRequest saved = collaborationRequestRepository.save(collabRequest);
        
        // Create a system message for the counter-offer
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("counterOfferPrice", request.getCounterOfferPrice());
        metadata.put("counterOfferTimeline", request.getCounterOfferTimeline());
        metadata.put("revisionNumber", revision.getRevisionNumber());
        
        NegotiationMessage systemMessage = new NegotiationMessage();
        systemMessage.setCollaborationRequestId(request.getCollaborationRequestId());
        systemMessage.setSenderId(request.getCounterOfferedBy());
        systemMessage.setSenderType("COMPANY".equals(request.getCounterOfferedByType()) ? 
                NegotiationMessage.SenderType.COMPANY : NegotiationMessage.SenderType.FREELANCER);
        systemMessage.setMessageType(NegotiationMessage.MessageType.COUNTER_OFFER);
        systemMessage.setMessage(request.getCounterOfferMessage());
        systemMessage.setMetadata(metadata);
        systemMessage.setIsRead(false);
        
        negotiationMessageRepository.save(systemMessage);
        
        log.info("Counter-offer sent successfully. Revision #{}", revision.getRevisionNumber());
        return saved;
    }
    
    /**
     * Agree to negotiated terms
     */
    @Transactional
    public CollaborationRequest agreeToTerms(AgreeToTermsRequest request) {
        log.info("User {} ({}) agreeing to terms for request {}", 
                request.getUserId(), request.getUserType(), request.getCollaborationRequestId());
        
        CollaborationRequest collabRequest = collaborationRequestRepository
                .findById(request.getCollaborationRequestId())
                .orElseThrow(() -> new RuntimeException("Collaboration request not found"));
        
        if (!request.getAgreed()) {
            // User declined the terms
            collabRequest.setNegotiationStatus(CollaborationRequest.NegotiationStatus.DECLINED);
            log.info("Terms declined by {} ({})", request.getUserId(), request.getUserType());
            
            // Create system message
            NegotiationMessage message = new NegotiationMessage();
            message.setCollaborationRequestId(request.getCollaborationRequestId());
            message.setSenderId(request.getUserId());
            message.setSenderType("COMPANY".equals(request.getUserType()) ? 
                    NegotiationMessage.SenderType.COMPANY : NegotiationMessage.SenderType.FREELANCER);
            message.setMessageType(NegotiationMessage.MessageType.SYSTEM);
            message.setMessage("Terms declined");
            message.setIsRead(false);
            negotiationMessageRepository.save(message);
            
            return collaborationRequestRepository.save(collabRequest);
        }
        
        // User agreed to terms
        if ("FREELANCER".equals(request.getUserType())) {
            collabRequest.setFreelancerAgreed(true);
            collabRequest.setFreelancerAgreedAt(LocalDateTime.now());
        } else if ("COMPANY".equals(request.getUserType())) {
            collabRequest.setCompanyAgreed(true);
            collabRequest.setCompanyAgreedAt(LocalDateTime.now());
        }
        
        // Check if both parties have agreed
        if (collabRequest.getFreelancerAgreed() && collabRequest.getCompanyAgreed()) {
            collabRequest.setNegotiationStatus(CollaborationRequest.NegotiationStatus.AGREED);
            
            // Set final agreed terms
            collabRequest.setFinalAgreedPrice(collabRequest.getCounterOfferPrice() != null ? 
                    collabRequest.getCounterOfferPrice() : collabRequest.getProposedPrice());
            collabRequest.setFinalAgreedTimeline(collabRequest.getCounterOfferTimeline());
            collabRequest.setAgreedMilestones(collabRequest.getProposedMilestones());
            
            log.info("Both parties agreed! Negotiation complete for request {}", request.getCollaborationRequestId());
            
            // Create system message
            NegotiationMessage message = new NegotiationMessage();
            message.setCollaborationRequestId(request.getCollaborationRequestId());
            message.setSenderId(request.getUserId());
            message.setSenderType("COMPANY".equals(request.getUserType()) ? 
                    NegotiationMessage.SenderType.COMPANY : NegotiationMessage.SenderType.FREELANCER);
            message.setMessageType(NegotiationMessage.MessageType.SYSTEM);
            message.setMessage("Both parties have agreed to the terms. Contract is ready to be finalized.");
            message.setIsRead(false);
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("finalPrice", collabRequest.getFinalAgreedPrice());
            metadata.put("finalTimeline", collabRequest.getFinalAgreedTimeline());
            message.setMetadata(metadata);
            
            negotiationMessageRepository.save(message);
        } else {
            collabRequest.setNegotiationStatus(CollaborationRequest.NegotiationStatus.NEGOTIATING);
            log.info("Waiting for other party to agree. Freelancer: {}, Company: {}", 
                    collabRequest.getFreelancerAgreed(), collabRequest.getCompanyAgreed());
        }
        
        return collaborationRequestRepository.save(collabRequest);
    }
    
    /**
     * Get all messages for a collaboration request
     */
    public List<NegotiationMessage> getMessages(Long collaborationRequestId) {
        return negotiationMessageRepository.findByCollaborationRequestIdOrderByCreatedAtAsc(collaborationRequestId);
    }
    
    /**
     * Get unread message count
     */
    public Long getUnreadCount(Long collaborationRequestId) {
        return negotiationMessageRepository.countByCollaborationRequestIdAndIsReadFalse(collaborationRequestId);
    }
    
    /**
     * Mark messages as read
     */
    @Transactional
    public void markMessagesAsRead(Long collaborationRequestId, Long userId) {
        List<NegotiationMessage> unreadMessages = negotiationMessageRepository
                .findByCollaborationRequestIdAndIsReadFalse(collaborationRequestId);
        
        unreadMessages.stream()
                .filter(msg -> !msg.getSenderId().equals(userId)) // Don't mark own messages as read
                .forEach(msg -> msg.setIsRead(true));
        
        negotiationMessageRepository.saveAll(unreadMessages);
    }
    
    /**
     * Get revision history
     */
    public List<ProposalRevision> getRevisionHistory(Long collaborationRequestId) {
        return proposalRevisionRepository.findByCollaborationRequestIdOrderByRevisionNumberAsc(collaborationRequestId);
    }
}
