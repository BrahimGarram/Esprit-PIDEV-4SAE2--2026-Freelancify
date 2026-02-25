package com.freelance.userservice.util;

import com.freelance.userservice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Keycloak User Seeder
 * 
 * Seeds Keycloak with sample users that match the collaboration service sample data.
 * These users correspond to the user IDs referenced in V4__insert_sample_data.sql
 * 
 * To run this seeder:
 * 1. Make sure Keycloak is running on port 9090
 * 2. Uncomment the @Component annotation below
 * 3. Start the backend application
 * 4. The seeder will run automatically on startup
 * 5. Comment out @Component again after first run to prevent duplicate users
 */
@Slf4j
@RequiredArgsConstructor
// @Component  // Uncomment to enable automatic seeding on startup
public class KeycloakUserSeeder implements CommandLineRunner {
    
    private final KeycloakService keycloakService;
    
    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("Starting Keycloak User Seeding...");
        log.info("========================================");
        
        try {
            // User 1: Enterprise/Company (not a freelancer, but referenced in collaborations)
            createUser("company1", "company1@example.com", "password123", "ENTERPRISE");
            
            // User 2: Project Manager
            createUser("sarah.manager", "sarah.manager@example.com", "password123", "FREELANCER");
            
            // User 3: Backend Developer
            createUser("mike.backend", "mike.backend@example.com", "password123", "FREELANCER");
            
            // User 4: Frontend Developer
            createUser("john.frontend", "john.frontend@example.com", "password123", "FREELANCER");
            
            // User 5: Designer
            createUser("emma.designer", "emma.designer@example.com", "password123", "FREELANCER");
            
            // User 6: QA Tester
            createUser("alex.qa", "alex.qa@example.com", "password123", "FREELANCER");
            
            // User 7: Fullstack Developer
            createUser("lisa.fullstack", "lisa.fullstack@example.com", "password123", "FREELANCER");
            
            // User 8: QA Tester 2
            createUser("david.qa", "david.qa@example.com", "password123", "FREELANCER");
            
            // User 9: Designer 2
            createUser("sophia.designer", "sophia.designer@example.com", "password123", "FREELANCER");
            
            // User 10: Frontend Developer 2
            createUser("james.frontend", "james.frontend@example.com", "password123", "FREELANCER");
            
            log.info("========================================");
            log.info("Keycloak User Seeding Completed!");
            log.info("========================================");
            log.info("All users created with password: password123");
            log.info("You can now login with any of these accounts");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("Error during Keycloak user seeding", e);
            log.error("========================================");
            log.error("Make sure:");
            log.error("1. Keycloak is running on port 9090");
            log.error("2. Keycloak admin credentials are correct (admin/admin)");
            log.error("3. Realm 'projetpidev' exists in Keycloak");
            log.error("========================================");
        }
    }
    
    private void createUser(String username, String email, String password, String role) {
        try {
            log.info("Creating user: {} ({})", username, email);
            String keycloakUserId = keycloakService.registerUser(username, email, password, role);
            log.info("✓ User created successfully: {} with Keycloak ID: {}", username, keycloakUserId);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                log.warn("⚠ User {} already exists in Keycloak, skipping...", username);
            } else {
                log.error("✗ Failed to create user: {}", username, e);
                throw e;
            }
        }
    }
}
