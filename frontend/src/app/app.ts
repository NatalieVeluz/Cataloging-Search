// ================= ANGULAR CORE =================
import { Component } from '@angular/core';

// Router tools
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';

// RxJS operator to filter router events
import { filter } from 'rxjs/operators';

// Angular structural directive
import { NgIf } from '@angular/common';

// ================= CUSTOM COMPONENTS =================
import { Heading } from './heading/heading';
import { NavigationComponent } from './layout/navigation/navigation.component';

/**
 * AppComponent (Root Component)
 * -----------------------------
 * Controls global layout structure of the application.
 *
 * Responsibilities:
 * - Render routed components using RouterOutlet
 * - Conditionally display layout elements (Header + Navigation)
 * - Detect route changes
 * - Hide layout on Login page
 *
 * This component acts as the main layout controller.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,        // Enables route rendering
    Heading,             // Top header component
    NavigationComponent, // Sidebar navigation
    NgIf                 // Enables conditional rendering
  ],
  templateUrl: './app.html'
})
export class AppComponent {

  /**
   * Controls visibility of layout components.
   * When false → Header & Navigation are hidden.
   */
  showLayout = true;

  constructor(private router: Router) {

    /**
     * Listen to router navigation events.
     * Only react when navigation ends.
     */
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {

        /**
         * Hide layout for:
         * - Root path (/)
         * - Login route
         *
         * Show layout for all authenticated routes.
         */
        if (event.url === '/' || event.url.includes('login')) {
          this.showLayout = false;
        } else {
          this.showLayout = true;
        }

      });

  }

}