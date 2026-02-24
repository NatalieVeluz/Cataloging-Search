package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import com.catalog.dto.SearchLogDTO;

import java.util.List;

/**
 * BookManagementService
 *
 * This service handles internal book management features
 * of the Cataloging Search Resources Platform.
 *
 * Unlike BookAggregationService (which handles external API searches),
 * this service manages:
 * - Manual book entries
 * - Book updates
 * - Pin/unpin functionality
 * - Search logs management
 * - Admin-level log deletion
 *
 * Most methods require userEmail to:
 * - Associate actions with a user
 * - Apply role-based access control (RBAC)
 * - Record audit logs
 */
public interface BookManagementService {

    // =====================================================
    // MANUAL ENTRY
    // =====================================================

    /**
     * Creates a manually encoded book entry.
     * Used when the book is not available from external APIs.
     *
     * @param dto        Book details entered manually
     * @param userEmail  Email of the user creating the record
     * @return Saved book data
     */
    BookResponseDTO createManualBook(BookResponseDTO dto, String userEmail);

    // =====================================================
    // UPDATE BOOK
    // =====================================================

    /**
     * Updates an existing book using ISBN as identifier.
     * Typically allowed for Admin/Librarian roles.
     *
     * @param isbn       Unique identifier of the book
     * @param dto        Updated book details
     * @param userEmail  Email of the user performing the update
     * @return Updated book data
     */
    BookResponseDTO updateBook(String isbn, BookResponseDTO dto, String userEmail);

    // =====================================================
    // PIN BOOK
    // =====================================================

    /**
     * Pins a book for a specific user.
     * Prevents duplicate pinning.
     *
     * @param isbn       Book ISBN
     * @param userEmail  Email of the user
     * @return Book details after pinning
     */
    BookResponseDTO pinBook(String isbn, String userEmail);

    // =====================================================
    // UNPIN BOOK
    // =====================================================

    /**
     * Removes a pinned book for a specific user.
     *
     * @param isbn       Book ISBN
     * @param userEmail  Email of the user
     */
    void unpinBook(String isbn, String userEmail);

    // =====================================================
    // VIEW PINNED BOOKS
    // =====================================================

    /**
     * Retrieves all books pinned by a specific user.
     *
     * @param userEmail Email of the user
     * @return List of pinned books
     */
    List<BookResponseDTO> getAllPinnedBooks(String userEmail);

    // =====================================================
    // SEARCH LOGS
    // =====================================================

    /**
     * Retrieves search logs based on keyword and search type.
     *
     * searchBy can be:
     * - title
     * - author
     * - isbn
     *
     * Used in search history filtering.
     *
     * @param keyword  Search keyword
     * @param searchBy Field to search by
     * @return List of search log records
     */
    List<SearchLogDTO> getAllBooks(String keyword, String searchBy);

    // =====================================================
    // DELETE SINGLE SEARCH LOG (ADMIN)
    // =====================================================

    /**
     * Deletes a specific search log entry.
     * Typically restricted to Admin role.
     *
     * @param id         Search log ID
     * @param userEmail  Email of the admin performing deletion
     */
    void deleteSearchLog(Long id, String userEmail);

    // =====================================================
    // DELETE ALL SEARCH LOGS (ADMIN)
    // =====================================================

    /**
     * Deletes all search logs in the system.
     * Should validate admin privileges before execution.
     *
     * @param userEmail Email of the admin performing deletion
     */
    void deleteAllSearchLogs(String userEmail);

    // =====================================================
    // GET BOOK BY ISBN
    // =====================================================

    /**
     * Retrieves a single book using ISBN.
     *
     * @param isbn Unique book identifier
     * @return Book details
     */
    BookResponseDTO getBookByIsbn(String isbn);
}