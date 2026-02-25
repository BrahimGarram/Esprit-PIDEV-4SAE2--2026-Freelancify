import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-workspace-container',
  templateUrl: './workspace-container.component.html',
  styleUrls: ['./workspace-container.component.css']
})
export class WorkspaceContainerComponent implements OnInit {
  collaborationId!: number;
  activeTab: 'dashboard' | 'kanban' | 'milestones' | 'sprints' | 'team' | 'timesheets' = 'dashboard';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.collaborationId = +params['id'];
    });
  }

  setActiveTab(tab: 'dashboard' | 'kanban' | 'milestones' | 'sprints' | 'team' | 'timesheets'): void {
    this.activeTab = tab;
  }
}
