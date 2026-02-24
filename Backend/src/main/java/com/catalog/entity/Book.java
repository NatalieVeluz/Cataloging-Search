package com.catalog.entity;

import jakarta.persistence.*;

/**
 * Book Entity
 *
 * This entity represents a book record stored in the system database.
 * It contains complete bibliographic metadata including core details
 * and enriched information retrieved from external APIs.
 *
 * The entity is mapped to the "books" table in the database.
 */
@Entity
@Table(name = "books")
public class Book {

    /**
     * Primary key of the book.
     * Automatically generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * International Standard Book Number.
     * Must be unique in the database.
     */
    @Column(unique = true)
    private String isbn;

    /**
     * Core bibliographic fields.
     */
    private String title;
    private String authors;
    private String lccn;
    private String cutterNumber;
    private String coverImageUrl;
    private String publicationYear;
    private String edition;
    private String publisher;

    /**
     * Detailed book summary.
     * Stored as TEXT to support long descriptions.
     */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /**
     * Additional content notes related to the book.
     * Stored as TEXT to allow extended information.
     */
    @Column(columnDefinition = "TEXT")
    private String contentNotes;

    /**
     * Indicates the source of metadata enrichment
     * (e.g., Manual Entry, Google Books, LOC, Open Library).
     */
    private String metadataSource;

    // ================= GETTERS AND SETTERS =================

    public Long getId() { return id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getLccn() { return lccn; }
    public void setLccn(String lccn) { this.lccn = lccn; }

    public String getCutterNumber() { return cutterNumber; }
    public void setCutterNumber(String cutterNumber) { this.cutterNumber = cutterNumber; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContentNotes() { return contentNotes; }
    public void setContentNotes(String contentNotes) { this.contentNotes = contentNotes; }

    public String getMetadataSource() { return metadataSource; }
    public void setMetadataSource(String metadataSource) { this.metadataSource = metadataSource; }
}