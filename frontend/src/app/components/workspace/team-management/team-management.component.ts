import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkspaceService, TeamMember } from '../../../services/workspace.service';

@Component({
  selector: 'app-team-management',
  templateUrl: './team-management.component.html',
  styleUrls: ['./team-management.component.css']
})
export class TeamManagementComponent implements OnInit {
  @Input() collaborationId!: number;

  teamMembers: TeamMember[] = [];
  loading = false;
  showAddForm = false;
  memberForm!: FormGroup;

  roles = [
    'PROJECT_MANAGER',
    'FRONTEND_DEVELOPER',
    'BACKEND_DEVELOPER',
    'FULLSTACK_DEVELOPER',
    'DESIGNER',
    'QA_TESTER',
    'DEVOPS_ENGINEER',
    'BUSINESS_ANALYST',
    'TECHNICAL_WRITER',
    'OTHER'
  ];

  constructor(
    private fb: FormBuilder,
    private workspaceService: WorkspaceService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadTeamMembers();
  }

  initForm(): void {
    this.memberForm = this.fb.group({
      freelancerId: ['', Validators.required],
      freelancerName: ['', Validators.required],
      freelancerEmail: ['', [Validators.required, Validators.email]],
      role: ['FRONTEND_DEVELOPER', Validators.required]
    });
  }

  loadTeamMembers(): void {
    this.loading = true;
    this.workspaceService.getTeamMembers(this.collaborationId).subscribe({
      next: (members) => {
        this.teamMembers = members;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading team members:', error);
        this.loading = false;
      }
    });
  }

  openAddForm(): void {
    this.showAddForm = true;
    this.memberForm.reset({ role: 'FRONTEND_DEVELOPER' });
  }

  onSubmit(): void {
    if (this.memberForm.invalid) return;

    const member: TeamMember = {
      ...this.memberForm.value,
      collaborationId: this.collaborationId
    };

    this.workspaceService.addTeamMember(member).subscribe({
      next: () => {
        this.loadTeamMembers();
        this.cancelForm();
      },
      error: (error) => console.error('Error adding team member:', error)
    });
  }

  updateRole(member: TeamMember, newRole: string): void {
    this.workspaceService.updateMemberRole(member.id!, newRole).subscribe({
      next: () => this.loadTeamMembers(),
      error: (error) => console.error('Error updating role:', error)
    });
  }

  removeMember(member: TeamMember): void {
    if (!confirm(`Remove ${member.freelancerName} from the team?`)) return;

    this.workspaceService.removeTeamMember(member.id!).subscribe({
      next: () => this.loadTeamMembers(),
      error: (error) => console.error('Error removing member:', error)
    });
  }

  cancelForm(): void {
    this.showAddForm = false;
    this.memberForm.reset();
  }

  getRoleColor(role: string): string {
    const colors: { [key: string]: string } = {
      'PROJECT_MANAGER': 'bg-purple-100 text-purple-800',
      'FRONTEND_DEVELOPER': 'bg-blue-100 text-blue-800',
      'BACKEND_DEVELOPER': 'bg-green-100 text-green-800',
      'FULLSTACK_DEVELOPER': 'bg-indigo-100 text-indigo-800',
      'DESIGNER': 'bg-pink-100 text-pink-800',
      'QA_TESTER': 'bg-yellow-100 text-yellow-800',
      'DEVOPS_ENGINEER': 'bg-red-100 text-red-800',
      'BUSINESS_ANALYST': 'bg-teal-100 text-teal-800',
      'TECHNICAL_WRITER': 'bg-orange-100 text-orange-800',
      'OTHER': 'bg-gray-100 text-gray-800'
    };
    return colors[role] || 'bg-gray-100 text-gray-800';
  }

  formatRole(role: string): string {
    return role.replace(/_/g, ' ');
  }
}
