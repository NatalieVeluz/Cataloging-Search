// ================= ANGULAR CORE =================
import { Component, OnInit } from '@angular/core';

// Enables structural directives like *ngIf
import { CommonModule } from '@angular/common';

// Used for making HTTP requests to backend
import { HttpClient, HttpClientModule } from '@angular/common/http';

/**
 * AccountComponent
 * ----------------
 * Displays the logged-in user's account information.
 *
 * Responsibilities:
 * - Retrieve stored userId from localStorage
 * - Fetch user details from backend API
 * - Display loading state
 * - Handle error cases
 *
 * Note:
 * NavigationComponent is NOT imported here because
 * it is handled globally by AppComponent.
 */
@Component({
  selector: 'app-account',   // Custom HTML tag <app-account>
  standalone: true,          // Standalone Angular component
  imports: [
    CommonModule,            // Enables *ngIf and other directives
    HttpClientModule         // Enables HTTP communication
  ],
  templateUrl: './account.html',
  styleUrls: ['./account.css']
})
export class AccountComponent implements OnInit {

  /**
   * Stores the logged-in user data
   */
  user: any = null;

  /**
   * Controls loading state indicator
   */
  loading: boolean = true;

  /**
   * Inject HttpClient for API communication
   */
  constructor(private http: HttpClient) {}

  /**
   * Lifecycle hook
   *
   * Steps:
   * 1. Retrieve userId from localStorage
   * 2. Fetch user details from backend
   * 3. Handle success and error states
   */
  ngOnInit(): void {

    // Retrieve stored userId from browser storage
    const userId = localStorage.getItem('userId');

    console.log('Stored userId:', userId);

    // If userId does not exist, stop loading
    if (!userId) {
      console.error('No userId found in localStorage');
      this.loading = false;
      return;
    }

    // Send GET request to backend to fetch user details
    this.http.get(`http://localhost:8080/api/users/${userId}`)
      .subscribe({

        // Successful response
        next: (data) => {
          console.log('User data received:', data);
          this.user = data;
          this.loading = false;
        },

        // Error response
        error: (err) => {
          console.error('Error fetching user:', err);
          this.loading = false;
        }
      });
  }
}