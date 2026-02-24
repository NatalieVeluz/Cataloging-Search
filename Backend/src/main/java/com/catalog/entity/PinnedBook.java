package com.catalog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PinnedBook Entity
 *
 * This entity represents a book that has been pinned
 * by a specific user. It establishes a relationship
 * between the Book entity and the user who pinned it.
 *
 * The entity is mapped to the "pinned_books" table.
 * A unique constraint ensures that a user cannot pin
 * the same book multiple times.
 */
@Entity
@Table(name = "pinned_books",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"book_id", "pinned_by"})
        })
public class PinnedBook {

    /**
     * Primary key of the pinned record.
     * Automatically generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-one relationship with the Book entity.
     * Each pinned record refers to one book.
     */
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Email of the user who pinned the book.
     * Stored as a string to simplify association.
     */
    @Column(name = "pinned_by", nullable = false)
    private String pinnedBy;

    /**
     * Timestamp indicating when the book was pinned.
     * Automatically initialized with the current time.
     */
    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt = LocalDateTime.now();

    // ================= GETTERS AND SETTERS =================

    public Long getId() { return id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getPinnedBy() { return pinnedBy; }
    public void setPinnedBy(String pinnedBy) { this.pinnedBy = pinnedBy; }

    public LocalDateTime getPinnedAt() { return pinnedAt; }
    public void setPinnedAt(LocalDateTime pinnedAt) { this.pinnedAt = pinnedAt; }
}