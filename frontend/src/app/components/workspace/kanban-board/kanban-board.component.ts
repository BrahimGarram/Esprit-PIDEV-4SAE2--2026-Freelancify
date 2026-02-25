import { Component, OnInit, Input } from '@angular/core';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { WorkspaceService, Task } from '../../../services/workspace.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-kanban-board',
  templateUrl: './kanban-board.component.html',
  styleUrls: ['./kanban-board.component.css']
})
export class KanbanBoardComponent implements OnInit {
  @Input() collaborationId!: number;

  columns = [
    { id: 'BACKLOG', title: 'Backlog', tasks: [] as Task[] },
    { id: 'TODO', title: 'To Do', tasks: [] as Task[] },
    { id: 'IN_PROGRESS', title: 'In Progress', tasks: [] as Task[] },
    { id: 'REVIEW', title: 'Review', tasks: [] as Task[] },
    { id: 'DONE', title: 'Done', tasks: [] as Task[] }
  ];

  loading = false;
  selectedTask: Task | null = null;
  showTaskModal = false;
  showCreateModal = false;

  constructor(
    private workspaceService: WorkspaceService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.workspaceService.getTasksByCollaboration(this.collaborationId).subscribe({
      next: (tasks) => {
        // Clear all columns
        this.columns.forEach(col => col.tasks = []);
        
        // Distribute tasks to columns
        tasks.forEach(task => {
          const column = this.columns.find(col => col.id === task.status);
          if (column) {
            column.tasks.push(task);
          }
        });
        
        // Sort by orderIndex
        this.columns.forEach(col => {
          col.tasks.sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
        });
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
        this.toastService.showError('Failed to load tasks');
        this.loading = false;
      }
    });
  }

  drop(event: CdkDragDrop<Task[]>, columnId: string): void {
    if (event.previousContainer === event.container) {
      // Reorder within same column
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      this.updateTaskOrder(event.container.data);
    } else {
      // Move to different column
      const task = event.previousContainer.data[event.previousIndex];
      
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      
      // Update task status on backend
      this.workspaceService.moveTask(task.id!, columnId, event.currentIndex).subscribe({
        next: () => {
          this.toastService.showSuccess('Task moved successfully');
          this.updateTaskOrder(event.container.data);
        },
        error: (error) => {
          console.error('Error moving task:', error);
          this.toastService.showError('Failed to move task');
          // Revert the move
          transferArrayItem(
            event.container.data,
            event.previousContainer.data,
            event.currentIndex,
            event.previousIndex
          );
        }
      });
    }
  }

  updateTaskOrder(tasks: Task[]): void {
    tasks.forEach((task, index) => {
      if (task.orderIndex !== index) {
        this.workspaceService.updateTask(task.id!, { orderIndex: index }).subscribe();
      }
    });
  }

  openTaskDetails(task: Task): void {
    this.selectedTask = task;
    this.showTaskModal = true;
  }

  closeTaskModal(): void {
    this.showTaskModal = false;
    this.selectedTask = null;
  }

  openCreateModal(): void {
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  onTaskCreated(): void {
    this.closeCreateModal();
    this.loadTasks();
  }

  onTaskUpdated(): void {
    this.closeTaskModal();
    this.loadTasks();
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

  getColumnColor(columnId: string): string {
    const colors: { [key: string]: string } = {
      'BACKLOG': 'bg-gray-50',
      'TODO': 'bg-blue-50',
      'IN_PROGRESS': 'bg-yellow-50',
      'REVIEW': 'bg-purple-50',
      'DONE': 'bg-green-50'
    };
    return colors[columnId] || 'bg-gray-50';
  }

  isOverdue(deadline: string): boolean {
    if (!deadline) return false;
    return new Date(deadline) < new Date();
  }

  getConnectedLists(): string[] {
    return this.columns.map(c => c.id);
  }
}
