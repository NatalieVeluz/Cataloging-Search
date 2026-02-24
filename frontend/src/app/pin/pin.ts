// ================= ANGULAR CORE =================
import { Component, OnInit } from '@angular/core';

// Enables structural directives like *ngIf, *ngFor
import { CommonModule } from '@angular/common';

// Enables routing support inside template
import { RouterModule } from '@angular/router';

// ================= SERVICES =================
import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';

// ================= CUSTOM COMPONENTS =================
import { NavigationComponent } from '../layout/navigation/navigation.component';
import { BookResult } from '../book-result/book-result';

/**
 * Book Interface
 * --------------
 * Defines the structure of a Book object.
 * Ensures strong typing and better maintainability.
 */
interface Book {
  isbn: string;
  title?: string;
  authors?: string;
  coverImageUrl?: string;
  lccn?: string;
  cutterNumber?: string;
  summary?: string;
  contentNotes?: string;
  publicationYear?: string;
  edition?: string;
  publisher?: string;
}

/**
 * PinComponent
 * ------------
 * Displays all pinned books.
 *
 * Responsibilities:
 * - Retrieve pinned books from backend
 * - React to pin state changes
 * - Display pinned book list using BookResult component
 */
@Component({
  selector: 'app-pin',   // Custom tag <app-pin>
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NavigationComponent,
    BookResult
  ],
  templateUrl: './pin.html',
  styleUrls: ['./pin.css']
})
export class PinComponent implements OnInit {

  /**
   * Stores list of pinned books
   */
  pinnedBooks: Book[] = [];

  constructor(
    private bookService: BookService,
    private pinService: PinService
  ) {}

  /**
   * Lifecycle hook
   *
   * 1. Load pinned books initially
   * 2. Subscribe to pin state changes
   *    to refresh list dynamically
   */
  ngOnInit(): void {
    this.loadPinnedBooks();

    this.pinService.pinnedIsbns$.subscribe(() => {
      this.loadPinnedBooks();
    });
  }

  /**
   * Fetches pinned books from backend.
   * Ensures latest data is retrieved from database.
   */
  loadPinnedBooks(): void {
    this.bookService.getPinnedBooks()
      .subscribe((data: Book[]) => {
        this.pinnedBooks = data || [];
      });
  }

  /**
   * Toggles pin state of a book.
   * Uses PinService to maintain centralized pin logic.
   */
  togglePin(book: Book): void {
    this.pinService.togglePin(book.isbn);
  }
}