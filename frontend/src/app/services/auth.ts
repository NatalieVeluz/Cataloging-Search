// ================= ANGULAR CORE =================
import { Injectable } from '@angular/core';

// Used for making HTTP requests to backend API
import { HttpClient } from '@angular/common/http';

// Observable allows asynchronous handling of HTTP responses
import { Observable } from 'rxjs';

/**
 * AuthService
 * -----------
 * Handles authentication-related API communication.
 *
 * Responsibilities:
 * - Send login credentials to backend
 * - Return authentication response as Observable
 * - Keep authentication logic separated from UI components
 *
 * This service acts as the communication layer between
 * the LoginComponent and the backend authentication controller.
 */
@Injectable({
  providedIn: 'root' // Makes service globally available
})
export class AuthService {

  /**
   * Backend authentication endpoint
   */
  private apiUrl = 'http://localhost:8080/api/auth/login';

  /**
   * Inject HttpClient for API communication
   */
  constructor(private http: HttpClient) {}

  /**
   * Sends login request to backend.
   *
   * @param email - User email
   * @param password - User password
   * @returns Observable containing authentication response
   *
   * Backend is responsible for:
   * - Validating credentials
   * - Returning user details
   * - Returning authentication token (if implemented)
   */
  login(email: string, password: string): Observable<any> {

    return this.http.post(this.apiUrl, {
      email: email,
      password: password
    });

  }
}