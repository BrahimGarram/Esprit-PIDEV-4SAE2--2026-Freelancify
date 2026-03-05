package com.freelance.collaborationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * NegotiationMessage Entity
 * 
 * Represents messages exchanged during the negotiation phase between
 * freelancers and companies before a collaboration contract is finalized.
 */
@Entity
@Table(name = "negotiation_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "collaboration_request_id", nullable = false)
    private Long collaborationRequestId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> metadata;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum SenderType {
        FREELANCER,
        COMPANY
    }
    
    public enum MessageType {
        TEXT,              // Regular text message
        COUNTER_OFFER,     // Counter-offer with new terms
        MILESTONE_PROPOSAL,// Milestone breakdown proposal
        QUESTION,          // Question from one party
        ANSWER,            // Answer to a question
        SYSTEM             // System-generated message
    }
}
