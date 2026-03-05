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

@Entity
@Table(name = "collaboration_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long collaborationId;

    @Column(nullable = false)
    private Long freelancerId;

    @Column(columnDefinition = "TEXT")
    private String proposalMessage;

    @Column(precision = 12, scale = 2)
    private BigDecimal proposedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CollaborationRequestStatus status = CollaborationRequestStatus.PENDING;

    // Negotiation fields
    @Enumerated(EnumType.STRING)
    @Column(name = "negotiation_status", length = 20)
    private NegotiationStatus negotiationStatus = NegotiationStatus.INITIAL;

    @Column(name = "counter_offer_price", precision = 10, scale = 2)
    private BigDecimal counterOfferPrice;

    @Column(name = "counter_offer_timeline", length = 100)
    private String counterOfferTimeline;

    @Column(name = "counter_offer_message", columnDefinition = "TEXT")
    private String counterOfferMessage;

    @Column(name = "counter_offered_by")
    private Long counterOfferedBy;

    @Column(name = "counter_offered_at")
    private LocalDateTime counterOfferedAt;

    @Column(name = "counter_offer_count")
    private Integer counterOfferCount = 0;

    // Milestone proposals
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "proposed_milestones", columnDefinition = "JSON")
    private List<Map<String, Object>> proposedMilestones;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "agreed_milestones", columnDefinition = "JSON")
    private List<Map<String, Object>> agreedMilestones;

    // Contract agreement
    @Column(name = "freelancer_agreed")
    private Boolean freelancerAgreed = false;

    @Column(name = "freelancer_agreed_at")
    private LocalDateTime freelancerAgreedAt;

    @Column(name = "company_agreed")
    private Boolean companyAgreed = false;

    @Column(name = "company_agreed_at")
    private LocalDateTime companyAgreedAt;

    @Column(name = "final_agreed_price", precision = 10, scale = 2)
    private BigDecimal finalAgreedPrice;

    @Column(name = "final_agreed_timeline", length = 100)
    private String finalAgreedTimeline;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = CollaborationRequestStatus.PENDING;
        if (negotiationStatus == null) negotiationStatus = NegotiationStatus.INITIAL;
        if (counterOfferCount == null) counterOfferCount = 0;
        if (freelancerAgreed == null) freelancerAgreed = false;
        if (companyAgreed == null) companyAgreed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum NegotiationStatus {
        INITIAL,           // Just applied, no negotiation yet
        NEGOTIATING,       // Active discussion/negotiation
        COUNTER_OFFERED,   // Counter-offer sent, waiting for response
        AGREED,            // Terms agreed, ready for contract
        DECLINED           // Negotiation failed/declined
    }
}
