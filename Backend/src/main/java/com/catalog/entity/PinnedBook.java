package com.catalog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pinned_books",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"book_id", "pinned_by"})
        })
public class PinnedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "pinned_by", nullable = false)
    private String pinnedBy;

    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt = LocalDateTime.now();

    // Getters & Setters

    public Long getId() { return id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getPinnedBy() { return pinnedBy; }
    public void setPinnedBy(String pinnedBy) { this.pinnedBy = pinnedBy; }

    public LocalDateTime getPinnedAt() { return pinnedAt; }
    public void setPinnedAt(LocalDateTime pinnedAt) { this.pinnedAt = pinnedAt; }
}
