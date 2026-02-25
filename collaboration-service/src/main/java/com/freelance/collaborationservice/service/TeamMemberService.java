package com.freelance.collaborationservice.service;

import com.freelance.collaborationservice.dto.AddTeamMemberRequest;
import com.freelance.collaborationservice.dto.TeamMemberDTO;
import com.freelance.collaborationservice.model.ProjectRole;
import com.freelance.collaborationservice.model.TaskStatus;
import com.freelance.collaborationservice.model.TeamMember;
import com.freelance.collaborationservice.repository.CollaborationRepository;
import com.freelance.collaborationservice.repository.TaskRepository;
import com.freelance.collaborationservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final CollaborationRepository collaborationRepository;
    private final TaskRepository taskRepository;

    public TeamMemberDTO addTeamMember(AddTeamMemberRequest request) {
        log.info("Adding team member {} to collaboration {}", request.getFreelancerId(), request.getCollaborationId());
        
        // Validate collaboration exists
        collaborationRepository.findById(request.getCollaborationId())
                .orElseThrow(() -> new RuntimeException("Collaboration not found with ID: " + request.getCollaborationId()));
        
        // Check if already a member
        if (teamMemberRepository.existsByCollaborationIdAndFreelancerId(
                request.getCollaborationId(), request.getFreelancerId())) {
            throw new RuntimeException("Freelancer is already a team member");
        }
        
        TeamMember teamMember = new TeamMember();
        teamMember.setCollaborationId(request.getCollaborationId());
        teamMember.setFreelancerId(request.getFreelancerId());
        teamMember.setRole(request.getRole());
        teamMember.setIsActive(true);
        
        TeamMember savedMember = teamMemberRepository.save(teamMember);
        log.info("Team member added successfully with ID: {}", savedMember.getId());
        
        return convertToDTO(savedMember);
    }

    public TeamMemberDTO updateMemberRole(Long memberId, ProjectRole newRole) {
        log.info("Updating team member {} role to {}", memberId, newRole);
        
        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Team member not found with ID: " + memberId));
        
        member.setRole(newRole);
        TeamMember updatedMember = teamMemberRepository.save(member);
        
        return convertToDTO(updatedMember);
    }

    public void removeTeamMember(Long memberId) {
        log.info("Removing team member: {}", memberId);
        
        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Team member not found with ID: " + memberId));
        
        member.setIsActive(false);
        member.setLeftAt(LocalDateTime.now());
        teamMemberRepository.save(member);
        
        log.info("Team member removed successfully: {}", memberId);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDTO> getTeamMembers(Long collaborationId) {
        List<TeamMember> members = teamMemberRepository.findByCollaborationIdAndIsActiveTrue(collaborationId);
        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDTO> getFreelancerTeams(Long freelancerId) {
        List<TeamMember> members = teamMemberRepository.findByFreelancerId(freelancerId);
        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamMemberDTO getTeamMember(Long collaborationId, Long freelancerId) {
        TeamMember member = teamMemberRepository.findByCollaborationIdAndFreelancerId(collaborationId, freelancerId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));
        return convertToDTO(member);
    }

    private TeamMemberDTO convertToDTO(TeamMember member) {
        TeamMemberDTO dto = TeamMemberDTO.builder()
                .id(member.getId())
                .collaborationId(member.getCollaborationId())
                .freelancerId(member.getFreelancerId())
                .role(member.getRole())
                .isActive(member.getIsActive())
                .joinedAt(member.getJoinedAt())
                .leftAt(member.getLeftAt())
                .build();
        
        // Get task statistics for this specific freelancer
        List<com.freelance.collaborationservice.model.Task> assignedTasks = taskRepository
                .findByCollaborationIdAndAssignedFreelancerId(
                        member.getCollaborationId(), member.getFreelancerId());
        
        dto.setAssignedTasksCount(assignedTasks.size());
        
        // Count only completed tasks for this freelancer
        long completedTasks = assignedTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        dto.setCompletedTasksCount((int) completedTasks);
        
        return dto;
    }
}
