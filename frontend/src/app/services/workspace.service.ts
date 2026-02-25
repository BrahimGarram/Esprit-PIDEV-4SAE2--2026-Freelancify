import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Task {
  id?: number;
  collaborationId: number;
  title: string;
  description?: string;
  assignedFreelancerId: number;
  assignedFreelancerName?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: 'BACKLOG' | 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE';
  deadline?: string;
  estimatedHours?: number;
  actualHours?: number;
  attachments?: string;
  milestoneId?: number;
  milestoneName?: string;
  parentTaskId?: number;
  sprintId?: number;
  sprintName?: string;
  dependsOnTaskIds?: number[];
  orderIndex?: number;
  createdAt?: string;
  updatedAt?: string;
  completedAt?: string;
  commentCount?: number;
  subtasks?: Task[];
}

export interface TaskComment {
  id?: number;
  taskId: number;
  userId: number;
  userName?: string;
  content: string;
  mentionedUserIds?: number[];
  attachments?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface TeamMember {
  id?: number;
  collaborationId: number;
  freelancerId: number;
  freelancerName?: string;
  freelancerEmail?: string;
  role: 'PROJECT_MANAGER' | 'FRONTEND_DEVELOPER' | 'BACKEND_DEVELOPER' | 
        'FULLSTACK_DEVELOPER' | 'DESIGNER' | 'QA_TESTER' | 'DEVOPS_ENGINEER' | 
        'BUSINESS_ANALYST' | 'TECHNICAL_WRITER' | 'OTHER';
  isActive?: boolean;
  joinedAt?: string;
  leftAt?: string;
  assignedTasksCount?: number;
  completedTasksCount?: number;
}

export interface Milestone {
  id?: number;
  collaborationId: number;
  title: string;
  description?: string;
  orderIndex?: number;
  dueDate?: string;
  paymentAmount?: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  createdAt?: string;
  updatedAt?: string;
  completedAt?: string;
  totalTasks?: number;
  completedTasks?: number;
  progressPercentage?: number;
}

export interface Sprint {
  id?: number;
  collaborationId: number;
  name: string;
  goal?: string;
  startDate: string;
  endDate: string;
  durationWeeks?: number;
  status: 'PLANNED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  createdAt?: string;
  updatedAt?: string;
  totalTasks?: number;
  completedTasks?: number;
  progressPercentage?: number;
  totalEstimatedHours?: number;
  totalActualHours?: number;
}

export interface TimeLog {
  id?: number;
  taskId: number;
  taskTitle?: string;
  freelancerId: number;
  freelancerName?: string;
  startTime: string;
  endTime?: string;
  durationMinutes?: number;
  description?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt?: string;
  updatedAt?: string;
}

export interface WorkspaceStats {
  collaborationId: number;
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  overdueTasks: number;
  progressPercentage: number;
  totalTeamMembers: number;
  totalMilestones: number;
  completedMilestones: number;
  totalSprints: number;
  activeSprints: number;
  tasksByStatus: { [key: string]: number };
  tasksByPriority: { [key: string]: number };
  totalEstimatedHours: number;
  totalActualHours: number;
  burnRate: number;
}

@Injectable({
  providedIn: 'root'
})
export class WorkspaceService {
  private apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // Task Management
  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/tasks`, task);
  }

  updateTask(taskId: number, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/tasks/${taskId}`, task);
  }

  deleteTask(taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tasks/${taskId}`);
  }

  getTask(taskId: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/tasks/${taskId}`);
  }

  getTasksByCollaboration(collaborationId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks/collaboration/${collaborationId}`);
  }

  getTasksByStatus(collaborationId: number, status: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks/collaboration/${collaborationId}/status/${status}`);
  }

  getTasksByFreelancer(freelancerId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks/freelancer/${freelancerId}`);
  }

  getOverdueTasks(collaborationId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks/collaboration/${collaborationId}/overdue`);
  }

  moveTask(taskId: number, status: string, orderIndex?: number): Observable<Task> {
    let url = `${this.apiUrl}/tasks/${taskId}/move?status=${status}`;
    if (orderIndex !== undefined) {
      url += `&orderIndex=${orderIndex}`;
    }
    return this.http.patch<Task>(url, {});
  }

  // Task Comments
  createComment(comment: TaskComment): Observable<TaskComment> {
    return this.http.post<TaskComment>(`${this.apiUrl}/task-comments`, comment);
  }

  getCommentsByTask(taskId: number): Observable<TaskComment[]> {
    return this.http.get<TaskComment[]>(`${this.apiUrl}/task-comments/task/${taskId}`);
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/task-comments/${commentId}`);
  }

  // Team Members
  addTeamMember(member: TeamMember): Observable<TeamMember> {
    return this.http.post<TeamMember>(`${this.apiUrl}/team-members`, member);
  }

  getTeamMembers(collaborationId: number): Observable<TeamMember[]> {
    return this.http.get<TeamMember[]>(`${this.apiUrl}/team-members/collaboration/${collaborationId}`);
  }

  updateMemberRole(memberId: number, role: string): Observable<TeamMember> {
    return this.http.patch<TeamMember>(`${this.apiUrl}/team-members/${memberId}/role?role=${role}`, {});
  }

  removeTeamMember(memberId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/team-members/${memberId}`);
  }

  // Milestones
  createMilestone(milestone: Milestone): Observable<Milestone> {
    return this.http.post<Milestone>(`${this.apiUrl}/milestones`, milestone);
  }

  getMilestones(collaborationId: number): Observable<Milestone[]> {
    return this.http.get<Milestone[]>(`${this.apiUrl}/milestones/collaboration/${collaborationId}`);
  }

  updateMilestone(milestoneId: number, milestone: Partial<Milestone>): Observable<Milestone> {
    return this.http.put<Milestone>(`${this.apiUrl}/milestones/${milestoneId}`, milestone);
  }

  deleteMilestone(milestoneId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/milestones/${milestoneId}`);
  }

  // Sprints
  createSprint(sprint: Sprint): Observable<Sprint> {
    return this.http.post<Sprint>(`${this.apiUrl}/sprints`, sprint);
  }

  getSprints(collaborationId: number): Observable<Sprint[]> {
    return this.http.get<Sprint[]>(`${this.apiUrl}/sprints/collaboration/${collaborationId}`);
  }

  getActiveSprint(collaborationId: number): Observable<Sprint> {
    return this.http.get<Sprint>(`${this.apiUrl}/sprints/collaboration/${collaborationId}/active`);
  }

  startSprint(sprintId: number): Observable<Sprint> {
    return this.http.post<Sprint>(`${this.apiUrl}/sprints/${sprintId}/start`, {});
  }

  completeSprint(sprintId: number): Observable<Sprint> {
    return this.http.post<Sprint>(`${this.apiUrl}/sprints/${sprintId}/complete`, {});
  }

  // Time Logs
  startTimer(taskId: number, freelancerId: number): Observable<TimeLog> {
    return this.http.post<TimeLog>(`${this.apiUrl}/time-logs/start?taskId=${taskId}&freelancerId=${freelancerId}`, {});
  }

  stopTimer(timeLogId: number): Observable<TimeLog> {
    return this.http.post<TimeLog>(`${this.apiUrl}/time-logs/${timeLogId}/stop`, {});
  }

  getActiveTimeLogs(freelancerId: number): Observable<TimeLog[]> {
    return this.http.get<TimeLog[]>(`${this.apiUrl}/time-logs/freelancer/${freelancerId}/active`);
  }

  getPendingTimeLogs(freelancerId: number): Observable<TimeLog[]> {
    return this.http.get<TimeLog[]>(`${this.apiUrl}/time-logs/freelancer/${freelancerId}/pending`);
  }

  approveTimeLog(timeLogId: number): Observable<TimeLog> {
    return this.http.post<TimeLog>(`${this.apiUrl}/time-logs/${timeLogId}/approve`, {});
  }

  rejectTimeLog(timeLogId: number): Observable<TimeLog> {
    return this.http.post<TimeLog>(`${this.apiUrl}/time-logs/${timeLogId}/reject`, {});
  }

  // Statistics
  getWorkspaceStats(collaborationId: number): Observable<WorkspaceStats> {
    return this.http.get<WorkspaceStats>(`${this.apiUrl}/workspace-stats/collaboration/${collaborationId}`);
  }

  getTeamWorkload(collaborationId: number): Observable<{ [key: number]: number }> {
    return this.http.get<{ [key: number]: number }>(`${this.apiUrl}/workspace-stats/collaboration/${collaborationId}/team-workload`);
  }

  getSprintBurndown(sprintId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/workspace-stats/sprint/${sprintId}/burndown`);
  }
}
