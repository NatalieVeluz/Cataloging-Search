import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { PinnedBooksComponent } from './pinned-books/pinned-books';
import { SearchLogsComponent } from './search-logs/search-logs';
import { LoginComponent } from './login/login';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'home', component: HomeComponent },
  { path: 'pinned-books', component: PinnedBooksComponent },
  { path: 'search-logs', component: SearchLogsComponent },

  // ✅ Standalone lazy load
  {
    path: 'manage-account',
    loadComponent: () =>
      import('./account/account').then(m => m.AccountComponent)
  },

  { path: '**', redirectTo: '' }
];
