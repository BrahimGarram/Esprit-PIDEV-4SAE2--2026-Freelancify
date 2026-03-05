package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.ProposalRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRevisionRepository extends JpaRepository<ProposalRevision, Long> {
    
    List<ProposalRevision> findByCollaborationRequestIdOrderByRevisionNumberAsc(Long collaborationRequestId);
    
    ProposalRevision findTopByCollaborationRequestIdOrderByRevisionNumberDesc(Long collaborationRequestId);
}
