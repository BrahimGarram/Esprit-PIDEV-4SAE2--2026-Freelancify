package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.Milestone;
import com.freelance.collaborationservice.model.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    
    List<Milestone> findByCollaborationIdOrderByOrderIndexAsc(Long collaborationId);
    
    List<Milestone> findByCollaborationIdAndStatus(Long collaborationId, MilestoneStatus status);
    
    Long countByCollaborationIdAndStatus(Long collaborationId, MilestoneStatus status);
}
