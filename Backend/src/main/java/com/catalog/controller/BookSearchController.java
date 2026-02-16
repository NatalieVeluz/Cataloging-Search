package com.catalog.controller;

import com.catalog.service.BookAggregationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class BookSearchController {

    private final BookAggregationService service;

    public BookSearchController(BookAggregationService service) {
        this.service = service;
    }

    // ================= ISBN =================
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> searchByIsbn(
            @PathVariable String isbn,
            @RequestParam String userEmail) {  // 🔥 REQUIRED now

        if (!isbn.matches("\\d{10}|\\d{13}")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid ISBN format");
        }

        return ResponseEntity.ok(
                service.searchByIsbn(isbn, userEmail)
        );
    }

    // ================= TITLE =================
    @GetMapping("/title")
    public ResponseEntity<?> searchByTitle(
            @RequestParam String value,
            @RequestParam String userEmail) {  // 🔥 REQUIRED now

        return ResponseEntity.ok(
                service.searchByTitle(value, userEmail)
        );
    }

    // ================= AUTHOR =================
    @GetMapping("/author")
    public ResponseEntity<?> searchByAuthor(
            @RequestParam String value,
            @RequestParam String userEmail) {  // 🔥 REQUIRED now

        return ResponseEntity.ok(
                service.searchByAuthor(value, userEmail)
        );
    }
}
