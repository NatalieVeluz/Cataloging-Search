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

  logout() {
    // Clear authentication storage
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    // Redirect to login page
    this.router.navigate(['/login']);
  }
}
