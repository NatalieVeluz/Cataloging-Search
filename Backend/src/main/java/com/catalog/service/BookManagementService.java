package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import com.catalog.dto.SearchLogDTO;

import java.util.List;

public interface BookManagementService {

    // =====================================================
    // 🛠 MANUAL ENTRY
    // =====================================================
    BookResponseDTO createManualBook(BookResponseDTO dto, String userEmail);

    // =====================================================
    // ✏ UPDATE BOOK
    // =====================================================
    BookResponseDTO updateBook(String isbn, BookResponseDTO dto, String userEmail);

    // =====================================================
    // 📌 PIN BOOK
    // =====================================================
    BookResponseDTO pinBook(String isbn, String userEmail);

    // =====================================================
    // ❌ UNPIN BOOK
    // =====================================================
    void unpinBook(String isbn, String userEmail);

    // =====================================================
    // 📚 VIEW PINNED BOOKS
    // =====================================================
    List<BookResponseDTO> getAllPinnedBooks(String userEmail);

    // =====================================================
    // 🔎 SEARCH LOGS
    // =====================================================
    List<SearchLogDTO> getAllBooks(String keyword, String searchBy);

    // =====================================================
    // 🗑 DELETE SINGLE SEARCH LOG
    // =====================================================
    void deleteSearchLog(Long id);

    // =====================================================
    // 🗑 DELETE ALL SEARCH LOGS
    // =====================================================
    void deleteAllSearchLogs();

    // =====================================================
    // 📖 GET BOOK BY ISBN
    // =====================================================
    BookResponseDTO getBookByIsbn(String isbn);
}
