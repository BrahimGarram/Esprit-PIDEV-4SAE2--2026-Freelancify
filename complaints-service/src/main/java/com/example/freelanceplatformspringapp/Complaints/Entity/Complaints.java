package com.example.freelanceplatformspringapp.Complaints.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Complaints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReclamation;
    /**
     * Identifier of the user who created this complaint.
     * This should correspond to the user ID from the user-service.
     */
    private Long userId;
    private String subject;
    private String description;
    @Enumerated(EnumType.STRING)
    private ClaimStatus claimStatus;

    @OneToMany
    private List<Response> responses;

}
