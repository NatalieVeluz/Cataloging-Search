package com.catalog.service;

// Import DTO used to return aggregated book data
import com.catalog.dto.BookResponseDTO;

import java.util.List;

/**
 * BookAggregationService
 *
 * This service is responsible for aggregating book data
 * from multiple external bibliographic sources such as:
 * - Library of Congress (LOC)
 * - WorldCat
 * - Amazon Books
 * - Goodreads
 *
 * It centralizes search results and formats them into
 * a unified BookResponseDTO before returning to the frontend.
 *
 * The userEmail parameter is used to:
 * - Save search logs
 * - Associate search history with a specific user
 * - Apply user-based features (like pinned books)
 */
public interface BookAggregationService {

    /**
     * Searches for a book using ISBN.
     *
     * ISBN search usually returns a single exact match.
     *
     * @param isbn       Unique ISBN number of the book
     * @param userEmail  Email of the user performing the search
     * @return Aggregated and normalized book result
     */
    BookResponseDTO searchByIsbn(String isbn, String userEmail);

    /**
     * Searches for books using title (partial matching allowed).
     *
     * Can return multiple results from different sources.
     *
     * @param title      Book title (can be partial)
     * @param userEmail  Email of the user performing the search
     * @return List of aggregated book results
     */
    List<BookResponseDTO> searchByTitle(String title, String userEmail);

    /**
     * Searches for books using author name (partial matching allowed).
     *
     * Can return multiple results from different sources.
     *
     * @param author     Author name (can be partial)
     * @param userEmail  Email of the user performing the search
     * @return List of aggregated book results
     */
    List<BookResponseDTO> searchByAuthor(String author, String userEmail);
}