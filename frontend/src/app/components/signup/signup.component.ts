import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  
  isLoading = false;
  signUpError = '';
  signUpSuccess = '';

  signUpForm = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'USER',
    acceptTerms: false
  };

  constructor(
    private keycloakService: KeycloakService,
    private router: Router,
    private http: HttpClient,
    private toastService: ToastService
  ) {}
  
  async ngOnInit() {
    // If already logged in, check role and redirect accordingly
    let isLoggedIn = await this.keycloakService.isLoggedIn();
    
    // Also check localStorage for token (REST API login)
    if (!isLoggedIn) {
      const token = localStorage.getItem('kc-access-token');
      if (token) {
        isLoggedIn = true;
      }
    }
    
    if (isLoggedIn) {
      // Check roles from KeycloakService or localStorage
      let userRoles: string[] = [];
      try {
        userRoles = await this.keycloakService.getUserRoles();
      } catch (error) {
        // If KeycloakService fails, get roles from localStorage
        const rolesStr = localStorage.getItem('kc-roles');
        if (rolesStr) {
          userRoles = JSON.parse(rolesStr);
        }
      }
      
      // Check for ADMIN role (case-insensitive)
      const isAdmin = userRoles.some((role: string) => role.toUpperCase() === 'ADMIN');
      
      if (isAdmin) {
        console.log('SignupComponent ngOnInit: User is admin, redirecting to /admin/dashboard');
        window.location.href = '/admin/dashboard';
      } else {
        console.log('SignupComponent ngOnInit: User is not admin, redirecting to home');
        window.location.href = '/';
      }
    }
  }

  /**
   * Handle Sign Up form submission
   * Uses backend API to register user in Keycloak
   */
  async onSignUp() {
    this.isLoading = true;
    this.signUpError = '';
    this.signUpSuccess = '';

    // Validate form
    if (!this.signUpForm.username || !this.signUpForm.email || !this.signUpForm.password) {
      this.signUpError = 'Please fill in all required fields.';
      this.isLoading = false;
      return;
    }

    if (this.signUpForm.password !== this.signUpForm.confirmPassword) {
      this.signUpError = 'Passwords do not match.';
      this.isLoading = false;
      return;
    }

    if (this.signUpForm.password.length < 8) {
      this.signUpError = 'Password must be at least 8 characters long.';
      this.isLoading = false;
      return;
    }

    if (!this.signUpForm.acceptTerms) {
      this.signUpError = 'Please accept the Terms of Service and Privacy Policy.';
      this.isLoading = false;
      return;
    }

    try {
      // Register user via backend API
      const response: any = await this.http.post(
        'http://localhost:8081/api/users/register',
        {
          username: this.signUpForm.username,
          email: this.signUpForm.email,
          password: this.signUpForm.password,
          role: this.signUpForm.role
        },
        {
          headers: new HttpHeaders({
            'Content-Type': 'application/json'
          })
        }
      ).toPromise();

      this.toastService.success('Account created successfully! Redirecting to login...');
      this.signUpSuccess = 'Account created successfully! Redirecting to login...';
      
      // Clear form
      this.signUpForm = {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: 'USER',
        acceptTerms: false
      };
      
      // Redirect to login page after 2 seconds
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      
      this.isLoading = false;
    } catch (error: any) {
      console.error('Sign up error:', error);
      console.error('Error status:', error.status);
      console.error('Error error:', error.error);
      console.error('Error message:', error.message);
      console.error('Full error object:', JSON.stringify(error, null, 2));
      
      let errorMessage = 'Failed to create account. Please try again.';
      
      // Handle null or undefined error body
      if (error.error === null || error.error === undefined) {
        if (error.status === 400) {
          errorMessage = 'Invalid registration data. Please check your input and try again.';
        } else if (error.status === 409) {
          errorMessage = 'Username or email already exists. Please use different credentials.';
        } else if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please check if the backend is running.';
        } else if (error.message) {
          errorMessage = error.message;
        }
      } else if (error.error) {
        // Handle validation errors (Map of field errors from backend)
        if (typeof error.error === 'object' && !Array.isArray(error.error)) {
          const fieldErrors: string[] = [];
          
          // Check if it's a validation error map (e.g., {"username": "Username is required", "email": "Email must be valid"})
          if (error.error.username) fieldErrors.push(error.error.username);
          if (error.error.email) fieldErrors.push(error.error.email);
          if (error.error.password) fieldErrors.push(error.error.password);
          if (error.error.role) fieldErrors.push(error.error.role);
          
          // If we found field-specific errors, use them
          if (fieldErrors.length > 0) {
            errorMessage = fieldErrors.join('. ');
          } 
          // Otherwise check for standard error fields
          else if (error.error.message) {
            errorMessage = error.error.message;
          } else if (error.error.error) {
            errorMessage = error.error.error;
          }
        }
        // Handle string error messages
        else if (typeof error.error === 'string') {
          errorMessage = error.error;
        }
      } else if (error.message) {
        errorMessage = error.message;
      }

      // Never show raw HTML or Keycloak server messages to the user
      if (errorMessage && (errorMessage.includes('<') || errorMessage.includes('DOCTYPE') || errorMessage.includes('token was not found on this server'))) {
        errorMessage = 'Keycloak is unavailable. Ensure Keycloak is running on port 8080 and no other application is using that port.';
      }

      // Check for specific error cases
      if (error.status === 409 || errorMessage.toLowerCase().includes('already exists')) {
        errorMessage = 'Username or email already exists. Please use different credentials.';
      } else if (error.status === 400) {
        // If we still have a generic message for 400, try to be more specific
        if (errorMessage === 'Failed to create account. Please try again.') {
          errorMessage = 'Invalid registration data. Please check your input and try again.';
        }
      } else if (error.status === 0) {
        errorMessage = 'Cannot connect to server. Please check if the backend is running.';
      }
      
      this.signUpError = errorMessage;
      this.toastService.error(errorMessage);
      this.isLoading = false;
    }
  }
}
