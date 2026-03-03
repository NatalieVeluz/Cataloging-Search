// ================= ANGULAR ROUTING =================
import { Routes } from '@angular/router';

// ================= COMPONENT IMPORTS =================
import { HomeComponent } from './home/home';
import { PinnedBooksComponent } from './pinned-books/pinned-books';
import { SearchLogsComponent } from './search-logs/search-logs';
import { LoginComponent } from './login/login';

export const routes: Routes = [

  // Default route → Login
  { path: '', component: LoginComponent },

  // Home Dashboard
  { path: 'home', component: HomeComponent },

  // Pinned Books
  { path: 'pinned-books', component: PinnedBooksComponent },

  // Search Logs
  { path: 'search-logs', component: SearchLogsComponent },

  // Manage Account (Lazy Loaded)
  {
    path: 'manage-account',
    loadComponent: () =>
      import('./account/account')
        .then(m => m.AccountComponent)
  },

  // 🔥 NEW RESET PASSWORD ROUTE (Lazy Loaded)
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./reset-password/reset-password')
        .then(m => m.ResetPasswordComponent)
  },

  {
    path: 'register',
    loadComponent: () =>
      import('./register/register')
        .then(m => m.RegisterComponent)
  },

  // Wildcard
  { path: '**', redirectTo: '' }
];