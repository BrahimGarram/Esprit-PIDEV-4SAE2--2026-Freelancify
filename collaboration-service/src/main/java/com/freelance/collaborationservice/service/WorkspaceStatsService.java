package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.WorkspaceStatsDTO;
import com.freelance.collaborationservice.model.*;
import com.freelance.collaborationservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WorkspaceStatsService {

    private final TaskRepository taskRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MilestoneRepository milestoneRepository;
    private final SprintRepository sprintRepository;

    public WorkspaceStatsDTO getWorkspaceStats(Long collaborationId) {
        log.info("Calculating workspace statistics for collaboration: {}", collaborationId);
        
        // Get all tasks
        List<Task> allTasks = taskRepository.findByCollaborationIdOrderByOrderIndexAsc(collaborationId);
        
        // Calculate task statistics
        int totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        long inProgressTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
        
        // Get overdue tasks
        List<Task> overdueTasks = taskRepository.findOverdueTasks(collaborationId, LocalDateTime.now());
        
        // Calculate progress percentage
        int progressPercentage = totalTasks > 0 ? (int) ((completedTasks * 100) / totalTasks) : 0;
        
        // Get team statistics
        List<TeamMember> teamMembers = teamMemberRepository.findByCollaborationIdAndIsActiveTrue(collaborationId);
        
        // Get milestone statistics
        List<Milestone> allMilestones = milestoneRepository.findByCollaborationIdOrderByOrderIndexAsc(collaborationId);
        long completedMilestones = milestoneRepository.countByCollaborationIdAndStatus(
                collaborationId, MilestoneStatus.COMPLETED);
        
        // Get sprint statistics
        List<Sprint> allSprints = sprintRepository.findByCollaborationIdOrderByStartDateDesc(collaborationId);
        long activeSprints = sprintRepository.findByCollaborationIdAndStatus(
                collaborationId, SprintStatus.ACTIVE).size();
        
        // Calculate tasks by status
        Map<String, Integer> tasksByStatus = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            long count = allTasks.stream()
                    .filter(task -> task.getStatus() == status)
                    .count();
            tasksByStatus.put(status.name(), (int) count);
        }
        
        // Calculate tasks by priority
        Map<String, Integer> tasksByPriority = new HashMap<>();
        for (TaskPriority priority : TaskPriority.values()) {
            long count = allTasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .count();
            tasksByPriority.put(priority.name(), (int) count);
        }
        
        // Calculate hours
        int totalEstimatedHours = allTasks.stream()
                .mapToInt(Task::getEstimatedHours)
                .sum();
        
        int totalActualHours = allTasks.stream()
                .mapToInt(Task::getActualHours)
                .sum();
        
        // Calculate burn rate (actual hours / estimated hours)
        double burnRate = totalEstimatedHours > 0 
                ? (double) totalActualHours / totalEstimatedHours 
                : 0.0;
        
        return WorkspaceStatsDTO.builder()
                .collaborationId(collaborationId)
                .totalTasks(totalTasks)
                .completedTasks((int) completedTasks)
                .inProgressTasks((int) inProgressTasks)
                .overdueTasks(overdueTasks.size())
                .progressPercentage(progressPercentage)
                .totalTeamMembers(teamMembers.size())
                .totalMilestones(allMilestones.size())
                .completedMilestones((int) completedMilestones)
                .totalSprints(allSprints.size())
                .activeSprints((int) activeSprints)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .totalEstimatedHours(totalEstimatedHours)
                .totalActualHours(totalActualHours)
                .burnRate(burnRate)
                .build();
    }

    public Map<Long, Integer> getTeamWorkload(Long collaborationId) {
        log.info("Calculating team workload for collaboration: {}", collaborationId);
        
        List<TeamMember> teamMembers = teamMemberRepository.findByCollaborationIdAndIsActiveTrue(collaborationId);
        Map<Long, Integer> workload = new HashMap<>();
        
        for (TeamMember member : teamMembers) {
            List<Task> assignedTasks = taskRepository.findByCollaborationIdAndAssignedFreelancerId(
                    collaborationId, member.getFreelancerId());
            
            // Count only non-completed tasks
            long activeTasks = assignedTasks.stream()
                    .filter(task -> task.getStatus() != TaskStatus.DONE)
                    .count();
            
            workload.put(member.getFreelancerId(), (int) activeTasks);
        }
        
        return workload;
    }

    public Map<String, Object> getFreelancerStats(Long collaborationId, Long freelancerId) {
        log.info("Calculating freelancer statistics for collaboration: {} and freelancer: {}", 
                collaborationId, freelancerId);
        
        List<Task> assignedTasks = taskRepository.findByCollaborationIdAndAssignedFreelancerId(
                collaborationId, freelancerId);
        
        long completedTasks = assignedTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        
        long inProgressTasks = assignedTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
        
        List<Task> overdueTasks = assignedTasks.stream()
                .filter(task -> task.getDeadline() != null 
                        && task.getDeadline().isBefore(LocalDateTime.now())
                        && task.getStatus() != TaskStatus.DONE)
                .toList();
        
        int totalEstimatedHours = assignedTasks.stream()
                .mapToInt(Task::getEstimatedHours)
                .sum();
        
        int totalActualHours = assignedTasks.stream()
                .mapToInt(Task::getActualHours)
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", assignedTasks.size());
        stats.put("completedTasks", completedTasks);
        stats.put("inProgressTasks", inProgressTasks);
        stats.put("overdueTasks", overdueTasks.size());
        stats.put("totalEstimatedHours", totalEstimatedHours);
        stats.put("totalActualHours", totalActualHours);
        
        if (assignedTasks.size() > 0) {
            stats.put("completionRate", (completedTasks * 100) / assignedTasks.size());
        } else {
            stats.put("completionRate", 0);
        }
        
        return stats;
    }

    public Map<String, Object> getMilestoneProgress(Long collaborationId) {
        log.info("Calculating milestone progress for collaboration: {}", collaborationId);
        
        List<Milestone> milestones = milestoneRepository.findByCollaborationIdOrderByOrderIndexAsc(collaborationId);
        
        Map<String, Object> progress = new HashMap<>();
        
        for (Milestone milestone : milestones) {
            List<Task> milestoneTasks = taskRepository.findByMilestoneId(milestone.getId());
            
            long completedTasks = milestoneTasks.stream()
                    .filter(task -> task.getStatus() == TaskStatus.DONE)
                    .count();
            
            int progressPercentage = milestoneTasks.size() > 0 
                    ? (int) ((completedTasks * 100) / milestoneTasks.size()) 
                    : 0;
            
            Map<String, Object> milestoneData = new HashMap<>();
            milestoneData.put("title", milestone.getTitle());
            milestoneData.put("totalTasks", milestoneTasks.size());
            milestoneData.put("completedTasks", completedTasks);
            milestoneData.put("progressPercentage", progressPercentage);
            milestoneData.put("status", milestone.getStatus());
            
            progress.put("milestone_" + milestone.getId(), milestoneData);
        }
        
        return progress;
    }

    public Map<String, Object> getSprintBurndown(Long sprintId) {
        log.info("Calculating sprint burndown for sprint: {}", sprintId);
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found with ID: " + sprintId));
        
        List<Task> sprintTasks = taskRepository.findBySprintId(sprintId);
        
        int totalEstimatedHours = sprintTasks.stream()
                .mapToInt(Task::getEstimatedHours)
                .sum();
        
        int completedHours = sprintTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .mapToInt(Task::getEstimatedHours)
                .sum();
        
        int remainingHours = totalEstimatedHours - completedHours;
        
        Map<String, Object> burndown = new HashMap<>();
        burndown.put("sprintName", sprint.getName());
        burndown.put("totalEstimatedHours", totalEstimatedHours);
        burndown.put("completedHours", completedHours);
        burndown.put("remainingHours", remainingHours);
        burndown.put("totalTasks", sprintTasks.size());
        
        long completedTasks = sprintTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        burndown.put("completedTasks", completedTasks);
        
        if (totalEstimatedHours > 0) {
            burndown.put("progressPercentage", (completedHours * 100) / totalEstimatedHours);
        } else {
            burndown.put("progressPercentage", 0);
        }
        
        return burndown;
    }
}
