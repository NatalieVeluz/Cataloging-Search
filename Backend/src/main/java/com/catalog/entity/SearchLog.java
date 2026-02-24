package com.catalog.entity;

import com.catalog.enums.SearchType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * SearchLog Entity
 *
 * This entity represents a record of a search operation
 * performed by a user within the system.
 *
 * Each search log stores:
 * - The search keyword entered
 * - The type of search performed (ISBN, Title, Author)
 * - The email of the user who performed the search
 * - The timestamp of the search
 * - The associated book retrieved during the search
 *
 * The entity is mapped to the "search_logs" table.
 */
@Entity
@Table(name = "search_logs")
public class SearchLog {

    /**
     * Primary key of the search log.
     * Automatically generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The actual value entered by the user during search.
     */
    @Column(name = "query_value")
    private String queryValue;

    /**
     * Type of search performed.
     * Stored as a String representation of the enum value.
     *
     * Possible values:
     * - ISBN
     * - TITLE
     * - AUTHOR
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "query_type")
    private SearchType searchType;

    /**
     * Email of the user who performed the search.
     * Used for audit and tracking purposes.
     */
    @Column(name = "user_email")
    private String userEmail;

    /**
     * Timestamp indicating when the search was executed.
     */
    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    /**
     * Many-to-one relationship with the Book entity.
     * Each search log is associated with one book result.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Automatically sets the search timestamp
     * before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        searchedAt = LocalDateTime.now();
    }

    // ================= GETTERS AND SETTERS =================

    public Long getId() { return id; }

    public String getQueryValue() { return queryValue; }
    public void setQueryValue(String queryValue) { this.queryValue = queryValue; }

    public SearchType getSearchType() { return searchType; }
    public void setSearchType(SearchType searchType) { this.searchType = searchType; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getSearchedAt() { return searchedAt; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}