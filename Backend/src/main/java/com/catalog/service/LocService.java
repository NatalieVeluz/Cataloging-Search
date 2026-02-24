package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import java.util.List;

/**
 * LocService
 *
 * This service handles communication with the
 * Library of Congress (LOC) API.
 *
 * It is used as one of the primary bibliographic
 * data sources in the Cataloging Search Resources Platform.
 *
 * Responsibilities:
 * - Enrich book metadata using ISBN
 * - Perform title-based search
 * - Perform author-based search
 */
public interface LocService {

    /**
     * Enriches an existing book record using
     * data retrieved from the Library of Congress.
     *
     * Typically used when searching by ISBN
     * to complete missing bibliographic fields.
     *
     * @param book BookResponseDTO to be enriched
     */
    void enrich(BookResponseDTO book);

    /**
     * Searches books in the LOC database by title.
     *
     * This acts as a primary source for title searches.
     *
     * @param title Book title (partial or full)
     * @return List of matching books
     */
    List<BookResponseDTO> searchByTitle(String title);

    /**
     * Searches books in the LOC database by author.
     *
     * This acts as a primary source for author searches.
     *
     * @param author Author name (partial or full)
     * @return List of matching books
     */
    List<BookResponseDTO> searchByAuthor(String author);
}