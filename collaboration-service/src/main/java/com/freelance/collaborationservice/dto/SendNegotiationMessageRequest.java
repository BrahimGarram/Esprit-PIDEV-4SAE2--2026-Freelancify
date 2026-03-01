package com.freelance.collaborationservice.dto;

import com.freelance.collaborationservice.model.NegotiationMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for sending a negotiation message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNegotiationMessageRequest {
    private Long collaborationRequestId;
    private Long senderId;
    private NegotiationMessage.SenderType senderType;
    private NegotiationMessage.MessageType messageType;
    private String message;
    private Map<String, Object> metadata; // For counter-offers, milestone proposals, etc.
}
