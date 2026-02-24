package com.catalog.controller;

import com.catalog.service.BookAggregationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Book Search Controller
 *
 * This controller handles book search requests using different
 * search criteria such as:
 * - ISBN
 * - Title
 * - Author
 *
 * The controller delegates search operations to the
 * BookAggregationService, which retrieves and aggregates
 * metadata from multiple external sources.
 *
 * Base URL: /api/search
 */
@RestController
@RequestMapping("/api/search")
public class BookSearchController {

    private final BookAggregationService service;

    /**
     * Constructor-based dependency injection of BookAggregationService.
     *
     * @param service service responsible for book aggregation logic
     */
    public BookSearchController(BookAggregationService service) {
        this.service = service;
    }

    /**
     * Searches for a book using its ISBN.
     *
     * Endpoint: GET /api/search/isbn/{isbn}
     *
     * Validates the ISBN format before delegating the request
     * to the service layer.
     *
     * @param isbn ISBN number (must be 10 or 13 digits)
     * @param userEmail email of the logged-in user
     * @return aggregated book metadata or error message
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> searchByIsbn(
            @PathVariable String isbn,
            @RequestParam String userEmail) {

        // Validate ISBN format (10 or 13 numeric digits)
        if (!isbn.matches("\\d{10}|\\d{13}")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid ISBN format");
        }

        return ResponseEntity.ok(
                service.searchByIsbn(isbn, userEmail)
        );
    }

    /**
     * Searches for books by title.
     *
     * Endpoint: GET /api/search/title
     *
     * @param value title keyword entered by the user
     * @param userEmail email of the logged-in user
     * @return aggregated search results
     */
    @GetMapping("/title")
    public ResponseEntity<?> searchByTitle(
            @RequestParam String value,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                service.searchByTitle(value, userEmail)
        );
    }

    /**
     * Searches for books by author name.
     *
     * Endpoint: GET /api/search/author
     *
     * @param value author name keyword entered by the user
     * @param userEmail email of the logged-in user
     * @return aggregated search results
     */
    @GetMapping("/author")
    public ResponseEntity<?> searchByAuthor(
            @RequestParam String value,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                service.searchByAuthor(value, userEmail)
        );
    }
}