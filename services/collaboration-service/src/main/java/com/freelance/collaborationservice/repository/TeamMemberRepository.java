package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    List<TeamMember> findByCollaborationIdAndIsActiveTrue(Long collaborationId);
    
    List<TeamMember> findByFreelancerId(Long freelancerId);
    
    Optional<TeamMember> findByCollaborationIdAndFreelancerId(Long collaborationId, Long freelancerId);
    
    boolean existsByCollaborationIdAndFreelancerId(Long collaborationId, Long freelancerId);
}
