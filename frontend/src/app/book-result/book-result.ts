// ================= ANGULAR CORE IMPORTS =================
import {
  Component,
  Input,
  Output,
  EventEmitter,
  HostListener,
  OnInit
} from '@angular/core';

// Allows use of structural directives like *ngIf
import { CommonModule } from '@angular/common';

// Required for ngModel (two-way binding in edit mode)
import { FormsModule } from '@angular/forms';

// Service responsible for updating book records via backend API
import { BookService } from '../services/book.service';

// Service responsible for managing pinned books (client-side state)
import { PinService } from '../services/pin.service';

@Component({
  selector: 'app-book-result', // Custom tag for book card
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book-result.html',
  styleUrls: ['./book-result.css']
})
export class BookResult implements OnInit {

  /**
   * Receives book data from parent component
   * This represents a single book search result
   */
  @Input() book: any;

  /**
   * Emits pin event to parent component
   * Allows parent to manage pinned book collection
   */
  @Output() pin = new EventEmitter<any>();

  /**
   * Controls visibility of modal (book details popup)
   */
  showModal = false;

  /**
   * Enables/disables edit mode inside modal
   */
  editMode = false;

  /**
   * Temporary editable copy of book
   * Used to prevent modifying original data until Save is clicked
   */
  editableBook: any = null;

  // ================= ROLE-BASED ACCESS CONTROL =================

  /**
   * Stores current user's role retrieved from localStorage
   */
  userRole: string = '';

  /**
   * Boolean flag for Admin access
   * Only Admin can edit book records
   */
  isAdmin: boolean = false;

  constructor(
    private bookService: BookService,
    public pinService: PinService
  ) {}

  /**
   * Lifecycle hook
   * Initializes user role and determines admin privileges
   */
  ngOnInit(): void {
    this.userRole = localStorage.getItem('userRole') || '';
    this.isAdmin = this.userRole === 'ADMIN';
  }

  // ================= PIN FUNCTIONALITY =================

  /**
   * Toggles pin status of a book
   * Stops event propagation to prevent opening modal
   */
  togglePin(event: Event): void {
    event.stopPropagation();
    this.pin.emit(this.book);
  }

  // ================= MODAL OPEN =================

  /**
   * Opens book details modal
   * Creates shallow copy of book for editing
   * Disables background scrolling
   */
  openDetails(): void {
    this.showModal = true;

    // Clone book object to avoid directly mutating original
    this.editableBook = { ...this.book };

    this.editMode = false;
    document.body.style.overflow = 'hidden';
  }

  // ================= MODAL CLOSE =================

  /**
   * Closes modal and resets edit state
   * Restores page scrolling
   */
  closeDetails(): void {
    this.showModal = false;
    this.editMode = false;
    document.body.style.overflow = 'auto';
  }

  // ================= ENABLE EDIT =================

  /**
   * Enables edit mode
   * Access restricted to Admin role
   */
  enableEdit(): void {

    if (!this.isAdmin) {
      alert('Access denied. Admin only.');
      return;
    }

    this.editMode = true;
  }

  /**
   * Cancels edit mode
   * Restores original book data
   */
  cancelEdit(): void {
    this.editMode = false;
    this.editableBook = { ...this.book };
  }

  // ================= SAVE CHANGES =================

  /**
   * Saves updated book details to backend
   * Only Admin can perform update
   */
  saveChanges(): void {

    if (!this.isAdmin) {
      alert('Access denied. Admin only.');
      return;
    }

    this.bookService.updateBook(
      this.book.isbn,
      this.editableBook
    ).subscribe(() => {

      alert('Book updated successfully');

      // Update original book object after successful API call
      Object.assign(this.book, this.editableBook);

      this.editMode = false;
    });
  }

  // ================= ESC KEY CLOSE =================

  /**
   * Closes modal when Escape key is pressed
   */
  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.showModal) {
      this.closeDetails();
    }
  }

  // ================= CLICK OUTSIDE CLOSE =================

  /**
   * Closes modal when clicking outside modal content
   */
  onOverlayClick(event: Event): void {
    if ((event.target as HTMLElement).classList.contains('details-overlay')) {
      this.closeDetails();
    }
  }
}