import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookService {

  private searchUrl = 'http://localhost:8080/api/search';
  private booksUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) {}

  // 🔥 Get logged-in user dynamically
  private getUserEmail(): string {
    const email = localStorage.getItem('userEmail');
    if (!email) {
      throw new Error('No logged-in user found.');
    }
    return email;
  }

  // ================= SEARCH =================

  searchBooks(type: string, query: string): Observable<any> {

    const userEmail = this.getUserEmail();

    if (type === 'ISBN') {
      return this.http.get(
        `${this.searchUrl}/isbn/${query}?userEmail=${userEmail}`
      );
    }

    if (type === 'Title') {
      return this.http.get(
        `${this.searchUrl}/title?value=${query}&userEmail=${userEmail}`
      );
    }

    if (type === 'Author') {
      return this.http.get(
        `${this.searchUrl}/author?value=${query}&userEmail=${userEmail}`
      );
    }

    throw new Error('Invalid search type');
  }

  // ================= MANUAL =================

  saveManualBook(book: any): Observable<any> {
    const userEmail = this.getUserEmail();
    return this.http.post(
      `${this.booksUrl}/manual?userEmail=${userEmail}`,
      book
    );
  }

  // ================= PIN =================

  pinBook(isbn: string): Observable<any> {
    const userEmail = this.getUserEmail();
    return this.http.post(
      `${this.booksUrl}/pin/${isbn}?userEmail=${userEmail}`,
      null
    );
  }

  // ================= UNPIN =================

  unpinBook(isbn: string): Observable<any> {
    const userEmail = this.getUserEmail();
    return this.http.delete(
      `${this.booksUrl}/unpin/${isbn}?userEmail=${userEmail}`
    );
  }

  // ================= GET PINNED =================

  getPinnedBooks(): Observable<any> {
    const userEmail = this.getUserEmail();
    return this.http.get(
      `${this.booksUrl}/pinned?userEmail=${userEmail}`
    );
  }

  // ================= UPDATE PINNED BOOK =================

  updatePinnedBook(isbn: string, book: any): Observable<any> {
    const userEmail = this.getUserEmail();
    return this.http.put(
      `${this.booksUrl}/${isbn}?userEmail=${userEmail}`,
      book
    );
  }

  // ================= GET BOOK DETAILS =================

  getBookByIsbn(isbn: string) {
    return this.http.get(
      `${this.booksUrl}/${isbn}`
    );
  }
}
