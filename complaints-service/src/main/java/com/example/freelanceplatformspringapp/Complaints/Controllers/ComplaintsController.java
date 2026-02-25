package com.example.freelanceplatformspringapp.Complaints.Controllers;

import com.example.freelanceplatformspringapp.Complaints.Entity.Complaints;
import com.example.freelanceplatformspringapp.Complaints.Services.IComplaintsInterface;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/report")
public class ComplaintsController {
    IComplaintsInterface ic;

    @GetMapping("/retrieve-all-complaints")
    public List<Complaints> retrieveAllComplaints(@RequestParam("userId") Long userId) {
        // Only return complaints created by the given user
        return ic.retrieveComplaintsByUser(userId);
    }

    @GetMapping("/retrieve-claim/{claim-id}")
    public Complaints retrieveClaimComplaint(@PathVariable("claim-id") Long claimId,
                                             @RequestParam("userId") Long userId) {
        Optional<Complaints> claimOpt = ic.retrieveClaimForUser(claimId, userId);
        return claimOpt.orElse(null);
    }

    @PostMapping("/create-claim")
    public Complaints createClaim(@RequestBody Complaints c,
                                  @RequestParam("userId") Long userId) {
        // Always enforce the owner of the complaint from the request parameter
        c.setUserId(userId);
        return ic.addClaim(c);
    }

    @PutMapping("/update-claim")
    public Complaints updateClaim(@RequestBody Complaints c,
                                  @RequestParam("userId") Long userId) {
        // Ensure the complaint belongs to the user before updating
        if (c.getIdReclamation() == null) {
            return null;
        }
        Optional<Complaints> existing = ic.retrieveClaimForUser(c.getIdReclamation(), userId);
        if (existing.isEmpty()) {
            // Either not found or does not belong to this user
            return null;
        }
        c.setUserId(userId);
        return ic.updateComplaint(c);
    }

    @DeleteMapping("/drop-claim/{claim-id}")
    void deleteClaim(@PathVariable("claim-id") Long claimId,
                     @RequestParam("userId") Long userId) {
        Optional<Complaints> existing = ic.retrieveClaimForUser(claimId, userId);
        if (existing.isPresent()) {
            ic.deleteComplaint(claimId);
        }
    }
}
