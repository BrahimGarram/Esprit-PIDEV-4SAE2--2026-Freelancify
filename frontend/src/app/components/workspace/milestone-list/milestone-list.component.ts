import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkspaceService, Milestone } from '../../../services/workspace.service';

@Component({
  selector: 'app-milestone-list',
  templateUrl: './milestone-list.component.html',
  styleUrls: ['./milestone-list.component.css']
})
export class MilestoneListComponent implements OnInit {
  @Input() collaborationId!: number;

  milestones: Milestone[] = [];
  loading = false;
  showCreateForm = false;
  editingMilestone: Milestone | null = null;
  milestoneForm!: FormGroup;

  statuses = ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];

  constructor(
    private fb: FormBuilder,
    private workspaceService: WorkspaceService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadMilestones();
  }

  initForm(): void {
    this.milestoneForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      dueDate: [''],
      paymentAmount: [0, [Validators.min(0)]],
      status: ['NOT_STARTED', Validators.required]
    });
  }

  loadMilestones(): void {
    this.loading = true;
    this.workspaceService.getMilestones(this.collaborationId).subscribe({
      next: (milestones) => {
        this.milestones = milestones;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading milestones:', error);
        this.loading = false;
      }
    });
  }

  openCreateForm(): void {
    this.showCreateForm = true;
    this.editingMilestone = null;
    this.milestoneForm.reset({ status: 'NOT_STARTED', paymentAmount: 0 });
  }

  editMilestone(milestone: Milestone): void {
    this.editingMilestone = milestone;
    this.showCreateForm = true;
    this.milestoneForm.patchValue({
      title: milestone.title,
      description: milestone.description,
      dueDate: milestone.dueDate,
      paymentAmount: milestone.paymentAmount,
      status: milestone.status
    });
  }

  onSubmit(): void {
    if (this.milestoneForm.invalid) return;

    const milestone: Milestone = {
      ...this.milestoneForm.value,
      collaborationId: this.collaborationId
    };

    if (this.editingMilestone) {
      this.workspaceService.updateMilestone(this.editingMilestone.id!, milestone).subscribe({
        next: () => {
          this.loadMilestones();
          this.cancelForm();
        },
        error: (error) => console.error('Error updating milestone:', error)
      });
    } else {
      this.workspaceService.createMilestone(milestone).subscribe({
        next: () => {
          this.loadMilestones();
          this.cancelForm();
        },
        error: (error) => console.error('Error creating milestone:', error)
      });
    }
  }

  deleteMilestone(milestone: Milestone): void {
    if (!confirm(`Delete milestone "${milestone.title}"?`)) return;

    this.workspaceService.deleteMilestone(milestone.id!).subscribe({
      next: () => this.loadMilestones(),
      error: (error) => console.error('Error deleting milestone:', error)
    });
  }

  cancelForm(): void {
    this.showCreateForm = false;
    this.editingMilestone = null;
    this.milestoneForm.reset();
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'NOT_STARTED': 'bg-gray-100 text-gray-800',
      'IN_PROGRESS': 'bg-blue-100 text-blue-800',
      'COMPLETED': 'bg-green-100 text-green-800',
      'CANCELLED': 'bg-red-100 text-red-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  }
}
