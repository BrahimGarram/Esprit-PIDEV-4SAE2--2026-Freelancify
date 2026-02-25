import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { WorkspaceService, WorkspaceStats } from '../../../services/workspace.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-workspace-dashboard',
  templateUrl: './workspace-dashboard.component.html',
  styleUrls: ['./workspace-dashboard.component.css']
})
export class WorkspaceDashboardComponent implements OnInit, OnChanges {
  @Input() collaborationId!: number;
  @Input() refreshTrigger: number = 0;

  stats: WorkspaceStats | null = null;
  teamWorkload: { [key: number]: number } = {};
  loading = false;

  // Chart data
  taskStatusChartData: any;
  taskPriorityChartData: any;
  progressChartData: any;

  constructor(
    private workspaceService: WorkspaceService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadTeamWorkload();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadStats();
      this.loadTeamWorkload();
    }
  }

  loadStats(): void {
    this.loading = true;
    this.workspaceService.getWorkspaceStats(this.collaborationId).subscribe({
      next: (stats) => {
        this.stats = stats;
        this.prepareChartData();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading stats:', error);
        this.toastService.showError('Failed to load workspace statistics');
        this.loading = false;
      }
    });
  }

  loadTeamWorkload(): void {
    this.workspaceService.getTeamWorkload(this.collaborationId).subscribe({
      next: (workload) => {
        this.teamWorkload = workload;
      },
      error: (error) => {
        console.error('Error loading team workload:', error);
      }
    });
  }

  prepareChartData(): void {
    if (!this.stats) return;

    // Task Status Chart
    this.taskStatusChartData = {
      labels: Object.keys(this.stats.tasksByStatus),
      datasets: [{
        data: Object.values(this.stats.tasksByStatus),
        backgroundColor: [
          '#9CA3AF', // BACKLOG - Gray
          '#3B82F6', // TODO - Blue
          '#F59E0B', // IN_PROGRESS - Yellow
          '#8B5CF6', // REVIEW - Purple
          '#10B981'  // DONE - Green
        ]
      }]
    };

    // Task Priority Chart
    this.taskPriorityChartData = {
      labels: Object.keys(this.stats.tasksByPriority),
      datasets: [{
        data: Object.values(this.stats.tasksByPriority),
        backgroundColor: [
          '#3B82F6', // LOW - Blue
          '#F59E0B', // MEDIUM - Yellow
          '#F97316', // HIGH - Orange
          '#EF4444'  // CRITICAL - Red
        ]
      }]
    };

    // Progress Chart
    this.progressChartData = {
      labels: ['Completed', 'In Progress', 'Remaining'],
      datasets: [{
        data: [
          this.stats.completedTasks,
          this.stats.inProgressTasks,
          this.stats.totalTasks - this.stats.completedTasks - this.stats.inProgressTasks
        ],
        backgroundColor: ['#10B981', '#F59E0B', '#E5E7EB']
      }]
    };
  }

  getProgressPercentage(): number {
    return this.stats?.progressPercentage || 0;
  }

  getBurnRateColor(): string {
    if (!this.stats) return 'text-gray-600';
    if (this.stats.burnRate <= 1) return 'text-green-600';
    if (this.stats.burnRate <= 1.2) return 'text-yellow-600';
    return 'text-red-600';
  }

  getOverdueTasksColor(): string {
    if (!this.stats) return 'text-gray-600';
    if (this.stats.overdueTasks === 0) return 'text-green-600';
    if (this.stats.overdueTasks <= 3) return 'text-yellow-600';
    return 'text-red-600';
  }
}
