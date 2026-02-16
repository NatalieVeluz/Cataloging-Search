package com.catalog.entity;

import com.catalog.enums.SearchType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_logs")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query_value")
    private String queryValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "query_type")
    private SearchType searchType;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    // ✅ FK RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @PrePersist
    protected void onCreate() {
        searchedAt = LocalDateTime.now();
    }

    // ===== GETTERS & SETTERS =====

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
