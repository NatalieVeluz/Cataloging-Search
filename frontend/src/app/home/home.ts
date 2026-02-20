import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Subscription } from 'rxjs';

import { NavigationComponent } from '../layout/navigation/navigation.component';
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
    NavigationComponent,
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

  searchQuery: string = '';
  searchType: string = 'Title';
  searchOptions: string[] = ['Title', 'Author', 'ISBN'];

  results: any[] = [];
  selectedBook: any = null;
  isLoading: boolean = false;
  hasSearched: boolean = false;   // 🔥 NEW

  private pinSubscription!: Subscription;

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

  // ================= SEARCH =================

  performSearch(): void {

    if (!this.searchQuery.trim()) {
      alert('Please enter a search value.');
      return;
    }

    this.results = [];          // clear old results
    this.isLoading = true;
    this.hasSearched = true;    // 🔥 mark that search was triggered

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

  // ================= PIN =================

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

  // ================= DETAILS =================

  openDetails(book: any): void {
    this.selectedBook = { ...book };
    this.selectedBook.isPinned =
      this.pinService.isPinned(book.isbn);
  }

  closeDetails(): void {
    this.selectedBook = null;
  }

  // ================= MANUAL =================

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
        },

        error: (err) => {

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
