package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.Sprint;
import com.freelance.collaborationservice.model.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    
    List<Sprint> findByCollaborationIdOrderByStartDateDesc(Long collaborationId);
    
    List<Sprint> findByCollaborationIdAndStatus(Long collaborationId, SprintStatus status);
    
    @Query("SELECT s FROM Sprint s WHERE s.collaborationId = :collaborationId AND s.status = 'ACTIVE' AND s.startDate <= :now AND s.endDate >= :now")
    Optional<Sprint> findActiveSprint(Long collaborationId, LocalDateTime now);
}
