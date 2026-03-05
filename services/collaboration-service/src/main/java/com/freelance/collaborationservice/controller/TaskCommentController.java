package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CreateTaskCommentRequest;
import com.freelance.collaborationservice.dto.TaskCommentDTO;
import com.freelance.collaborationservice.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TaskCommentController {

    private final TaskCommentService commentService;

    @PostMapping
    public ResponseEntity<TaskCommentDTO> createComment(@Valid @RequestBody CreateTaskCommentRequest request) {
        log.info("REST request to create comment for task: {}", request.getTaskId());
        TaskCommentDTO comment = commentService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<TaskCommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody String content) {
        log.info("REST request to update comment: {}", commentId);
        TaskCommentDTO comment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("REST request to delete comment: {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskCommentDTO>> getCommentsByTask(@PathVariable Long taskId) {
        log.info("REST request to get comments for task: {}", taskId);
        List<TaskCommentDTO> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskCommentDTO>> getCommentsByUser(@PathVariable Long userId) {
        log.info("REST request to get comments by user: {}", userId);
        List<TaskCommentDTO> comments = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(comments);
    }
}
