package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import java.util.List;

/**
 * OpenLibraryService
 *
 * This service handles communication with the Open Library API.
 *
 * It serves as an additional bibliographic source in the
 * Cataloging Search Resources Platform to improve data coverage
 * and availability when other sources return limited results.
 *
 * Responsibilities:
 * - Enrich book metadata using ISBN
 * - Perform title-based search
 * - Perform author-based search
 */
public interface OpenLibraryService {

    /**
     * Enriches an existing book record using
     * data retrieved from Open Library.
     *
     * This is typically used to supplement or complete
     * missing bibliographic information.
     *
     * @param book BookResponseDTO to be enriched
     */
    void enrich(BookResponseDTO book);

    /**
     * Searches books in Open Library by title.
     *
     * @param title Book title (partial or full)
     * @return List of matching books
     */
    List<BookResponseDTO> searchByTitle(String title);

    /**
     * Searches books in Open Library by author.
     *
     * @param author Author name (partial or full)
     * @return List of matching books
     */
    List<BookResponseDTO> searchByAuthor(String author);
}