import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-workspace-container',
  templateUrl: './workspace-container.component.html',
  styleUrls: ['./workspace-container.component.css']
})
export class WorkspaceContainerComponent implements OnInit {
  collaborationId!: number;
  activeTab: 'dashboard' | 'kanban' | 'milestones' | 'sprints' | 'team' | 'timesheets' = 'dashboard';
  dashboardRefreshTrigger = 0;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.collaborationId = +params['id'];
    });
  }

  setActiveTab(tab: 'dashboard' | 'kanban' | 'milestones' | 'sprints' | 'team' | 'timesheets'): void {
    this.activeTab = tab;
  }

  onDataChanged(): void {
    // Increment trigger to force dashboard refresh
    this.dashboardRefreshTrigger++;
  }
}
