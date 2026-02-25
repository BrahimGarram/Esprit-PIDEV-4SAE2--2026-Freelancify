import { Component, Input, OnInit } from '@angular/core';
import { WorkspaceService, TimeLog } from '../../../services/workspace.service';

@Component({
  selector: 'app-timesheet-management',
  templateUrl: './timesheet-management.component.html',
  styleUrls: ['./timesheet-management.component.css']
})
export class TimesheetManagementComponent implements OnInit {
  @Input() collaborationId!: number;
  @Input() freelancerId!: number;
  @Input() isManager: boolean = false;

  timeLogs: TimeLog[] = [];
  loading = false;
  filterStatus: string = 'ALL';

  constructor(private workspaceService: WorkspaceService) {}

  ngOnInit(): void {
    this.loadTimeLogs();
  }

  loadTimeLogs(): void {
    this.loading = true;
    this.workspaceService.getPendingTimeLogs(this.freelancerId).subscribe({
      next: (logs) => {
        this.timeLogs = logs;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading time logs:', error);
        this.loading = false;
      }
    });
  }

  approveTimeLog(log: TimeLog): void {
    if (!confirm(`Approve ${log.durationMinutes} minutes for ${log.taskTitle}?`)) return;

    this.workspaceService.approveTimeLog(log.id!).subscribe({
      next: () => this.loadTimeLogs(),
      error: (error) => console.error('Error approving time log:', error)
    });
  }

  rejectTimeLog(log: TimeLog): void {
    if (!confirm(`Reject ${log.durationMinutes} minutes for ${log.taskTitle}?`)) return;

    this.workspaceService.rejectTimeLog(log.id!).subscribe({
      next: () => this.loadTimeLogs(),
      error: (error) => console.error('Error rejecting time log:', error)
    });
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'PENDING': 'bg-yellow-100 text-yellow-800',
      'APPROVED': 'bg-green-100 text-green-800',
      'REJECTED': 'bg-red-100 text-red-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  }

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected'
    };
    return classes[status] || 'status-pending';
  }

  getAvatarColor(name: string | undefined): string {
    if (!name) return '#9ca3af';
    const colors = [
      '#ff6b35', '#4a90e2', '#50c878', '#9b59b6',
      '#e74c3c', '#f39c12', '#1abc9c', '#34495e',
      '#e91e63', '#00bcd4', '#8bc34a', '#ff5722'
    ];
    const index = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    return colors[index % colors.length];
  }

  formatDuration(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  }

  get filteredTimeLogs(): TimeLog[] {
    if (this.filterStatus === 'ALL') return this.timeLogs;
    return this.timeLogs.filter(log => log.status === this.filterStatus);
  }

  get totalHours(): number {
    return this.filteredTimeLogs.reduce((sum, log) => sum + (log.durationMinutes || 0), 0) / 60;
  }

  get pendingCount(): number {
    return this.timeLogs.filter(log => log.status === 'PENDING').length;
  }

  get approvedCount(): number {
    return this.timeLogs.filter(log => log.status === 'APPROVED').length;
  }
}
