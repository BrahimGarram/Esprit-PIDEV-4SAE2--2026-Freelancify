package com.freelance.userservice.controller;

import com.freelance.userservice.dto.CreateUserRequest;
import com.freelance.userservice.model.User;
import com.freelance.userservice.repository.UserRepository;
import com.freelance.userservice.service.KeycloakService;
import com.freelance.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Controller
 * 
 * Provides administrative endpoints for user management and seeding
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final KeycloakService keycloakService;
    private final UserService userService;
    private final UserRepository userRepository;
    
    /**
     * Seed Keycloak with sample users
     * 
     * This endpoint creates 10 sample users in Keycloak that match
     * the collaboration service sample data.
     * 
     * @return List of created users with their Keycloak IDs
     */
    @PostMapping("/seed-users")
    public ResponseEntity<?> seedUsers() {
        log.info("========================================");
        log.info("Starting Keycloak User Seeding via API...");
        log.info("========================================");
        
        List<Map<String, String>> createdUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try {
            // User 1: Enterprise/Company
            createAndLog("company1", "company1@example.com", "password123", "ENTERPRISE", createdUsers, errors);
            
            // User 2: Project Manager
            createAndLog("sarah.manager", "sarah.manager@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 3: Backend Developer
            createAndLog("mike.backend", "mike.backend@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 4: Frontend Developer
            createAndLog("john.frontend", "john.frontend@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 5: Designer
            createAndLog("emma.designer", "emma.designer@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 6: QA Tester
            createAndLog("alex.qa", "alex.qa@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 7: Fullstack Developer
            createAndLog("lisa.fullstack", "lisa.fullstack@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 8: QA Tester 2
            createAndLog("david.qa", "david.qa@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 9: Designer 2
            createAndLog("sophia.designer", "sophia.designer@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            // User 10: Frontend Developer 2
            createAndLog("james.frontend", "james.frontend@example.com", "password123", "FREELANCER", createdUsers, errors);
            
            log.info("========================================");
            log.info("Keycloak User Seeding Completed!");
            log.info("Created: {} users", createdUsers.size());
            log.info("Errors: {}", errors.size());
            log.info("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User seeding completed");
            response.put("createdUsers", createdUsers);
            response.put("totalCreated", createdUsers.size());
            response.put("errors", errors);
            response.put("totalErrors", errors.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during user seeding", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "User seeding failed: " + e.getMessage());
            errorResponse.put("createdUsers", createdUsers);
            errorResponse.put("errors", errors);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    private void createAndLog(String username, String email, String password, String role,
                             List<Map<String, String>> createdUsers, List<String> errors) {
        try {
            log.info("Creating user: {} ({})", username, email);
            String keycloakUserId = keycloakService.registerUser(username, email, password, role);
            log.info("✓ User created successfully: {} with Keycloak ID: {}", username, keycloakUserId);
            
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", username);
            userInfo.put("email", email);
            userInfo.put("role", role);
            userInfo.put("keycloakId", keycloakUserId);
            userInfo.put("status", "created");
            createdUsers.add(userInfo);
            
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                log.warn("⚠ User {} already exists in Keycloak, skipping...", username);
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("email", email);
                userInfo.put("role", role);
                userInfo.put("status", "already_exists");
                createdUsers.add(userInfo);
            } else {
                log.error("✗ Failed to create user: {}", username, e);
                errors.add(username + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "admin-controller");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Sync all Keycloak users to MySQL database
     * 
     * This endpoint fetches all users from Keycloak and creates corresponding
     * records in the MySQL database. This is needed so the collaboration service
     * sample data can reference these user IDs.
     * 
     * @return Summary of synced users
     */
    @PostMapping("/sync-all-users")
    public ResponseEntity<?> syncAllUsers() {
        log.info("========================================");
        log.info("Starting MySQL User Sync from Keycloak...");
        log.info("========================================");
        
        List<Map<String, String>> syncedUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try {
            // Get admin token
            String adminToken = getKeycloakAdminToken();
            
            // Get all users from Keycloak
            String usersUrl = "http://localhost:9090/admin/realms/projetpidev/users";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(adminToken);
            org.springframework.http.HttpEntity<Void> request = new org.springframework.http.HttpEntity<>(headers);
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                usersUrl,
                org.springframework.http.HttpMethod.GET,
                request,
                String.class
            );
            
            // Parse users
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode usersArray = mapper.readTree(response.getBody());
            
            log.info("Found {} users in Keycloak", usersArray.size());
            
            // Sync each user to MySQL
            for (com.fasterxml.jackson.databind.JsonNode userNode : usersArray) {
                try {
                    String keycloakId = userNode.get("id").asText();
                    String username = userNode.get("username").asText();
                    String email = userNode.get("email") != null ? userNode.get("email").asText() : username + "@example.com";
                    
                    log.info("Syncing user: {} ({})", username, email);
                    
                    // Create sync request
                    CreateUserRequest syncRequest = new CreateUserRequest(
                        keycloakId,
                        username,
                        email,
                        com.freelance.userservice.model.UserRole.FREELANCER, // Default role
                        null // Country will be detected
                    );
                    
                    // Sync to MySQL
                    userService.syncUser(syncRequest);
                    
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("keycloakId", keycloakId);
                    userInfo.put("username", username);
                    userInfo.put("email", email);
                    userInfo.put("status", "synced");
                    syncedUsers.add(userInfo);
                    
                    log.info("✓ User synced: {}", username);
                    
                } catch (Exception e) {
                    log.error("✗ Failed to sync user", e);
                    errors.add("Failed to sync user: " + e.getMessage());
                }
            }
            
            log.info("========================================");
            log.info("MySQL User Sync Completed!");
            log.info("Synced: {} users", syncedUsers.size());
            log.info("Errors: {}", errors.size());
            log.info("========================================");
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", "User sync completed");
            responseMap.put("syncedUsers", syncedUsers);
            responseMap.put("totalSynced", syncedUsers.size());
            responseMap.put("errors", errors);
            responseMap.put("totalErrors", errors.size());
            
            return ResponseEntity.ok(responseMap);
            
        } catch (Exception e) {
            log.error("Error during user sync", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "User sync failed: " + e.getMessage());
            errorResponse.put("syncedUsers", syncedUsers);
            errorResponse.put("errors", errors);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Get Keycloak admin token
     */
    private String getKeycloakAdminToken() throws Exception {
        String tokenUrl = "http://localhost:9090/realms/master/protocol/openid-connect/token";
        
        org.springframework.util.MultiValueMap<String, String> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", "admin");
        body.add("password", "admin");
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = 
            new org.springframework.http.HttpEntity<>(body, headers);
        
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }
    
    /**
     * Get user ID mapping from MySQL
     * 
     * Returns a mapping of usernames to their actual MySQL user IDs.
     * This is needed to update the SQL migration file with correct IDs.
     * 
     * @return Map of username to user ID
     */
    @GetMapping("/get-user-id-mapping")
    public ResponseEntity<?> getUserIdMapping() {
        try {
            // Query all users from database via UserService
            // We'll need to add a method to get all users
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User ID mapping endpoint - implementation needed");
            response.put("note", "Use the update-sql-migration endpoint instead");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting user ID mapping", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Update SQL migration file with actual user IDs
     * 
     * This endpoint:
     * 1. Fetches actual user IDs from MySQL
     * 2. Creates a mapping of expected usernames to actual IDs
     * 3. Updates the V4__insert_sample_data.sql file with correct IDs
     * 
     * @return Summary of updates made
     */
    @PostMapping("/update-sql-migration")
    public ResponseEntity<?> updateSqlMigration() {
        log.info("========================================");
        log.info("Updating SQL Migration with Actual User IDs...");
        log.info("========================================");
        
        try {
            // Get user ID mapping from UserService
            Map<String, Long> userIdMap = getUserIdMappingFromDatabase();
            
            log.info("User ID Mapping:");
            userIdMap.forEach((username, id) -> log.info("  {} -> ID {}", username, id));
            
            // Read the SQL file
            String sqlFilePath = "collaboration-service/src/main/resources/db/migration/V4__insert_sample_data.sql";
            java.nio.file.Path path = java.nio.file.Paths.get(sqlFilePath);
            String sqlContent = java.nio.file.Files.readString(path);
            
            // Create the ID mapping based on the expected usernames in SQL
            // SQL uses IDs 1-10 for these users:
            Map<Integer, Long> idMapping = new HashMap<>();
            idMapping.put(1, userIdMap.getOrDefault("company1", 1L));
            idMapping.put(2, userIdMap.getOrDefault("sarah.manager", 2L));
            idMapping.put(3, userIdMap.getOrDefault("mike.backend", 3L));
            idMapping.put(4, userIdMap.getOrDefault("john.frontend", 4L));
            idMapping.put(5, userIdMap.getOrDefault("emma.designer", 5L));
            idMapping.put(6, userIdMap.getOrDefault("alex.qa", 6L));
            idMapping.put(7, userIdMap.getOrDefault("lisa.fullstack", 7L));
            idMapping.put(8, userIdMap.getOrDefault("david.qa", 8L));
            idMapping.put(9, userIdMap.getOrDefault("sophia.designer", 9L));
            idMapping.put(10, userIdMap.getOrDefault("james.frontend", 10L));
            
            // Replace user IDs in SQL
            String updatedSql = sqlContent;
            
            // Replace in specific contexts to avoid replacing other numbers
            // Replace in company_id, freelancer_id, user_id, assigned_freelancer_id, mentioned_user_id
            for (Map.Entry<Integer, Long> entry : idMapping.entrySet()) {
                Integer oldId = entry.getKey();
                Long newId = entry.getValue();
                
                if (!oldId.equals(newId.intValue())) {
                    // Replace company_id
                    updatedSql = updatedSql.replaceAll("company_id, " + oldId + ",", "company_id, " + newId + ",");
                    updatedSql = updatedSql.replaceAll("company_id = " + oldId + "\\b", "company_id = " + newId);
                    
                    // Replace freelancer_id
                    updatedSql = updatedSql.replaceAll("freelancer_id, " + oldId + ",", "freelancer_id, " + newId + ",");
                    updatedSql = updatedSql.replaceAll("freelancer_id = " + oldId + "\\b", "freelancer_id = " + newId);
                    
                    // Replace user_id
                    updatedSql = updatedSql.replaceAll("user_id, " + oldId + ",", "user_id, " + newId + ",");
                    updatedSql = updatedSql.replaceAll("user_id = " + oldId + "\\b", "user_id = " + newId);
                    
                    // Replace assigned_freelancer_id
                    updatedSql = updatedSql.replaceAll("assigned_freelancer_id, " + oldId + ",", "assigned_freelancer_id, " + newId + ",");
                    
                    // Replace mentioned_user_id
                    updatedSql = updatedSql.replaceAll("mentioned_user_id\\) VALUES \\(" + oldId + "\\)", "mentioned_user_id) VALUES (" + newId + ")");
                    
                    log.info("Replaced user ID {} with {}", oldId, newId);
                }
            }
            
            // Write updated SQL back to file
            java.nio.file.Files.writeString(path, updatedSql);
            
            log.info("========================================");
            log.info("SQL Migration Updated Successfully!");
            log.info("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "SQL migration file updated with actual user IDs");
            response.put("filePath", sqlFilePath);
            response.put("userIdMapping", idMapping);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating SQL migration", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update SQL migration: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Get user ID mapping from database
     */
    private Map<String, Long> getUserIdMappingFromDatabase() {
        Map<String, Long> mapping = new HashMap<>();
        
        try {
            // Get all users from database
            List<User> users = userRepository.findAll();
            
            for (User user : users) {
                mapping.put(user.getUsername(), user.getId());
                log.info("Found user: {} -> ID {}", user.getUsername(), user.getId());
            }
            
        } catch (Exception e) {
            log.error("Error getting user mapping from database", e);
        }
        
        return mapping;
    }
}
