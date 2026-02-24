// ================= ANGULAR CORE =================
import { ApplicationConfig } from '@angular/core';

// Provides routing configuration for standalone apps
import { provideRouter } from '@angular/router';

// Provides HttpClient globally without importing HttpClientModule
import { provideHttpClient } from '@angular/common/http';

// Application route definitions
import { routes } from './app.routes';

/**
 * Application Configuration
 * -------------------------
 * Configures global providers for the Angular application.
 *
 * Responsibilities:
 * - Initialize application routing
 * - Enable HTTP communication globally
 *
 * This replaces traditional AppModule configuration
 * in standalone Angular architecture.
 */
export const appConfig: ApplicationConfig = {
  providers: [

    /**
     * Registers application routes
     * Enables navigation between components
     */
    provideRouter(routes),

    /**
     * Enables HttpClient across the entire application
     * Required for API communication
     */
    provideHttpClient()
  ]
};