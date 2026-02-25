package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.CreateTimeLogRequest;
import com.freelance.collaborationservice.dto.TimeLogDTO;
import com.freelance.collaborationservice.model.Task;
import com.freelance.collaborationservice.model.TimeLog;
import com.freelance.collaborationservice.model.TimeLogStatus;
import com.freelance.collaborationservice.repository.TaskRepository;
import com.freelance.collaborationservice.repository.TimeLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final TaskRepository taskRepository;

    public TimeLogDTO createTimeLog(CreateTimeLogRequest request) {
        log.info("Creating time log for task: {}", request.getTaskId());
        
        // Validate task exists
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + request.getTaskId()));
        
        // Validate freelancer is assigned to task (check both fields for compatibility)
        boolean isAssigned = task.getAssignedFreelancerId().equals(request.getFreelancerId()) ||
                            (task.getAssignedTo() != null && task.getAssignedTo().equals(request.getFreelancerId()));
        
        if (!isAssigned) {
            log.error("Freelancer {} is not assigned to task {}. Task assigned to: {} (assignedFreelancerId) or {} (assignedTo)", 
                     request.getFreelancerId(), request.getTaskId(), 
                     task.getAssignedFreelancerId(), task.getAssignedTo());
            throw new RuntimeException("Freelancer is not assigned to this task");
        }
        
        TimeLog timeLog = new TimeLog();
        timeLog.setTaskId(request.getTaskId());
        timeLog.setFreelancerId(request.getFreelancerId());
        timeLog.setStartTime(request.getStartTime());
        timeLog.setEndTime(request.getEndTime());
        timeLog.setDescription(request.getDescription());
        timeLog.setStatus(TimeLogStatus.PENDING);
        
        // Calculate duration if end time is provided
        if (request.getEndTime() != null) {
            Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
            timeLog.setDurationMinutes((int) duration.toMinutes());
        }
        
        TimeLog savedTimeLog = timeLogRepository.save(timeLog);
        log.info("Time log created successfully with ID: {}", savedTimeLog.getId());
        
        return convertToDTO(savedTimeLog);
    }

    public TimeLogDTO startTimer(Long taskId, Long freelancerId) {
        log.info("Starting timer for task {} by freelancer {}", taskId, freelancerId);
        
        // Check if there's already an active timer
        List<TimeLog> activeLogs = timeLogRepository.findActiveTimeLogs(freelancerId);
        if (!activeLogs.isEmpty()) {
            throw new RuntimeException("There is already an active timer. Stop it before starting a new one.");
        }
        
        CreateTimeLogRequest request = new CreateTimeLogRequest();
        request.setTaskId(taskId);
        request.setFreelancerId(freelancerId);
        request.setStartTime(LocalDateTime.now());
        
        return createTimeLog(request);
    }

    public TimeLogDTO stopTimer(Long timeLogId) {
        log.info("Stopping timer for time log: {}", timeLogId);
        
        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found with ID: " + timeLogId));
        
        if (timeLog.getEndTime() != null) {
            throw new RuntimeException("Timer is already stopped");
        }
        
        timeLog.setEndTime(LocalDateTime.now());
        TimeLog updatedTimeLog = timeLogRepository.save(timeLog);
        
        return convertToDTO(updatedTimeLog);
    }

    public TimeLogDTO updateTimeLog(Long timeLogId, CreateTimeLogRequest request) {
        log.info("Updating time log: {}", timeLogId);
        
        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found with ID: " + timeLogId));
        
        if (request.getStartTime() != null) {
            timeLog.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            timeLog.setEndTime(request.getEndTime());
        }
        if (request.getDescription() != null) {
            timeLog.setDescription(request.getDescription());
        }
        
        TimeLog updatedTimeLog = timeLogRepository.save(timeLog);
        return convertToDTO(updatedTimeLog);
    }

    public TimeLogDTO approveTimeLog(Long timeLogId) {
        log.info("Approving time log: {}", timeLogId);
        
        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found with ID: " + timeLogId));
        
        timeLog.setStatus(TimeLogStatus.APPROVED);
        TimeLog updatedTimeLog = timeLogRepository.save(timeLog);
        
        // Update task actual hours
        updateTaskActualHours(timeLog.getTaskId());
        
        return convertToDTO(updatedTimeLog);
    }

    public TimeLogDTO rejectTimeLog(Long timeLogId) {
        log.info("Rejecting time log: {}", timeLogId);
        
        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found with ID: " + timeLogId));
        
        timeLog.setStatus(TimeLogStatus.REJECTED);
        TimeLog updatedTimeLog = timeLogRepository.save(timeLog);
        
        return convertToDTO(updatedTimeLog);
    }

    public void deleteTimeLog(Long timeLogId) {
        log.info("Deleting time log: {}", timeLogId);
        
        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found with ID: " + timeLogId));
        
        Long taskId = timeLog.getTaskId();
        timeLogRepository.delete(timeLog);
        
        // Update task actual hours
        updateTaskActualHours(taskId);
        
        log.info("Time log deleted successfully: {}", timeLogId);
    }

    @Transactional(readOnly = true)
    public List<TimeLogDTO> getTimeLogsByTask(Long taskId) {
        List<TimeLog> timeLogs = timeLogRepository.findByTaskId(taskId);
        return timeLogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeLogDTO> getTimeLogsByFreelancer(Long freelancerId) {
        List<TimeLog> timeLogs = timeLogRepository.findByFreelancerId(freelancerId);
        return timeLogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeLogDTO> getPendingTimeLogs(Long freelancerId) {
        List<TimeLog> timeLogs = timeLogRepository.findByFreelancerIdAndStatus(freelancerId, TimeLogStatus.PENDING);
        return timeLogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeLogDTO> getActiveTimeLogs(Long freelancerId) {
        List<TimeLog> timeLogs = timeLogRepository.findActiveTimeLogs(freelancerId);
        return timeLogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getTotalApprovedHours(Long taskId) {
        Integer totalMinutes = timeLogRepository.getTotalApprovedMinutesForTask(taskId);
        return totalMinutes != null ? totalMinutes / 60 : 0;
    }

    private void updateTaskActualHours(Long taskId) {
        Integer totalMinutes = timeLogRepository.getTotalApprovedMinutesForTask(taskId);
        int totalHours = totalMinutes != null ? totalMinutes / 60 : 0;
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        task.setActualHours(totalHours);
        taskRepository.save(task);
    }

    private TimeLogDTO convertToDTO(TimeLog timeLog) {
        TimeLogDTO dto = TimeLogDTO.builder()
                .id(timeLog.getId())
                .taskId(timeLog.getTaskId())
                .freelancerId(timeLog.getFreelancerId())
                .startTime(timeLog.getStartTime())
                .endTime(timeLog.getEndTime())
                .durationMinutes(timeLog.getDurationMinutes())
                .description(timeLog.getDescription())
                .status(timeLog.getStatus())
                .createdAt(timeLog.getCreatedAt())
                .updatedAt(timeLog.getUpdatedAt())
                .build();
        
        // Get task title
        taskRepository.findById(timeLog.getTaskId())
                .ifPresent(task -> dto.setTaskTitle(task.getTitle()));
        
        return dto;
    }
}
