package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.CreateSprintRequest;
import com.freelance.collaborationservice.dto.SprintDTO;
import com.freelance.collaborationservice.model.Sprint;
import com.freelance.collaborationservice.model.SprintStatus;
import com.freelance.collaborationservice.model.Task;
import com.freelance.collaborationservice.model.TaskStatus;
import com.freelance.collaborationservice.repository.CollaborationRepository;
import com.freelance.collaborationservice.repository.SprintRepository;
import com.freelance.collaborationservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SprintService {

    private final SprintRepository sprintRepository;
    private final CollaborationRepository collaborationRepository;
    private final TaskRepository taskRepository;

    public SprintDTO createSprint(CreateSprintRequest request) {
        log.info("Creating sprint: {} for collaboration: {}", request.getName(), request.getCollaborationId());
        
        // Validate collaboration exists
        collaborationRepository.findById(request.getCollaborationId())
                .orElseThrow(() -> new RuntimeException("Collaboration not found with ID: " + request.getCollaborationId()));
        
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        
        Sprint sprint = new Sprint();
        sprint.setCollaborationId(request.getCollaborationId());
        sprint.setProjectId(request.getCollaborationId()); // Set project ID same as collaboration ID
        sprint.setCreatedBy(1L); // Default to user ID 1 (will be updated when we have proper auth context)
        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setDurationWeeks(request.getDurationWeeks() != null ? request.getDurationWeeks() : 2);
        sprint.setStatus(SprintStatus.PLANNED);
        
        Sprint savedSprint = sprintRepository.save(sprint);
        log.info("Sprint created successfully with ID: {}", savedSprint.getId());
        
        return convertToDTO(savedSprint);
    }

    public SprintDTO updateSprint(Long sprintId, CreateSprintRequest request) {
        log.info("Updating sprint: {}", sprintId);
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        
        if (request.getName() != null) {
            sprint.setName(request.getName());
        }
        if (request.getGoal() != null) {
            sprint.setGoal(request.getGoal());
        }
        if (request.getStartDate() != null) {
            sprint.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            sprint.setEndDate(request.getEndDate());
        }
        if (request.getDurationWeeks() != null) {
            sprint.setDurationWeeks(request.getDurationWeeks());
        }
        
        Sprint updatedSprint = sprintRepository.save(sprint);
        return convertToDTO(updatedSprint);
    }

    public SprintDTO updateSprintStatus(Long sprintId, SprintStatus status) {
        log.info("Updating sprint {} status to {}", sprintId, status);
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        
        sprint.setStatus(status);
        Sprint updatedSprint = sprintRepository.save(sprint);
        
        return convertToDTO(updatedSprint);
    }

    public SprintDTO startSprint(Long sprintId) {
        log.info("Starting sprint: {}", sprintId);
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        
        // Check if there's already an active sprint
        Optional<Sprint> activeSprint = sprintRepository.findActiveSprint(
                sprint.getCollaborationId(), LocalDateTime.now());
        
        if (activeSprint.isPresent() && !activeSprint.get().getId().equals(sprintId)) {
            throw new RuntimeException("There is already an active sprint. Complete it before starting a new one.");
        }
        
        sprint.setStatus(SprintStatus.ACTIVE);
        Sprint updatedSprint = sprintRepository.save(sprint);
        
        return convertToDTO(updatedSprint);
    }

    public SprintDTO completeSprint(Long sprintId) {
        log.info("Completing sprint: {}", sprintId);
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        
        sprint.setStatus(SprintStatus.COMPLETED);
        Sprint updatedSprint = sprintRepository.save(sprint);
        
        return convertToDTO(updatedSprint);
    }

    public void deleteSprint(Long sprintId) {
        log.info("Deleting sprint: {}", sprintId);
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        
        // Unlink tasks from this sprint
        List<Task> tasks = taskRepository.findBySprintId(sprintId);
        tasks.forEach(task -> {
            task.setSprintId(null);
            taskRepository.save(task);
        });
        
        sprintRepository.delete(sprint);
        log.info("Sprint deleted successfully: {}", sprintId);
    }

    @Transactional(readOnly = true)
    public SprintDTO getSprintById(Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        return convertToDTO(sprint);
    }

    @Transactional(readOnly = true)
    public List<SprintDTO> getSprintsByCollaboration(Long collaborationId) {
        List<Sprint> sprints = sprintRepository.findByCollaborationIdOrderByStartDateDesc(collaborationId);
        return sprints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SprintDTO> getActiveSprint(Long collaborationId) {
        Optional<Sprint> sprint = sprintRepository.findActiveSprint(collaborationId, LocalDateTime.now());
        return sprint.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<SprintDTO> getSprintsByStatus(Long collaborationId, SprintStatus status) {
        List<Sprint> sprints = sprintRepository.findByCollaborationIdAndStatus(collaborationId, status);
        return sprints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SprintDTO convertToDTO(Sprint sprint) {
        SprintDTO dto = SprintDTO.builder()
                .id(sprint.getId())
                .collaborationId(sprint.getCollaborationId())
                .name(sprint.getName())
                .goal(sprint.getGoal())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .durationWeeks(sprint.getDurationWeeks())
                .status(sprint.getStatus())
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();
        
        // Calculate task statistics
        List<Task> tasks = taskRepository.findBySprintId(sprint.getId());
        dto.setTotalTasks(tasks.size());
        
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        dto.setCompletedTasks((int) completedTasks);
        
        if (tasks.size() > 0) {
            dto.setProgressPercentage((int) ((completedTasks * 100) / tasks.size()));
        } else {
            dto.setProgressPercentage(0);
        }
        
        // Calculate hours
        int totalEstimated = tasks.stream()
                .mapToInt(Task::getEstimatedHours)
                .sum();
        dto.setTotalEstimatedHours(totalEstimated);
        
        int totalActual = tasks.stream()
                .mapToInt(Task::getActualHours)
                .sum();
        dto.setTotalActualHours(totalActual);
        
        return dto;
    }
}
