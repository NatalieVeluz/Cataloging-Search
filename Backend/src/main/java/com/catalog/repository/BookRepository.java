package com.catalog.repository;

import com.catalog.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * BookRepository
 *
 * This repository interface provides database access
 * operations for the Book entity.
 *
 * It extends JpaRepository, which automatically provides
 * standard CRUD operations such as:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 *
 * Additional custom query methods are defined for
 * optimized search functionality.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Retrieves a book by its exact ISBN.
     *
     * @param isbn unique ISBN number
     * @return Optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Searches books by title using case-insensitive matching.
     * Results are ordered by most recently inserted records.
     *
     * @param title partial or full title keyword
     * @return list of matching books
     */
    List<Book> findByTitleContainingIgnoreCaseOrderByIdDesc(String title);

    /**
     * Searches books by author name using case-insensitive matching.
     * Results are ordered by most recently inserted records.
     *
     * @param authors partial or full author keyword
     * @return list of matching books
     */
    List<Book> findByAuthorsContainingIgnoreCaseOrderByIdDesc(String authors);

    /**
     * Searches books using partial ISBN matching.
     * Useful for flexible numeric searches.
     *
     * @param isbn partial ISBN value
     * @return list of matching books
     */
    List<Book> findByIsbnContainingOrderByIdDesc(String isbn);
}