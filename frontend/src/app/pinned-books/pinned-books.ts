// ================= ANGULAR CORE =================
import { Component, OnInit } from '@angular/core';

// Enables Angular structural directives (*ngIf, *ngFor)
import { CommonModule } from '@angular/common';

// Required for ngModel (search inputs)
import { FormsModule } from '@angular/forms';

// ================= CUSTOM COMPONENTS =================
import { BookResult } from '../book-result/book-result';

// ================= SERVICES =================
import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';

/**
 * PinnedBooksComponent
 * --------------------
 * Displays and manages all pinned books.
 *
 * Responsibilities:
 * - Load pinned books from backend
 * - Provide filtering (title, author, ISBN)
 * - Provide pagination
 * - Reactively update list when pin state changes
 */
@Component({
  selector: 'app-pinned-books',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    BookResult
  ],
  templateUrl: './pinned-books.html',
  styleUrls: ['./pinned-books.css']
})
export class PinnedBooksComponent implements OnInit {

  /**
   * Stores full pinned book list from backend
   */
  pinnedBooks: any[] = [];

  /**
   * Stores filtered result after search
   */
  filteredBooks: any[] = [];

  /**
   * Search keyword input
   */
  keyword: string = '';

  /**
   * Search category selector
   * Options: title | author | isbn
   */
  searchBy: string = 'title';

  /**
   * Loading indicator for async operations
   */
  isLoading = false;

  // ================= PAGINATION STATE =================

  /**
   * Current active page
   */
  currentPage = 1;

  /**
   * Number of books per page
   */
  itemsPerPage = 10;

  constructor(
    private bookService: BookService,
    private pinService: PinService
  ) {}

  /**
   * Lifecycle hook
   *
   * 1. Load pinned books initially
   * 2. Subscribe to pin changes for reactive refresh
   */
  ngOnInit(): void {
    this.loadPinnedBooks();

    this.pinService.pinnedIsbns$.subscribe(() => {
      this.loadPinnedBooks();
    });
  }

  /**
   * Retrieves pinned books from backend.
   * Resets pagination and filtering state.
   */
  loadPinnedBooks(): void {
    this.isLoading = true;

    this.bookService.getPinnedBooks().subscribe({
      next: (data) => {

        this.pinnedBooks = data || [];

        // Initialize filtered list
        this.filteredBooks = [...this.pinnedBooks];

        // Reset to first page
        this.currentPage = 1;

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading pinned books:', err);
        this.isLoading = false;
      }
    });
  }

  // =====================================================
  // ================= SEARCH & FILTER ===================
  // =====================================================

  /**
   * Filters pinned books based on:
   * - Title
   * - Author
   * - ISBN
   */
  search(): void {

    const value = this.keyword.toLowerCase();

    this.filteredBooks = this.pinnedBooks.filter(book => {

      if (this.searchBy === 'title') {
        return book.title?.toLowerCase().includes(value);
      }

      if (this.searchBy === 'author') {
        return book.authors?.toLowerCase().includes(value);
      }

      if (this.searchBy === 'isbn') {
        return book.isbn?.includes(value);
      }

      return true;
    });

    // Reset pagination after filtering
    this.currentPage = 1;
  }

  /**
   * Clears search filters and restores full list.
   */
  resetFilters(): void {
    this.keyword = '';
    this.searchBy = 'title';
    this.filteredBooks = [...this.pinnedBooks];
    this.currentPage = 1;
  }

  /**
   * Toggles pin state of selected book.
   */
  togglePin(book: any): void {
    this.pinService.togglePin(book.isbn);
  }

  // =====================================================
  // ================= PAGINATION LOGIC ==================
  // =====================================================

  /**
   * Returns only books for current page.
   * Used by template for rendering paginated results.
   */
  get paginatedBooks(): any[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredBooks.slice(start, end);
  }

  /**
   * Calculates total number of pages.
   */
  get totalPages(): number {
    return Math.ceil(this.filteredBooks.length / this.itemsPerPage);
  }

  /**
   * Changes current page.
   */
  changePage(page: number): void {
    this.currentPage = page;
  }

  /**
   * Moves to next page if available.
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  /**
   * Moves to previous page if available.
   */
  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  /**
   * trackBy function for performance optimization.
   * Prevents unnecessary DOM re-rendering.
   */
  trackByIsbn(index: number, item: any): string {
    return item.isbn;
  }
}