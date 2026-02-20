import {
  Component,
  Input,
  Output,
  EventEmitter,
  HostListener
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService } from '../services/book.service';
import { PinService } from '../services/pin.service';

@Component({
  selector: 'app-book-result',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book-result.html',
  styleUrls: ['./book-result.css']
})
export class BookResult {

  @Input() book: any;
  @Output() pin = new EventEmitter<any>();

  showModal = false;
  editMode = false;

  editableBook: any = null;

  constructor(
    private bookService: BookService,
    public pinService: PinService
  ) {}

  // ================= PIN =================

  togglePin(event: Event): void {
    event.stopPropagation();
    this.pin.emit(this.book);
  }

  // ================= OPEN =================

  openDetails(): void {
    this.showModal = true;

    // create editable copy
    this.editableBook = { ...this.book };

    this.editMode = false;
    document.body.style.overflow = 'hidden';
  }

  // ================= CLOSE =================

  closeDetails(): void {
    this.showModal = false;
    this.editMode = false;
    document.body.style.overflow = 'auto';
  }

  // ================= EDIT =================

  enableEdit(): void {
    this.editMode = true;
  }

  cancelEdit(): void {
    this.editMode = false;

    // reset copy
    this.editableBook = { ...this.book };
  }

  saveChanges(): void {

    this.bookService.updatePinnedBook(
      this.book.isbn,
      this.editableBook
    ).subscribe(() => {

      alert('Book updated successfully');

      // apply changes to original book
      Object.assign(this.book, this.editableBook);

      this.editMode = false;
    });
  }

  // ================= ESC CLOSE =================

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.showModal) {
      this.closeDetails();
    }
  }

  // ================= CLICK OUTSIDE CLOSE =================

  onOverlayClick(event: Event): void {
    if ((event.target as HTMLElement).classList.contains('details-overlay')) {
      this.closeDetails();
    }
  }
}