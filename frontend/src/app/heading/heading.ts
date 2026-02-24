// Angular core import for defining a component
import { Component } from '@angular/core';

/**
 * Heading Component
 * -----------------
 * Displays the official header section of the application.
 * This includes:
 * - Mapúa University logo
 * - System title (Mapúa Library)
 * - Platform name (Cataloging Search Platform)
 * 
 * This component is reusable and intended to appear
 * at the top of system pages for branding consistency.
 */
@Component({
  selector: 'app-heading',     // Custom HTML tag <app-heading>
  standalone: true,            // Standalone component (no NgModule required)
  imports: [],                 // No additional modules required
  templateUrl: './heading.html',
  styleUrl: './heading.css',   // Component-specific styling
})
export class Heading {
  // No logic required since this is a static branding component
}