package com.example.freelanceplatformspringapp.Complaints.Services;
import com.example.freelanceplatformspringapp.Complaints.Entity.Complaints;
import com.example.freelanceplatformspringapp.Complaints.Repository.ComplaintsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintsService implements IComplaintsInterface {
    private final ComplaintsRepository cr;

    @Override
    public Complaints addClaim(Complaints complaints) {
        return cr.save(complaints);
    }

    @Override
    public Optional<Complaints> retrieveClaim(Long id) {
        return cr.findById(id);
    }

    @Override
    public List<Complaints> retrieveAllComplaints() {
        return cr.findAll();
    }

    @Override
    public List<Complaints> retrieveComplaintsByUser(Long userId) {
        return cr.findByUserId(userId);
    }

    @Override
    public Optional<Complaints> retrieveClaimForUser(Long id, Long userId) {
        return cr.findByIdReclamationAndUserId(id, userId);
    }

    @Override
    public Complaints updateComplaint(Complaints complaints) {
        return cr.save(complaints);
    }

    @Override
    public void deleteComplaint(Long id) {
        cr.deleteById(id);
    }

}