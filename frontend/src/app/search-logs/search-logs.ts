import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavigationComponent } from '../layout/navigation/navigation.component';
import { SearchLogsService, SearchLog } from '../services/search-logs.service';
import { BookResult } from '../book-result/book-result';
import { PinService } from '../services/pin.service';

@Component({
  selector: 'app-search-logs',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavigationComponent,
    BookResult
  ],
  templateUrl: './search-logs.html',
  styleUrls: ['./search-logs.css']
})
export class SearchLogsComponent implements OnInit {

  logs: SearchLog[] = [];

  keyword: string = '';
  searchBy: string = 'title';

  isLoading: boolean = false;

  // ================= PAGINATION =================
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalPages: number = 1;

  constructor(
    private searchLogsService: SearchLogsService,
    private pinService: PinService
  ) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  // ================= LOAD =================
  loadBooks(): void {

    this.isLoading = true;

    this.searchLogsService
      .getBooks(this.keyword, this.searchBy)
      .subscribe({
        next: (data) => {

          this.logs = data;
          this.updatePinnedState();

          // Pagination calculation
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

  // ================= PAGINATED VIEW =================
  get paginatedLogs(): SearchLog[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.logs.slice(start, start + this.itemsPerPage);
  }

  // ================= PAGE CONTROLS =================
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

  // ================= SEARCH =================
  search(): void {
    this.loadBooks();
  }

  resetFilters(): void {
    this.keyword = '';
    this.searchBy = 'title';
    this.loadBooks();
  }

  // ================= PIN =================
  togglePin(book: any): void {
    this.pinService.togglePin(book.isbn);
    this.updatePinnedState();
  }

  updatePinnedState(): void {
    this.logs.forEach(log => {
      log.isPinned = this.pinService.isPinned(log.isbn);
    });
  }

  // ================= DELETE SINGLE =================
  deleteLog(log: SearchLog): void {
    this.searchLogsService.deleteLog(log.id)
      .subscribe({
        next: () => this.loadBooks(),
        error: (err) => console.error('Delete failed:', err)
      });
  }

  // ================= DELETE ALL =================
  deleteAll(): void {

    if (!confirm('Are you sure you want to delete ALL search logs?')) {
      return;
    }

    this.searchLogsService.deleteAllLogs()
      .subscribe({
        next: () => this.loadBooks(),
        error: (err) => console.error('Delete all failed:', err)
      });
  }

  // ================= TRACK BY =================
  trackById(index: number, item: SearchLog): number {
    return item.id;
  }
}
