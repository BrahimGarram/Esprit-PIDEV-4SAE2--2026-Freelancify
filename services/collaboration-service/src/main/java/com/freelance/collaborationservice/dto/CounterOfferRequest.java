package com.freelance.collaborationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for sending a counter-offer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounterOfferRequest {
    private Long collaborationRequestId;
    private Long counterOfferedBy;
    private String counterOfferedByType; // "FREELANCER" or "COMPANY"
    
    private BigDecimal counterOfferPrice;
    private String counterOfferTimeline;
    private String counterOfferMessage;
    
    // Optional: Proposed milestone breakdown
    private List<MilestoneProposal> proposedMilestones;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilestoneProposal {
        private String name;
        private Integer percentage;
        private String deliverables;
        private String duration;
    }
}
