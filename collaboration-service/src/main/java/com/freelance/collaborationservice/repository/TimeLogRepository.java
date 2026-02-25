package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.TimeLog;
import com.freelance.collaborationservice.model.TimeLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
    
    List<TimeLog> findByTaskId(Long taskId);
    
    List<TimeLog> findByFreelancerId(Long freelancerId);
    
    List<TimeLog> findByTaskIdAndFreelancerId(Long taskId, Long freelancerId);
    
    List<TimeLog> findByFreelancerIdAndStatus(Long freelancerId, TimeLogStatus status);
    
    @Query("SELECT SUM(tl.durationMinutes) FROM TimeLog tl WHERE tl.taskId = :taskId AND tl.status = 'APPROVED'")
    Integer getTotalApprovedMinutesForTask(Long taskId);
    
    @Query("SELECT tl FROM TimeLog tl WHERE tl.freelancerId = :freelancerId AND tl.endTime IS NULL")
    List<TimeLog> findActiveTimeLogs(Long freelancerId);
}
