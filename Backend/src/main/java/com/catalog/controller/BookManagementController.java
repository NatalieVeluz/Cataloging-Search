package com.catalog.controller;

import com.catalog.dto.BookResponseDTO;
import com.catalog.dto.SearchLogDTO;
import com.catalog.service.BookManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:4200")
public class BookManagementController {

    private final BookManagementService service;

    public BookManagementController(BookManagementService service) {
        this.service = service;
    }

    // =====================================================
    // 🛠 MANUAL ENTRY
    // =====================================================
    @PostMapping("/manual")
    public ResponseEntity<BookResponseDTO> createManualBook(
            @RequestBody BookResponseDTO dto,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                service.createManualBook(dto, userEmail)
        );
    }

    // =====================================================
    // 📌 PIN BOOK
    // =====================================================
    @PostMapping("/pin/{isbn}")
    public ResponseEntity<BookResponseDTO> pinBook(
            @PathVariable String isbn,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                service.pinBook(isbn, userEmail)
        );
    }

    // =====================================================
    // ✏ UPDATE BOOK
    // =====================================================
    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable String isbn,
            @RequestBody BookResponseDTO dto,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                service.updateBook(isbn, dto, userEmail)
        );
    }

    // =====================================================
    // ❌ UNPIN BOOK
    // =====================================================
    @DeleteMapping("/unpin/{isbn}")
    public ResponseEntity<String> unpinBook(
            @PathVariable String isbn,
            @RequestParam String userEmail) {

        service.unpinBook(isbn, userEmail);
        return ResponseEntity.ok("Book unpinned successfully");
    }

    // =====================================================
    // 📚 VIEW PINNED BOOKS
    // =====================================================
    @GetMapping("/pinned")
    public ResponseEntity<List<BookResponseDTO>> getPinnedBooks(
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                service.getAllPinnedBooks(userEmail)
        );
    }

    // =====================================================
    // 🔎 SEARCH LOGS
    // =====================================================
    @GetMapping("/search-logs")
    public ResponseEntity<List<SearchLogDTO>> getSearchLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "title") String searchBy
    ) {

        return ResponseEntity.ok(
                service.getAllBooks(keyword, searchBy)
        );
    }

    // =====================================================
    // 🗑 DELETE SEARCH LOG
    // =====================================================
    @DeleteMapping("/search-logs/{id}")
    public ResponseEntity<String> deleteSearchLog(
            @PathVariable Long id) {

        service.deleteSearchLog(id);
        return ResponseEntity.ok("Search log deleted successfully");
    }

    // =====================================================
    // 🗑 DELETE ALL SEARCH LOGS
    // =====================================================
    @DeleteMapping("/search-logs")
    public ResponseEntity<String> deleteAllSearchLogs() {

        service.deleteAllSearchLogs();
        return ResponseEntity.ok("All search logs deleted successfully");
    }

    // =====================================================
    // 📖 GET BOOK BY ISBN
    // =====================================================
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(
            @PathVariable String isbn) {

        return ResponseEntity.ok(
                service.getBookByIsbn(isbn)
        );
    }
}
