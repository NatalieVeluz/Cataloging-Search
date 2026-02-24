// ================= ANGULAR CORE =================
import { Component, OnInit } from '@angular/core';

// Enables structural directives (*ngIf, *ngFor)
import { CommonModule } from '@angular/common';

// Required for ngModel (search inputs)
import { FormsModule } from '@angular/forms';

// ================= CUSTOM COMPONENTS =================
import { BookResult } from '../book-result/book-result';

// ================= SERVICES =================
import { SearchLogsService, SearchLog } from '../services/search-logs.service';
import { PinService } from '../services/pin.service';

/**
 * SearchLogsComponent
 * -------------------
 * Displays search history records.
 *
 * Responsibilities:
 * - Retrieve search logs from backend
 * - Filter logs by title/author/ISBN
 * - Provide pagination
 * - Allow Admin to delete logs
 * - Sync pin state with PinService
 *
 * This component enhances accountability and system monitoring.
 */
@Component({
  selector: 'app-search-logs',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    BookResult
  ],
  templateUrl: './search-logs.html',
  styleUrls: ['./search-logs.css']
})
export class SearchLogsComponent implements OnInit {

  /**
   * Stores retrieved search logs
   */
  logs: SearchLog[] = [];

  /**
   * Search keyword input
   */
  keyword: string = '';

  /**
   * Search filter type
   */
  searchBy: string = 'title';

  /**
   * Loading state indicator
   */
  isLoading: boolean = false;

  // ================= ROLE-BASED ACCESS CONTROL =================

  /**
   * Logged-in user's email
   */
  userEmail: string = '';

  /**
   * Logged-in user's role
   */
  userRole: string = '';

  /**
   * Boolean flag for Admin privilege
   */
  isAdmin: boolean = false;

  // ================= PAGINATION =================

  /**
   * Current active page
   */
  currentPage: number = 1;

  /**
   * Number of items per page
   */
  itemsPerPage: number = 10;

  /**
   * Total number of pages
   */
  totalPages: number = 1;

  constructor(
    private searchLogsService: SearchLogsService,
    private pinService: PinService
  ) {}

  /**
   * Lifecycle hook
   *
   * Retrieves logged-in user data
   * and initializes search log loading.
   */
  ngOnInit(): void {

    // Retrieve authentication data from localStorage
    this.userEmail = localStorage.getItem('userEmail') || '';
    this.userRole = localStorage.getItem('userRole') || '';
    this.isAdmin = this.userRole === 'ADMIN';

    this.loadBooks();
  }

  // =====================================================
  // ================= LOAD SEARCH LOGS ==================
  // =====================================================

  /**
   * Fetches search logs from backend.
   * Applies search filter parameters.
   */
  loadBooks(): void {

    this.isLoading = true;

    this.searchLogsService
      .getBooks(this.keyword, this.searchBy)
      .subscribe({
        next: (data) => {

          this.logs = data;

          // Synchronize pinned state
          this.updatePinnedState();

          // Recalculate pagination
          this.totalPages = Math.ceil(this.logs.length / this.itemsPerPage);
          this.currentPage = 1;

          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading search logs:', err);
          this.isLoading = false;
        }
      });
  }

  // =====================================================
  // ================= PAGINATION ========================
  // =====================================================

  /**
   * Returns paginated subset of logs
   */
  get paginatedLogs(): SearchLog[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.logs.slice(start, start + this.itemsPerPage);
  }

  changePage(page: number): void {
    this.currentPage = page;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  // =====================================================
  // ================= SEARCH & FILTER ===================
  // =====================================================

  /**
   * Triggers filtered search request
   */
  search(): void {
    this.loadBooks();
  }

  /**
   * Clears search filters and reloads data
   */
  resetFilters(): void {
    this.keyword = '';
    this.searchBy = 'title';
    this.loadBooks();
  }

  // =====================================================
  // ================= PIN SYNCHRONIZATION ===============
  // =====================================================

  /**
   * Toggles pin state of a log book entry
   */
  togglePin(book: any): void {
    this.pinService.togglePin(book.isbn);
    this.updatePinnedState();
  }

  /**
   * Updates pinned status for each log
   */
  updatePinnedState(): void {
    this.logs.forEach(log => {
      log.isPinned = this.pinService.isPinned(log.isbn);
    });
  }

  // =====================================================
  // ================= DELETE OPERATIONS =================
  // =====================================================

  /**
   * Deletes a single search log.
   * Admin only.
   */
  deleteLog(log: SearchLog): void {

    if (!this.isAdmin) {
      alert('Access denied. Admin only.');
      return;
    }

    this.searchLogsService
      .deleteLog(log.id)
      .subscribe({
        next: () => this.loadBooks(),
        error: (err) => console.error('Delete failed:', err)
      });
  }

  /**
   * Deletes all search logs.
   * Admin only with confirmation.
   */
  deleteAll(): void {

    if (!this.isAdmin) {
      alert('Access denied. Admin only.');
      return;
    }

    if (!confirm('Are you sure you want to delete ALL search logs?')) {
      return;
    }

    this.searchLogsService
      .deleteAllLogs()
      .subscribe({
        next: () => this.loadBooks(),
        error: (err) => console.error('Delete all failed:', err)
      });
  }

  // =====================================================
  // ================= PERFORMANCE OPTIMIZATION ==========
  // =====================================================

  /**
   * trackBy function for better rendering performance.
   * Prevents unnecessary DOM re-rendering.
   */
  trackById(index: number, item: SearchLog): number {
    return item.id;
  }
}