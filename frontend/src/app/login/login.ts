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
 */
@Component({
  selector: 'app-login',     // Custom tag <app-login>
  standalone: true,          // Standalone Angular component
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  /**
   * Stores user email input
   */
  email: string = '';

  /**
   * Stores user password input
   */
  password: string = '';

  /**
   * Displays authentication error messages
   */
  errorMessage: string = '';

  /**
   * Controls password visibility toggle
   */
  showPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Toggles password visibility between hidden and visible
   * Improves user experience during login
   */
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Performs login authentication.
   *
   * Steps:
   * 1. Clear previous error message
   * 2. Call AuthService login method
   * 3. Store returned user details in localStorage
   * 4. Redirect to Home page
   * 5. Display error message if login fails
   */
  login(): void {

    // Clear any previous error message
    this.errorMessage = '';

    this.authService.login(this.email, this.password)
      .subscribe({
        next: (response: any) => {

          /**
           * Store authenticated user data
           * Used for:
           * - Role-based UI control
           * - Account page display
           * - Session persistence
           */
          localStorage.setItem('userId', response.userId);
          localStorage.setItem('userName', response.name);
          localStorage.setItem('userEmail', response.email);
          localStorage.setItem('userRole', response.role);

          // Navigate to Home dashboard after successful login
          this.router.navigate(['/home']);
        },
        error: (error) => {

          /**
           * Display backend error message
           * Fallback to generic message if none provided
           */
          this.errorMessage =
            error.error?.message || 'Login failed';
        }
      });
  }
}