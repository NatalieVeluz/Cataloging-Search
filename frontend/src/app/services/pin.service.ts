// ================= ANGULAR CORE =================
import { Injectable } from '@angular/core';

// BehaviorSubject enables reactive state management
import { BehaviorSubject } from 'rxjs';

// Service used for backend communication
import { BookService } from './book.service';

/**
 * PinService
 * ----------
 * Manages pinned book state across the application.
 *
 * Responsibilities:
 * - Maintain reactive list of pinned ISBNs
 * - Synchronize UI state with backend
 * - Provide instant UI feedback (optimistic update)
 * - Centralize pin/unpin logic
 *
 * This service ensures consistent pin state
 * across all components (Home, Search Logs, Pinned Books, etc.).
 */
@Injectable({
  providedIn: 'root'
})
export class PinService {

  /**
   * Stores list of pinned ISBNs using BehaviorSubject.
   * BehaviorSubject ensures:
   * - Current value is always available
   * - All subscribers receive latest state
   */
  private pinnedIsbnsSubject = new BehaviorSubject<string[]>([]);

  /**
   * Public observable for components to subscribe to.
   */
  pinnedIsbns$ = this.pinnedIsbnsSubject.asObservable();

  constructor(private bookService: BookService) {

    /**
     * Load initial pinned state when service initializes.
     */
    this.refreshPinned();
  }

  // =====================================================
  // REFRESH PINNED STATE FROM BACKEND
  // =====================================================

  /**
   * Fetches pinned books from backend
   * and updates local reactive state.
   */
  refreshPinned(): void {
    this.bookService.getPinnedBooks().subscribe(data => {

      // Extract ISBN values from returned book objects
      const isbns = (data || []).map((b: any) => b.isbn);

      // Update reactive state
      this.pinnedIsbnsSubject.next(isbns);
    });
  }

  // =====================================================
  // CHECK IF BOOK IS PINNED
  // =====================================================

  /**
   * Returns true if ISBN exists in pinned list.
   */
  isPinned(isbn: string): boolean {
    return this.pinnedIsbnsSubject
      .getValue()
      .includes(isbn);
  }

  // =====================================================
  // TOGGLE PIN STATE (OPTIMISTIC UPDATE)
  // =====================================================

  /**
   * Toggles pin status.
   *
   * Uses optimistic UI update strategy:
   * 1. Update UI immediately
   * 2. Send request to backend
   * 3. If backend fails, resync state
   */
  togglePin(isbn: string): void {

    const current = this.pinnedIsbnsSubject.getValue();
    const isPinned = current.includes(isbn);

    if (isPinned) {

      // ======================================
      // UNPIN (Optimistic Removal)
      // ======================================

      // Remove locally first for instant UI response
      const updated = current.filter(id => id !== isbn);
      this.pinnedIsbnsSubject.next(updated);

      // Then notify backend
      this.bookService.unpinBook(isbn).subscribe({
        error: () => this.refreshPinned() // rollback if error
      });

    } else {

      // ======================================
      // PIN (Optimistic Add)
      // ======================================

      // Add locally first for instant UI response
      const updated = [...current, isbn];
      this.pinnedIsbnsSubject.next(updated);

      // Then notify backend
      this.bookService.pinBook(isbn).subscribe({
        error: () => this.refreshPinned() // rollback if error
      });
    }
  }
}