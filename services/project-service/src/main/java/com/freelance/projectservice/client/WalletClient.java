package com.freelance.projectservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Calls payment-service to deduct tokens (e.g. project creation, proposal submission).
 */
@Component
public class WalletClient {

    private static final Logger log = LoggerFactory.getLogger(WalletClient.class);

    private final RestTemplate restTemplate;
    private final String paymentServiceBaseUrl;

    public WalletClient(RestTemplate restTemplate,
                        @Value("${app.payment-service-url:http://localhost:8095/api/payments}") String paymentServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.paymentServiceBaseUrl = paymentServiceBaseUrl;
        log.info("WalletClient using payment-service URL: {}", paymentServiceBaseUrl);
    }

    /**
     * Deduct tokens from user wallet. Returns true if successful, false if insufficient balance or error.
     */
    public boolean deductTokens(Long userId, int amount, String description) {
        if (amount <= 0) return true;
        String url = paymentServiceBaseUrl + "/wallet/deduct";
        Map<String, Object> body = Map.of(
                "userId", userId,
                "amount", amount,
                "description", description != null ? description : "Token use"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object balance = response.getBody().get("tokenBalance");
                log.info("Deducted {} tokens from user {} for: {}. New balance: {}", amount, userId, description, balance);
                return true;
            }
            log.warn("Wallet deduct returned non-2xx or empty body: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Wallet deduct failed for user {} amount {}: {} - check payment-service at {}", userId, amount, e.getMessage(), url);
        }
        return false;
    }
}
