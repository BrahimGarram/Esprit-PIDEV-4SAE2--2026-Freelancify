import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
import { CollaborationsComponent } from './components/collaborations/collaborations.component';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

const routes: Routes = [
  // User-facing routes
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'browse-users', component: BrowseUsersComponent, canActivate: [AuthGuard] },
  { path: 'messages', component: MessagingComponent, canActivate: [AuthGuard] },
  { path: 'messages/:userId', component: MessagingComponent, canActivate: [AuthGuard] },
  { path: 'user/:id', component: PublicProfileComponent, canActivate: [AuthGuard] },
  { path: 'user/username/:username', component: PublicProfileComponent, canActivate: [AuthGuard] },
  { path: 'projects', component: ProjectsComponent, canActivate: [AuthGuard] },
  { path: 'collaborations', component: CollaborationsComponent, canActivate: [AuthGuard] },
  
  // Admin routes
  { path: 'admin/dashboard', component: DashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/profile', component: AdminProfileComponent, canActivate: [AdminGuard] },
  { path: 'users', component: UsersComponent, canActivate: [AdminGuard] },
  { path: 'admin/users', component: UsersComponent, canActivate: [AdminGuard] },
  { path: 'admin/test', component: SettingsComponent, canActivate: [AdminGuard] }, // Using SettingsComponent as placeholder for Test
  { path: 'admin/projects', component: ProjectsComponent, canActivate: [AdminGuard] },
  { path: 'admin/collaborations', component: CollaborationsComponent, canActivate: [AdminGuard] },
  { path: 'admin/settings', component: SettingsComponent, canActivate: [AdminGuard] },
  
  // Fallback route
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
