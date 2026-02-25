package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.CreateTaskCommentRequest;
import com.freelance.collaborationservice.dto.TaskCommentDTO;
import com.freelance.collaborationservice.dto.WebSocketMessage;
import com.freelance.collaborationservice.model.Task;
import com.freelance.collaborationservice.model.TaskComment;
import com.freelance.collaborationservice.repository.TaskCommentRepository;
import com.freelance.collaborationservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TaskCommentDTO createComment(CreateTaskCommentRequest request) {
        log.info("Creating comment for task: {}", request.getTaskId());
        
        // Validate task exists and get collaboration ID
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + request.getTaskId()));
        
        TaskComment comment = new TaskComment();
        comment.setTaskId(request.getTaskId());
        comment.setUserId(request.getUserId());
        comment.setContent(request.getContent());
        comment.setMentionedUserIds(request.getMentionedUserIds());
        comment.setAttachments(request.getAttachments());
        
        TaskComment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with ID: {}", savedComment.getId());
        
        TaskCommentDTO commentDTO = convertToDTO(savedComment);
        sendWebSocketNotification("COMMENT_ADDED", task.getCollaborationId(), commentDTO);
        
        return commentDTO;
    }

    public TaskCommentDTO updateComment(Long commentId, String content) {
        log.info("Updating comment: {}", commentId);
        
        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        
        comment.setContent(content);
        TaskComment updatedComment = commentRepository.save(comment);
        
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Long commentId) {
        log.info("Deleting comment: {}", commentId);
        
        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        
        Task task = taskRepository.findById(comment.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        commentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", commentId);
        
        sendWebSocketNotification("COMMENT_DELETED", task.getCollaborationId(), commentId);
    }

    @Transactional(readOnly = true)
    public List<TaskCommentDTO> getCommentsByTask(Long taskId) {
        List<TaskComment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskCommentDTO> getCommentsByUser(Long userId) {
        List<TaskComment> comments = commentRepository.findByUserId(userId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TaskCommentDTO convertToDTO(TaskComment comment) {
        return TaskCommentDTO.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .mentionedUserIds(comment.getMentionedUserIds())
                .attachments(comment.getAttachments())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private void sendWebSocketNotification(String type, Long collaborationId, Object payload) {
        try {
            WebSocketMessage message = new WebSocketMessage(type, collaborationId, payload);
            messagingTemplate.convertAndSend("/topic/collaboration/" + collaborationId, message);
            log.debug("WebSocket notification sent: {} for collaboration: {}", type, collaborationId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification: {}", e.getMessage());
        }
    }
}
