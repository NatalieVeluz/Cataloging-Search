// ================= ANGULAR CORE =================
import { Component, OnInit, OnDestroy } from '@angular/core';

// Enables structural directives (*ngIf, *ngFor)
import { CommonModule } from '@angular/common';

// Required for ngModel (form inputs)
import { FormsModule } from '@angular/forms';

// Enables HTTP communication
import { HttpClientModule } from '@angular/common/http';

// RxJS subscription for observable cleanup
import { Subscription } from 'rxjs';

// ================= CUSTOM COMPONENTS & SERVICES =================
import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';
import { BookResult } from '../book-result/book-result';

/**
 * HomeComponent
 * -------------
 * Main dashboard of the Cataloging Search Platform.
 *
 * Responsibilities:
 * - Perform bibliographic search (Title, Author, ISBN)
 * - Display search results
 * - Synchronize pinned book state
 * - Handle manual cataloging submissions
 * - Manage observable subscriptions
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    BookResult
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit, OnDestroy {

  constructor(
    private bookService: BookService,
    private pinService: PinService
  ) {}

  // ================= SEARCH STATE =================

  /**
   * Stores user search input
   */
  searchQuery: string = '';

  /**
   * Selected search category
   */
  searchType: string = 'Title';

  /**
   * Available search categories
   */
  searchOptions: string[] = ['Title', 'Author', 'ISBN'];

  /**
   * Search results array
   */
  results: any[] = [];

  /**
   * Currently selected book (for details view)
   */
  selectedBook: any = null;

  /**
   * Controls loading spinner visibility
   */
  isLoading: boolean = false;

  /**
   * Tracks if user has performed at least one search
   * Used for conditional UI messages
   */
  hasSearched: boolean = false;

  /**
   * Subscription to pinned book observable
   * Needed for manual cleanup on destroy
   */
  private pinSubscription!: Subscription;

  // ================= LIFECYCLE =================

  /**
   * Subscribes to pinned book updates.
   * Ensures UI updates automatically when pin state changes.
   */
  ngOnInit(): void {
    this.pinSubscription = this.pinService.pinnedIsbns$
      .subscribe(() => {
        this.updatePinnedState();
      });
  }

  /**
   * Prevents memory leaks by unsubscribing from observable
   */
  ngOnDestroy(): void {
    if (this.pinSubscription) {
      this.pinSubscription.unsubscribe();
    }
  }

  // =====================================================
  // ================= SEARCH FUNCTION ===================
  // =====================================================

  /**
   * Performs search based on selected type and query.
   * Validates input before sending request to backend.
   */
  performSearch(): void {

    if (!this.searchQuery.trim()) {
      alert('Please enter a search value.');
      return;
    }

    // Reset state before new search
    this.results = [];
    this.isLoading = true;
    this.hasSearched = true;

    this.bookService.searchBooks(this.searchType, this.searchQuery)
      .subscribe({
        next: (data) => {

          // Normalize response (single object vs array)
          this.results = Array.isArray(data)
            ? data
            : (data ? [data] : []);

          this.updatePinnedState();
          this.isLoading = false;
        },
        error: () => {
          alert('Error connecting to backend.');
          this.isLoading = false;
        }
      });
  }

  // =====================================================
  // ================= PIN MANAGEMENT ====================
  // =====================================================

  /**
   * Updates pin state of all displayed books.
   * Ensures consistency between UI and PinService.
   */
  updatePinnedState(): void {
    this.results.forEach(book => {
      book.isPinned = this.pinService.isPinned(book.isbn);
    });

    if (this.selectedBook) {
      this.selectedBook.isPinned =
        this.pinService.isPinned(this.selectedBook.isbn);
    }
  }

  /**
   * Toggles pinned state of a specific book.
   */
  togglePin(book: any): void {
    this.pinService.togglePin(book.isbn);
  }

  // =====================================================
  // ================= BOOK DETAILS ======================
  // =====================================================

  /**
   * Opens detailed view of selected book.
   * Clones object to avoid accidental mutation.
   */
  openDetails(book: any): void {
    this.selectedBook = { ...book };
    this.selectedBook.isPinned =
      this.pinService.isPinned(book.isbn);
  }

  /**
   * Closes detailed view modal.
   */
  closeDetails(): void {
    this.selectedBook = null;
  }

  // =====================================================
  // ================= MANUAL CATALOGING =================
  // =====================================================

  /**
   * Data model for manual book entry.
   * Used when external APIs do not return desired results.
   */
  manualBook = {
    isbn: '',
    title: '',
    authors: '',
    lccn: '',
    cutterNumber: '',
    publisher: '',
    edition: '',
    publicationYear: ''
  };

  /**
   * Submits manually entered bibliographic record.
   * Handles duplicate ISBN conflict (HTTP 409).
   */
  submitManualCatalog(): void {

    if (!this.manualBook.isbn.trim()) {
      alert('ISBN is required.');
      return;
    }

    this.bookService.saveManualBook(this.manualBook)
      .subscribe({
        next: (savedBook) => {

          // Add newly saved book to top of results
          this.results = [savedBook, ...this.results];
          this.updatePinnedState();

          // Reset form after successful submission
          this.manualBook = {
            isbn: '',
            title: '',
            authors: '',
            lccn: '',
            cutterNumber: '',
            publisher: '',
            edition: '',
            publicationYear: ''
          };
        },

        error: (err) => {

          // Handle duplicate ISBN case
          if (err.status === 409) {

            this.bookService.getBookByIsbn(this.manualBook.isbn)
              .subscribe(existingBook => {

                this.results = [existingBook, ...this.results];
                this.updatePinnedState();

              });

          } else {
            alert('Failed to save manual entry.');
          }
        }
      });
  }
}