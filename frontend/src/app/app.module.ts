import { NgModule, APP_INITIALIZER } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminProfileComponent } from './components/admin-profile/admin-profile.component';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { UsersComponent } from './components/users/users.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ProjectsComponent } from './components/projects/projects.component';
import { SettingsComponent } from './components/settings/settings.component';
import { PublicProfileComponent } from './components/public-profile/public-profile.component';
import { BrowseUsersComponent } from './components/browse-users/browse-users.component';
import { MessagingComponent } from './components/messaging/messaging.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { HeaderComponent } from './components/header/header.component';
import { ToastComponent } from './components/toast/toast.component';
import { CollaborationsComponent } from './components/collaborations/collaborations.component';
import { WorkspaceContainerComponent } from './components/workspace/workspace-container/workspace-container.component';
import { KanbanBoardComponent } from './components/workspace/kanban-board/kanban-board.component';
import { WorkspaceDashboardComponent } from './components/workspace/workspace-dashboard/workspace-dashboard.component';
import { TaskDetailsModalComponent } from './components/workspace/task-details-modal/task-details-modal.component';
import { CreateTaskModalComponent } from './components/workspace/create-task-modal/create-task-modal.component';
import { MilestoneListComponent } from './components/workspace/milestone-list/milestone-list.component';
import { SprintListComponent } from './components/workspace/sprint-list/sprint-list.component';
import { TeamManagementComponent } from './components/workspace/team-management/team-management.component';
import { TimesheetManagementComponent } from './components/workspace/timesheet-management/timesheet-management.component';

/**
 * Initialize Keycloak
 * This function is called before the app starts to initialize Keycloak
 */
function initializeKeycloak(keycloak: KeycloakService) {
  return () => {
    return keycloak.init({
      config: {
        url: 'http://localhost:9090',
        realm: 'projetpidev',
        clientId: 'freelance-client'
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false
      },
      loadUserProfileAtStartUp: true
    });
  };
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ProfileComponent,
    AdminProfileComponent,
    LoginComponent,
    SignupComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    UsersComponent,
    DashboardComponent,
    ProjectsComponent,
    SettingsComponent,
    PublicProfileComponent,
    BrowseUsersComponent,
    MessagingComponent,
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    HeaderComponent,
    ToastComponent,
    CollaborationsComponent,
    WorkspaceContainerComponent,
    KanbanBoardComponent,
    WorkspaceDashboardComponent,
    TaskDetailsModalComponent,
    CreateTaskModalComponent,
    MilestoneListComponent,
    SprintListComponent,
    TeamManagementComponent,
    TimesheetManagementComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    DragDropModule,
    KeycloakAngularModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    AuthGuard,
    AdminGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
