import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkspaceService, Task, TeamMember, Milestone, Sprint } from '../../../services/workspace.service';

@Component({
  selector: 'app-create-task-modal',
  templateUrl: './create-task-modal.component.html',
  styleUrls: ['./create-task-modal.component.css']
})
export class CreateTaskModalComponent implements OnInit {
  @Input() collaborationId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() taskCreated = new EventEmitter<void>();

  taskForm!: FormGroup;
  teamMembers: TeamMember[] = [];
  milestones: Milestone[] = [];
  sprints: Sprint[] = [];
  loading = false;

  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  statuses = ['BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];

  constructor(
    private fb: FormBuilder,
    private workspaceService: WorkspaceService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadTeamMembers();
    this.loadMilestones();
    this.loadSprints();
  }

  initForm(): void {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(300)]],
      description: [''],
      assignedFreelancerId: ['', Validators.required],
      priority: ['MEDIUM', Validators.required],
      status: ['TODO', Validators.required],
      estimatedHours: [0, [Validators.min(0)]],
      deadline: [''],
      milestoneId: [''],
      sprintId: [''],
      orderIndex: [0]
    });
  }

  loadTeamMembers(): void {
    this.workspaceService.getTeamMembers(this.collaborationId).subscribe({
      next: (members) => {
        this.teamMembers = members;
      },
      error: (error) => {
        console.error('Error loading team members:', error);
      }
    });
  }

  loadMilestones(): void {
    this.workspaceService.getMilestones(this.collaborationId).subscribe({
      next: (milestones) => {
        this.milestones = milestones;
      },
      error: (error) => {
        console.error('Error loading milestones:', error);
      }
    });
  }

  loadSprints(): void {
    this.workspaceService.getSprints(this.collaborationId).subscribe({
      next: (sprints) => {
        this.sprints = sprints.filter(s => s.status === 'ACTIVE' || s.status === 'PLANNED');
      },
      error: (error) => {
        console.error('Error loading sprints:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.taskForm.invalid) {
      this.markFormGroupTouched(this.taskForm);
      return;
    }

    this.loading = true;
    const task: Task = {
      ...this.taskForm.value,
      collaborationId: this.collaborationId
    };

    // Convert empty strings to null
    if (!task.milestoneId) task.milestoneId = undefined;
    if (!task.sprintId) task.sprintId = undefined;
    if (!task.deadline) task.deadline = undefined;

    this.workspaceService.createTask(task).subscribe({
      next: () => {
        console.log('Task created successfully');
        this.taskCreated.emit();
        this.closeModal();
      },
      error: (error) => {
        console.error('Error creating task:', error);
        alert('Failed to create task');
        this.loading = false;
      }
    });
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.taskForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.taskForm.get(fieldName);
    if (field?.errors) {
      if (field.errors['required']) return 'This field is required';
      if (field.errors['minlength']) return `Minimum length is ${field.errors['minlength'].requiredLength}`;
      if (field.errors['maxlength']) return `Maximum length is ${field.errors['maxlength'].requiredLength}`;
      if (field.errors['min']) return `Minimum value is ${field.errors['min'].min}`;
    }
    return '';
  }

  closeModal(): void {
    this.close.emit();
  }
}
