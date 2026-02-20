import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavigationComponent } from '../layout/navigation/navigation.component';
import { BookResult } from '../book-result/book-result';
import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';

@Component({
  selector: 'app-pinned-books',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavigationComponent,
    BookResult
  ],
  templateUrl: './pinned-books.html',
  styleUrls: ['./pinned-books.css']
})
export class PinnedBooksComponent implements OnInit {

  pinnedBooks: any[] = [];
  filteredBooks: any[] = [];

  keyword: string = '';
  searchBy: string = 'title';

  isLoading = false;

  // ================= PAGINATION =================
  currentPage = 1;
  itemsPerPage = 10;

  constructor(
    private bookService: BookService,
    private pinService: PinService
  ) {}

  ngOnInit(): void {
    this.loadPinnedBooks();

    this.pinService.pinnedIsbns$.subscribe(() => {
      this.loadPinnedBooks();
    });
  }

  loadPinnedBooks(): void {
    this.isLoading = true;

    this.bookService.getPinnedBooks().subscribe({
      next: (data) => {
        this.pinnedBooks = data || [];
        this.filteredBooks = [...this.pinnedBooks];
        this.currentPage = 1; // reset page
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading pinned books:', err);
        this.isLoading = false;
      }
    });
  }

  // ================= SEARCH =================
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

    this.currentPage = 1;
  }

  resetFilters(): void {
    this.keyword = '';
    this.searchBy = 'title';
    this.filteredBooks = [...this.pinnedBooks];
    this.currentPage = 1;
  }

  togglePin(book: any): void {
    this.pinService.togglePin(book.isbn);
  }

  // ================= PAGINATION LOGIC =================

  get paginatedBooks(): any[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredBooks.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredBooks.length / this.itemsPerPage);
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

  trackByIsbn(index: number, item: any): string {
    return item.isbn;
  }
}