package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.AddTeamMemberRequest;
import com.freelance.collaborationservice.dto.TeamMemberDTO;
import com.freelance.collaborationservice.model.ProjectRole;
import com.freelance.collaborationservice.service.TeamMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<TeamMemberDTO> addTeamMember(@Valid @RequestBody AddTeamMemberRequest request) {
        log.info("REST request to add team member {} to collaboration {}", 
                request.getFreelancerId(), request.getCollaborationId());
        TeamMemberDTO member = teamMemberService.addTeamMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PatchMapping("/{memberId}/role")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<TeamMemberDTO> updateMemberRole(
            @PathVariable Long memberId,
            @RequestParam ProjectRole role) {
        log.info("REST request to update team member {} role to {}", memberId, role);
        TeamMemberDTO member = teamMemberService.updateMemberRole(memberId, role);
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<Void> removeTeamMember(@PathVariable Long memberId) {
        log.info("REST request to remove team member: {}", memberId);
        teamMemberService.removeTeamMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/collaboration/{collaborationId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<TeamMemberDTO>> getTeamMembers(@PathVariable Long collaborationId) {
        log.info("REST request to get team members for collaboration: {}", collaborationId);
        List<TeamMemberDTO> members = teamMemberService.getTeamMembers(collaborationId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/freelancer/{freelancerId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<TeamMemberDTO>> getFreelancerTeams(@PathVariable Long freelancerId) {
        log.info("REST request to get teams for freelancer: {}", freelancerId);
        List<TeamMemberDTO> teams = teamMemberService.getFreelancerTeams(freelancerId);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/collaboration/{collaborationId}/freelancer/{freelancerId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<TeamMemberDTO> getTeamMember(
            @PathVariable Long collaborationId,
            @PathVariable Long freelancerId) {
        log.info("REST request to get team member for collaboration {} and freelancer {}", 
                collaborationId, freelancerId);
        TeamMemberDTO member = teamMemberService.getTeamMember(collaborationId, freelancerId);
        return ResponseEntity.ok(member);
    }
}
