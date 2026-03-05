package com.freelance.collaborationservice.repository;

import com.freelance.collaborationservice.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    
    List<TaskComment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    
    List<TaskComment> findByUserId(Long userId);
}
