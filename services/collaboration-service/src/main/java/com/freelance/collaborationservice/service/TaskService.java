package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.CreateTaskRequest;
import com.freelance.collaborationservice.dto.TaskDTO;
import com.freelance.collaborationservice.dto.UpdateTaskRequest;
import com.freelance.collaborationservice.dto.WebSocketMessage;
import com.freelance.collaborationservice.model.*;
import com.freelance.collaborationservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final MilestoneRepository milestoneRepository;
    private final SprintRepository sprintRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TaskDTO createTask(CreateTaskRequest request) {
        log.info("Creating task: {} for collaboration: {}", request.getTitle(), request.getCollaborationId());
        
        // Validate team member exists
        if (!teamMemberRepository.existsByCollaborationIdAndFreelancerId(
                request.getCollaborationId(), request.getAssignedFreelancerId())) {
            throw new RuntimeException("Freelancer is not a team member of this collaboration");
        }
        
        Task task = new Task();
        task.setCollaborationId(request.getCollaborationId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssignedFreelancerId(request.getAssignedFreelancerId());
        task.setAssignedTo(request.getAssignedFreelancerId()); // Set both fields
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        task.setDeadline(request.getDeadline());
        task.setDueDate(request.getDeadline()); // Set both deadline fields
        task.setEstimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : 0);
        task.setAttachments(request.getAttachments());
        task.setMilestoneId(request.getMilestoneId());
        task.setParentTaskId(request.getParentTaskId());
        task.setSprintId(request.getSprintId());
        task.setDependsOnTaskIds(request.getDependsOnTaskIds());
        task.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0);
        task.setCreatedBy(request.getAssignedFreelancerId()); // Set creator as the assigned freelancer
        task.setProjectId(request.getCollaborationId()); // Set project ID same as collaboration ID
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        
        TaskDTO taskDTO = convertToDTO(savedTask);
        sendWebSocketNotification("TASK_CREATED", savedTask.getCollaborationId(), taskDTO);
        
        return taskDTO;
    }

    public TaskDTO updateTask(Long taskId, UpdateTaskRequest request) {
        log.info("Updating task: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getAssignedFreelancerId() != null) {
            // Validate team member
            if (!teamMemberRepository.existsByCollaborationIdAndFreelancerId(
                    task.getCollaborationId(), request.getAssignedFreelancerId())) {
                throw new RuntimeException("Freelancer is not a team member of this collaboration");
            }
            task.setAssignedFreelancerId(request.getAssignedFreelancerId());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            // Validate status transition and handle completion
            TaskStatus oldStatus = task.getStatus();
            TaskStatus newStatus = request.getStatus();
            
            // Auto-set completedAt when marking as DONE
            if (newStatus == TaskStatus.DONE && oldStatus != TaskStatus.DONE) {
                task.setCompletedAt(LocalDateTime.now());
                log.info("Task {} marked as DONE, setting completedAt", taskId);
            }
            
            // Clear completedAt if moving back from DONE
            if (oldStatus == TaskStatus.DONE && newStatus != TaskStatus.DONE) {
                task.setCompletedAt(null);
                log.info("Task {} moved from DONE, clearing completedAt", taskId);
            }
            
            task.setStatus(newStatus);
        }
        if (request.getDeadline() != null) {
            task.setDeadline(request.getDeadline());
        }
        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }
        if (request.getActualHours() != null) {
            task.setActualHours(request.getActualHours());
        }
        if (request.getAttachments() != null) {
            task.setAttachments(request.getAttachments());
        }
        if (request.getMilestoneId() != null) {
            // Validate milestone belongs to same collaboration
            if (request.getMilestoneId() > 0) {
                milestoneRepository.findById(request.getMilestoneId())
                    .ifPresent(milestone -> {
                        if (!milestone.getCollaborationId().equals(task.getCollaborationId())) {
                            throw new RuntimeException("Milestone does not belong to this collaboration");
                        }
                    });
            }
            task.setMilestoneId(request.getMilestoneId());
        }
        if (request.getSprintId() != null) {
            // Validate sprint belongs to same collaboration
            if (request.getSprintId() > 0) {
                sprintRepository.findById(request.getSprintId())
                    .ifPresent(sprint -> {
                        if (!sprint.getCollaborationId().equals(task.getCollaborationId())) {
                            throw new RuntimeException("Sprint does not belong to this collaboration");
                        }
                    });
            }
            task.setSprintId(request.getSprintId());
        }
        if (request.getDependsOnTaskIds() != null) {
            task.setDependsOnTaskIds(request.getDependsOnTaskIds());
        }
        if (request.getOrderIndex() != null) {
            task.setOrderIndex(request.getOrderIndex());
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", taskId);
        
        TaskDTO taskDTO = convertToDTO(updatedTask);
        sendWebSocketNotification("TASK_UPDATED", updatedTask.getCollaborationId(), taskDTO);
        
        return taskDTO;
    }

    public void deleteTask(Long taskId) {
        log.info("Deleting task: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        // Delete all subtasks
        List<Task> subtasks = taskRepository.findByParentTaskId(taskId);
        taskRepository.deleteAll(subtasks);
        
        // Delete all comments
        List<TaskComment> comments = taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        taskCommentRepository.deleteAll(comments);
        
        Long collaborationId = task.getCollaborationId();
        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", taskId);
        
        sendWebSocketNotification("TASK_DELETED", collaborationId, taskId);
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        return convertToDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByCollaboration(Long collaborationId) {
        List<Task> tasks = taskRepository.findByCollaborationIdOrderByOrderIndexAsc(collaborationId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(Long collaborationId, TaskStatus status) {
        List<Task> tasks = taskRepository.findByCollaborationIdAndStatus(collaborationId, status);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByFreelancer(Long freelancerId) {
        List<Task> tasks = taskRepository.findByAssignedFreelancerId(freelancerId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByMilestone(Long milestoneId) {
        List<Task> tasks = taskRepository.findByMilestoneId(milestoneId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksBySprint(Long sprintId) {
        List<Task> tasks = taskRepository.findBySprintId(sprintId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getSubtasks(Long parentTaskId) {
        List<Task> subtasks = taskRepository.findByParentTaskId(parentTaskId);
        return subtasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getOverdueTasks(Long collaborationId) {
        List<Task> tasks = taskRepository.findOverdueTasks(collaborationId, LocalDateTime.now());
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO moveTask(Long taskId, TaskStatus newStatus, Integer newOrderIndex) {
        log.info("Moving task {} to status {} with order {}", taskId, newStatus, newOrderIndex);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        TaskStatus oldStatus = task.getStatus();
        
        // Auto-set completedAt when marking as DONE
        if (newStatus == TaskStatus.DONE && oldStatus != TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
            log.info("Task {} marked as DONE via move, setting completedAt", taskId);
        }
        
        // Clear completedAt if moving back from DONE
        if (oldStatus == TaskStatus.DONE && newStatus != TaskStatus.DONE) {
            task.setCompletedAt(null);
            log.info("Task {} moved from DONE, clearing completedAt", taskId);
        }
        
        task.setStatus(newStatus);
        if (newOrderIndex != null) {
            task.setOrderIndex(newOrderIndex);
        }
        
        Task updatedTask = taskRepository.save(task);
        TaskDTO taskDTO = convertToDTO(updatedTask);
        sendWebSocketNotification("TASK_MOVED", updatedTask.getCollaborationId(), taskDTO);
        
        return taskDTO;
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = TaskDTO.builder()
                .id(task.getId())
                .collaborationId(task.getCollaborationId())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedFreelancerId(task.getAssignedFreelancerId())
                .priority(task.getPriority())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .attachments(task.getAttachments())
                .milestoneId(task.getMilestoneId())
                .parentTaskId(task.getParentTaskId())
                .sprintId(task.getSprintId())
                .dependsOnTaskIds(task.getDependsOnTaskIds())
                .orderIndex(task.getOrderIndex())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .build();
        
        // Get comment count
        List<TaskComment> comments = taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(task.getId());
        dto.setCommentCount(comments.size());
        
        // Get milestone name if exists
        if (task.getMilestoneId() != null) {
            milestoneRepository.findById(task.getMilestoneId())
                    .ifPresent(milestone -> dto.setMilestoneName(milestone.getTitle()));
        }
        
        // Get sprint name if exists
        if (task.getSprintId() != null) {
            sprintRepository.findById(task.getSprintId())
                    .ifPresent(sprint -> dto.setSprintName(sprint.getName()));
        }
        
        // Get subtasks
        List<Task> subtasks = taskRepository.findByParentTaskId(task.getId());
        if (!subtasks.isEmpty()) {
            dto.setSubtasks(subtasks.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
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
