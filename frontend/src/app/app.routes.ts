// ================= ANGULAR ROUTING =================
import { Routes } from '@angular/router';

// ================= COMPONENT IMPORTS =================
import { HomeComponent } from './home/home';
import { PinnedBooksComponent } from './pinned-books/pinned-books';
import { SearchLogsComponent } from './search-logs/search-logs';
import { LoginComponent } from './login/login';

/**
 * Application Routes
 * ------------------
 * Defines navigation paths for the application.
 *
 * Responsibilities:
 * - Map URL paths to components
 * - Enable lazy loading where appropriate
 * - Provide fallback route handling
 */
export const routes: Routes = [

  /**
   * Default route
   * Redirects to Login page when accessing root URL
   */
  { path: '', component: LoginComponent },

  /**
   * Home Dashboard
   * Main search and cataloging page
   */
  { path: 'home', component: HomeComponent },

  /**
   * Pinned Books Page
   * Displays user's pinned book collection
   */
  { path: 'pinned-books', component: PinnedBooksComponent },

  /**
   * Search Logs Page
   * Displays audit history of searches
   */
  { path: 'search-logs', component: SearchLogsComponent },

  // =====================================================
  // Lazy-Loaded Standalone Component
  // =====================================================

  /**
   * Manage Account Page
   *
   * Uses lazy loading to:
   * - Improve initial load performance
   * - Reduce bundle size
   * - Load component only when needed
   */
  {
    path: 'manage-account',
    loadComponent: () =>
      import('./account/account')
        .then(m => m.AccountComponent)
  },

  /**
   * Wildcard Route
   * Redirects unknown URLs back to login
   */
  { path: '**', redirectTo: '' }
];