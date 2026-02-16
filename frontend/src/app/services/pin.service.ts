import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BookService } from './book.service';

@Injectable({
  providedIn: 'root'
})
export class PinService {

  private pinnedIsbnsSubject = new BehaviorSubject<string[]>([]);
  pinnedIsbns$ = this.pinnedIsbnsSubject.asObservable();

  constructor(private bookService: BookService) {
    this.refreshPinned();
  }

  refreshPinned(): void {
    this.bookService.getPinnedBooks().subscribe(data => {
      const isbns = (data || []).map((b: any) => b.isbn);
      this.pinnedIsbnsSubject.next(isbns);
    });
  }

  isPinned(isbn: string): boolean {
    return this.pinnedIsbnsSubject.getValue().includes(isbn);
  }

  togglePin(isbn: string): void {

    const current = this.pinnedIsbnsSubject.getValue();
    const isPinned = current.includes(isbn);

    if (isPinned) {

      // 🔥 REMOVE LOCALLY FIRST
      const updated = current.filter(id => id !== isbn);
      this.pinnedIsbnsSubject.next(updated);

      // 🔥 THEN CALL BACKEND
      this.bookService.unpinBook(isbn).subscribe({
        error: () => this.refreshPinned()
      });

    } else {

      // 🔥 ADD LOCALLY FIRST
      const updated = [...current, isbn];
      this.pinnedIsbnsSubject.next(updated);

      // 🔥 THEN CALL BACKEND
      this.bookService.pinBook(isbn).subscribe({
        error: () => this.refreshPinned()
      });
    }
  }
}
