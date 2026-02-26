// ================= ANGULAR CORE =================
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Subscription } from 'rxjs';

// ================= CUSTOM COMPONENTS & SERVICES =================
import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';
import { BookResult } from '../book-result/book-result';

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

  searchQuery: string = '';
  searchType: string = 'Title';
  searchOptions: string[] = ['Title', 'Author', 'ISBN'];

  results: any[] = [];
  selectedBook: any = null;

  isLoading: boolean = false;   // 🔎 search spinner
  isSaving: boolean = false;    // 📝 manual spinner
  hasSearched: boolean = false;

  private pinSubscription!: Subscription;

  // ================= LIFECYCLE =================

  ngOnInit(): void {
    this.pinSubscription = this.pinService.pinnedIsbns$
      .subscribe(() => {
        this.updatePinnedState();
      });
  }

  ngOnDestroy(): void {
    if (this.pinSubscription) {
      this.pinSubscription.unsubscribe();
    }
  }

  // =====================================================
  // ================= SEARCH FUNCTION ===================
  // =====================================================

  performSearch(): void {

    if (!this.searchQuery.trim()) {
      alert('Please enter a search value.');
      return;
    }

    this.results = [];
    this.isLoading = true;
    this.hasSearched = true;

    this.bookService.searchBooks(this.searchType, this.searchQuery)
      .subscribe({
        next: (data) => {
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

  updatePinnedState(): void {
    this.results.forEach(book => {
      book.isPinned = this.pinService.isPinned(book.isbn);
    });

    if (this.selectedBook) {
      this.selectedBook.isPinned =
        this.pinService.isPinned(this.selectedBook.isbn);
    }
  }

  togglePin(book: any): void {
    this.pinService.togglePin(book.isbn);
  }

  // =====================================================
  // ================= BOOK DETAILS ======================
  // =====================================================

  openDetails(book: any): void {
    this.selectedBook = { ...book };
    this.selectedBook.isPinned =
      this.pinService.isPinned(book.isbn);
  }

  closeDetails(): void {
    this.selectedBook = null;
  }

  // =====================================================
  // ================= MANUAL CATALOGING =================
  // =====================================================

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

  submitManualCatalog(): void {

    if (!this.manualBook.isbn.trim()) {
      alert('ISBN is required.');
      return;
    }

    // 🔄 Show spinner in Search Results section
    this.isLoading = true;
    this.hasSearched = true;   // ensures results section activates

    this.bookService.saveManualBook(this.manualBook)
      .subscribe({
        next: (savedBook) => {

          this.results = [savedBook, ...this.results];
          this.updatePinnedState();

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

          this.isLoading = false;  // 🔄 Stop spinner
        },

        error: (err) => {

          if (err.status === 409) {

            this.bookService.getBookByIsbn(this.manualBook.isbn)
              .subscribe({
                next: (existingBook) => {
                  this.results = [existingBook, ...this.results];
                  this.updatePinnedState();
                  this.isLoading = false;
                },
                error: () => {
                  alert('Failed to retrieve existing book.');
                  this.isLoading = false;
                }
              });

          } else {
            alert('Failed to save manual entry.');
            this.isLoading = false;
          }
        }
      });
  }
}