package com.freelance.collaborationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ProposalRevision Entity
 * 
 * Tracks the history of proposal revisions during negotiation.
 * Each counter-offer creates a new revision record.
 */
@Entity
@Table(name = "proposal_revisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalRevision {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "collaboration_request_id", nullable = false)
    private Long collaborationRequestId;
    
    @Column(name = "revision_number", nullable = false)
    private Integer revisionNumber;
    
    @Column(name = "revised_by", nullable = false)
    private Long revisedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "revised_by_type", nullable = false)
    private ReviserType revisedByType;
    
    @Column(name = "previous_price")
    private BigDecimal previousPrice;
    
    @Column(name = "new_price")
    private BigDecimal newPrice;
    
    @Column(name = "previous_timeline", length = 100)
    private String previousTimeline;
    
    @Column(name = "new_timeline", length = 100)
    private String newTimeline;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "previous_milestones", columnDefinition = "JSON")
    private List<Map<String, Object>> previousMilestones;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_milestones", columnDefinition = "JSON")
    private List<Map<String, Object>> newMilestones;
    
    @Column(name = "revision_message", columnDefinition = "TEXT")
    private String revisionMessage;
    
    @Column(name = "revision_reason")
    private String revisionReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum ReviserType {
        FREELANCER,
        COMPANY
    }
}
