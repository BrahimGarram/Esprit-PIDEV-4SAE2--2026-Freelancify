package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.Task;
import com.freelance.collaborationservice.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByCollaborationIdOrderByOrderIndexAsc(Long collaborationId);
    
    List<Task> findByCollaborationIdAndStatus(Long collaborationId, TaskStatus status);
    
    List<Task> findByAssignedFreelancerId(Long freelancerId);
    
    List<Task> findByCollaborationIdAndAssignedFreelancerId(Long collaborationId, Long freelancerId);
    
    List<Task> findByMilestoneId(Long milestoneId);
    
    List<Task> findBySprintId(Long sprintId);
    
    List<Task> findByParentTaskId(Long parentTaskId);
    
    @Query("SELECT t FROM Task t WHERE t.collaborationId = :collaborationId AND t.deadline < :now AND t.status != 'DONE'")
    List<Task> findOverdueTasks(Long collaborationId, LocalDateTime now);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.collaborationId = :collaborationId AND t.status = :status")
    Long countByCollaborationIdAndStatus(Long collaborationId, TaskStatus status);
}
