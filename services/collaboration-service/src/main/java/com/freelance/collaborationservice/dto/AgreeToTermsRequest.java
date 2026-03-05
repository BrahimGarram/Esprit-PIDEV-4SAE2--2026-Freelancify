package com.freelance.collaborationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for agreeing to negotiated terms
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgreeToTermsRequest {
    private Long collaborationRequestId;
    private Long userId;
    private String userType; // "FREELANCER" or "COMPANY"
    private Boolean agreed; // true to agree, false to decline
}
