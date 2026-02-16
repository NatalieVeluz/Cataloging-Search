import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {
    this.errorMessage = '';

    this.authService.login(this.email, this.password)
      .subscribe({
        next: (response: any) => {

          console.log('Login successful:', response);

          // 🔥 SAVE USER DATA TO LOCAL STORAGE
          localStorage.setItem('userId', response.userId);
          localStorage.setItem('userName', response.name);
          localStorage.setItem('userEmail', response.email);
          localStorage.setItem('userRole', response.role);

          // Redirect to home
          this.router.navigate(['/home']);
        },
        error: (error) => {
          console.error('Login failed:', error);
          this.errorMessage = error.error?.message || 'Login failed';
        }
      });
  }
}
