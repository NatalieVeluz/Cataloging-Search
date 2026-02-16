import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SearchLog {
  id: number;
  isbn: string;
  title: string;
  authors: string;
  coverImageUrl: string;
  publicationYear: string;
  searchedAt: string;
  isPinned?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SearchLogsService {

  private baseUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) {}

  getBooks(
    keyword: string = '',
    searchBy: string = 'title'
  ): Observable<SearchLog[]> {

    let params = new HttpParams();

    if (keyword && keyword.trim() !== '') {
      params = params
        .set('keyword', keyword)
        .set('searchBy', searchBy);
    }

    return this.http.get<SearchLog[]>(
      `${this.baseUrl}/search-logs`,
      { params }
    );
  }

  deleteLog(id: number) {
    return this.http.delete(
      `${this.baseUrl}/search-logs/${id}`
    );
  }

  deleteAllLogs() {
    return this.http.delete(
      `${this.baseUrl}/search-logs`
    );
  }
}
