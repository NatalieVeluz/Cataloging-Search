import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationComponent } from '../layout/navigation/navigation.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, NavigationComponent, HttpClientModule],
  templateUrl: './account.html',
  styleUrls: ['./account.css']
})
export class AccountComponent implements OnInit {

  user: any = null;
  loading: boolean = true;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {

    const userId = localStorage.getItem('userId');
    console.log("Stored userId:", userId);

    if (!userId) {
      console.error("No userId found in localStorage");
      this.loading = false;
      return;
    }

    this.http.get(`http://localhost:8080/api/users/${userId}`)
      .subscribe({
        next: (data) => {
          console.log("User data received:", data);
          this.user = data;
          this.loading = false;
        },
        error: (err) => {
          console.error("Error fetching user:", err);
          this.loading = false;
        }
      });
  }
}
