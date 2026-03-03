import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.css']
})
export class ResetPasswordComponent {

  // ================= FORM FIELDS =================
  email: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  // ================= UI STATES =================
  errorMessage: string = '';
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // ================= TOGGLE PASSWORD =================
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  // ================= PASSWORD RULE CHECKS =================

  hasMinLength(): boolean {
    return this.newPassword.length >= 12;
  }

  hasUppercase(): boolean {
    return /[A-Z]/.test(this.newPassword);
  }

  hasLowercase(): boolean {
    return /[a-z]/.test(this.newPassword);
  }

  hasNumber(): boolean {
    return /\d/.test(this.newPassword);
  }

  hasSymbol(): boolean {
    return /[\W_]/.test(this.newPassword);
  }

  isPasswordValid(): boolean {
    return this.hasMinLength()
        && this.hasUppercase()
        && this.hasLowercase()
        && this.hasNumber()
        && this.hasSymbol();
  }

  passwordsMatch(): boolean {
    return this.newPassword === this.confirmPassword;
  }

  // ================= RESET FUNCTION =================
  reset(): void {

    this.errorMessage = '';

    if (!this.isPasswordValid()) {
      this.errorMessage =
        "Password does not meet security requirements.";
      return;
    }

    if (!this.passwordsMatch()) {
      this.errorMessage =
        "Passwords do not match.";
      return;
    }

    this.authService.resetPassword(
      this.email,
      this.newPassword
    ).subscribe({
      next: () => this.router.navigate(['/']),
      error: (error) => {
        this.errorMessage =
          error.error?.message || "Reset failed";
      }
    });
  }

  // ================= NAVIGATION =================
  goBack(): void {
    this.router.navigate(['/']);
  }
}