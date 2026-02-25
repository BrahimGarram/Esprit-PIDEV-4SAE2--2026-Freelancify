import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { WorkspaceService, Task, TaskComment, TeamMember } from '../../../services/workspace.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-task-details-modal',
  templateUrl: './task-details-modal.component.html',
  styleUrls: ['./task-details-modal.component.css']
})
export class TaskDetailsModalComponent implements OnInit {
  @Input() task!: Task;
  @Output() close = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<void>();

  comments: TaskComment[] = [];
  teamMembers: TeamMember[] = [];
  newComment = '';
  isEditing = false;
  editedTask: Partial<Task> = {};
  loading = false;
  activeTimer: any = null;

  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  statuses = ['BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];

  constructor(
    private workspaceService: WorkspaceService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadComments();
    this.loadTeamMembers();
    this.checkActiveTimer();
    this.editedTask = { ...this.task };
  }

  loadComments(): void {
    this.workspaceService.getCommentsByTask(this.task.id!).subscribe({
      next: (comments) => {
        this.comments = comments;
      },
      error: (error) => {
        console.error('Error loading comments:', error);
      }
    });
  }

  loadTeamMembers(): void {
    this.workspaceService.getTeamMembers(this.task.collaborationId).subscribe({
      next: (members) => {
        this.teamMembers = members;
      },
      error: (error) => {
        console.error('Error loading team members:', error);
      }
    });
  }

  checkActiveTimer(): void {
    const currentUserId = this.getCurrentUserId();
    this.workspaceService.getActiveTimeLogs(currentUserId).subscribe({
      next: (timeLogs) => {
        this.activeTimer = timeLogs.find(log => log.taskId === this.task.id);
      },
      error: (error) => {
        console.error('Error checking timer:', error);
      }
    });
  }

  addComment(): void {
    if (!this.newComment.trim()) return;

    const comment: TaskComment = {
      taskId: this.task.id!,
      userId: this.getCurrentUserId(),
      content: this.newComment,
      mentionedUserIds: this.extractMentions(this.newComment)
    };

    this.workspaceService.createComment(comment).subscribe({
      next: (result) => {
        this.comments.unshift(result);
        this.newComment = '';
        this.toastService.showSuccess('Comment added');
      },
      error: (error) => {
        console.error('Error adding comment:', error);
        this.toastService.showError('Failed to add comment');
      }
    });
  }

  deleteComment(commentId: number): void {
    if (!confirm('Delete this comment?')) return;

    this.workspaceService.deleteComment(commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter(c => c.id !== commentId);
        this.toastService.showSuccess('Comment deleted');
      },
      error: (error) => {
        console.error('Error deleting comment:', error);
        this.toastService.showError('Failed to delete comment');
      }
    });
  }

  extractMentions(text: string): number[] {
    // Extract @mentions from text (simplified)
    const mentions: number[] = [];
    const regex = /@(\w+)/g;
    let match;
    while ((match = regex.exec(text)) !== null) {
      // In real implementation, map username to userId
      // mentions.push(userId);
    }
    return mentions;
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      this.editedTask = { ...this.task };
    }
  }

  saveChanges(): void {
    this.loading = true;
    this.workspaceService.updateTask(this.task.id!, this.editedTask).subscribe({
      next: (updated) => {
        Object.assign(this.task, updated);
        this.isEditing = false;
        this.loading = false;
        this.toastService.showSuccess('Task updated');
        this.taskUpdated.emit();
      },
      error: (error) => {
        console.error('Error updating task:', error);
        this.toastService.showError('Failed to update task');
        this.loading = false;
      }
    });
  }

  deleteTask(): void {
    if (!confirm('Are you sure you want to delete this task?')) return;

    this.workspaceService.deleteTask(this.task.id!).subscribe({
      next: () => {
        this.toastService.showSuccess('Task deleted');
        this.taskUpdated.emit();
        this.close.emit();
      },
      error: (error) => {
        console.error('Error deleting task:', error);
        this.toastService.showError('Failed to delete task');
      }
    });
  }

  startTimer(): void {
    const userId = this.getCurrentUserId();
    this.workspaceService.startTimer(this.task.id!, userId).subscribe({
      next: (timeLog) => {
        this.activeTimer = timeLog;
        this.toastService.showSuccess('Timer started');
      },
      error: (error) => {
        console.error('Error starting timer:', error);
        this.toastService.showError('Failed to start timer');
      }
    });
  }

  stopTimer(): void {
    if (!this.activeTimer) return;

    this.workspaceService.stopTimer(this.activeTimer.id).subscribe({
      next: () => {
        this.activeTimer = null;
        this.toastService.showSuccess('Timer stopped');
        this.taskUpdated.emit();
      },
      error: (error) => {
        console.error('Error stopping timer:', error);
        this.toastService.showError('Failed to stop timer');
      }
    });
  }

  getTimerDuration(): string {
    if (!this.activeTimer) return '00:00:00';
    
    const start = new Date(this.activeTimer.startTime);
    const now = new Date();
    const diff = now.getTime() - start.getTime();
    
    const hours = Math.floor(diff / 3600000);
    const minutes = Math.floor((diff % 3600000) / 60000);
    const seconds = Math.floor((diff % 60000) / 1000);
    
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }

  getCurrentUserId(): number {
    // Get from auth service or local storage
    return 1; // Placeholder
  }

  closeModal(): void {
    this.close.emit();
  }

  getPriorityColor(priority: string): string {
    const colors: { [key: string]: string } = {
      'LOW': 'bg-blue-100 text-blue-800',
      'MEDIUM': 'bg-yellow-100 text-yellow-800',
      'HIGH': 'bg-orange-100 text-orange-800',
      'CRITICAL': 'bg-red-100 text-red-800'
    };
    return colors[priority] || 'bg-gray-100 text-gray-800';
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'BACKLOG': 'bg-gray-100 text-gray-800',
      'TODO': 'bg-blue-100 text-blue-800',
      'IN_PROGRESS': 'bg-yellow-100 text-yellow-800',
      'REVIEW': 'bg-purple-100 text-purple-800',
      'DONE': 'bg-green-100 text-green-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  }
}
