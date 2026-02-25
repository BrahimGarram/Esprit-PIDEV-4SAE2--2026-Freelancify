package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CreateTaskRequest;
import com.freelance.collaborationservice.dto.TaskDTO;
import com.freelance.collaborationservice.dto.UpdateTaskRequest;
import com.freelance.collaborationservice.model.TaskStatus;
import com.freelance.collaborationservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody CreateTaskRequest request) {
        log.info("REST request to create task: {}", request.getTitle());
        TaskDTO task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        log.info("REST request to update task: {}", taskId);
        TaskDTO task = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        log.info("REST request to delete task: {}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long taskId) {
        log.info("REST request to get task: {}", taskId);
        TaskDTO task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/collaboration/{collaborationId}")
    public ResponseEntity<List<TaskDTO>> getTasksByCollaboration(@PathVariable Long collaborationId) {
        log.info("REST request to get tasks for collaboration: {}", collaborationId);
        List<TaskDTO> tasks = taskService.getTasksByCollaboration(collaborationId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/collaboration/{collaborationId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @PathVariable Long collaborationId,
            @PathVariable TaskStatus status) {
        log.info("REST request to get tasks by status {} for collaboration: {}", status, collaborationId);
        List<TaskDTO> tasks = taskService.getTasksByStatus(collaborationId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<TaskDTO>> getTasksByFreelancer(@PathVariable Long freelancerId) {
        log.info("REST request to get tasks for freelancer: {}", freelancerId);
        List<TaskDTO> tasks = taskService.getTasksByFreelancer(freelancerId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/milestone/{milestoneId}")
    public ResponseEntity<List<TaskDTO>> getTasksByMilestone(@PathVariable Long milestoneId) {
        log.info("REST request to get tasks for milestone: {}", milestoneId);
        List<TaskDTO> tasks = taskService.getTasksByMilestone(milestoneId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<TaskDTO>> getTasksBySprint(@PathVariable Long sprintId) {
        log.info("REST request to get tasks for sprint: {}", sprintId);
        List<TaskDTO> tasks = taskService.getTasksBySprint(sprintId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}/subtasks")
    public ResponseEntity<List<TaskDTO>> getSubtasks(@PathVariable Long taskId) {
        log.info("REST request to get subtasks for task: {}", taskId);
        List<TaskDTO> subtasks = taskService.getSubtasks(taskId);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/collaboration/{collaborationId}/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(@PathVariable Long collaborationId) {
        log.info("REST request to get overdue tasks for collaboration: {}", collaborationId);
        List<TaskDTO> tasks = taskService.getOverdueTasks(collaborationId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskDTO> moveTask(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @RequestParam(required = false) Integer orderIndex) {
        log.info("REST request to move task {} to status {} with order {}", taskId, status, orderIndex);
        TaskDTO task = taskService.moveTask(taskId, status, orderIndex);
        return ResponseEntity.ok(task);
    }
}
