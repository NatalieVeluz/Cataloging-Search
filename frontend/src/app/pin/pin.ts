import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';
import { NavigationComponent } from '../layout/navigation/navigation.component';
import { BookResult } from '../book-result/book-result';

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

@Component({
  selector: 'app-pin',
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

  pinnedBooks: Book[] = [];

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
    this.bookService.getPinnedBooks().subscribe((data: Book[]) => {
      this.pinnedBooks = data || [];
    });
  }

  togglePin(book: Book): void {
    this.pinService.togglePin(book.isbn);
  }
}
