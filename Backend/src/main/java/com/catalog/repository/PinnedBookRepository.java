package com.catalog.repository;

// Import the PinnedBook entity
import com.catalog.entity.PinnedBook;

// Import JpaRepository to get built-in CRUD methods
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * PinnedBookRepository
 *
 * This repository handles all database operations related to
 * pinned books in the system.
 *
 * It extends JpaRepository, which automatically provides:
 * - save()        -> insert/update pinned book
 * - findById()    -> get pinned book by ID
 * - findAll()     -> get all pinned books
 * - deleteById()  -> delete pinned book by ID
 * - and other built-in CRUD operations
 */
public interface PinnedBookRepository
        extends JpaRepository<PinnedBook, Long> {

    /**
     * Checks if a specific book is already pinned
     * by a specific user.
     *
     * Used to prevent duplicate pinning.
     *
     * @param bookId  ID of the book
     * @param pinnedBy Email or username of the user
     * @return Optional containing the pinned record if it exists
     */
    Optional<PinnedBook> findByBook_IdAndPinnedBy(
            Long bookId, String pinnedBy);

    /**
     * Retrieves all pinned books of a specific user.
     *
     * Used when displaying the "My Pinned Books" page.
     *
     * @param pinnedBy Email or username of the user
     * @return List of pinned books
     */
    List<PinnedBook> findByPinnedBy(String pinnedBy);

    /**
     * Deletes a pinned book record based on
     * book ID and the user who pinned it.
     *
     * Used when a user clicks "Unpin".
     *
     * @param bookId  ID of the book
     * @param pinnedBy Email or username of the user
     */
    void deleteByBook_IdAndPinnedBy(
            Long bookId, String pinnedBy);
}