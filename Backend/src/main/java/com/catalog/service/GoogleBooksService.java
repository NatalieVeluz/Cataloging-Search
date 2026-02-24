package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import java.util.List;

/**
 * GoogleBooksService
 *
 * This service handles communication with the Google Books API.
 *
 * It is responsible for:
 * - Searching books by title
 * - Searching books by author
 * - Enriching existing book data with additional metadata
 *
 * In the Cataloging Search Resources Platform, this service
 * helps enhance bibliographic records retrieved from other
 * sources such as LOC or WorldCat.
 */
public interface GoogleBooksService {

    /**
     * Enriches an existing book record with additional data
     * from Google Books (e.g., description, publisher, thumbnail).
     *
     * This is typically used after a book is retrieved
     * from another source to improve metadata completeness.
     *
     * @param book BookResponseDTO to be enriched
     */
    void enrich(BookResponseDTO book);

    /**
     * Searches Google Books by title.
     *
     * May return multiple matching results.
     *
     * @param title Book title (can be partial)
     * @return List of matching books
     */
    List<BookResponseDTO> searchByTitle(String title);

    /**
     * Searches Google Books by author name.
     *
     * May return multiple matching results.
     *
     * @param author Author name (can be partial)
     * @return List of matching books
     */
    List<BookResponseDTO> searchByAuthor(String author);
}