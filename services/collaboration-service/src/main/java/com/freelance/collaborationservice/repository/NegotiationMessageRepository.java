package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.NegotiationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NegotiationMessageRepository extends JpaRepository<NegotiationMessage, Long> {
    
    List<NegotiationMessage> findByCollaborationRequestIdOrderByCreatedAtAsc(Long collaborationRequestId);
    
    List<NegotiationMessage> findByCollaborationRequestIdAndIsReadFalse(Long collaborationRequestId);
    
    Long countByCollaborationRequestIdAndIsReadFalse(Long collaborationRequestId);
}
