package com.freelance.collaborationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for Collaboration Microservice.
 * Handles companies, collaborations, and collaboration requests.
 */
@SpringBootApplication
public class CollaborationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollaborationServiceApplication.class, args);
    }
}
