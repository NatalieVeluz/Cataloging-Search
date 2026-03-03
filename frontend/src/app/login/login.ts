// ================= ANGULAR CORE =================
import { Component } from '@angular/core';

// Enables structural directives (*ngIf) and common Angular features
import { CommonModule } from '@angular/common';

// Required for ngModel (two-way data binding in form inputs)
import { FormsModule } from '@angular/forms';

// Router allows navigation between pages
import { Router } from '@angular/router';

// Custom authentication service
import { AuthService } from '../services/auth';

/**
 * LoginComponent
 * --------------
 * Handles user authentication.
 *
 * Responsibilities:
 * - Capture user credentials (email & password)
 * - Send authentication request to backend
 * - Store authenticated user data in localStorage
 * - Redirect user to Home page upon success
 * - Display error message if authentication fails
 * - Navigate to Reset Password page
 * - Navigate to Register page
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  // ================= FORM FIELDS =================
  email: string = '';
  password: string = '';

  // ================= UI STATES =================
  errorMessage: string = '';
  showPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // ================= TOGGLE PASSWORD =================
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  // ================= LOGIN FUNCTION =================
  login(): void {

    // Clear previous error
    this.errorMessage = '';

    this.authService.login(this.email, this.password)
      .subscribe({
        next: (response: any) => {

          // Store authenticated user data
          localStorage.setItem('userId', response.userId);
          localStorage.setItem('userName', response.name);
          localStorage.setItem('userEmail', response.email);
          localStorage.setItem('userRole', response.role);

          // Redirect to Home
          this.router.navigate(['/home']);
        },
        error: (error) => {

          this.errorMessage =
            error.error?.message ||
            error.error ||
            'Login failed';
        }
      });
  }

  // ================= FORGOT PASSWORD =================
  goToReset(): void {
    this.router.navigate(['/reset-password']);
  }

  // ================= GO TO REGISTER =================
  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}