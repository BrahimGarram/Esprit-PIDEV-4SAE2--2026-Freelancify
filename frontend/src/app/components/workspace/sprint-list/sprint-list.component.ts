import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkspaceService, Sprint } from '../../../services/workspace.service';

@Component({
  selector: 'app-sprint-list',
  templateUrl: './sprint-list.component.html',
  styleUrls: ['./sprint-list.component.css']
})
export class SprintListComponent implements OnInit {
  @Input() collaborationId!: number;
  @Output() sprintChanged = new EventEmitter<void>();

  sprints: Sprint[] = [];
  loading = false;
  showCreateForm = false;
  sprintForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private workspaceService: WorkspaceService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadSprints();
  }

  initForm(): void {
    this.sprintForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      goal: [''],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      durationWeeks: [2, [Validators.required, Validators.min(1)]]
    });
  }

  loadSprints(): void {
    this.loading = true;
    this.workspaceService.getSprints(this.collaborationId).subscribe({
      next: (sprints) => {
        this.sprints = sprints;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading sprints:', error);
        this.loading = false;
      }
    });
  }

  openCreateForm(): void {
    this.showCreateForm = true;
    this.sprintForm.reset({ durationWeeks: 2 });
  }

  onSubmit(): void {
    if (this.sprintForm.invalid) return;

    const sprint: Sprint = {
      ...this.sprintForm.value,
      collaborationId: this.collaborationId
    };

    this.workspaceService.createSprint(sprint).subscribe({
      next: () => {
        this.loadSprints();
        this.cancelForm();
        this.sprintChanged.emit(); // Notify parent to refresh dashboard
      },
      error: (error) => console.error('Error creating sprint:', error)
    });
  }

  startSprint(sprint: Sprint): void {
    if (!confirm(`Start sprint "${sprint.name}"?`)) return;

    this.workspaceService.startSprint(sprint.id!).subscribe({
      next: () => {
        this.loadSprints();
        this.sprintChanged.emit(); // Notify parent to refresh dashboard
      },
      error: (error) => console.error('Error starting sprint:', error)
    });
  }

  completeSprint(sprint: Sprint): void {
    if (!confirm(`Complete sprint "${sprint.name}"?`)) return;

    this.workspaceService.completeSprint(sprint.id!).subscribe({
      next: () => {
        this.loadSprints();
        this.sprintChanged.emit(); // Notify parent to refresh dashboard
      },
      error: (error) => console.error('Error completing sprint:', error)
    });
  }

  cancelForm(): void {
    this.showCreateForm = false;
    this.sprintForm.reset();
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'PLANNED': 'bg-gray-100 text-gray-800',
      'ACTIVE': 'bg-green-100 text-green-800',
      'COMPLETED': 'bg-blue-100 text-blue-800',
      'CANCELLED': 'bg-red-100 text-red-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  }

  getDaysRemaining(endDate: string): number {
    const end = new Date(endDate);
    const now = new Date();
    const diff = end.getTime() - now.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }
}
