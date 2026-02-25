package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.CreateMilestoneRequest;
import com.freelance.collaborationservice.dto.MilestoneDTO;
import com.freelance.collaborationservice.model.Milestone;
import com.freelance.collaborationservice.model.MilestoneStatus;
import com.freelance.collaborationservice.model.Task;
import com.freelance.collaborationservice.model.TaskStatus;
import com.freelance.collaborationservice.repository.CollaborationRepository;
import com.freelance.collaborationservice.repository.MilestoneRepository;
import com.freelance.collaborationservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final CollaborationRepository collaborationRepository;
    private final TaskRepository taskRepository;

    public MilestoneDTO createMilestone(CreateMilestoneRequest request) {
        log.info("Creating milestone: {} for collaboration: {}", request.getTitle(), request.getCollaborationId());
        
        // Validate collaboration exists
        collaborationRepository.findById(request.getCollaborationId())
                .orElseThrow(() -> new RuntimeException("Collaboration not found with ID: " + request.getCollaborationId()));
        
        Milestone milestone = new Milestone();
        milestone.setCollaborationId(request.getCollaborationId());
        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0);
        milestone.setDueDate(request.getDueDate());
        milestone.setPaymentAmount(request.getPaymentAmount());
        milestone.setStatus(MilestoneStatus.NOT_STARTED);
        
        Milestone savedMilestone = milestoneRepository.save(milestone);
        log.info("Milestone created successfully with ID: {}", savedMilestone.getId());
        
        return convertToDTO(savedMilestone);
    }

    public MilestoneDTO updateMilestone(Long milestoneId, CreateMilestoneRequest request) {
        log.info("Updating milestone: {}", milestoneId);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        
        if (request.getTitle() != null) {
            milestone.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            milestone.setDescription(request.getDescription());
        }
        if (request.getOrderIndex() != null) {
            milestone.setOrderIndex(request.getOrderIndex());
        }
        if (request.getDueDate() != null) {
            milestone.setDueDate(request.getDueDate());
        }
        if (request.getPaymentAmount() != null) {
            milestone.setPaymentAmount(request.getPaymentAmount());
        }
        
        Milestone updatedMilestone = milestoneRepository.save(milestone);
        return convertToDTO(updatedMilestone);
    }

    public MilestoneDTO updateMilestoneStatus(Long milestoneId, MilestoneStatus status) {
        log.info("Updating milestone {} status to {}", milestoneId, status);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        
        milestone.setStatus(status);
        Milestone updatedMilestone = milestoneRepository.save(milestone);
        
        return convertToDTO(updatedMilestone);
    }

    public void deleteMilestone(Long milestoneId) {
        log.info("Deleting milestone: {}", milestoneId);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        
        // Unlink tasks from this milestone
        List<Task> tasks = taskRepository.findByMilestoneId(milestoneId);
        tasks.forEach(task -> {
            task.setMilestoneId(null);
            taskRepository.save(task);
        });
        
        milestoneRepository.delete(milestone);
        log.info("Milestone deleted successfully: {}", milestoneId);
    }

    @Transactional(readOnly = true)
    public MilestoneDTO getMilestoneById(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        return convertToDTO(milestone);
    }

    @Transactional(readOnly = true)
    public List<MilestoneDTO> getMilestonesByCollaboration(Long collaborationId) {
        List<Milestone> milestones = milestoneRepository.findByCollaborationIdOrderByOrderIndexAsc(collaborationId);
        return milestones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MilestoneDTO> getMilestonesByStatus(Long collaborationId, MilestoneStatus status) {
        List<Milestone> milestones = milestoneRepository.findByCollaborationIdAndStatus(collaborationId, status);
        return milestones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void autoUpdateMilestoneStatus(Long milestoneId) {
        log.info("Auto-updating milestone status: {}", milestoneId);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with ID: " + milestoneId));
        
        List<Task> tasks = taskRepository.findByMilestoneId(milestoneId);
        
        if (tasks.isEmpty()) {
            return;
        }
        
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        
        if (completedTasks == tasks.size()) {
            milestone.setStatus(MilestoneStatus.COMPLETED);
        } else if (completedTasks > 0) {
            milestone.setStatus(MilestoneStatus.IN_PROGRESS);
        } else {
            milestone.setStatus(MilestoneStatus.NOT_STARTED);
        }
        
        milestoneRepository.save(milestone);
    }

    private MilestoneDTO convertToDTO(Milestone milestone) {
        MilestoneDTO dto = MilestoneDTO.builder()
                .id(milestone.getId())
                .collaborationId(milestone.getCollaborationId())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .orderIndex(milestone.getOrderIndex())
                .dueDate(milestone.getDueDate())
                .paymentAmount(milestone.getPaymentAmount())
                .status(milestone.getStatus())
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .completedAt(milestone.getCompletedAt())
                .build();
        
        // Calculate task statistics
        List<Task> tasks = taskRepository.findByMilestoneId(milestone.getId());
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
        
        return dto;
    }
}
