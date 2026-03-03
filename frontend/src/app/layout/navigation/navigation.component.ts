// ================= ANGULAR CORE =================
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {

  constructor(private router: Router) {}

  /**
   * LOGOUT METHOD
   * -------------
   * Clears entire session and redirects safely to login.
   */
  logout(): void {

    // 🔥 Clear everything stored in browser
    localStorage.clear();
    sessionStorage.clear();

    // 🔥 Navigate to login
    // replaceUrl prevents user from going back using browser back button
    this.router.navigate(['/login'], { replaceUrl: true });
  }
}