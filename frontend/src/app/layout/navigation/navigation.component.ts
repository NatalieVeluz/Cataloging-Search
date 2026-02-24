// ================= ANGULAR CORE =================
import { Component } from '@angular/core';

// Enables structural directives like *ngIf, *ngFor
import { CommonModule } from '@angular/common';

// Router is used for navigation between application routes
import { Router, RouterModule } from '@angular/router';

/**
 * NavigationComponent
 * -------------------
 * Provides sidebar or top navigation functionality.
 *
 * Responsibilities:
 * - Route navigation between pages
 * - Handle logout process
 * - Clear authentication data
 *
 * This component ensures proper session termination
 * and secure redirection to login page.
 */
@Component({
  selector: 'app-navigation',     // Custom HTML tag <app-navigation>
  standalone: true,               // Standalone Angular component
  imports: [CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {

  /**
   * Inject Router for programmatic navigation
   */
  constructor(private router: Router) {}

  /**
   * Logs out the current user.
   * 
   * Steps:
   * 1. Remove authentication token from localStorage
   * 2. Remove stored user information
   * 3. Redirect user to login page
   * 
   * This ensures session data is cleared before navigation.
   */
  logout(): void {

    // Remove stored authentication token
    localStorage.removeItem('token');

    // Remove stored user information
    localStorage.removeItem('user');

    // Redirect to login route
    this.router.navigate(['/login']);
  }
}